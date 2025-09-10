package crazywoddman.warium_create.util;

import net.mcreator.valkyrienwarium.block.entity.VehicleControlNodeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

public class WariumCreateUtil {
    
    /**
     * Gets {@code throttle} from Control Node
     * @param blockEntity this BlockEntity
     * @param altThrottle value returned if Control Node position isn't set or Control Node is not found
     * @return {@code altThrottle} if Control Node position is not set or Control Node is not found;
     * {@code 0} if configured key restricts current {@code throttle} value;
     * {@code throttle} set by Control Node in other cases
     */
    public static Integer getThrottle(BlockEntity blockEntity, Integer altThrottle) {
        Integer result = altThrottle;
        CompoundTag persistentData = blockEntity.getPersistentData();

        if (persistentData.contains("ControlX")) {
            BlockEntity controlNode = blockEntity.getLevel().getBlockEntity(
                new BlockPos(
                    persistentData.getInt("ControlX"),
                    persistentData.getInt("ControlY"),
                    persistentData.getInt("ControlZ")
                )
            );

            if (controlNode != null && controlNode instanceof VehicleControlNodeBlockEntity) {
                int throttle = controlNode.getPersistentData().getInt("Throttle");
                String key = persistentData.getString("Key");

                if (throttle != 0 && switch (key) {
                    case "" -> true;
                    case "Throttle+" -> throttle > 0;
                    case "Throttle-" -> throttle < 0;
                    default -> true;
                }) result = throttle;
                else
                    result = 0;
            }
        }
        
        return result;
    }
}