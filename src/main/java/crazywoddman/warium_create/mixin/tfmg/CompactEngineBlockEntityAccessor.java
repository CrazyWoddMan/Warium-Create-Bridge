package crazywoddman.warium_create.mixin.tfmg;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import com.drmangotea.tfmg.blocks.engines.compact.CompactEngineBlockEntity;

@Mixin(remap = false, value = CompactEngineBlockEntity.class)
public interface CompactEngineBlockEntityAccessor {
    @Accessor("signal")
    void setSignal(int value);
}