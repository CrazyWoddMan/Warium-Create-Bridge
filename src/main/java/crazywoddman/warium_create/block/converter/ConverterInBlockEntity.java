package crazywoddman.warium_create.block.converter;

import java.util.List;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.motor.KineticScrollValueBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.VecHelper;

import crazywoddman.warium_create.Config;
import net.mcreator.valkyrienwarium.block.entity.VehicleControlNodeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public class ConverterInBlockEntity extends GeneratingKineticBlockEntity {

    public ConverterInBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    private final int defaultStress = Config.SERVER.defaultStress.get();
    private final int defaultSpeed = Config.SERVER.defaultSpeed.get();
    private final int maxThrottle = Config.SERVER.maxThrottle.get();
    private final int kineticConverterReponse = Config.SERVER.kineticConverterReponse.get();
    private final boolean converterSpeedControl = Config.SERVER.converterSpeedControl.get();
    private int lastThrottle;
    public ScrollOptionBehaviour<RotationDirection> movementDirection;
    public ScrollValueBehaviour generatedSpeed;

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);

        if (Config.SERVER.converterSpeedControl.get()) {
            generatedSpeed = new KineticScrollValueBehaviour(
                Lang.translateDirect("kinetics.creative_motor.rotation_speed"),
                this,
                new ValueBox()
            );
            generatedSpeed.between(-256, 256);
            generatedSpeed.value = Config.SERVER.defaultSpeed.get() * 50;
            generatedSpeed.withCallback(i -> this.updateGeneratedRotation());
            behaviours.add(generatedSpeed);
        } else {
            movementDirection = new ScrollOptionBehaviour<>(
                RotationDirection.class,
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

        if (!Config.SERVER.converterSpeedControl.get() && compound.contains("ScrollValue")) {
            int savedValue = compound.getInt("ScrollValue");
            
            if (savedValue < 0 || savedValue >= RotationDirection.values().length) {
                compound.putInt("ScrollValue", 0);
                if (movementDirection != null)
                    movementDirection.setValue(0);
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
        float kineticPower = this.getPersistentData().getFloat("KineticPower");

        if (kineticPower <= 0 || getThrottle() == 0)
            return 0;

        float speed;

        if (converterSpeedControl && generatedSpeed != null) 
            speed = generatedSpeed.getValue();
        else if (movementDirection != null) {
            int sign = movementDirection.get() == RotationDirection.CLOCKWISE ? 1 : -1;
            speed = defaultSpeed * sign * kineticPower;
        } else
            return 0;
        
        float result = convertToDirection(
            speed / maxThrottle * getThrottle(), 
            getBlockState().getValue(ConverterIn.FACING)
        );
        return result;
    }

    @Override
    public float calculateAddedStressCapacity() {

        if (speed == 0)
            return 0;

        float capacity = (float) defaultStress / 2;
        lastCapacityProvided = capacity;

        return capacity;
    }

    @Override
    public void tick() {
        super.tick();

        if (level.isClientSide || level.getGameTime() % kineticConverterReponse != 0)
            return;

        Direction facing = getBlockState().getValue(ConverterIn.FACING);
        BlockPos backPos = getBlockPos().relative(facing.getOpposite());
        BlockState backState = level.getBlockState(backPos);
        BlockEntity blockEntity = level.getBlockEntity(backPos);
        CompoundTag data = getPersistentData();
        double prevKP = data.getDouble("KineticPower");
        double newKP = 0.0;
        int throttle = getThrottle();

        if (blockEntity != null) {
            ResourceLocation blockKey = ForgeRegistries.BLOCKS.getKey(backState.getBlock());
            boolean isWarium = blockKey != null && blockKey.getNamespace().equals("crusty_chunks");
            if (isWarium) {
                DirectionProperty facingProp = null;
                for (Property<?> property : backState.getProperties()) {
                    if (property.getName().equals("facing") && property instanceof DirectionProperty directionProperty) {
                        facingProp = directionProperty;
                        break;
                    }
                }
                if (facingProp != null && backState.getValue(facingProp) == facing)
                    newKP = blockEntity.getPersistentData().getDouble("KineticPower");
            }
        }

        if (prevKP != newKP || throttle != lastThrottle) {
            data.putDouble("KineticPower", newKP);
            updateGeneratedRotation();
            lastThrottle = throttle;
        }
    }

    private int getThrottle() {
        CompoundTag data = getPersistentData();

        if (data.contains("ControlX")) {
            BlockEntity controlNode =
                level != null ?
                level.getBlockEntity(new BlockPos(
                    data.getInt("ControlX"),
                    data.getInt("ControlY"),
                    data.getInt("ControlZ")
                ))
                : null;

            if (controlNode != null && controlNode instanceof VehicleControlNodeBlockEntity) {
                int throttle = controlNode.getPersistentData().getInt("Throttle");
                String key = data.getString("Key");

                if (key.isEmpty() || (throttle > 0 && key.equals("Throttle+")) || (throttle < 0 && key.equals("Throttle-")))
                    return Math.abs(throttle);
            }
        }
        
        return maxThrottle;
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