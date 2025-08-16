package crazywoddman.warium_create.block.converter;

import java.util.List;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.VecHelper;

import crazywoddman.warium_create.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;

public class ConverterInBlockEntity extends GeneratingKineticBlockEntity {

    public ConverterInBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    private final double defaultStress = Config.SERVER.defaultStress.get();
    private final int defaultSpeed = Config.SERVER.defaultSpeed.get();
    public ScrollOptionBehaviour<RotationDirection> movementDirection;

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        movementDirection = new ScrollOptionBehaviour<>(
            RotationDirection.class,
            Lang.translateDirect("contraptions.windmill.rotation_direction"),
            this,
            new ValueBox()
        );
        movementDirection.withCallback($ -> this.updateGeneratedRotation());
        behaviours.add(movementDirection);
    }

    @Override
    public void initialize() {
        super.initialize();
        updateGeneratedRotation();
    }

    @Override
    public float getGeneratedSpeed() {
        if (this.getPersistentData().getDouble("KineticPower") <= 0)
            return 0;
        int sign = movementDirection.get() == RotationDirection.CLOCKWISE ? -1 : 1;
        return convertToDirection(defaultSpeed * sign, getBlockState().getValue(ConverterIn.FACING));
    }

    @Override
    public float calculateAddedStressCapacity() {
        float speed = Math.abs(getGeneratedSpeed());
        if (speed == 0) return 0;
        return (float) (this.getPersistentData().getDouble("KineticPower") / 50 * defaultStress / speed);
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide) return;

        Direction facing = getBlockState().getValue(ConverterIn.FACING);
        BlockPos backPos = getBlockPos().relative(facing.getOpposite());
        BlockState backState = level.getBlockState(backPos);
        BlockEntity blockEntity = level.getBlockEntity(backPos);

        double prevKP = this.getPersistentData().getDouble("KineticPower");
        double newKP = 0.0;

        if (blockEntity != null) {
            var blockKey = backState.getBlock().builtInRegistryHolder().key().location();
            boolean isWarium = blockKey != null && blockKey.getNamespace().equals("crusty_chunks");
            if (isWarium) {
                DirectionProperty facingProp = null;
                for (var prop : backState.getProperties()) {
                    if (prop.getName().equals("facing") && prop instanceof DirectionProperty dp) {
                        facingProp = dp;
                        break;
                    }
                }
                if (facingProp != null) {
                    Direction wariumFacing = backState.getValue(facingProp);
                    if (wariumFacing == facing) {
                        newKP = blockEntity.getPersistentData().getDouble("KineticPower");
                    }
                }
            }
        }

        if (prevKP != newKP) {
            this.getPersistentData().putDouble("KineticPower", newKP);
            this.networkDirty = true;
            updateGeneratedRotation();
        }
    }

    class ValueBox extends ValueBoxTransform.Sided {
        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 14.5);
        }

        @Override
        public Vec3 getLocalOffset(BlockState state) {
            Direction facing = state.getValue(ConverterIn.FACING);
            return super.getLocalOffset(state)
                .add(Vec3.atLowerCornerOf(facing.getNormal()).scale(2 / 16f));
        }

        @Override
        public void rotate(BlockState state, PoseStack ms) {
            super.rotate(state, ms);
            Direction facing = state.getValue(ConverterIn.FACING);
            if (facing.getAxis() == Direction.Axis.Y)
                return;
            if (getSide() != Direction.UP)
                return;
            TransformStack.cast(ms)
                .rotateZ(-AngleHelper.horizontalAngle(facing) + 180);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            Direction facing = state.getValue(ConverterIn.FACING);
            if (facing.getAxis() != Direction.Axis.Y && direction == Direction.DOWN)
                return false;
            return direction.getAxis() != facing.getAxis();
        }
    }
    
    public enum RotationDirection implements INamedIconOptions {
        CLOCKWISE, COUNTER_CLOCKWISE;

        @Override
        public String getTranslationKey() {
            return "generic." + Lang.asId(name());
        }

        @Override
        public AllIcons getIcon() {
            return this == CLOCKWISE ? AllIcons.I_REFRESH : AllIcons.I_ROTATE_CCW;
        }
    }
}