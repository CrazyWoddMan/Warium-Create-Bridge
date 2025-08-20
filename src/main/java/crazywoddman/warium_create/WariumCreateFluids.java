package crazywoddman.warium_create;

import org.joml.Vector3f;

import com.simibubi.create.AllFluids.TintedFluidType;
import com.tterrag.registrate.builders.FluidBuilder.FluidTypeFactory;
import com.tterrag.registrate.util.entry.FluidEntry;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid.Flowing;

public class WariumCreateFluids {
    public static final FluidEntry<Flowing> YELLOWCAKE_FLUID =
        WariumCreate.REGISTRATE
            .fluid(
                "yellowcake_mixture",
                new ResourceLocation("warium_create:block/yellowcake_mixture_still"),
                new ResourceLocation("warium_create:block/yellowcake_mixture_flow"),
                YellowcakeFluidType.create()
            )
			.fluidProperties(p -> p
                .levelDecreasePerBlock(2)
				.tickRate(25)
				.slopeFindDistance(3)
				.explosionResistance(100f)
            )
            .register();

    public static void register() {}

    private static class YellowcakeFluidType extends TintedFluidType {

		public static FluidTypeFactory create() {
			return (p, s, f) -> {
				YellowcakeFluidType fluidType = new YellowcakeFluidType(p, s, f);
				return fluidType;
			};
		}

		private YellowcakeFluidType(Properties properties, ResourceLocation stillTexture,
			ResourceLocation flowingTexture) {
			super(properties, stillTexture, flowingTexture);
		}

		@Override
		protected int getTintColor(FluidStack stack) {
			return -1;
		}

		@Override
		public int getTintColor(FluidState state, BlockAndTintGetter world, BlockPos pos) {
			return 0x00ffffff;
		}

		@Override
		protected Vector3f getCustomFogColor() {
			return new Vector3f(255, 255, 0);
		}

		@Override
		protected float getFogDistanceModifier() {
			return 1f/2f;
		}

        @Override
        public boolean canSwim(Entity entity) {
            return false;
        }

        @Override
        public boolean move(FluidState state, LivingEntity entity, Vec3 movementVector, double gravity) {
            entity.setDeltaMovement(entity.getDeltaMovement().scale(0.8));
            return false;
        }
	}
}