package crazywoddman.warium_create;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec SERVER_SPEC;
    public static final Server SERVER;

    static {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        SERVER = new Server(builder);
        SERVER_SPEC = builder.build();
    }

    public static class Server {
        public final ForgeConfigSpec.IntValue energyToFErate;
        public final ForgeConfigSpec.DoubleValue defaultStress;
        public final ForgeConfigSpec.IntValue defaultSpeed;
        public final ForgeConfigSpec.BooleanValue TFMGspeedControl;
        public final ForgeConfigSpec.ConfigValue<HeatLevel> fireboxHeat;
        public final ForgeConfigSpec.ConfigValue<HeatLevel> oilFireboxHeat;
        public final ForgeConfigSpec.ConfigValue<HeatLevel> electricFireboxHeat;
        public final ForgeConfigSpec.IntValue minThrottle;
        public final ForgeConfigSpec.IntValue maxThrottle;

        public Server(ForgeConfigSpec.Builder builder) {
            builder.push("conversion");
            energyToFErate = builder
                .comment("How much ForgeEnergy = 1 Warium Energy unit. ")
                .comment("This affects:")
                .comment("  Energy Battery and Power Reactor FE capacity (8000 * this number)")
                .comment("  Rotation Generator FE output ")
                .comment("  FE needed for Electric Motor to work")
                .defineInRange("energyToForgeEnergyRate", 100, 1, Integer.MAX_VALUE);
            defaultStress = builder
                .comment("Light Combustion Engine Stress Units equivalent")
                .comment("Note: this will affect whole conversion process, Light Combustion Engine is just for referernce")
                .defineInRange("combustionEngineStress", 2000, 1, Double.MAX_VALUE);
            defaultSpeed = builder
                .comment("Light Combustion Engine Speed equivalent")
                .comment("Note: this will affect whole conversion process, Light Combustion Engine is just for referernce")
                .defineInRange("defaultSpeed", 100, 1, 256);
            TFMGspeedControl = builder
                .comment("Whether TFMG engines connected to Vehicle Control Node should change their speed from throttle value")
                .comment("If set to false, Vehicle Control Node will only turn engines on/off (max speed when turned on)")
                .define("TFMGspeedControl", true);
            fireboxHeat = builder
                .comment("Blaze Burner type heat level for fireboxes")
                .comment("Allowed values are KINDLED or SEETHING")
                .define("fireboxHeatLevel", HeatLevel.KINDLED, value ->
                    value instanceof HeatLevel &&
                    (value.equals(HeatLevel.KINDLED) || value.equals(HeatLevel.SEETHING))
                );
            oilFireboxHeat = builder
                .define("keroseneFireboxHeatLevel", HeatLevel.SEETHING, value ->
                    value instanceof HeatLevel &&
                    (value.equals(HeatLevel.KINDLED) || value.equals(HeatLevel.SEETHING))
                );
            electricFireboxHeat = builder
                .define("electricFireboxHeatLevel", HeatLevel.SEETHING, value ->
                    value instanceof HeatLevel &&
                    (value.equals(HeatLevel.KINDLED) || value.equals(HeatLevel.SEETHING))
                );
            minThrottle = builder
                .comment("Minimal throttle value that can be set using Control Seat")
                .defineInRange("minThrottle", -10, Integer.MIN_VALUE, 0);
            maxThrottle = builder
                .comment("Maximum throttle value that can be set using Control Seat")
                .defineInRange("maxThrottle", 10, 0, Integer.MAX_VALUE);
            builder.pop();
        }
    }
}