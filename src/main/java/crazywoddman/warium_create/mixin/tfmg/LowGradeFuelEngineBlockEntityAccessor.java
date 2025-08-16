package crazywoddman.warium_create.mixin.tfmg;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import com.drmangotea.tfmg.blocks.engines.low_grade_fuel.LowGradeFuelEngineBlockEntity;

@Mixin(LowGradeFuelEngineBlockEntity.class)
public interface LowGradeFuelEngineBlockEntityAccessor {
    @Accessor("signal")
    void setSignal(int value);

    @Accessor("signal")
    int getSignal();
}