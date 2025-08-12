package crazywoddman.warium_create;

import crazywoddman.warium_create.block.WariumCreateBlocks;
import crazywoddman.warium_create.block.WariumCreateBlockEntities;
import crazywoddman.warium_create.item.WariumCreateItems;

import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(WariumCreate.ID)
public class WariumCreate {
    public static final String ID = "warium_create";
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(ID);

    public WariumCreate() {
        WariumCreateBlocks.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
        REGISTRATE.registerEventListeners(FMLJavaModLoadingContext.get().getModEventBus());
        WariumCreateBlocks.register();
        WariumCreateBlockEntities.register();
        WariumCreateItems.register();
        WariumCreateFluids.register();
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC);
    }
}