package crazywoddman.warium_create.mixin.energy;

import crazywoddman.warium_create.Config;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import net.mcreator.crustychunks.block.entity.PowerReactorPortBlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(PowerReactorPortBlockEntity.class)
public class PowerReactorPortBlockEntityMixin implements IHaveGoggleInformation {

    private final LazyOptional<IEnergyStorage> warium$energy = LazyOptional.of(this::warium$getEnergyStorage);
    private final int energyToFErate = Config.SERVER.energyToFErate.get();

    private IEnergyStorage warium$getEnergyStorage() {
        PowerReactorPortBlockEntity be = (PowerReactorPortBlockEntity)(Object)this;
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