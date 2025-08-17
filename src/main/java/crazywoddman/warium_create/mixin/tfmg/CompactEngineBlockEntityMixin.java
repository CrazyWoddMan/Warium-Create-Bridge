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
    private void injectSignalTick(CallbackInfo ci) {
        CompactEngineBlockEntity self = (CompactEngineBlockEntity) (Object) this;
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
                throttle = Math.abs(controlNode.getPersistentData().getInt("Throttle"));
            }
        }
        if (throttle != 0) {
            ((CompactEngineBlockEntityAccessor) self).setSignal(Config.SERVER.TFMGspeedControl.get() ? throttle + 5 : 15);
        }
    }
}