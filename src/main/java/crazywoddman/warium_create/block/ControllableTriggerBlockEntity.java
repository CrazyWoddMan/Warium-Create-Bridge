package crazywoddman.warium_create.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ControllableTriggerBlockEntity extends BlockEntity {
    public ControllableTriggerBlockEntity(BlockEntityType<ControllableTriggerBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (level == null || level.isClientSide) return;
        double controlX = blockEntity.getPersistentData().getDouble("ControlX");
        double controlY = blockEntity.getPersistentData().getDouble("ControlY");
        double controlZ = blockEntity.getPersistentData().getDouble("ControlZ");
        boolean hasLink = !(controlX == 0 && controlY == 0 && controlZ == 0);
        boolean powered = false;

        if (hasLink) {
            BlockPos controlPos = new BlockPos((int) controlX, (int) controlY, (int) controlZ);
            BlockEntity controlNode = level.getBlockEntity(controlPos);
            
            if (controlNode != null) {
                String key = blockEntity.getPersistentData().getString("Key");
                if (key != null && key.length() >= 2) {
                    String mode = key.substring(0, key.length() - 1);
                    boolean isPositive = key.charAt(key.length() - 1) == '+';
                    powered = isPositive ? controlNode.getPersistentData().getDouble(mode) > 0 : controlNode.getPersistentData().getDouble(mode) < 0;
                }
            }
        }

        boolean currentPowered = state.getValue(ControllableTrigger.POWERED);      
        if (currentPowered != powered) {
            level.setBlock(pos, state.setValue(ControllableTrigger.POWERED, powered), 3);
            level.updateNeighborsAt(pos, state.getBlock());
            for (Direction dir : Direction.values()) {
                level.updateNeighborsAt(pos.relative(dir), state.getBlock());
            }
        }
    }
}