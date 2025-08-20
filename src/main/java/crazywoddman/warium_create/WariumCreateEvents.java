package crazywoddman.warium_create;

import crazywoddman.warium_create.item.WariumCreateItems;
import net.mcreator.crustychunks.procedures.Rad1TickProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = WariumCreate.ID, bus = EventBusSubscriber.Bus.FORGE)
public class WariumCreateEvents {
    @SubscribeEvent
    public static void onFurnaceFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        if (event.getItemStack().is(WariumCreateItems.YELLOWCAKE.get())) {
            event.setBurnTime(12800);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent event) {
        Player player = event.player;
        Level level = player.level();
        BlockPos pos = player.blockPosition();
        FluidState fluidState = level.getFluidState(pos);

        if (fluidState.getType() == WariumCreateFluids.YELLOWCAKE_FLUID.get().getSource())
            if (player.tickCount % 10 == 0) {
                Rad1TickProcedure.execute(level, pos.getX(), pos.getY(), pos.getZ());
            }
    }
}