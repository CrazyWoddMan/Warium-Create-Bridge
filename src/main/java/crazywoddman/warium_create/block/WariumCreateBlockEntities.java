package crazywoddman.warium_create.block;

import crazywoddman.warium_create.WariumCreate;
import crazywoddman.warium_create.block.converter.*;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class WariumCreateBlockEntities {
    public static final BlockEntityEntry<ConverterOutBlockEntity> CONVERTER_OUT_BE = WariumCreate.REGISTRATE
        .blockEntity("converter_out", ConverterOutBlockEntity::new)
        .instance(() -> ConverterOutInstance::new, false)
        .validBlocks(WariumCreateBlocks.CONVERTER_OUT)
        .renderer(() -> ConverterOutRenderer::new)
        .register();

    public static final BlockEntityEntry<ConverterInBlockEntity> CONVERTER_IN_BE = WariumCreate.REGISTRATE
        .blockEntity("converter_in", ConverterInBlockEntity::new)
        .instance(() -> ConverterInInstance::new, false)
        .validBlocks(WariumCreateBlocks.CONVERTER_IN)
        .renderer(() -> ConverterInRenderer::new)
        .register();
        
    public static final BlockEntityEntry<ControllableTriggerBlockEntity> CONTROLLABLE_TRIGGER_BE = WariumCreate.REGISTRATE
        .blockEntity("controllable_trigger", ControllableTriggerBlockEntity::new)
        .validBlocks(WariumCreateBlocks.CONTROLLABLE_TRIGGER)
        .register();

    public static void register() {}
}