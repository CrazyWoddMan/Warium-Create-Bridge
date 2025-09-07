package crazywoddman.warium_create.mixin.tfmg;

import com.drmangotea.tfmg.blocks.engines.compact.CompactEngineBlockEntity;
import com.drmangotea.tfmg.blocks.engines.low_grade_fuel.LowGradeFuelEngineBlockEntity;
import com.drmangotea.tfmg.blocks.engines.radial.RadialEngineBlockEntity;
import com.drmangotea.tfmg.blocks.engines.small.AbstractEngineBlockEntity;

import crazywoddman.warium_create.Config;
import net.mcreator.valkyrienwarium.block.entity.VehicleControlNodeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
    remap = false,
    value = {
        AbstractEngineBlockEntity.class,
        CompactEngineBlockEntity.class,
        LowGradeFuelEngineBlockEntity.class,
        RadialEngineBlockEntity.class
    }
)
public class EnginesThrottleControl {

    @Unique
    private final double maxThrottle = Config.SERVER.maxThrottle.get();

    @Unique
    private int lastSignal = -1;

    @Unique
    private int lastRedstoneSignal;

    @Inject(
        method = "analogSignalChanged",
        at = @At("HEAD"),
        cancellable = true
    )
    private void analogSignalChangedTweak(int newSignal, CallbackInfo ci) {
        lastRedstoneSignal = newSignal;
        ci.cancel();
    }

    @Inject(
        method = "tick",
        at = @At("HEAD")
    )
    private void injectSignalTick(CallbackInfo ci) {
        BlockEntity blockEntity = (BlockEntity)(Object) this;

        if (blockEntity.getLevel().getGameTime() % 8 != 0)
            return;
  
        CompoundTag data = blockEntity.getPersistentData();

        if (lastRedstoneSignal == 0 && data.contains("ControlX")) {
            BlockEntity controlNode = blockEntity.getLevel().getBlockEntity(
                BlockPos.containing(
                    data.getInt("ControlX"),
                    data.getInt("ControlY"),
                    data.getInt("ControlZ")
                )
            );

            if (controlNode != null && controlNode instanceof VehicleControlNodeBlockEntity) {
                int throttle = controlNode.getPersistentData().getInt("Throttle");
                int newSignal = 0;

                if (throttle != 0) {
                    String key = data.getString("Key");

                    if (key.isEmpty() || (key.equals("Throttle+") && throttle > 0) || (key.equals("Throttle-") && throttle < 0))
                        newSignal = Math.round((float) (15 / maxThrottle * Math.abs(throttle)));
                }

                if (lastSignal != newSignal) {
                    lastSignal = newSignal;
                    setEngineSignal(blockEntity, newSignal);
                }

                return;
            }
        }

        if (lastSignal != lastRedstoneSignal) {
            lastSignal = lastRedstoneSignal;
            setEngineSignal(blockEntity, lastRedstoneSignal);
        }
    }

    @Unique
    private void setEngineSignal(BlockEntity blockEntity, int signal) {
        if (blockEntity instanceof RadialEngineBlockEntity)
            ((RadialEngineBlockEntityAccessor) blockEntity).setSignal(signal);
        else if (blockEntity instanceof LowGradeFuelEngineBlockEntity)
            ((LowGradeFuelEngineBlockEntityAccessor) blockEntity).setSignal(signal);
        else if (blockEntity instanceof CompactEngineBlockEntity)
            ((CompactEngineBlockEntityAccessor) blockEntity).setSignal(signal);
        else if (blockEntity instanceof AbstractEngineBlockEntity)
            ((AbstractEngineBlockEntityAccessor) blockEntity).setSignal(signal);
    }
}