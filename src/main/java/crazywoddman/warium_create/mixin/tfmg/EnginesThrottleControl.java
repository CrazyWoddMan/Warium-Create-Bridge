package crazywoddman.warium_create.mixin.tfmg;

import com.drmangotea.tfmg.blocks.engines.compact.CompactEngineBlockEntity;
import com.drmangotea.tfmg.blocks.engines.low_grade_fuel.LowGradeFuelEngineBlockEntity;
import com.drmangotea.tfmg.blocks.engines.radial.RadialEngineBlockEntity;
import com.drmangotea.tfmg.blocks.engines.small.AbstractEngineBlockEntity;

import crazywoddman.warium_create.Config;
import crazywoddman.warium_create.util.WariumCreateUtil;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
    private final int maxThrottle = Config.SERVER.maxThrottle.get();
    private final boolean throttleToRotationDirection = Config.SERVER.throttleToRotationDirection.get();

    @Unique
    private int lastSignal;

    @Unique
    private int lastRedstoneSignal;

    @Inject(
        method = "analogSignalChanged",
        at = @At("HEAD"),
        cancellable = true
    )
    private void analogSignalChangedTweak(int newSignal, CallbackInfo ci) {
        this.lastRedstoneSignal = newSignal;
        ci.cancel();
    }

    @Inject(
        method = "getGeneratedSpeed",
        at = @At("RETURN"),
        cancellable = true
    )
    private void injectReturn(CallbackInfoReturnable<Float> cir) {
        float originalValue = cir.getReturnValue();

        if (originalValue != 0.0F && this.throttleToRotationDirection && this.lastSignal < 0)
            cir.setReturnValue(-1 * originalValue);

    }

    @Inject(
        method = "tick",
        at = @At("HEAD")
    )
    private void injectSignalTick(CallbackInfo ci) {
        BlockEntity blockEntity = (BlockEntity)(Object) this;

        if (blockEntity.getLevel().getGameTime() % 8 != 0)
            return;
        
        int newSignal = this.lastRedstoneSignal;
        
        if (this.lastRedstoneSignal == 0) {
            int throttle = WariumCreateUtil.getThrottle(blockEntity, 0);

            if (throttle != 0)
                newSignal = Math.round(15.0F / this.maxThrottle * throttle);
        }

        if (this.lastSignal != newSignal) {
            this.lastSignal = newSignal;
            int absSignal = Math.abs(newSignal);

            if (blockEntity instanceof RadialEngineBlockEntity)
                ((RadialEngineBlockEntityAccessor) blockEntity).setSignal(absSignal);
            else if (blockEntity instanceof LowGradeFuelEngineBlockEntity)
                ((LowGradeFuelEngineBlockEntityAccessor) blockEntity).setSignal(absSignal);
            else if (blockEntity instanceof CompactEngineBlockEntity)
                ((CompactEngineBlockEntityAccessor) blockEntity).setSignal(absSignal);
            else if (blockEntity instanceof AbstractEngineBlockEntity)
                ((AbstractEngineBlockEntityAccessor) blockEntity).setSignal(absSignal);
        }
    }
}