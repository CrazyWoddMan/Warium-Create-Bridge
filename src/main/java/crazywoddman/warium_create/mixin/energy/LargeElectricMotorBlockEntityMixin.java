package crazywoddman.warium_create.mixin.energy;

import crazywoddman.warium_create.Config;
import net.mcreator.crustychunks.block.entity.LargeElectricMotorBlockEntity;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LargeElectricMotorBlockEntity.class)
public class LargeElectricMotorBlockEntityMixin {

    private final LazyOptional<IEnergyStorage> warium$energy = LazyOptional.of(this::warium$getEnergyStorage);
    private final int energyToFErate = Config.SERVER.energyToFErate.get();

    private IEnergyStorage warium$getEnergyStorage() {
        LargeElectricMotorBlockEntity blockEntity = (LargeElectricMotorBlockEntity)(Object)this;
        return new IEnergyStorage() {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                int energy = (int) blockEntity.getPersistentData().getDouble("Energy");
                int capacity = (int) blockEntity.getPersistentData().getDouble("Capacity");
                if (!simulate && maxReceive > 0 && energy < capacity) {
                    blockEntity.getPersistentData().putDouble("Energy", Math.min(capacity, energy + maxReceive / energyToFErate));
                    blockEntity.setChanged();
                }
                return Math.min(maxReceive, (capacity - energy) * energyToFErate);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return 0;
            }

            @Override
            public int getEnergyStored() {
                int energy = (int) blockEntity.getPersistentData().getDouble("Energy");
                return energy * energyToFErate;
            }

            @Override
            public int getMaxEnergyStored() {
                int capacity = (int) blockEntity.getPersistentData().getDouble("Capacity");
                return capacity * energyToFErate;
            }

            @Override
            public boolean canExtract() {
                return false;
            }

            @Override
            public boolean canReceive() {
                return true;
            }
        };
    }

    @Inject(
        method = "getCapability",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void warium$injectEnergyCapability(Capability<?> capability, Direction facing, CallbackInfoReturnable<LazyOptional<?>> cir) {
        if (capability == ForgeCapabilities.ENERGY) {
            cir.setReturnValue(warium$energy.cast());
        }
    }
}