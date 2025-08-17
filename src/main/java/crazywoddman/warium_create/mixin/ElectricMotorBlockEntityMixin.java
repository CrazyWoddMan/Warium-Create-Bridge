package crazywoddman.warium_create.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mrh0.createaddition.blocks.electric_motor.ElectricMotorBlock;
import com.mrh0.createaddition.blocks.electric_motor.ElectricMotorBlockEntity;

@Mixin(ElectricMotorBlockEntity.class)
public class ElectricMotorBlockEntityMixin {

    @Inject(
        method = "tick",
        at = @At("HEAD"),
        remap = false
    )
    private void setHeatLevel(CallbackInfo callbackInfo) {
        ElectricMotorBlockEntity self = (ElectricMotorBlockEntity) (Object) this;
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
                self.getLevel().setBlock(self.getBlockPos(),
                    self.getBlockState().setValue(ElectricMotorBlock.POWERED, throttle == 0), 3);
                self.updateGeneratedRotation();
            }
        }
    }
}