package crazywoddman.warium_create.mixin.tfmg;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import com.drmangotea.tfmg.blocks.engines.compact.CompactEngineBlockEntity;

@Mixin(CompactEngineBlockEntity.class)
public interface CompactEngineBlockEntityAccessor {
    @Accessor("signal")
    void setSignal(int value);

    @Accessor("signal")
    int getSignal();
}