package crazywoddman.warium_create.block;

import crazywoddman.warium_create.item.ItemTooltip;
import crazywoddman.warium_create.WariumCreate;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.Block;
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
        .properties(p -> p.mapColor(MapColor.METAL))
        .properties(p -> p.strength(2.0F))
        .blockstate(BlockStateGen.directionalBlockProvider(true))
        .item(ItemTooltip::new)
        .build()
        .register();

    public static final BlockEntry<ConverterIn> CONVERTER_IN = WariumCreate.REGISTRATE
        .block("converter_in", ConverterIn::new)
        .properties(p -> p.mapColor(MapColor.METAL))
        .properties(p -> p.strength(2.0F))
        .blockstate(BlockStateGen.directionalBlockProvider(true))
        .item(ItemTooltip::new)
        .build()
        .register();

    public static void register() {}
}