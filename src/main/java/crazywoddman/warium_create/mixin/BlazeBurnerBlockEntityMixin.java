package crazywoddman.warium_create.mixin;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlazeBurnerBlockEntity.class)
public class BlazeBurnerBlockEntityMixin {

    @Inject(
        method = "tick",
        at = @At("HEAD"),
        remap = false
    )
    private void provideHeat(CallbackInfo callback) {
        BlazeBurnerBlockEntity self = (BlazeBurnerBlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null || level.isClientSide()) return;

        BlazeBurnerBlock.HeatLevel blazeHeat = self.getHeatLevelFromBlock();
        if (!blazeHeat.isAtLeast(BlazeBurnerBlock.HeatLevel.KINDLED)) return;

        BlockPos pos = self.getBlockPos();
        for (BlockPos offset : new BlockPos[]{
                pos.north(), pos.south(), pos.east(), pos.west(), pos.above()
        }) {
            BlockEntity blockEntity = level.getBlockEntity(offset);
            if (blockEntity != null) {
                double currentHeat = blockEntity.getPersistentData().getDouble("Heat");
                double resultHeat = Math.min(currentHeat + 1.0, 1000.0);
                blockEntity.getPersistentData().putDouble("Heat", resultHeat);
                BlockState state = level.getBlockState(offset);
                level.sendBlockUpdated(offset, state, state, 3);
            }
        }
    }
}