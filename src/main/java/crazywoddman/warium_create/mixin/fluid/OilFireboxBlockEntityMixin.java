package crazywoddman.warium_create.mixin.fluid;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;

import net.mcreator.crustychunks.block.entity.OilFireboxBlockEntity;
import net.mcreator.crustychunks.init.CrustyChunksModFluids;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.ForgeRegistries;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(OilFireboxBlockEntity.class)
public abstract class OilFireboxBlockEntityMixin implements IHaveGoggleInformation {

    @Unique
    private FluidTank fluidTank;
    
    @Unique
    private FluidTank getOrCreateFluidTank() {
        if (fluidTank == null) {
            final OilFireboxBlockEntity blockEntity = (OilFireboxBlockEntity)(Object)this;
            fluidTank = new FluidTank(5000) {
                @Override
                public int fill(FluidStack fluidStack, FluidAction action) {
                    boolean isAcceptableId = ForgeRegistries.FLUIDS.getKey(fluidStack.getFluid()).getPath().equals("kerosene");
                    boolean isAcceptableTag = fluidStack.getFluid().is(FluidTags.create(new ResourceLocation("forge", "kerosene")));
                    if (fluidStack.isEmpty() || !(isAcceptableId || isAcceptableTag))
                        return 0;
                    int fuel = blockEntity.getPersistentData().getInt("Fuel");
                    int capacity = this.getCapacity();
                    int toAdd = fluidStack.getAmount();
                    if (action != FluidAction.SIMULATE && toAdd > 0 && fuel < capacity) {
                        blockEntity.getPersistentData().putDouble("Fuel", Math.min(capacity, fuel + toAdd));
                        blockEntity.setChanged();
                        blockEntity.getLevel().sendBlockUpdated(blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
                    }
                    syncFromPersistent();
                    return Math.min(toAdd, capacity - fuel);
                }
                @Override
                public FluidStack drain(FluidStack fluidstack, FluidAction action) {
                    return FluidStack.EMPTY;
                }
                @Override
                public FluidStack drain(int maxDrain, FluidAction action) {
                    return FluidStack.EMPTY;
                }
            };
        }
        return fluidTank;
    }

    @Unique
    private final LazyOptional<IFluidHandler> lazyFluid = LazyOptional.of(() -> getOrCreateFluidTank());

    @Inject(
        method = "getCapability",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void injectFluidCap(Capability<?> cap, Direction side, CallbackInfoReturnable<LazyOptional<Object>> cir) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            syncFromPersistent();
            cir.setReturnValue(lazyFluid.cast());
        }
    }

    @Unique
    private void syncFromPersistent() {
        OilFireboxBlockEntity blockEntity = (OilFireboxBlockEntity)(Object)this;
        int fuel = blockEntity.getPersistentData().getInt("Fuel");
        FluidStack stack = FluidStack.EMPTY;
        if (fuel > 0)
            stack = new FluidStack(CrustyChunksModFluids.KEROSENE.get(), fuel);
        getOrCreateFluidTank().setFluid(stack);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        syncFromPersistent();
        return ((IHaveGoggleInformation) (Object) this).containedFluidTooltip(
            tooltip,
            isPlayerSneaking,
            LazyOptional.of(this::getOrCreateFluidTank)
        );
    }
}