package crazywoddman.warium_create.mixin.energy;

import crazywoddman.warium_create.Config;
import net.mcreator.crustychunks.block.entity.GeneratorBlockEntity;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GeneratorBlockEntity.class)
public class GeneratorBlockEntityMixin {

    private final LazyOptional<IEnergyStorage> warium$energy = LazyOptional.of(this::warium$getEnergyStorage);
    private final int energyToFErate = Config.SERVER.energyToFErate.get();


    private IEnergyStorage warium$getEnergyStorage() {
        GeneratorBlockEntity be = (GeneratorBlockEntity)(Object)this;
        return new IEnergyStorage() {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return 0;
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                int energy = (int) be.getPersistentData().getDouble("Energy");
                    if (!simulate && maxExtract > 0 && energy > 0) {
                        be.getPersistentData().putDouble("Energy", Math.max(0, energy - maxExtract / energyToFErate));
                        be.setChanged();
                    }
                    return Math.min(maxExtract, energy * energyToFErate);
            }

            @Override
            public int getEnergyStored() {
                return (int) be.getPersistentData().getDouble("Energy") * energyToFErate;
            }

            @Override
            public int getMaxEnergyStored() {
                return (int) be.getPersistentData().getDouble("Capacity") * energyToFErate;
            }

            @Override
            public boolean canExtract() {
                return true;
            }

            @Override
            public boolean canReceive() {
                return false;
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