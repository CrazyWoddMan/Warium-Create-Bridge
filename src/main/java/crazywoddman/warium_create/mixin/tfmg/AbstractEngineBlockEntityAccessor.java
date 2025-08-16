package crazywoddman.warium_create.mixin.tfmg;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import com.drmangotea.tfmg.blocks.engines.small.AbstractEngineBlockEntity;

@Mixin(AbstractEngineBlockEntity.class)
public interface AbstractEngineBlockEntityAccessor {
    @Accessor("signal")
    void setSignal(int value);

    @Accessor("signal")
    int getSignal();
}