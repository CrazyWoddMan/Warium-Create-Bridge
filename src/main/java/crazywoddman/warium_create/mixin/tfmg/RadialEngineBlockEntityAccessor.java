package crazywoddman.warium_create.mixin.tfmg;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import com.drmangotea.tfmg.blocks.engines.radial.RadialEngineBlockEntity;

@Mixin(RadialEngineBlockEntity.class)
public interface RadialEngineBlockEntityAccessor {
    @Accessor("signal")
    void setSignal(int value);

    @Accessor("signal")
    int getSignal();
}