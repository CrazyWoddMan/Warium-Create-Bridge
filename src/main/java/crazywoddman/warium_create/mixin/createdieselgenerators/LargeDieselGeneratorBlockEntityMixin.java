package crazywoddman.warium_create.mixin.createdieselgenerators;

import com.jesz.createdieselgenerators.blocks.DieselGeneratorBlock;
import com.jesz.createdieselgenerators.blocks.entity.LargeDieselGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LargeDieselGeneratorBlockEntity.class)
public class LargeDieselGeneratorBlockEntityMixin {

    @Inject(
        method = "tick",
        at = @At("HEAD"),
        remap = false
    )
    private void throttleControl(CallbackInfo callbackInfo) {
        LargeDieselGeneratorBlockEntity self = (LargeDieselGeneratorBlockEntity) (Object) this;
        int throttle = 0;
        if (
            self.getPersistentData().contains("ControlX") &&
            self.getPersistentData().contains("ControlY") &&
            self.getPersistentData().contains("ControlZ")
        ) {
            int controlX = self.getPersistentData().getInt("ControlX");
            int controlY = self.getPersistentData().getInt("ControlY");
            int controlZ = self.getPersistentData().getInt("ControlZ");
            BlockPos controlPos = new BlockPos(controlX, controlY, controlZ);
            BlockEntity controlNode = self.getLevel().getBlockEntity(controlPos);
            if (controlNode != null && controlNode.getPersistentData().contains("Throttle")) {
                throttle = controlNode.getPersistentData().getInt("Throttle");
                self.getLevel().setBlock(
                    self.getBlockPos(),
                    self.getBlockState().setValue(DieselGeneratorBlock.POWERED, throttle == 0),
                    3
                );
                self.updateGeneratedRotation();
            }
        }
    }
}