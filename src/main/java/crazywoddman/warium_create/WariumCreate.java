package crazywoddman.warium_create;

import crazywoddman.warium_create.block.WariumCreateBlocks;
import crazywoddman.warium_create.block.WariumCreateBlockEntities;
import crazywoddman.warium_create.item.WariumCreateItems;
import crazywoddman.warium_create.recipe.WariumCreateRecipeTypes;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper.Palette;
import com.simibubi.create.foundation.item.TooltipModifier;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(WariumCreate.ID)
public class WariumCreate {
    public static final String ID = "warium_create";
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(ID);
    static {
		REGISTRATE.setTooltipModifierFactory(item -> {
			return new ItemDescription.Modifier(item, Palette.STANDARD_CREATE)
				.andThen(TooltipModifier.mapNull(KineticStats.create(item)));
		});
	}

    public WariumCreate(FMLJavaModLoadingContext context) {
        IEventBus bus = context.getModEventBus();
        WariumCreateBlocks.REGISTRY.register(bus);
        REGISTRATE.registerEventListeners(bus);
        WariumCreateBlocks.register();
        WariumCreateRecipeTypes.register();
        WariumCreateRecipeTypes.Recipes.SERIALIZER_REGISTER.register(bus);
        WariumCreateRecipeTypes.Recipes.TYPE_REGISTER.register(bus);
        WariumCreateBlockEntities.register();
        WariumCreateItems.register();
        WariumCreateFluids.register();
        context.registerConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC);
    }
}