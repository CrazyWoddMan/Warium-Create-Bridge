package crazywoddman.warium_create;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;

@EventBusSubscriber(modid = WariumCreate.ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ConfigClient {
    @SuppressWarnings("removal")
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {

        if (!ModList.get().isLoaded("cloth_config"))
            return;
        
        FMLJavaModLoadingContext.get().registerExtensionPoint(ConfigScreenFactory.class, () -> new ConfigScreenFactory((mc, screen) -> {

            ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(screen)
                .setTitle(Component.literal("Warium-Create Bridge Config"));

            builder.setGlobalized(false);
            builder.setTransparentBackground(true);

            ConfigCategory category = builder.getOrCreateCategory(Component.literal("Conversion Rates"));
            ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();
            boolean inWorld = Minecraft.getInstance().level != null;

            category.addEntry(
                entryBuilder
                    .startTextDescription(
                        inWorld
                        ? Component.literal("§eChanges only apply for the current world. If you're playing on a server, use Create «Access Configs of other mods» feature for changes to take effect. World re-enter or server restart is required for some changes to apply")
                        : Component.literal("§cChanges can't be made from the main menu. Enter a world")
                    )
                    .build()
            );

            if (!inWorld)
                return builder.build();
                
            category.addEntry(
                entryBuilder
                    .startIntField(
                        Component.literal("How much ForgeEnergy = 1 Warium Energy unit"),
                        Config.SERVER.energyToFErate.get()
                    )
                    .setTooltip(
                        Component.literal("This affects:"),
                        Component.literal("* Energy Battery and Power Reactor FE capacity (8000 * value)"),
                        Component.literal("* Rotation Generator FE output"),
                        Component.literal("* FE needed for Electric Motor to work")
                    )
                    .setDefaultValue(Config.SERVER.energyToFErate.getDefault())
                    .setMin(1)
                    .setSaveConsumer(newValue -> {
                        Config.SERVER.energyToFErate.set(newValue);
                        Config.SERVER_SPEC.save();
                    })
                    .build()
            );
            category.addEntry(
                entryBuilder
                    .startIntField(
                        Component.literal("Kinetic Power to su ratio"),
                        Config.SERVER.defaultStress.get()
                    )
                    .setTooltip(Component.literal("How many Stress Untis will be equivalent to 1 Kinetic Power unit"))
                    .setDefaultValue(Config.SERVER.defaultStress.getDefault())
                    .setMin(1)
                    .setSaveConsumer(newValue -> {
                        Config.SERVER.defaultStress.set(newValue);
                        Config.SERVER_SPEC.save();
                    })
                    .build()
            );
            category.addEntry(
                entryBuilder
                    .startIntField(
                        Component.literal("Kinetic Power to speed ratio"),
                        Config.SERVER.defaultSpeed.get()
                    )
                    .setTooltip(Component.literal("What Rotation Speed is equivalent to 1 Kinetic Power unit"))
                    .setDefaultValue(Config.SERVER.defaultSpeed.getDefault())
                    .setMin(1)
                    .setMax(5)
                    .setSaveConsumer(newValue -> {
                        Config.SERVER.defaultSpeed.set(newValue);
                        Config.SERVER_SPEC.save();
                    })
                    .build()
            );
            category.addEntry(
                entryBuilder
                    .startBooleanToggle(
                        Component.literal("Kinetic Converter speed control"),
                        Config.SERVER.converterSpeedControl.get()
                    )
                    .setTooltip(Component.literal("Whether Kinetic Converter value box allows to select generating speed"))
                    .setDefaultValue(Config.SERVER.converterSpeedControl.getDefault())
                    .setSaveConsumer(newValue -> {
                        Config.SERVER.converterSpeedControl.set(newValue);
                        Config.SERVER_SPEC.save();
                    })
                    .build()
            );
            category.addEntry(
                entryBuilder
                    .startBooleanToggle(
                        Component.literal("Throttle Rotation Direction Reverse"),
                        Config.SERVER.throttleToRotationDirection.get()
                    )
                    .setTooltip(Component.literal("Whether negative throttle values should reverse rotation direction"))
                    .setDefaultValue(Config.SERVER.throttleToRotationDirection.getDefault())
                    .setSaveConsumer(newValue -> {
                        Config.SERVER.throttleToRotationDirection.set(newValue);
                        Config.SERVER_SPEC.save();
                    })
                    .build()
            );
            category.addEntry(
                entryBuilder
                    .startSelector(
                        Component.literal("Firebox Heat Level"),
                        new String[] { "KINDLED", "SEETHING" },
                        Config.SERVER.fireboxHeat.get().name()
                    )
                    .setDefaultValue(Config.SERVER.fireboxHeat.getDefault().name())
                    .setSaveConsumer(newValue -> {
                        Config.SERVER.fireboxHeat.set(BlazeBurnerBlock.HeatLevel.valueOf(newValue));
                        Config.SERVER_SPEC.save();
                    })
                    .build()
            );
            category.addEntry(
                entryBuilder
                    .startSelector(
                        Component.literal("Kerosene Firebox Heat Level"),
                        new String[] { "KINDLED", "SEETHING" },
                        Config.SERVER.oilFireboxHeat.get().name()
                    )
                    .setDefaultValue(Config.SERVER.oilFireboxHeat.getDefault().name())
                    .setSaveConsumer(newValue -> {
                        Config.SERVER.oilFireboxHeat.set(BlazeBurnerBlock.HeatLevel.valueOf(newValue));
                        Config.SERVER_SPEC.save();
                    })
                    .build()
            );
            category.addEntry(
                entryBuilder
                    .startSelector(
                        Component.literal("Electric Firebox Heat Level"),
                        new String[] { "KINDLED", "SEETHING" },
                        Config.SERVER.electricFireboxHeat.get().name()
                    )
                    .setDefaultValue(Config.SERVER.electricFireboxHeat.getDefault().name())
                    .setSaveConsumer(newValue -> {
                        Config.SERVER.electricFireboxHeat.set(BlazeBurnerBlock.HeatLevel.valueOf(newValue));
                        Config.SERVER_SPEC.save();
                    })
                    .build()
            );
            category.addEntry(
                entryBuilder
                    .startIntField(
                        Component.literal("Minimal throttle value"),
                        Config.SERVER.minThrottle.get()
                    )
                    .setDefaultValue(Config.SERVER.minThrottle.getDefault())
                    .setMax(-1)
                    .setSaveConsumer(newValue -> {
                        Config.SERVER.minThrottle.set(newValue);
                        Config.SERVER_SPEC.save();
                    })
                    .build()
            );
            category.addEntry(
                entryBuilder
                    .startIntField(
                        Component.literal("Maximum throttle value"),
                        Config.SERVER.maxThrottle.get()
                    )
                    .setDefaultValue(Config.SERVER.maxThrottle.getDefault())
                    .setMin(1)
                    .setSaveConsumer(newValue -> {
                        Config.SERVER.maxThrottle.set(newValue);
                        Config.SERVER_SPEC.save();
                    })
                    .build()
            );
            category.addEntry(
                entryBuilder
                    .startIntField(
                        Component.literal("Kinetic Converter response delay"),
                        Config.SERVER.kineticConverterReponse.get()
                    )
                    .setTooltip(Component.literal("Tick-measured Kinetic Converter response delay when changing throttle"))
                    .setTooltip(Component.literal("WARNING: lowering this value may cause shafts to break when changing throttle too fast"))
                    .setDefaultValue(Config.SERVER.kineticConverterReponse.getDefault())
                    .setMin(0)
                    .setMax(40)
                    .setSaveConsumer(newValue -> {
                        Config.SERVER.kineticConverterReponse.set(newValue);
                        Config.SERVER_SPEC.save();
                    })
                    .build()
            );
            category.addEntry(
                entryBuilder
                    .startDoubleField(
                        Component.literal("Combution engines KineticPower output"),
                        Config.SERVER.enginePower.get()
                    )
                    .setDefaultValue(Config.SERVER.enginePower.getDefault())
                    .setMin(0)
                    .setSaveConsumer(newValue -> {
                        Config.SERVER.enginePower.set(newValue);
                        Config.SERVER_SPEC.save();
                    })
                    .build()
            );
            category.addEntry(
                entryBuilder
                    .startDoubleField(
                        Component.literal("Jet Turbine KineticPower output"),
                        Config.SERVER.turbinePower.get()
                    )
                    .setDefaultValue(Config.SERVER.turbinePower.getDefault())
                    .setMin(0)
                    .setSaveConsumer(newValue -> {
                        Config.SERVER.turbinePower.set(newValue);
                        Config.SERVER_SPEC.save();
                    })
                    .build()
            );

            return builder.build();
        }));
    }
}