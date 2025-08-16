package crazywoddman.warium_create.block.converter;

import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;

public class ConverterOutRenderer extends KineticBlockEntityRenderer<ConverterOutBlockEntity> {

    public ConverterOutRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(ConverterOutBlockEntity blockEntity, BlockState state) {
        Direction facing = state.getValue(ConverterOut.FACING);
        return CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, state, facing.getOpposite());
    }

    @Override
    protected void renderSafe(ConverterOutBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (Backend.canUseInstancing(blockEntity.getLevel())) return;

        super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay);

        BlockState state = blockEntity.getBlockState();
        Direction facing = state.getValue(ConverterOut.FACING);
        AttachFace face = state.getValue(ConverterOut.FACE);
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());

        SuperByteBuffer shaftHalf = CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, state, facing.getOpposite());
        standardKineticRotationTransform(shaftHalf, blockEntity, light).renderInto(ms, vb);

        SuperByteBuffer dialBuffer = CachedBufferer.partial(AllPartialModels.GAUGE_DIAL, state);
        SuperByteBuffer headBuffer = CachedBufferer.partial(AllPartialModels.GAUGE_HEAD_SPEED, state);

        float dialPivot = 5.75f / 16;
        float progress = Mth.lerp(partialTicks, blockEntity.prevDialState, blockEntity.dialState);

        if (face == AttachFace.WALL) {
            renderGaugePanelWall(ms, vb, dialBuffer, headBuffer, facing, -1f / 16f, dialPivot, progress, light, overlay);
            renderGaugePanelWall(ms, vb, dialBuffer, headBuffer, facing.getOpposite(), 1f / 16f, dialPivot, progress, light, overlay);
        } else {
            renderGaugePanel(ms, vb, dialBuffer, headBuffer, facing, face, dialPivot, progress, light, overlay);
        }
    }

    private void renderGaugePanelWall(PoseStack ms, VertexConsumer vb, SuperByteBuffer dialBuffer, SuperByteBuffer headBuffer,
                                 Direction panelFacing, float offset, float dialPivot, float progress, int light, int overlay) {
        dialBuffer.rotateCentered(Direction.UP, (float) ((-panelFacing.toYRot()) / 180 * Math.PI))
                .translate(0, 0, offset)
                .translate(0, dialPivot, dialPivot)
                .rotate(Direction.EAST, (float) (Math.PI / 2 * -progress))
                .translate(0, -dialPivot, -dialPivot)
                .light(light)
                .renderInto(ms, vb);

        headBuffer.rotateCentered(Direction.UP, (float) ((-panelFacing.toYRot()) / 180 * Math.PI))
                .translate(0, 0, offset)
                .light(light)
                .renderInto(ms, vb);
    }

    private void renderGaugePanel(
        PoseStack ms,
        VertexConsumer vb,
        SuperByteBuffer dialBuffer,
        SuperByteBuffer headBuffer,
        Direction facing,
        AttachFace face,
        float dialPivot,
        float progress,
        int light,
        int overlay
    ) {
        double offset;
        if (face == AttachFace.CEILING)
                offset = ((facing == Direction.SOUTH || facing == Direction.NORTH) ? -1f : 1f)/16f;
            else
                offset = -1f/16f;
        dialBuffer.rotateCentered(Direction.NORTH, (face == AttachFace.FLOOR ? -1 : 1) * (float)Math.PI / 2)
                .rotateCentered(Direction.EAST, (90 - facing.toYRot()) / 180 * (float)Math.PI)
                .translate(0, offset, 0)
                .translate(0, dialPivot, dialPivot)
                .rotate(Direction.EAST, (float) (Math.PI / 2 * -progress))
                .translate(0, -dialPivot, -dialPivot)
                .light(light)
                .renderInto(ms, vb);

        headBuffer.rotateCentered(Direction.NORTH, (face == AttachFace.FLOOR ? -1 : 1) * (float)Math.PI / 2)
                .rotateCentered(Direction.EAST, (90 - facing.toYRot()) / 180 * (float)Math.PI)
                .translate(0, offset, 0)
                .light(light)
                .renderInto(ms, vb);
    }
}
