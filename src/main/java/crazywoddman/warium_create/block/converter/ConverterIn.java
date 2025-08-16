package crazywoddman.warium_create.block.converter;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.VoxelShaper;

import crazywoddman.warium_create.block.WariumCreateBlockEntities;
import crazywoddman.warium_create.block.WariumCreateShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ConverterIn extends DirectionalKineticBlock implements IBE<ConverterInBlockEntity> {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final VoxelShaper SHAPE = WariumCreateShaper.shape(0, 0, 10, 16, 16, 16).add(1, 1, 2, 15, 15, 10).add(0, 0, 0, 16, 16, 2).forDirectional();
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE.get((Direction)state.getValue(FACING));
    }

    public ConverterIn(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferred = getPreferredFacing(context);
        Direction facing;
        Direction look = context.getHorizontalDirection();

        if (preferred != null && preferred.getAxis().isHorizontal()) {
            if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown())
                facing = look;
            else
                facing = preferred;
        } else {
            if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown())
                facing = look;
            else
                facing = look.getOpposite();
        }
        return defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.setValue(FACING, mirrorIn.mirror(state.getValue(FACING)));
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public Class<ConverterInBlockEntity> getBlockEntityClass() {
        return ConverterInBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ConverterInBlockEntity> getBlockEntityType() {
        return WariumCreateBlockEntities.CONVERTER_IN_BE.get();
    }
}