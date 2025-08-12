package crazywoddman.warium_create;

import crazywoddman.warium_create.item.WariumCreateItems;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WariumCreate.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WariumCreateEvents {
    @SubscribeEvent
    public static void onFurnaceFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        if (event.getItemStack().is(WariumCreateItems.YELLOWCAKE.get())) {
            event.setBurnTime(12800);
        }
    }
}