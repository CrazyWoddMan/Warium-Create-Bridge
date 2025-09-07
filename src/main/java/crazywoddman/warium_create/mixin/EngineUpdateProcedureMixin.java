package crazywoddman.warium_create.mixin;

import crazywoddman.warium_create.Config;
import net.mcreator.crustychunks.procedures.EngineUpdateProcedure;
import net.mcreator.valkyrienwarium.block.entity.VehicleControlNodeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = EngineUpdateProcedure.class, remap = false)
public class EngineUpdateProcedureMixin {

    @ModifyConstant(
        method = "execute",
        constant = @Constant(doubleValue = 50.0, ordinal = 0)
    )
    private static double modifyPower(double value, LevelAccessor world, double x, double y, double z) {
        return Config.SERVER.enginePower.get();
    }

    @ModifyConstant(
        method = "execute",
        constant = @Constant(doubleValue = 5.0, ordinal = 0)
    )
    private static double modifyPowerForPositiveThrottle(double value, LevelAccessor world, double x, double y, double z) {
        BlockEntity blockEntity = world.getBlockEntity(BlockPos.containing(x, y, z));

        if (blockEntity.getPersistentData().getString("Key").equals("Throttle-"))
            return 0.0;

        return Config.SERVER.enginePower.get() / Config.SERVER.maxThrottle.get();
    }

    @ModifyConstant(
        method = "execute",
        constant = @Constant(doubleValue = 0.0, ordinal = 7)
    )
    private static double modifyPowerForNegativeThrottle(double power, LevelAccessor world, double x, double y, double z) {
        BlockEntity blockEntity = world.getBlockEntity(BlockPos.containing(x, y, z));
        CompoundTag data = blockEntity.getPersistentData();

        if (blockEntity != null && data.contains("ControlX")) {
            BlockEntity controlNode = world.getBlockEntity(
                BlockPos.containing(
                    data.getDouble("ControlX"),
                    data.getDouble("ControlY"),
                    data.getDouble("ControlZ")
                )
            );

            if (controlNode != null && controlNode instanceof VehicleControlNodeBlockEntity) {
                double throttle = controlNode.getPersistentData().getDouble("Throttle");

                if (throttle < 0.0 && !data.getString("Key").equals("Throttle+"))
                    power = Config.SERVER.enginePower.get() / Config.SERVER.maxThrottle.get() * Math.abs(throttle);
            }
        }
        
        return power;
    }
}