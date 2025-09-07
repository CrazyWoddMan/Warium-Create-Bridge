package crazywoddman.warium_create.mixin.fluid;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;

import net.mcreator.crustychunks.block.entity.BlockMinerBlockEntity;
import net.mcreator.crustychunks.block.entity.OilFireboxBlockEntity;
import net.mcreator.crustychunks.init.CrustyChunksModFluids;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
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

@Mixin(
    remap = false,
    value = {
        OilFireboxBlockEntity.class,
        BlockMinerBlockEntity.class
    }
)
public abstract class OilFireboxAndBlockMinerBlockEntityMixin implements IHaveGoggleInformation {

    @Unique
    private FluidTank fluidTank;
    
    @Unique
    private FluidTank getOrCreateFluidTank() {
        if (fluidTank == null) {
            BlockEntity blockEntity = (BlockEntity)(Object)this;
            fluidTank = new FluidTank(5000) {
                @Override
                public int fill(FluidStack fluidStack, FluidAction action) {
                    if (!fluidStack.isEmpty()) {
                        Fluid fluid = fluidStack.getFluid();
                        String fuelName = blockEntity instanceof OilFireboxBlockEntity ? "kerosene" : "diesel";
                        boolean isAcceptableId = ForgeRegistries.FLUIDS
                            .getKey(fluid)
                            .getPath()
                            .equals(fuelName);
                        boolean isAcceptableTag = ForgeRegistries.FLUIDS
                            .tags()
                            .getTag(FluidTags.create(ResourceLocation.fromNamespaceAndPath("forge", fuelName)))
                            .contains(fluid);

                        if (isAcceptableId || isAcceptableTag) {
                            int amount = blockEntity.getPersistentData().getInt("Fuel");
                            int capacity = this.getCapacity();
                            int toAdd = fluidStack.getAmount();

                            if (action != FluidAction.SIMULATE && toAdd > 0 && amount < capacity) {
                                blockEntity.getPersistentData().putDouble("Fuel", Math.min(capacity, amount + toAdd));
                                blockEntity.setChanged();
                                blockEntity.getLevel().sendBlockUpdated(blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
                                syncFromPersistent();

                                return Math.min(toAdd, capacity - amount);
                            }
                        }
                    }
                    
                    return 0;
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
        cancellable = true
    )
    private void injectFluidCap(Capability<?> cap, Direction side, CallbackInfoReturnable<LazyOptional<Object>> cir) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            syncFromPersistent();
            cir.setReturnValue(lazyFluid.cast());
        }
    }

    @Unique
    private void syncFromPersistent() {
        BlockEntity blockEntity = (BlockEntity)(Object)this;
        int amount = blockEntity.getPersistentData().getInt("Fuel");
        FluidStack stack = FluidStack.EMPTY;
        if (amount > 0)
            stack = new FluidStack(
                blockEntity instanceof OilFireboxBlockEntity ? CrustyChunksModFluids.KEROSENE.get() : CrustyChunksModFluids.DIESEL.get(),
                amount
            );
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