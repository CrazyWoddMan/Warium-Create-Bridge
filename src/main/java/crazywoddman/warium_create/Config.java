package crazywoddman.warium_create;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    private static final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    public static final Server SERVER = new Server(builder);
    public static final ForgeConfigSpec SERVER_SPEC = builder.build();

    public static class Server {
        public final ForgeConfigSpec.IntValue energyToFErate;
        public final ForgeConfigSpec.IntValue defaultStress;
        public final ForgeConfigSpec.IntValue defaultSpeed;
        public final ForgeConfigSpec.BooleanValue converterSpeedControl;
        public final ForgeConfigSpec.ConfigValue<HeatLevel> fireboxHeat;
        public final ForgeConfigSpec.ConfigValue<HeatLevel> oilFireboxHeat;
        public final ForgeConfigSpec.ConfigValue<HeatLevel> electricFireboxHeat;
        public final ForgeConfigSpec.IntValue minThrottle;
        public final ForgeConfigSpec.IntValue maxThrottle;
        public final ForgeConfigSpec.DoubleValue enginePower;
        public final ForgeConfigSpec.DoubleValue turbinePower;
        public final ForgeConfigSpec.IntValue kineticConverterReponse;

        public Server(ForgeConfigSpec.Builder builder) {
            energyToFErate = builder
                .comment("How much ForgeEnergy = 1 Warium Energy unit. ")
                .comment("This affects:")
                .comment("  Energy Battery and Power Reactor FE capacity (8000 * this number)")
                .comment("  Rotation Generator FE output ")
                .comment("  FE needed for Electric Motor to work")
                .defineInRange("energyToForgeEnergyRate", 100, 1, Integer.MAX_VALUE);
            defaultStress = builder
                .comment("How many Stress Untis will be equivalent to 1 Kinetic Power unit")
                .defineInRange("kineticToStressRate", 40, 1, Integer.MAX_VALUE);
            defaultSpeed = builder
                .comment("What Rotation Speed is equivalent to 1 Kinetic Power unit")
                .defineInRange("kineticToSpeedRate", 2, 1, 256);
            converterSpeedControl = builder
                .comment("Whether Kinetic Converter value box allows to select generating speed")
                .define("kineticConverterSpeedControl", false);
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
            kineticConverterReponse = builder
                .comment("Tick-measured Kinetic Converter response delay when changing throttle")
                .comment("WARNING: lowering this value may cause shafts to break when changing throttle too fast")
                .defineInRange("kineticConverterReponse", 8, 0, 40);
            enginePower = builder
                .comment("Combution engines KineticPower output")
                .defineInRange("enginesPower", 50.0, 0, Double.MAX_VALUE);
            turbinePower = builder
                .comment("Jet Turbine KineticPower output")
                .defineInRange("turbinePower", 51.0, 0, Double.MAX_VALUE);
        }
    }
}