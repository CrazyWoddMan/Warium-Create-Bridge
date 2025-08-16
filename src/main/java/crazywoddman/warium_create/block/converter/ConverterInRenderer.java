package crazywoddman.warium_create.block.converter;

import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class ConverterInRenderer extends KineticBlockEntityRenderer<ConverterInBlockEntity> {

    public ConverterInRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(ConverterInBlockEntity be, BlockState state) {
        Direction facing = state.getValue(ConverterIn.FACING);
        return CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, state, facing);
    }

    @Override
    protected void renderSafe(ConverterInBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (Backend.canUseInstancing(be.getLevel())) return;

        BlockState state = be.getBlockState();
        Direction facing = state.getValue(ConverterIn.FACING);
        VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());
        int lightBehind = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().relative(facing.getOpposite()));
        SuperByteBuffer shaftHalf = CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, state, facing);
        
        standardKineticRotationTransform(shaftHalf, be, lightBehind).renderInto(ms, vb);
    }
}
