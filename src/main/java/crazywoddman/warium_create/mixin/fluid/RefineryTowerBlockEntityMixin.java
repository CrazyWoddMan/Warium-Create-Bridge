package crazywoddman.warium_create.mixin.fluid;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import net.mcreator.crustychunks.block.entity.RefineryTowerBlockEntity;
import net.mcreator.crustychunks.init.CrustyChunksModFluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(RefineryTowerBlockEntity.class)
public abstract class RefineryTowerBlockEntityMixin implements IHaveGoggleInformation {

    @Unique
    private FluidTank warium$fluidTank;

    @Unique
    private FluidTank warium$getOrCreateFluidTank() {
        if (warium$fluidTank == null) {
            final RefineryTowerBlockEntity blockEntity = (RefineryTowerBlockEntity)(Object)this;
            warium$fluidTank = new FluidTank(16000) {
                @Override
                public int fill(FluidStack resource, FluidAction action) {
                    return 0;
                }
                @Override
                public FluidStack drain(FluidStack resource, FluidAction action) {
                    warium$syncFromPersistent();
                    return super.drain(resource, action);
                }
                @Override
                public FluidStack drain(int maxDrain, FluidAction action) {
                    warium$syncFromPersistent();
                    return super.drain(maxDrain, action);
                }
                @Override
                protected void onContentsChanged() {
                    blockEntity.getPersistentData().putDouble("Level", this.getFluidAmount());
                    blockEntity.getPersistentData().putString("Fluid", warium$getFluidName(this.getFluid()));
                    blockEntity.setChanged();
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
        RefineryTowerBlockEntity blockEntity = (RefineryTowerBlockEntity)(Object)this;
        double level = blockEntity.getPersistentData().getDouble("Level");
        String fluid = blockEntity.getPersistentData().getString("Fluid");
        FluidStack stack = FluidStack.EMPTY;
        if ("Diesel".equals(fluid)) {
            stack = new FluidStack(CrustyChunksModFluids.DIESEL.get(), (int)level);
        } else if ("Kerosene".equals(fluid)) {
            stack = new FluidStack(CrustyChunksModFluids.KEROSENE.get(), (int)level);
        } else if ("Oil".equals(fluid)) {
            stack = new FluidStack(CrustyChunksModFluids.OIL.get(), (int)level);
        } else if ("Petrolium".equals(fluid)) {
            stack = new FluidStack(CrustyChunksModFluids.PETROLIUM.get(), (int)level);
        }
        warium$getOrCreateFluidTank().setFluid(stack);
    }

    @Unique
    private String warium$getFluidName(FluidStack stack) {
        if (stack.getFluid() == CrustyChunksModFluids.DIESEL.get()) return "Diesel";
        if (stack.getFluid() == CrustyChunksModFluids.KEROSENE.get()) return "Kerosene";
        if (stack.getFluid() == CrustyChunksModFluids.OIL.get()) return "Oil";
        if (stack.getFluid() == CrustyChunksModFluids.PETROLIUM.get()) return "Petrolium";
        return "";
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        warium$syncFromPersistent();
        return containedFluidTooltip(
            tooltip,
            isPlayerSneaking,
            LazyOptional.of(this::warium$getOrCreateFluidTank)
        );
    }
}