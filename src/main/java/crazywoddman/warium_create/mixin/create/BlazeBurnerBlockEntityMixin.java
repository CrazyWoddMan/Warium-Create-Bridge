package crazywoddman.warium_create.mixin.create;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(remap = false, value = BlazeBurnerBlockEntity.class)
public class BlazeBurnerBlockEntityMixin {

    @Inject(
        method = "tick",
        at = @At("HEAD")
    )
    private void provideHeat(CallbackInfo callback) {
        BlazeBurnerBlockEntity self = (BlazeBurnerBlockEntity) (Object) this;
        Level level = self.getLevel();
        
        if (!level.isClientSide()) {
            BlazeBurnerBlock.HeatLevel blazeHeat = self.getHeatLevelFromBlock();

            if (blazeHeat.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING)) {
                BlockPos pos = self.getBlockPos();

                for (BlockPos offset : new BlockPos[]{
                    pos.north(), pos.south(), pos.east(), pos.west(), pos.above()
                }) {
                    BlockEntity blockEntity = level.getBlockEntity(offset);

                    if (blockEntity != null) {
                        CompoundTag data = blockEntity.getPersistentData();
                        data.putDouble("Heat", Math.min(data.getDouble("Heat") + 1.0, 1000.0));
                        BlockState state = level.getBlockState(offset);
                        level.sendBlockUpdated(offset, state, state, 3);
                    }
                }
            }
        }
    }
}