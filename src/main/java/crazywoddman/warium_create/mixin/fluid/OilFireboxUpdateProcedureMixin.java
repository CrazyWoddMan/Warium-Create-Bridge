package crazywoddman.warium_create.mixin.fluid;

import net.mcreator.crustychunks.procedures.OilFireboxUpdateProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;

import crazywoddman.warium_create.Config;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(remap = false, value = OilFireboxUpdateProcedure.class)
public class OilFireboxUpdateProcedureMixin {

    @Inject(
        method = "execute",
        at = @At("HEAD")
    )
    private static void setHeatLevel(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        BlockPos pos = BlockPos.containing(x, y, z);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        BlockState state = world.getBlockState(pos);

        if (blockEntity != null && state != null && state.hasProperty(BlazeBurnerBlock.HEAT_LEVEL)) {
            double fuel = blockEntity.getPersistentData().getDouble("Fuel");
            HeatLevel heat = Config.SERVER.oilFireboxHeat.get();
            HeatLevel getheat = state.getValue(BlazeBurnerBlock.HEAT_LEVEL);

            if (fuel > 0) {
                if (getheat != heat)
                    world.setBlock(pos, state.setValue(BlazeBurnerBlock.HEAT_LEVEL, heat), 3);
            } else if (getheat == heat)
                world.setBlock(pos, state.setValue(BlazeBurnerBlock.HEAT_LEVEL, HeatLevel.NONE), 3);
        }
    }
}