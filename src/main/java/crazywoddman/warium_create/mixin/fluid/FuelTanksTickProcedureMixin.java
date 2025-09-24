package crazywoddman.warium_create.mixin.fluid;

import net.mcreator.crustychunks.procedures.FuelTankInputTickProcedure;
import net.mcreator.crustychunks.procedures.FuelTankModuleOnTickUpdateProcedure;
import net.mcreator.crustychunks.procedures.FuelTankTickProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.concurrent.atomic.AtomicInteger;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
    value = {
        FuelTankTickProcedure.class,
        FuelTankModuleOnTickUpdateProcedure.class,
        FuelTankInputTickProcedure.class
    },
    targets = {
        "net.mcreator.crustychunks.procedures.FuelTankModuleOnTickUpdateProcedure$9",
        "net.mcreator.crustychunks.procedures.FuelTankModuleOnTickUpdateProcedure$19",
        "net.mcreator.crustychunks.procedures.FuelTankInputTickProcedure$5",
        "net.mcreator.crustychunks.procedures.FuelTankInputTickProcedure$11",
        "net.mcreator.crustychunks.procedures.FuelTankTickProcedure$12",
        "net.mcreator.crustychunks.procedures.FuelTankTickProcedure$22"
    },
    remap = false
)
public class FuelTanksTickProcedureMixin {

    private static Fluid realFluid;
    private static Fluid neighborFluid;

    @Inject(
        method = "execute",
        at = @At("HEAD"),
        require = 0
    )
    private static void captureFluid(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        BlockEntity blockEntity = world.getBlockEntity(BlockPos.containing(x, y, z));

        if (blockEntity != null) {
            String key = blockEntity.saveWithoutMetadata().getCompound("fluidTank").getString("FluidName");

            if (key != null && !key.equals("minecraft:empty"))
                realFluid = ForgeRegistries.FLUIDS.getValue(ResourceLocation.tryParse(key));
        }
    }

    @Inject(
        method = "fillTankSimulate",
        at = @At("HEAD"),
        require = 0
    )
    private void captureFluidAlt(LevelAccessor world, BlockPos pos, int amount, CallbackInfoReturnable<Integer> cir) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity != null) {
            String key = blockEntity.saveWithoutMetadata().getCompound("fluidTank").getString("FluidName");

            if (key != null && !key.equals("minecraft:empty"))
                realFluid = ForgeRegistries.FLUIDS.getValue(ResourceLocation.tryParse(key));
        }
    }

    @Inject(
        method = "lambda$fillTankSimulate$0",
        at = @At("HEAD"),
        require = 0
    )
    private static void captureNeighborFluid(AtomicInteger atomicInteger, int amount, IFluidHandler capability, CallbackInfo ci) {
        if (capability != null)
            neighborFluid = capability.getFluidInTank(0).getFluid();
    }

    @Inject(
        method = {
            "lambda$execute$1",
            "lambda$execute$3"
        },
        at = @At("HEAD"),
        require = 0
    )
    private static void captureNeighborFluidAlt(int amount, IFluidHandler capability, CallbackInfo ci) {
        if (capability != null)
            neighborFluid = capability.getFluidInTank(0).getFluid();

    }

    @ModifyArg(
        method = {
            "lambda$execute$1",
            "lambda$execute$3",
            "lambda$fillTankSimulate$0"
        },
        at = @At(
            value = "INVOKE", 
            target = "Lnet/minecraftforge/fluids/FluidStack;<init>(Lnet/minecraft/world/level/material/Fluid;I)V"
        )
    )
    private static Fluid modifyFluid(Fluid fluid) {
        if (neighborFluid != null && !neighborFluid.equals(Fluids.EMPTY))
            return neighborFluid;
            
        return realFluid != null ? realFluid : fluid;
    }
}