package crazywoddman.warium_create.mixin.createdieselgenerators;

import com.jesz.createdieselgenerators.blocks.DieselGeneratorBlock;
import com.jesz.createdieselgenerators.blocks.entity.DieselGeneratorBlockEntity;
import com.jesz.createdieselgenerators.blocks.entity.HugeDieselEngineBlockEntity;
import com.jesz.createdieselgenerators.blocks.entity.LargeDieselGeneratorBlockEntity;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;

import net.mcreator.valkyrienwarium.block.entity.VehicleControlNodeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
    value = {
        DieselGeneratorBlockEntity.class,
        LargeDieselGeneratorBlockEntity.class,
        HugeDieselEngineBlockEntity.class
    },
    remap = false
)
public class GeneratorsThrottleControl {

    @Inject(
        method = "tick",
        at = @At("HEAD"),
        remap = false
    )
    private void throttleControl(CallbackInfo callbackInfo) {
        BlockEntity self = (BlockEntity) (Object) this;
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

                if (self.getBlockState().getValue(DieselGeneratorBlock.POWERED) != powered) {
                    self.getLevel().setBlock(
                        self.getBlockPos(),
                        self.getBlockState().setValue(
                            DieselGeneratorBlock.POWERED,
                            powered
                        ),
                        3
                    );
                    
                    if (self instanceof GeneratingKineticBlockEntity)
                        ((GeneratingKineticBlockEntity) self).updateGeneratedRotation();
                }
            }
        }

        return;
    }
}