package crazywoddman.warium_create.mixin.ordinance;

import net.mcreator.crustychunks.block.OrdinanceInlineFissionWarheadBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.AttachFace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;

@Mixin(OrdinanceInlineFissionWarheadBlock.class)
public class OrdinanceInlineFissionWarheadBlockMixin {
    @Inject(
        method = "getStateForPlacement",
        at = @At("HEAD"),
        cancellable = true
    )
    private void warium$onlyHorizontal(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
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

            Direction facing;
            if (placeFace == Direction.UP) {
                facing = axisAlongFirst ? Direction.EAST : Direction.SOUTH;
            } else if (placeFace == Direction.DOWN) {
                facing = axisAlongFirst ? Direction.WEST : Direction.NORTH;
            } else {
                facing = deployerFacing.getClockWise();
            }
            BlockState state = ((OrdinanceInlineFissionWarheadBlock)(Object)this).defaultBlockState()
                .setValue(OrdinanceInlineFissionWarheadBlock.FACE, AttachFace.WALL)
                .setValue(OrdinanceInlineFissionWarheadBlock.FACING, facing);
            cir.setReturnValue(state);
        }
    }
}