package crazywoddman.warium_create.item;

import crazywoddman.warium_create.WariumCreate;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.food.FoodProperties;

public class WariumCreateItems {
    public static final ItemEntry<YellowcakeItem> YELLOWCAKE = WariumCreate.REGISTRATE
        .item("yellowcake", YellowcakeItem::new)
        .properties(p -> p
            .food(new FoodProperties.Builder()
                .nutrition(20)
                .saturationMod(20_000_000.0f)
                .alwaysEat()
                .build()
            )
        )
        .register();

    public static void register() {}
}