package crazywoddman.warium_create.mixin.energy;

import crazywoddman.warium_create.Config;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import net.mcreator.crustychunks.block.entity.EnergyBatteryBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EnergyBatteryBlockEntity.class)
public abstract class EnergyBatteryBlockEntityMixin implements IHaveGoggleInformation {

    private static final String spacing = "    ";
    private final int energyToFErate = Config.SERVER.energyToFErate.get();
    private LazyOptional<IEnergyStorage> forgeEnergy = null;

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        BlockEntity blockEntity = (BlockEntity)(Object)this;

        tooltip.add(Component
                .literal(spacing)
                .append(Component.translatable("createaddition.tooltip.energy.stored"))
                .withStyle(ChatFormatting.GRAY)
        );
        tooltip.add(Component
                .literal(spacing)
                .append(Component.literal(" "))
                .append(Component.literal(formatFE(blockEntity.getPersistentData().getDouble("Energy") * energyToFErate)))
                .append("fe")
                .withStyle(ChatFormatting.AQUA)
        );
        tooltip.add(Component
                .literal(spacing)
                .append(Component.translatable("createaddition.tooltip.energy.capacity"))
                .withStyle(ChatFormatting.GRAY)
        );
        tooltip.add(Component
                .literal(spacing)
                .append(Component.literal(" "))
                .append(Component.literal(formatFE(blockEntity.getPersistentData().getDouble("Capacity") * energyToFErate)))
                .append("fe")
                .withStyle(ChatFormatting.AQUA)
        );
        return true;
    }

    private static String formatFE(double fe) {
        if(fe >= 1000_000_000)
                return Math.round(fe/100_000_000d)/10d + "G";
        if(fe >= 1000_000)
                return Math.round(fe/100_000d)/10d + "M";
        if(fe >= 1000)
                return Math.round(fe/100d)/10d + "K";
        return fe + "";
    }

    @Inject(
        method = "getCapability",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void warium$injectForgeEnergy(Capability<?> capability, Direction facing, CallbackInfoReturnable<LazyOptional<?>> cir) {
        if (capability == ForgeCapabilities.ENERGY) {
            if (forgeEnergy == null) {
                forgeEnergy = LazyOptional.of(this::createEnergyStorage);
            }
            cir.setReturnValue(forgeEnergy.cast());
        }
    }

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