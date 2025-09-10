package crazywoddman.warium_create.util;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class WariumCreateTooltipHelper {
    
    /**
     * Adds Forge Forge Energy capacity to the {@code tooltip}
     * @param tooltip
     * @param blockEntity this BlockEntity
     * @param energyToFErate Energy to Forge Energy converion ratio
     */
    public static void addEnergyTooltip(List<Component> tooltip, BlockEntity blockEntity, int energyToFErate) {
        CompoundTag data = blockEntity.getPersistentData();
        
        tooltip.add(Component
            .literal("    ")
            .append(Component.translatable("createaddition.tooltip.energy.stored"))
            .withStyle(ChatFormatting.GRAY)
        );
        tooltip.add(Component
            .literal("     ")
            .append(Component.literal(formatFE(data.getDouble("Energy") * energyToFErate)))
            .append("fe")
            .withStyle(ChatFormatting.AQUA)
        );
        
        tooltip.add(Component
            .literal("    ")
            .append(Component.translatable("createaddition.tooltip.energy.capacity"))
            .withStyle(ChatFormatting.GRAY)
        );
        tooltip.add(Component
            .literal("     ")
            .append(Component.literal(formatFE(data.getDouble("Capacity") * energyToFErate)))
            .append("fe")
            .withStyle(ChatFormatting.AQUA)
        );
    }

    public static String formatFE(double fe) {
        if (fe >= 1_000_000_000)
            return Math.round(fe / 100_000_000d) / 10d + "G";
        if (fe >= 1_000_000)
            return Math.round(fe / 100_000d) / 10d + "M";
        if (fe >= 1_000)
            return Math.round(fe / 100d) / 10d + "K";
        return Double.toString(fe);
    }

    public static String formatSeconds(int totalSeconds) {
        int seconds = totalSeconds % 60;
        if (totalSeconds >= 3600) {
            int hours = totalSeconds / 3600;
            int minutes = (totalSeconds % 3600) / 60;
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            int minutes = totalSeconds / 60;
            return String.format("%d:%02d", minutes, seconds);
        }
    }
}