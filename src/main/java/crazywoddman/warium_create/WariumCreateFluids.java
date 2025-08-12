package crazywoddman.warium_create;

import com.tterrag.registrate.util.entry.FluidEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class WariumCreateFluids {
    public static final FluidEntry<ForgeFlowingFluid.Flowing> YELLOWCAKE_FLUID =
        WariumCreate.REGISTRATE
            .fluid("yellowcake_mixture",
                new ResourceLocation("warium_create:block/yellowcake_mixture_still"),
                new ResourceLocation("warium_create:block/yellowcake_mixture_flow"))
            .properties(p -> p
                .density(2000)
                .viscosity(2000)
                .temperature(350)
            )
            .register();

    public static void register() {}
}