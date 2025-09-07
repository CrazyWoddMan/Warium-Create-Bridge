package crazywoddman.warium_create.mixin.energy;

import crazywoddman.warium_create.Config;
import crazywoddman.warium_create.util.WariumCreateTooltipHelper;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;

import net.mcreator.crustychunks.block.entity.GeneratorBlockEntity;
import net.mcreator.crustychunks.block.entity.PowerReactorPortBlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(
    remap = false,
    value = {
        PowerReactorPortBlockEntity.class,
        GeneratorBlockEntity.class
    }
)
public class GeneratingBlockEntityMixin implements IHaveGoggleInformation {

    private final LazyOptional<IEnergyStorage> energyStorage = LazyOptional.of(this::getEnergyStorage);
    private final int energyToFErate = Config.SERVER.energyToFErate.get();

    private IEnergyStorage getEnergyStorage() {
        BlockEntity blockEntity = (BlockEntity)(Object)this;
        return new IEnergyStorage() {

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return 0;
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                int energy = (int) blockEntity.getPersistentData().getDouble("Energy");

                if (!simulate && maxExtract > 0 && energy > 0) {
                    blockEntity.getPersistentData().putDouble("Energy", Math.max(0, energy - maxExtract / energyToFErate));
                    blockEntity.setChanged();
                }

                return Math.min(maxExtract, energy * energyToFErate);
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
                return false;
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

        if (blockEntity instanceof GeneratorBlockEntity)
            return false;

        WariumCreateTooltipHelper.addEnergyTooltip(tooltip, blockEntity, energyToFErate);
        
        return true;
    }
}