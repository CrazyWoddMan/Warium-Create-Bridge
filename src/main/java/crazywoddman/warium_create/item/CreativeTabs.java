package crazywoddman.warium_create.item;

import crazywoddman.warium_create.WariumCreate;
import crazywoddman.warium_create.block.WariumCreateBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WariumCreate.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreativeTabs {

    @SubscribeEvent
    public static void onBuildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().location().equals(new ResourceLocation("crusty_chunks", "warium_logistics"))) {
            event.accept(WariumCreateBlocks.CONVERTER_IN.asStack());
            event.accept(WariumCreateBlocks.CONVERTER_OUT.asStack());
        }
        if (event.getTabKey().location().equals(new ResourceLocation("create", "base"))) {
            event.accept(WariumCreateItems.YELLOWCAKE.get());
        }
    }
}