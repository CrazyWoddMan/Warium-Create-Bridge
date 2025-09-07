package crazywoddman.warium_create.mixin.valkyrien_warium;

import net.mcreator.valkyrienwarium.block.entity.LiquidFuelRocketBlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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

@Mixin(value = LiquidFuelRocketBlockEntity.class, remap = false)
public class LiquidFuelRocketBlockEntityMixin implements IHaveGoggleInformation {

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

        Fluid fluid = fluidStack.getFluid();
        boolean isAcceptableId = ForgeRegistries.FLUIDS
            .getKey(fluid)
            .getPath()
            .equals("kerosene");
        boolean isAcceptableTag = ForgeRegistries.FLUIDS
            .tags()
            .getTag(FluidTags.create(ResourceLocation.fromNamespaceAndPath("forge", "kerosene")))
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