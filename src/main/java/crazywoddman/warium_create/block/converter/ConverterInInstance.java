package crazywoddman.warium_create.block.converter;

import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import net.minecraft.core.Direction;

public class ConverterInInstance extends KineticBlockEntityInstance<ConverterInBlockEntity> {

    protected final RotatingData shaft;
    final Direction facing;

    public ConverterInInstance(MaterialManager materialManager, ConverterInBlockEntity blockEntity) {
        super(materialManager, blockEntity);

        facing = blockState.getValue(ConverterIn.FACING);
        shaft = getRotatingMaterial().getModel(AllPartialModels.SHAFT_HALF, blockState, facing).createInstance();
        
        setup(shaft);
    }

    @Override
    public void update() {
        updateRotation(shaft);
    }

    @Override
    public void updateLight() {
        relight(pos, shaft);
    }

    @Override
    public void remove() {
        shaft.delete();
    }
}