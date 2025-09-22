package crazywoddman.warium_create;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = WariumCreate.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ConfigClient {
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        if (ModList.get().isLoaded("cloth_config"))
            ClothConfigProvider.registerConfigScreen();
    }
}