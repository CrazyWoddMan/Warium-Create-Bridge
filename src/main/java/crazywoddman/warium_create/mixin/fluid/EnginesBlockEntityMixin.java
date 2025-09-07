package crazywoddman.warium_create.mixin.fluid;

import net.mcreator.crustychunks.block.entity.EngineCyllinderBlockEntity;
import net.mcreator.crustychunks.block.entity.JetTurbineBlockEntity;
import net.mcreator.crustychunks.block.entity.LightCombustionEngineBlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;

@Mixin(
    value = {
        LightCombustionEngineBlockEntity.class,
        EngineCyllinderBlockEntity.class,
        JetTurbineBlockEntity.class
    },
    remap = false
)
public class EnginesBlockEntityMixin implements IHaveGoggleInformation {

    @Shadow(remap = false)
    private FluidTank fluidTank;

    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    private void acceptAnyDiesel(BlockPos pos, BlockState state, CallbackInfo ci) {
        fluidTank.setValidator(this::allowedFluid);
    }

    @Unique
    private boolean allowedFluid(FluidStack fluidStack) {
        if (fluidStack.isEmpty())
            return false;
        BlockEntity self = (BlockEntity) (Object) this;
        String fuelType = "";

        if (self.getPersistentData().contains("FuelType"))
            fuelType = self.getPersistentData().getString("FuelType").toLowerCase();

        if (fuelType.isEmpty())
            return false;
            
        Fluid fluid = fluidStack.getFluid();
        boolean isAcceptableId = ForgeRegistries.FLUIDS
            .getKey(fluid)
            .getPath()
            .equals(fuelType);
        boolean isAcceptableTag = ForgeRegistries.FLUIDS
            .tags()
            .getTag(FluidTags.create(ResourceLocation.fromNamespaceAndPath("forge", fuelType)))
            .contains(fluid);
        return isAcceptableId || isAcceptableTag;
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