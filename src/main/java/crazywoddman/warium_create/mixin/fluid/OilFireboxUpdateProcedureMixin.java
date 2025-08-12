package crazywoddman.warium_create.mixin.fluid;

import net.mcreator.crustychunks.procedures.OilFireboxUpdateProcedure;
import net.mcreator.crustychunks.block.entity.OilFireboxBlockEntity;
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

@Mixin(OilFireboxUpdateProcedure.class)
public class OilFireboxUpdateProcedureMixin {

    @Inject(
        method = "execute",
        at = @At("HEAD"),
        remap = false
    )
    private static void warium$setHeatLevel(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        BlockPos pos = BlockPos.containing(x, y, z);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof OilFireboxBlockEntity) {
            double fuel = blockEntity.getPersistentData().getDouble("Fuel");
            BlockState state = world.getBlockState(pos);
            if (state.hasProperty(BlazeBurnerBlock.HEAT_LEVEL)) {
                if (fuel > 0) {
                    if (state.getValue(BlazeBurnerBlock.HEAT_LEVEL) != Config.SERVER.oilFireboxHeat.get()) {
                        world.setBlock(pos, state.setValue(BlazeBurnerBlock.HEAT_LEVEL, Config.SERVER.oilFireboxHeat.get()), 3);
                    }
                } else {
                    if (state.getValue(BlazeBurnerBlock.HEAT_LEVEL) == Config.SERVER.oilFireboxHeat.get()) {
                        world.setBlock(pos, state.setValue(BlazeBurnerBlock.HEAT_LEVEL, HeatLevel.NONE), 3);
                    }
                }
            }
        }
    }
}