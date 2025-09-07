package crazywoddman.warium_create.mixin.createaddition;

import net.mcreator.valkyrienwarium.block.entity.VehicleControlNodeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mrh0.createaddition.blocks.electric_motor.ElectricMotorBlock;
import com.mrh0.createaddition.blocks.electric_motor.ElectricMotorBlockEntity;

@Mixin(remap = false, value = ElectricMotorBlockEntity.class)
public class ElectricMotorBlockEntityMixin {

    @Inject(
        method = "tick",
        at = @At("HEAD")
    )
    private void setHeatLevel(CallbackInfo callbackInfo) {
        ElectricMotorBlockEntity self = (ElectricMotorBlockEntity) (Object) this;
        CompoundTag data = self.getPersistentData();
        
        if (data.contains("ControlX")) {
            BlockEntity controlNode = self.getLevel().getBlockEntity(
                BlockPos.containing(
                    data.getInt("ControlX"),
                    data.getInt("ControlY"),
                    data.getInt("ControlZ")
                )
            );

            if (controlNode != null && controlNode instanceof VehicleControlNodeBlockEntity) {
                int throttle = controlNode.getPersistentData().getInt("Throttle");
                String key = data.getString("Key");
                boolean powered = (throttle == 0) || (key == "Throttle+" && throttle < 0) || (key == "Throttle-" && throttle > 0);

                if (self.getBlockState().getValue(ElectricMotorBlock.POWERED) != powered) {
                    self.getLevel().setBlock(
                        self.getBlockPos(),
                        self.getBlockState().setValue(
                            ElectricMotorBlock.POWERED,
                            powered
                        ),
                        3
                    );
                    
                    self.updateGeneratedRotation();
                }
            }
        }

        return;
    }
}