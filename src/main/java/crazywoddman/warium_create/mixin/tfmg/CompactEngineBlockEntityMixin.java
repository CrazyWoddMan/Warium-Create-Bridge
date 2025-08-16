package crazywoddman.warium_create.mixin.tfmg;

import com.drmangotea.tfmg.blocks.engines.compact.CompactEngineBlockEntity;

import crazywoddman.warium_create.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CompactEngineBlockEntity.class)
public class CompactEngineBlockEntityMixin {

    @Inject(
        method = "tick",
        at = @At("HEAD"),
        remap = false
    )
    private void warium$injectSignalTick(CallbackInfo ci) {
        CompactEngineBlockEntity self = (CompactEngineBlockEntity) (Object) this;
        double controlX = self.getPersistentData().getDouble("ControlX");
        double controlY = self.getPersistentData().getDouble("ControlY");
        double controlZ = self.getPersistentData().getDouble("ControlZ");
        boolean hasLink = !(controlX == 0 && controlY == 0 && controlZ == 0);
        int throttle = 0;
        if (hasLink) {
            BlockPos controlPos = new BlockPos((int) controlX, (int) controlY, (int) controlZ);
            BlockEntity controlNode = self.getLevel().getBlockEntity(controlPos);
            if (controlNode != null && controlNode.getPersistentData().contains("Throttle")) {
                throttle = controlNode.getPersistentData().getInt("Throttle");
            }
        }
        if (throttle > 0) {
            ((CompactEngineBlockEntityAccessor) self).setSignal(Config.SERVER.TFMGspeedControl.get() ? throttle + 5 : 15);
        }
    }
}