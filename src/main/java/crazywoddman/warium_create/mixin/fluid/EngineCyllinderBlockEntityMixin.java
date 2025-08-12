package crazywoddman.warium_create.mixin.fluid;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import net.mcreator.crustychunks.block.entity.EngineCyllinderBlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
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

@Mixin(EngineCyllinderBlockEntity.class)
public class EngineCyllinderBlockEntityMixin implements IHaveGoggleInformation {

    @Shadow
    private FluidTank fluidTank;

    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    private void warium$acceptAnyFuel(BlockPos position, BlockState state, CallbackInfo ci) {
        fluidTank.setValidator(this::isAllowedFluid);
    }

    @Unique
    private boolean isAllowedFluid(FluidStack fluidStack) {
        boolean isAcceptableId = ForgeRegistries.FLUIDS.getKey(fluidStack.getFluid()).getPath().equals("diesel");
        boolean isAcceptableTag = fluidStack.getFluid().is(FluidTags.create(new ResourceLocation("forge", "diesel")));
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