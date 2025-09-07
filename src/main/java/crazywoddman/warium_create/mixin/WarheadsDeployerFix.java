package crazywoddman.warium_create.mixin;

import net.mcreator.crustychunks.block.OrdinanceClusterWarheadBlock;
import net.mcreator.crustychunks.block.OrdinanceCoreBlock;
import net.mcreator.crustychunks.block.OrdinanceFinsBlock;
import net.mcreator.crustychunks.block.OrdinanceFissionInitiatorHeadBlock;
import net.mcreator.crustychunks.block.OrdinanceHeavyWarheadBlock;
import net.mcreator.crustychunks.block.OrdinanceIRSeekerHeadBlock;
import net.mcreator.crustychunks.block.OrdinanceIncendiaryWarheadBlock;
import net.mcreator.crustychunks.block.OrdinanceInlineFissionWarheadBlock;
import net.mcreator.crustychunks.block.OrdinanceInlineFusionWarheadStage1Block;
import net.mcreator.crustychunks.block.OrdinanceInlineFusionWarheadStage2Block;
import net.mcreator.crustychunks.block.OrdinanceInlineWarheadBlock;
import net.mcreator.crustychunks.block.OrdinanceKineticHeadBlock;
import net.mcreator.crustychunks.block.OrdinanceSARHSeekerBlock;
import net.mcreator.crustychunks.block.OrdinanceThrusterBlock;
import net.mcreator.crustychunks.block.TorpedoCoreBlock;
import net.mcreator.crustychunks.block.TorpedoThrusterBlock;
import net.mcreator.crustychunks.block.TorpedoWarheadBlock;

import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.AttachFace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({
    OrdinanceClusterWarheadBlock.class,
    OrdinanceCoreBlock.class,
    OrdinanceFinsBlock.class,
    OrdinanceFissionInitiatorHeadBlock.class,
    OrdinanceHeavyWarheadBlock.class,
    OrdinanceIncendiaryWarheadBlock.class,
    OrdinanceInlineFissionWarheadBlock.class,
    OrdinanceInlineFusionWarheadStage1Block.class,
    OrdinanceInlineFusionWarheadStage2Block.class,
    OrdinanceInlineWarheadBlock.class,
    OrdinanceIRSeekerHeadBlock.class,
    OrdinanceKineticHeadBlock.class,
    OrdinanceSARHSeekerBlock.class,
    OrdinanceThrusterBlock.class,
    TorpedoCoreBlock.class,
    TorpedoThrusterBlock.class,
    TorpedoWarheadBlock.class,
})
public class WarheadsDeployerFix {
    @Inject(
        method = "getStateForPlacement",
        at = @At("HEAD"),
        cancellable = true
    )
    private void customPlacement(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        if (context.getPlayer() != null && context.getPlayer().getClass().equals(DeployerFakePlayer.class)) {
            Direction deployerFacing = context.getHorizontalDirection();
            Direction placeFace = context.getClickedFace();
            boolean axisAlongFirst = false;
            BlockState deployerState = context.getLevel().getBlockState(
                context.getClickedPos().relative(context.getClickedFace(), 2)
            );
            if (deployerState.hasProperty(DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE))
                axisAlongFirst = deployerState.getValue(DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE);

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

            Block thisBlock = (Block)(Object)this;
            BlockState state = thisBlock.defaultBlockState()
                .setValue(FaceAttachedHorizontalDirectionalBlock.FACE, face)
                .setValue(HorizontalDirectionalBlock.FACING, facing);
            cir.setReturnValue(state);
        }
    }
}