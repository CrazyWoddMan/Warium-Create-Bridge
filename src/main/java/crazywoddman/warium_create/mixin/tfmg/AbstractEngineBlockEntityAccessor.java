package crazywoddman.warium_create.mixin.tfmg;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import com.drmangotea.tfmg.blocks.engines.small.AbstractEngineBlockEntity;

@Mixin(value = AbstractEngineBlockEntity.class, remap = false)
public interface AbstractEngineBlockEntityAccessor {
    @Accessor("signal")
    void setSignal(int value);
}