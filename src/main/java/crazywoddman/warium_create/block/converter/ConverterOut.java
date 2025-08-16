package crazywoddman.warium_create.block.converter;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.VoxelShaper;

import crazywoddman.warium_create.block.WariumCreateBlockEntities;
import crazywoddman.warium_create.block.WariumCreateShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ConverterOut extends DirectionalKineticBlock implements IBE<ConverterOutBlockEntity> {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;
    public static final VoxelShaper SHAPE = WariumCreateShaper.shape(0, 0, 0, 16, 16, 4).add(2, 2, 4, 14, 14, 15).forDirectional();
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE.get((Direction)state.getValue(FACING));
    }

    public ConverterOut(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferred = getPreferredFacing(context);
        Direction facing;
        Direction look = context.getHorizontalDirection();

        if (preferred != null && (preferred.getAxis().isHorizontal())) {
            if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) 
                facing = look.getOpposite();
            else
                facing = preferred.getOpposite();
        } else {
            if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) 
                facing = look.getOpposite();
            else
                facing = look;
        }

        AttachFace face;
        switch (context.getClickedFace()) {
            case UP: face = AttachFace.FLOOR; break;
            case DOWN: face = AttachFace.CEILING; break;
            default: face = AttachFace.WALL; break;
        }

        return defaultBlockState().setValue(FACING, facing).setValue(FACE, face);
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
        return face == state.getValue(FACING).getOpposite();
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public Class<ConverterOutBlockEntity> getBlockEntityClass() {
        return ConverterOutBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ConverterOutBlockEntity> getBlockEntityType() {
        return WariumCreateBlockEntities.CONVERTER_OUT_BE.get();
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Direction clickedFace = context.getClickedFace();
        Direction facing = state.getValue(FACING);
        AttachFace face = state.getValue(FACE);

        if (clickedFace == facing || clickedFace == facing.getOpposite()) {
            AttachFace newFace;
            switch (face) {
                case FLOOR: newFace = AttachFace.WALL; break;
                case WALL: newFace = AttachFace.CEILING; break;
                case CEILING: newFace = AttachFace.FLOOR; break;
                default: newFace = AttachFace.FLOOR;
            }
            BlockState newState = state.setValue(FACE, newFace);
            context.getLevel().setBlock(context.getClickedPos(), newState, 3);
            return InteractionResult.SUCCESS;
        }
        return super.onWrenched(state, context);
    }
}