package crazywoddman.warium_create.mixin.createdieselgenerators;

import com.jesz.createdieselgenerators.blocks.DieselGeneratorBlock;
import com.jesz.createdieselgenerators.blocks.entity.DieselGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DieselGeneratorBlockEntity.class)
public class DieselGeneratorBlockEntityMixin {

    @Inject(
        method = "tick",
        at = @At("HEAD"),
        remap = false
    )
    private void warium$throttleControl(CallbackInfo callbackInfo) {
        DieselGeneratorBlockEntity self = (DieselGeneratorBlockEntity) (Object) this;
        double controlX = self.getPersistentData().getDouble("ControlX");
        double controlY = self.getPersistentData().getDouble("ControlY");
        double controlZ = self.getPersistentData().getDouble("ControlZ");
        boolean hasLink = !(controlX == 0 && controlY == 0 && controlZ == 0);
        double throttle = 0;
        if (hasLink) {
            BlockPos controlPos = new BlockPos((int) controlX, (int) controlY, (int) controlZ);
            BlockEntity controlNode = self.getLevel().getBlockEntity(controlPos);
            if (controlNode != null && controlNode.getPersistentData().contains("Throttle")) {
                throttle = controlNode.getPersistentData().getDouble("Throttle");
            }
            self.getLevel().setBlock(self.getBlockPos(),
                self.getBlockState().setValue(DieselGeneratorBlock.POWERED, throttle <= 0), 3);
            self.updateGeneratedRotation();

        }
    }
}