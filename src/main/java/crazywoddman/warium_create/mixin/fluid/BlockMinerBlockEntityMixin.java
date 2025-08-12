package crazywoddman.warium_create.mixin.fluid;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import net.mcreator.crustychunks.block.entity.BlockMinerBlockEntity;
import net.mcreator.crustychunks.init.CrustyChunksModFluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BlockMinerBlockEntity.class)
public abstract class BlockMinerBlockEntityMixin implements IHaveGoggleInformation {

    @Unique
    private FluidTank warium$fluidTank;
    
    @Unique
    private FluidTank warium$getOrCreateFluidTank() {
        if (warium$fluidTank == null) {
            final BlockMinerBlockEntity blockEntity = (BlockMinerBlockEntity)(Object)this;
            warium$fluidTank = new FluidTank(5000) {
                @Override
                public int fill(FluidStack fluidStack, FluidAction action) {
                    boolean isAcceptableId = ForgeRegistries.FLUIDS.getKey(fluidStack.getFluid()).getPath().equals("diesel");
                    boolean isAcceptableTag = fluidStack.getFluid().is(FluidTags.create(new ResourceLocation("forge", "diesel")));
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
                    warium$syncFromPersistent();
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
        return warium$fluidTank;
    }

    @Unique
    private final LazyOptional<IFluidHandler> warium$lazyFluid = LazyOptional.of(() -> warium$getOrCreateFluidTank());

    @Inject(
        method = "getCapability",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void warium$injectFluidCap(Capability<?> cap, Direction side, CallbackInfoReturnable<LazyOptional<Object>> cir) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            warium$syncFromPersistent();
            cir.setReturnValue(warium$lazyFluid.cast());
        }
    }

    @Unique
    private void warium$syncFromPersistent() {
        BlockMinerBlockEntity blockEntity = (BlockMinerBlockEntity)(Object)this;
        int fuel = blockEntity.getPersistentData().getInt("Fuel");
        FluidStack stack = FluidStack.EMPTY;
        if (fuel > 0)
            stack = new FluidStack(CrustyChunksModFluids.DIESEL.get(), fuel);
        warium$getOrCreateFluidTank().setFluid(stack);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        warium$syncFromPersistent();
        return ((IHaveGoggleInformation) (Object) this).containedFluidTooltip(
            tooltip,
            isPlayerSneaking,
            LazyOptional.of(this::warium$getOrCreateFluidTank)
        );
    }
}