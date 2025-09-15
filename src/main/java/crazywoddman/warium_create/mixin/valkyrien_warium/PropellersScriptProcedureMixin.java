package crazywoddman.warium_create.mixin.valkyrien_warium;

import net.mcreator.valkyrienwarium.block.entity.BoatPropellerTileEntity;
import net.mcreator.valkyrienwarium.block.entity.VehicleControlNodeBlockEntity;
import net.mcreator.valkyrienwarium.procedures.HeliRotorScriptProcedure;
import net.mcreator.valkyrienwarium.procedures.PropellerScriptProcedure;
import net.mcreator.valkyrienwarium.procedures.WaterPropScriptProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
    remap = false,
    value = {
        WaterPropScriptProcedure.class,
        PropellerScriptProcedure.class,
        HeliRotorScriptProcedure.class
    }
)
public class PropellersScriptProcedureMixin {

    @Inject(
        method = "execute",
        at = @At("HEAD")
    )
    private static void controlledDirection(LevelAccessor world, double x, double y, double z, BlockState blockstate, CallbackInfo ci) {

        if (!(world instanceof Level level) || level.isClientSide)
            return;

        BlockPos pos = BlockPos.containing(x, y, z);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        CompoundTag data = blockEntity.getPersistentData();
        int setDirection = blockEntity instanceof BoatPropellerTileEntity ? -1 : 1;

        if (level.hasNeighborSignal(pos))
            setDirection *= -1;
        else if (blockEntity != null && data.contains("ControlX")) {
            BlockEntity controlNode = blockEntity.getLevel().getBlockEntity(
                BlockPos.containing(
                    data.getInt("ControlX"),
                    data.getInt("ControlY"),
                    data.getInt("ControlZ")
                )
            );

            if (controlNode != null && controlNode instanceof VehicleControlNodeBlockEntity && controlNode.getPersistentData().getInt("Throttle") < 0)
                setDirection *= -1;
        }

        if (data.getInt("Direction") != setDirection) {
            data.putDouble("Direction", setDirection);
            blockEntity.setChanged();
        }
    }
}