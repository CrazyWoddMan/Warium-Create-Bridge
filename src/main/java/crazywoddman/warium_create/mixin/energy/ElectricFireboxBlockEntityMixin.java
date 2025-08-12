package crazywoddman.warium_create.mixin.energy;

import crazywoddman.warium_create.Config;
import net.mcreator.crustychunks.block.entity.ElectricFireboxBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.common.util.LazyOptional;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;

@Mixin(ElectricFireboxBlockEntity.class)
public class ElectricFireboxBlockEntityMixin implements IHaveGoggleInformation {

    private final LazyOptional<IEnergyStorage> warium$energy = LazyOptional.of(this::warium$getEnergyStorage);
    private final int energyToFErate = Config.SERVER.energyToFErate.get();

    private IEnergyStorage warium$getEnergyStorage() {
        ElectricFireboxBlockEntity blockEntity = (ElectricFireboxBlockEntity)(Object)this;
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
}