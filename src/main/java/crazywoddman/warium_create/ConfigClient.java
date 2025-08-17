package crazywoddman.warium_create;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;

@Mod.EventBusSubscriber(modid = WariumCreate.ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigClient {
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        if (ModList.get().isLoaded("cloth_config")) {
            ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenFactory.class,
                () -> new ConfigScreenFactory((mc, screen) -> {
                    boolean inWorld = Minecraft.getInstance().level != null;

                    ConfigBuilder builder = ConfigBuilder.create()
                        .setParentScreen(screen)
                        .setTitle(Component.literal("Warium Create Config"));

                    builder.setSavingRunnable(() -> {});
                    builder.setGlobalized(false);
                    builder.setTransparentBackground(true);

                    var category = builder.getOrCreateCategory(Component.literal("Conversion Rates"));
                    ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();

                    category.addEntry(
                        entryBuilder
                            .startTextDescription(
                                inWorld
                                    ? Component.literal("§eChanges only apply for the current world. If you're playing on a server, use Create «Access Configs of other mods» feature for changes to take effect. World re-enter or server restart is required for some changes to apply")
                                    : Component.literal("§cChanges can't be made from the main menu. Enter a world")
                            )
                            .build()
                    );

                    if (inWorld) {
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
                                .setDefaultValue(100)
                                .setMin(1)
                                .setSaveConsumer(newValue -> {
                                    Config.SERVER.energyToFErate.set(newValue);
                                    Config.SERVER_SPEC.save();
                                })
                                .build()
                        );
                        category.addEntry(
                            entryBuilder
                                .startDoubleField(
                                    Component.literal("Light Combustion Engine Stress Units"),
                                    Config.SERVER.defaultStress.get()
                                )
                                .setTooltip(
                                    Component.literal("Note: this will affect whole conversion process"),
                                    Component.literal("Light Combustion Engine is just for referernce")
                                )
                                .setDefaultValue(2000)
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
                                    Component.literal("Light Combustion Engine Speed"),
                                    Config.SERVER.defaultSpeed.get()
                                )
                                .setTooltip(
                                    Component.literal("Note: this will affect whole conversion process"),
                                    Component.literal("Light Combustion Engine is just for referernce")
                                )
                                .setDefaultValue(100)
                                .setMin(1)
                                .setMax(256)
                                .setSaveConsumer(newValue -> {
                                    Config.SERVER.defaultSpeed.set(newValue);
                                    Config.SERVER_SPEC.save();
                                })
                                .build()
                        );
                        category.addEntry(
                            entryBuilder
                                .startBooleanToggle(
                                    Component.literal("TFMG engines speed control"),
                                    Config.SERVER.TFMGspeedControl.get()
                                )
                                .setTooltip(
                                    Component.literal("Whether TFMG engines connected to Vehicle Control Node should change their speed from throttle value"),
                                    Component.literal("If set to No, Vehicle Control Node will only turn engines on/off (max speed when turned on)")
                                )
                                .setDefaultValue(true)
                                .setSaveConsumer(newValue -> {
                                    Config.SERVER.TFMGspeedControl.set(newValue);
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
                                .setDefaultValue("KINDLED")
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
                                .setDefaultValue("SEETHING")
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
                                .setDefaultValue("SEETHING")
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
                                .setDefaultValue(-10)
                                .setMax(0)
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
                                .setDefaultValue(10)
                                .setMin(0)
                                .setSaveConsumer(newValue -> {
                                    Config.SERVER.maxThrottle.set(newValue);
                                    Config.SERVER_SPEC.save();
                                })
                                .build()
                        );
                    }

                    return builder.build();
                })
            );
        }
    }
}