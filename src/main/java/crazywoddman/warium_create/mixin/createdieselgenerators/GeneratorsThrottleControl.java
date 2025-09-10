package crazywoddman.warium_create.mixin.createdieselgenerators;

import com.jesz.createdieselgenerators.blocks.DieselGeneratorBlock;
import com.jesz.createdieselgenerators.blocks.entity.DieselGeneratorBlockEntity;
import com.jesz.createdieselgenerators.blocks.entity.HugeDieselEngineBlockEntity;
import com.jesz.createdieselgenerators.blocks.entity.LargeDieselGeneratorBlockEntity;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;

import crazywoddman.warium_create.Config;
import crazywoddman.warium_create.util.WariumCreateUtil;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
    value = {
        DieselGeneratorBlockEntity.class,
        LargeDieselGeneratorBlockEntity.class,
        HugeDieselEngineBlockEntity.class
    },
    remap = false
)
public class GeneratorsThrottleControl {

    @Unique
    private boolean lastSignal;

    @Unique
    private Integer throttle;

    private final boolean throttleToRotationDirection = Config.SERVER.throttleToRotationDirection.get();

    @Inject(
        method = "getGeneratedSpeed",
        at = @At("RETURN"),
        cancellable = true,
        require = 0
    )
    private void injectReturn(CallbackInfoReturnable<Float> cir) {
        float originalValue = cir.getReturnValue();

        if (originalValue != 0.0F && this.throttleToRotationDirection && this.throttle != null && this.throttle < 0)
            cir.setReturnValue(-1 * originalValue);

    }

    @Redirect(
        method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lcom/jesz/createdieselgenerators/blocks/entity/HugeDieselEngineBlockEntity;movementDirection:Lcom/simibubi/create/foundation/blockEntity/behaviour/scrollValue/ScrollOptionBehaviour;",
            opcode = Opcodes.GETFIELD
        ),
        require = 0
    )
    private ScrollOptionBehaviour<WindmillBearingBlockEntity.RotationDirection> redirectMovementDirection(HugeDieselEngineBlockEntity instance) {
        return new ScrollOptionBehaviour<WindmillBearingBlockEntity.RotationDirection>(
            WindmillBearingBlockEntity.RotationDirection.class,
            null, instance, null
        ) {
            @Override
            public WindmillBearingBlockEntity.RotationDirection get() {
                WindmillBearingBlockEntity.RotationDirection originalDirection = instance.movementDirection.get();
                
                if (throttleToRotationDirection && throttle != null && throttle < 0)
                    return originalDirection == WindmillBearingBlockEntity.RotationDirection.CLOCKWISE 
                        ? WindmillBearingBlockEntity.RotationDirection.COUNTER_CLOCKWISE
                        : WindmillBearingBlockEntity.RotationDirection.CLOCKWISE;
                
                return originalDirection;
            }
        };
    }

    @Inject(
        method = "tick",
        at = @At("HEAD")
    )
    private void throttleControl(CallbackInfo callbackInfo) {
        BlockEntity blockEntity = (BlockEntity) (Object) this;

        if (blockEntity.getLevel().getGameTime() % 10 == 0) {
            this.lastSignal = blockEntity.getLevel().hasNeighborSignal(blockEntity.getBlockPos());
            boolean oldPowered = blockEntity.getBlockState().getValue(DieselGeneratorBlock.POWERED);
            boolean powered = this.lastSignal;

            if (!this.lastSignal) {
                this.throttle = WariumCreateUtil.getThrottle(blockEntity, null);
                if (this.throttle != null)
                    powered = this.throttle == 0;
            }

            if (oldPowered != powered) {
                blockEntity.getLevel().setBlock(
                    blockEntity.getBlockPos(),
                    blockEntity.getBlockState().setValue(
                        DieselGeneratorBlock.POWERED,
                        powered
                    ),
                    Block.UPDATE_CLIENTS
                );

                if (blockEntity instanceof GeneratingKineticBlockEntity genBlockEntity)
                    genBlockEntity.updateGeneratedRotation();
            }
        }
    }
}