package crazywoddman.warium_create.block.converter;

import java.util.List;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity.RotationDirection;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.motor.KineticScrollValueBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.VecHelper;

import crazywoddman.warium_create.Config;
import crazywoddman.warium_create.util.WariumCreateUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class ConverterInBlockEntity extends GeneratingKineticBlockEntity {

    public ConverterInBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    private final int defaultStress = Config.SERVER.defaultStress.get();
    private final int defaultSpeed = Config.SERVER.defaultSpeed.get();
    private final int maxThrottle = Config.SERVER.maxThrottle.get();
    private final int kineticConverterReponse = Config.SERVER.kineticConverterReponse.get();
    private boolean converterSpeedControl = Config.SERVER.converterSpeedControl.get();
    private final boolean throttleToRotationDirection = Config.SERVER.throttleToRotationDirection.get();
    private int lastThrottle;
    private double lastKineticPower;
    public ScrollOptionBehaviour<WindmillBearingBlockEntity.RotationDirection> movementDirection;
    public ScrollValueBehaviour generatedSpeed;

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.converterSpeedControl = Config.SERVER.converterSpeedControl.get();

        if (this.converterSpeedControl) {
            generatedSpeed = new KineticScrollValueBehaviour(
                Lang.translateDirect("kinetics.creative_motor.rotation_speed"),
                this,
                new ValueBox()
            );
            generatedSpeed.between(-256, 256);
            generatedSpeed.value = this.defaultSpeed * 50;
            generatedSpeed.withCallback(i -> this.updateGeneratedRotation());
            behaviours.add(generatedSpeed);
        } else {
            movementDirection = new ScrollOptionBehaviour<WindmillBearingBlockEntity.RotationDirection>(
                WindmillBearingBlockEntity.RotationDirection.class,
                Lang.translateDirect("contraptions.windmill.rotation_direction"),
                this,
                new ValueBox()
            );
            movementDirection.withCallback(i -> this.updateGeneratedRotation());
            behaviours.add(movementDirection);
        }
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);

        if (!this.converterSpeedControl && compound.contains("ScrollValue")) {
            int savedValue = compound.getInt("ScrollValue");
            
            if (savedValue < 0) {
                compound.putInt("ScrollValue", 0);

                if (this.movementDirection != null)
                    this.movementDirection.setValue(0);
            } else if (savedValue >= RotationDirection.values().length) {
                compound.putInt("ScrollValue", 1);

                if (this.movementDirection != null)
                    this.movementDirection.setValue(1);
            }
        }

        if (this.converterSpeedControl && compound.contains("ScrollValue")) {
            int savedValue = compound.getInt("ScrollValue");
            
            if (savedValue == 0) {
                compound.putInt("ScrollValue", 100);

                if (this.generatedSpeed != null)
                    this.generatedSpeed.setValue(100);
            }
        }
    }

    @Override
    public void initialize() {
        super.initialize();
        updateGeneratedRotation();
    }

    @Override
    public float getGeneratedSpeed() {

        if (this.lastKineticPower <= 0 || this.lastThrottle == 0)
            return 0;

        float speed;

        if (this.converterSpeedControl && this.generatedSpeed != null)
            speed = generatedSpeed.getValue();
        else if (this.movementDirection != null)
            speed = this.defaultSpeed * (movementDirection.get() == RotationDirection.CLOCKWISE ? -1 : 1) * (float) this.lastKineticPower;
        else
            return 0;
            
        return convertToDirection(
            speed / this.maxThrottle * (this.throttleToRotationDirection ? this.lastThrottle : Math.abs(this.lastThrottle)), 
            getBlockState().getValue(ConverterIn.FACING)
        );
    }

    @Override
    public float calculateAddedStressCapacity() {
        float capacity = (float) this.defaultStress / this.defaultSpeed;
        this.lastCapacityProvided = capacity;

        return capacity;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level.getGameTime() % kineticConverterReponse != 0)
            return;

        Direction facing = getBlockState().getValue(ConverterIn.FACING);
        BlockPos backPos = getBlockPos().relative(facing.getOpposite());
        BlockState backState = this.level.getBlockState(backPos);
        BlockEntity backBlockEntity = this.level.getBlockEntity(backPos);
        Double newKineticPower = 0.0;
        int newThrottle = 0;

        if (backBlockEntity != null) {
            CompoundTag backData = backBlockEntity.getPersistentData();

            if (backData.contains("KineticPower")) {
                DirectionProperty facingProp = null;

                for (Property<?> property : backState.getProperties()) {
                    if (property instanceof DirectionProperty directionProperty) {
                        facingProp = directionProperty;
                        break;
                    }
                }

                if (facingProp != null && backState.getValue(facingProp) == facing) {
                    newKineticPower = backData.getDouble("KineticPower");
                    newThrottle = WariumCreateUtil.getThrottle(this, this.maxThrottle);
                }
            }
        }

        boolean powerChanged = this.lastKineticPower != newKineticPower;

        if (powerChanged)
            this.lastKineticPower = newKineticPower;

        boolean throttleChanged = this.lastThrottle != newThrottle;

        if (throttleChanged)
            this.lastThrottle = newThrottle;
        
        if (powerChanged || throttleChanged)
            updateGeneratedRotation();
    }

    class ValueBox extends ValueBoxTransform.Sided {
        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 14.5);
        }

        @Override
        public Vec3 getLocalOffset(BlockState state) {     
            return super.getLocalOffset(state).add(Vec3
                .atLowerCornerOf(state.getValue(ConverterIn.FACING).getNormal())
                .scale(2 / 16f)
            );
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
}