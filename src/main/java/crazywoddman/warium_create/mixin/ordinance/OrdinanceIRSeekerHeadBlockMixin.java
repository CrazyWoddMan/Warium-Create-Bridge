package crazywoddman.warium_create.mixin.ordinance;

import net.mcreator.crustychunks.block.OrdinanceIRSeekerHeadBlock;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.AttachFace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OrdinanceIRSeekerHeadBlock.class)
public class OrdinanceIRSeekerHeadBlockMixin {
    @Inject(
        method = "getStateForPlacement",
        at = @At("HEAD"),
        cancellable = true
    )
    private void customPlacement(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        if (context.getPlayer() != null && context.getPlayer().getClass().getName().equals("com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer")) {
            Direction deployerFacing = context.getHorizontalDirection();
            Direction placeFace = context.getClickedFace();
            boolean axisAlongFirst = false;
            BlockState deployerState = context.getLevel().getBlockState(
                context.getClickedPos().relative(context.getClickedFace(), 2)
            );
            if (deployerState.hasProperty(DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE)) {
                axisAlongFirst = deployerState.getValue(DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE);
            }

            AttachFace face;
            Direction facing;

            if (placeFace == Direction.UP) {
                face = AttachFace.WALL;
                facing = axisAlongFirst ? Direction.EAST : Direction.SOUTH;
            } else if (placeFace == Direction.DOWN) {
                face = AttachFace.WALL;
                facing = axisAlongFirst ? Direction.WEST : Direction.NORTH;
            } else {
                if (
                    (deployerFacing.getAxis() == Direction.Axis.Z && axisAlongFirst) ||
                    (deployerFacing.getAxis() == Direction.Axis.X && !axisAlongFirst)
                ) {
                    face = AttachFace.WALL;
                    facing = deployerFacing.getClockWise();
                } else {
                    face = AttachFace.FLOOR;
                    facing = deployerFacing;
                }
            }

            BlockState state = ((OrdinanceIRSeekerHeadBlock)(Object)this).defaultBlockState()
                .setValue(OrdinanceIRSeekerHeadBlock.FACE, face)
                .setValue(OrdinanceIRSeekerHeadBlock.FACING, facing);
            cir.setReturnValue(state);
        }
    }
}