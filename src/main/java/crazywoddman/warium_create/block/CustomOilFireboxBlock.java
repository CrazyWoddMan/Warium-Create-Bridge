package crazywoddman.warium_create.block;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;

import net.mcreator.crustychunks.block.OilFireboxBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class CustomOilFireboxBlock extends OilFireboxBlock {
    public static final EnumProperty<HeatLevel> HEAT_LEVEL =
        EnumProperty.create("blaze", HeatLevel.class);

    public CustomOilFireboxBlock() {
        super();
        registerDefaultState(defaultBlockState().setValue(HEAT_LEVEL, HeatLevel.NONE));
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HEAT_LEVEL);
    }
}