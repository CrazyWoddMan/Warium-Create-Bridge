package crazywoddman.warium_create.mixin.energy;

import net.mcreator.crustychunks.procedures.ElectricFireboxUpdateTickProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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

@Mixin(remap = false, value = ElectricFireboxUpdateTickProcedure.class)
public class ElectricFireboxUpdateTickProcedureMixin {

    @Inject(
        method = "execute",
        at = @At("HEAD")
    )
    private static void setHeatLevel(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        BlockPos pos = BlockPos.containing(x, y, z);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        BlockState state = world.getBlockState(pos);

        if (blockEntity != null && state != null && state.hasProperty(BlazeBurnerBlock.HEAT_LEVEL)) {
            double energy = blockEntity.getPersistentData().getDouble("Energy");
            HeatLevel heat = Config.SERVER.electricFireboxHeat.get();
            HeatLevel getheat = state.getValue(BlazeBurnerBlock.HEAT_LEVEL);
            
            if (energy > 0) {
                if (getheat != heat)
                    world.setBlock(pos, state.setValue(BlazeBurnerBlock.HEAT_LEVEL, heat), 3);
            } else if (getheat == heat)
                world.setBlock(pos, state.setValue(BlazeBurnerBlock.HEAT_LEVEL, HeatLevel.NONE), 3);
        }
    }
    @Inject(
        method = "execute",
        at = @At("HEAD")
    )
    private static void fixEnergyDrain(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        BlockPos pos = BlockPos.containing(x, y, z);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        CompoundTag data = blockEntity.getPersistentData();
        if (blockEntity != null && data.contains("Energy")) {
            double energy = data.getDouble("Energy");
            if (energy > 0)
                data.putDouble(
                    "Energy",
                    Math.max(0.0, energy - 20.0)
                );
        }
    }
}