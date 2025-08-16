package crazywoddman.warium_create.block;

import crazywoddman.warium_create.block.converter.*;
import crazywoddman.warium_create.WariumCreate;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WariumCreateBlocks {
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, "crusty_chunks");
    public static final RegistryObject<Block> CUSTOM_FIREBOX = REGISTRY.register("firebox", CustomFireboxBlock::new);
    public static final RegistryObject<Block> CUSTOM_OIL_FIREBOX = REGISTRY.register("oil_firebox", CustomOilFireboxBlock::new);
    public static final RegistryObject<Block> CUSTOM_ELECTRIC_FIREBOX = REGISTRY.register("electric_firebox", CustomElectricFireboxBlock::new);

    public static final BlockEntry<ConverterOut> CONVERTER_OUT = WariumCreate.REGISTRATE
        .block("converter_out", ConverterOut::new)
        .properties(p -> p.strength(1.0F).mapColor(MapColor.METAL).sound(SoundType.ANVIL))
        .simpleItem()
        .register();

    public static final BlockEntry<ConverterIn> CONVERTER_IN = WariumCreate.REGISTRATE
        .block("converter_in", ConverterIn::new)
        .properties(p -> p.strength(1.0F).mapColor(MapColor.METAL).sound(SoundType.ANVIL))
        .simpleItem()
        .register();

    public static final BlockEntry<ControllableTrigger> CONTROLLABLE_TRIGGER = WariumCreate.REGISTRATE
        .block("controllable_trigger", ControllableTrigger::new)
        .properties(p -> p.mapColor(MapColor.COLOR_BLACK))
        .properties(p -> p.strength(1.0F).mapColor(MapColor.COLOR_BLACK).sound(SoundType.ANVIL))
        .simpleItem()
        .register();

    public static void register() {}
}