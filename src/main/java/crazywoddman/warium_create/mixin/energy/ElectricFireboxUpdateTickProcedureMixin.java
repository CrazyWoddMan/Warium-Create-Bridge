package crazywoddman.warium_create.mixin.energy;

import net.mcreator.crustychunks.procedures.ElectricFireboxUpdateTickProcedure;
import net.mcreator.crustychunks.block.entity.ElectricFireboxBlockEntity;
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

@Mixin(ElectricFireboxUpdateTickProcedure.class)
public class ElectricFireboxUpdateTickProcedureMixin {

    @Inject(
        method = "execute",
        at = @At("HEAD"),
        remap = false
    )
    private static void warium$setHeatLevel(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        BlockPos pos = BlockPos.containing(x, y, z);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ElectricFireboxBlockEntity) {
            double energy = blockEntity.getPersistentData().getDouble("Energy");
            BlockState state = world.getBlockState(pos);
            if (state.hasProperty(BlazeBurnerBlock.HEAT_LEVEL)) {
                if (energy > 0) {
                    if (state.getValue(BlazeBurnerBlock.HEAT_LEVEL) != Config.SERVER.electricFireboxHeat.get()) {
                        world.setBlock(pos, state.setValue(BlazeBurnerBlock.HEAT_LEVEL, Config.SERVER.electricFireboxHeat.get()), 3);
                    }
                } else {
                    if (state.getValue(BlazeBurnerBlock.HEAT_LEVEL) == Config.SERVER.electricFireboxHeat.get()) {
                        world.setBlock(pos, state.setValue(BlazeBurnerBlock.HEAT_LEVEL, HeatLevel.NONE), 3);
                    }
                }
            }
        }
    }
    @Inject(
        method = "execute",
        at = @At("HEAD"),
        remap = false
    )
    private static void warium$fixEnergyDrain(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        BlockPos pos = BlockPos.containing(x, y, z);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            double energy = blockEntity.getPersistentData().getDouble("Energy");
            if (energy > 0.0) {
                double newEnergy = Math.max(0.0, energy - 20.0);
                blockEntity.getPersistentData().putDouble("Energy", newEnergy);
            }
        }
    }
}