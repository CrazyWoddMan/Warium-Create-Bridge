package crazywoddman.warium_create.mixin.fluid;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;

import net.mcreator.crustychunks.block.entity.FuelTankBlockEntity;
import net.mcreator.crustychunks.block.entity.FuelTankInputBlockEntity;
import net.mcreator.crustychunks.block.entity.FuelTankModuleBlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(
    value = {
        FuelTankBlockEntity.class,
        FuelTankModuleBlockEntity.class,
        FuelTankInputBlockEntity.class
    },
    remap = false
)
public abstract class FuelTanksBlockEntityMixin implements IHaveGoggleInformation {

    @Shadow(remap = false)
    private FluidTank fluidTank;

    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    private void acceptAnyFuel(BlockPos position, BlockState state, CallbackInfo callback) {
        fluidTank.setValidator(this::isAllowedFluid);
    }

    @Unique
    private boolean isAllowedFluid(FluidStack fluidStack) {

        if (fluidStack != null && !fluidStack.isEmpty()) {
            String fuelType = "";
            Fluid fluid = fluidStack.getFluid();

            if (fluid != null) {
                String path = ForgeRegistries.FLUIDS.getKey(fluid).getPath();
                if (
                    path.equals("kerosene") ||
                    ForgeRegistries.FLUIDS
                        .tags()
                        .getTag(FluidTags.create(ResourceLocation.fromNamespaceAndPath("forge", "kerosene")))
                        .contains(fluid)
                ) 
                    fuelType = "Kerosene";

                else if (
                    path.equals("diesel") ||
                    ForgeRegistries.FLUIDS
                        .tags()
                        .getTag(FluidTags.create(ResourceLocation.fromNamespaceAndPath("forge", "diesel")))
                        .contains(fluid)
                )
                    fuelType = "Diesel";
            
                if (!fuelType.isEmpty()) {
                    ((BlockEntity) (Object) this).getPersistentData().putString("FuelType", fuelType);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return ((IHaveGoggleInformation) (Object) this).containedFluidTooltip(
            tooltip,
            isPlayerSneaking,
            LazyOptional.of(() -> this.fluidTank)
        );
    }
}