package crazywoddman.warium_create.block.converter;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.properties.AttachFace;

public class ConverterOutInstance extends KineticBlockEntityInstance<ConverterOutBlockEntity> implements DynamicInstance {

    protected final RotatingData shaft;
    protected final ModelData dial, head;
    protected final ModelData dial2, head2;
    protected final PoseStack ms;
    final Direction facing, opposite;
    final AttachFace face;

    public ConverterOutInstance(MaterialManager materialManager, ConverterOutBlockEntity blockEntity) {
        super(materialManager, blockEntity);

        facing = blockState.getValue(ConverterOut.FACING);
        face = blockState.getValue(ConverterOut.FACE);
        opposite = facing.getOpposite();

        shaft = getRotatingMaterial().getModel(AllPartialModels.SHAFT_HALF, blockState, opposite).createInstance();
        setup(shaft);

        Instancer<ModelData> dialModel = getTransformMaterial().getModel(AllPartialModels.GAUGE_DIAL, blockState);
        Instancer<ModelData> headModel = getTransformMaterial().getModel(AllPartialModels.GAUGE_HEAD_SPEED, blockState);

        ms = new PoseStack();
        TransformStack msr = TransformStack.cast(ms);
        msr.translate(getInstancePosition());

        float progress = Mth.lerp(0, blockEntity.prevDialState, blockEntity.dialState);
        dial = dialModel.createInstance();
        head = headModel.createInstance();

        if (face == AttachFace.WALL) {
            dial2 = dialModel.createInstance();
            head2 = headModel.createInstance();
        } else {
            dial2 = null;
            head2 = null;
        }

        setupDialTransform(msr, progress);
    }

    @Override
    public void beginFrame() {
        if (Mth.equal(blockEntity.prevDialState, blockEntity.dialState))
            return;

        float progress = Mth.lerp(0, blockEntity.prevDialState, blockEntity.dialState);

        TransformStack msr = TransformStack.cast(ms);
        setupDialTransform(msr, progress);
    }

    private void setupDialTransform(TransformStack msr, float progress) {
        float dialPivot = 5.75f / 16;

        msr.pushPose();

        if (face == AttachFace.WALL) {

            msr.centre()
               .rotate(Direction.UP, (float) ((-facing.toYRot()) / 180 * Math.PI))
               .unCentre();
            msr.translate(0, 0, -1f / 16f);

            head.setTransform(ms);

            msr.translate(0, dialPivot, dialPivot)
               .rotate(Direction.EAST, (float) (Math.PI / 2 * -progress))
               .translate(0, -dialPivot, -dialPivot);

            dial.setTransform(ms);

            msr.popPose();
            msr.pushPose();

            msr.centre()
               .rotate(Direction.UP, -opposite.toYRot() / 180 * (float)Math.PI)
               .unCentre();
            msr.translate(0, 0, 1f / 16f);

            head2.setTransform(ms);

            msr.translate(0, dialPivot, dialPivot)
               .rotate(Direction.EAST, (float) (Math.PI / 2 * -progress))
               .translate(0, -dialPivot, -dialPivot);

            dial2.setTransform(ms);

        } else {

            msr.centre()
               .rotate(Direction.NORTH, (face == AttachFace.FLOOR ? -1 : 1) * (float)Math.PI / 2)
               .rotate(Direction.EAST, (90 - facing.toYRot()) / 180 * (float)Math.PI)
               .unCentre();

            if (face == AttachFace.CEILING)
                msr.translate(0, ((facing == Direction.SOUTH || facing == Direction.NORTH) ? -1f : 1f)/16f, 0);
            else
                msr.translate(0, -1f/16f, 0);

            head.setTransform(ms);

            msr.translate(0, dialPivot, dialPivot)
               .rotate(Direction.EAST, (float) (Math.PI / 2 * -progress))
               .translate(0, -dialPivot, -dialPivot);

            dial.setTransform(ms);
        }

        msr.popPose();
    }

    @Override
    public void update() {
        updateRotation(shaft);
    }

    @Override
    public void updateLight() {
        relight(pos, shaft, dial, head);
        if (dial2 != null && head2 != null)
            relight(pos, dial2, head2);
    }

    @Override
    public void remove() {
        shaft.delete();
        dial.delete();
        head.delete();
        if (dial2 != null) dial2.delete();
        if (head2 != null) head2.delete();
    }
}