package crazywoddman.warium_create.mixin.fluid;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import net.mcreator.crustychunks.block.entity.FuelTankModuleBlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(FuelTankModuleBlockEntity.class)
public abstract class FuelTankModuleBlockEntityMixin implements IHaveGoggleInformation {

    @Shadow
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
        boolean isAcceptableId = (
            ForgeRegistries.FLUIDS.getKey(fluidStack.getFluid()).getPath().equals("kerosene") ||
            ForgeRegistries.FLUIDS.getKey(fluidStack.getFluid()).getPath().equals("diesel") ||
            ForgeRegistries.FLUIDS.getKey(fluidStack.getFluid()).getPath().equals("oil") ||
            ForgeRegistries.FLUIDS.getKey(fluidStack.getFluid()).getPath().equals("petrolium") ||
            ForgeRegistries.FLUIDS.getKey(fluidStack.getFluid()).getPath().equals("gasoline")
        );
        boolean isAcceptableTag = (
            fluidStack.getFluid().is(FluidTags.create(new ResourceLocation("forge", "kerosene"))) ||
            fluidStack.getFluid().is(FluidTags.create(new ResourceLocation("forge", "diesel"))) ||
            fluidStack.getFluid().is(FluidTags.create(new ResourceLocation("forge", "heavy_oil"))) ||
            fluidStack.getFluid().is(FluidTags.create(new ResourceLocation("forge", "gasoline")))
        );
        return !fluidStack.isEmpty() && (isAcceptableId || isAcceptableTag);
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