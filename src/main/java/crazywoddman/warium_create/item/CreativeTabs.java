package crazywoddman.warium_create.item;

import crazywoddman.warium_create.WariumCreate;
import crazywoddman.warium_create.WariumCreateFluids;
import crazywoddman.warium_create.block.WariumCreateBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = WariumCreate.MODID, bus = EventBusSubscriber.Bus.MOD)
public class CreativeTabs {

    @SubscribeEvent
    public static void onBuildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().location().equals(ResourceLocation.fromNamespaceAndPath("crusty_chunks", "warium_logistics"))) {
            event.accept(WariumCreateBlocks.CONVERTER_IN.asStack());
            event.accept(WariumCreateBlocks.CONVERTER_OUT.asStack());
        }
        if (event.getTabKey().location().equals(ResourceLocation.fromNamespaceAndPath("valkyrien_warium", "warium_vs")))
            event.accept(WariumCreateBlocks.CONTROLLABLE_TRIGGER.asStack());

        if (event.getTabKey().location().equals(ResourceLocation.fromNamespaceAndPath("crusty_chunks", "crusty_production")))
            event.accept(WariumCreateItems.YELLOWCAKE.get());

        if (event.getTabKey().location().equals(ResourceLocation.fromNamespaceAndPath("crusty_chunks", "crusty_ores")))
            event.accept(WariumCreateFluids.YELLOWCAKE_FLUID.getBucket().get());

        if (event.getTabKey().location().equals(ResourceLocation.fromNamespaceAndPath("crusty_chunks", "crusty_components")))
            event.accept(WariumCreateItems.PETROLIUM_BOTTLE.get());
    }
}