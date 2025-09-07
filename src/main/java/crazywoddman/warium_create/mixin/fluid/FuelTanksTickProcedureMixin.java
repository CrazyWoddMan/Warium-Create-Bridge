package crazywoddman.warium_create.mixin.fluid;

import net.mcreator.crustychunks.procedures.FuelTankInputTickProcedure;
import net.mcreator.crustychunks.procedures.FuelTankModuleOnTickUpdateProcedure;
import net.mcreator.crustychunks.procedures.FuelTankTickProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import crazywoddman.warium_create.util.FluidTransferContext;

@Mixin(
    value = {
        FuelTankTickProcedure.class,
        FuelTankModuleOnTickUpdateProcedure.class,
        FuelTankInputTickProcedure.class
    },
    remap = false
)
public class FuelTanksTickProcedureMixin {

    @Inject(
        method = "execute",
        at = @At("HEAD")
    )
    private static void captureSourceFluid(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        BlockEntity blockEntity = world.getBlockEntity(BlockPos.containing(x, y, z));

        if (blockEntity != null) {
            blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, null).ifPresent(capability -> {
                FluidStack stack = capability.getFluidInTank(0);

                if (!stack.isEmpty())
                    FluidTransferContext.setFluid(stack.getFluid());
            });
        }
    }

    @Inject(
        method = "execute",
        at = @At("RETURN")
    )
    private static void clearSourceFluid(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        FluidTransferContext.clearFluid();
    }

    @ModifyConstant(
        method = "execute",
        constant = @Constant(stringValue = "Kerosene")
    )
    private static String setType(String value, LevelAccessor world, double x, double y, double z) {
        BlockEntity blockEntity = world.getBlockEntity(BlockPos.containing(x, y, z));

        if (blockEntity != null) {
            CompoundTag data = blockEntity.getPersistentData();

            if (data.contains("FuelType"))
                return data.getString("FuelType");
        }
        return value;
    }

    @ModifyArg(
        method = "lambda$execute$1", 
        at = @At(
            value = "INVOKE", 
            target = "Lnet/minecraftforge/fluids/FluidStack;<init>(Lnet/minecraft/world/level/material/Fluid;I)V"
        )
    )
    private static Fluid modifyKeroseneFluid(Fluid kerosene) {
        Fluid realFluid = FluidTransferContext.getFluid();

        return realFluid != null ? realFluid : kerosene;
    }
}