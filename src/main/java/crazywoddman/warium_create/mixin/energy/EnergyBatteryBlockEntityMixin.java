package crazywoddman.warium_create.mixin.energy;

import crazywoddman.warium_create.Config;
import crazywoddman.warium_create.util.WariumCreateTooltipHelper;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import net.mcreator.crustychunks.block.entity.EnergyBatteryBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(remap = false, value = EnergyBatteryBlockEntity.class)
public abstract class EnergyBatteryBlockEntityMixin implements IHaveGoggleInformation {

    @Unique
    private final int energyToFErate = Config.SERVER.energyToFErate.get();

    @Unique
    private LazyOptional<IEnergyStorage> forgeEnergy = null;

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        BlockEntity blockEntity = (BlockEntity)(Object)this;
        WariumCreateTooltipHelper.addEnergyTooltip(tooltip, blockEntity, energyToFErate);
        return true;
    }

    @Inject(
        method = "getCapability",
        at = @At("HEAD"),
        cancellable = true
    )
    private void injectForgeEnergy(Capability<?> capability, Direction facing, CallbackInfoReturnable<LazyOptional<?>> cir) {
        if (capability == ForgeCapabilities.ENERGY) {
            if (forgeEnergy == null)
                forgeEnergy = LazyOptional.of(this::createEnergyStorage);
            cir.setReturnValue(forgeEnergy.cast());
        }
    }

    @Unique
    private IEnergyStorage createEnergyStorage() {
        BlockEntity blockEntity = (BlockEntity)(Object)this;
        return new IEnergyStorage() {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                int energy = (int) blockEntity.getPersistentData().getDouble("Energy");
                int capacity = (int) blockEntity.getPersistentData().getDouble("Capacity");
                if (!simulate && maxReceive > 0 && energy < capacity) {
                    blockEntity.getPersistentData().putDouble("Energy", Math.min(capacity, energy + maxReceive / energyToFErate));
                    blockEntity.setChanged();
                }
                return Math.min(maxReceive, (capacity - energy) * 100);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                int energy = (int) blockEntity.getPersistentData().getDouble("Energy");
                if (!simulate && maxExtract > 0 && energy > 0) {
                    blockEntity.getPersistentData().putDouble("Energy", Math.max(0, energy - maxExtract / energyToFErate));
                    blockEntity.setChanged();
                }
                return Math.min(maxExtract, energy * 100);
            }

            @Override
            public int getEnergyStored() {
                return (int) blockEntity.getPersistentData().getDouble("Energy") * energyToFErate;
            }

            @Override
            public int getMaxEnergyStored() {
                return (int) blockEntity.getPersistentData().getDouble("Capacity") * energyToFErate;
            }

            @Override
            public boolean canExtract() {
                return true;
            }

            @Override
            public boolean canReceive() {
                return true;
            }
        };
    }
}