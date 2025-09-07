package crazywoddman.warium_create.mixin.energy;

import crazywoddman.warium_create.Config;
import crazywoddman.warium_create.util.WariumCreateTooltipHelper;
import net.mcreator.crustychunks.block.entity.ElectricFireboxBlockEntity;
import net.mcreator.crustychunks.block.entity.LargeElectricMotorBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.common.util.LazyOptional;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;

@Mixin(
    remap = false,
    value = {
        ElectricFireboxBlockEntity.class,
        LargeElectricMotorBlockEntity.class
    }
)
public class ConsumingBlockEntityMixin implements IHaveGoggleInformation {

    @Unique
    private final LazyOptional<IEnergyStorage> energyStorage = LazyOptional.of(this::getEnergyStorage);

    @Unique
    private final int energyToFErate = Config.SERVER.energyToFErate.get();

    @Unique
    private IEnergyStorage getEnergyStorage() {
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
        cancellable = true
    )
    private void injectEnergyCapability(Capability<?> capability, Direction facing, CallbackInfoReturnable<LazyOptional<?>> cir) {
        if (capability == ForgeCapabilities.ENERGY)
            cir.setReturnValue(energyStorage.cast());
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        BlockEntity blockEntity = (BlockEntity)(Object)this;

        if (blockEntity instanceof LargeElectricMotorBlockEntity)
            return false;

        WariumCreateTooltipHelper.addEnergyTooltip(tooltip, blockEntity, energyToFErate);
        
        return true;
    }
}