package crazywoddman.warium_create.mixin;

import net.mcreator.valkyrienwarium.block.entity.HeliRotorTileEntity;
import net.mcreator.valkyrienwarium.procedures.HeliRotorScriptProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeliRotorScriptProcedure.class)
public class HeliRotorScriptProcedureMixin {

    @Inject(
        method = "execute",
        at = @At("HEAD"),
        remap = false
    )
    private static void controlledDirection(LevelAccessor world, double x, double y, double z, BlockState blockstate, CallbackInfo ci) {
        if (!(world instanceof Level level) || level.isClientSide)
            return;
        BlockPos pos = BlockPos.containing(x, y, z);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        BlockPos controlPos;
        int throttle = 0;
        double setDirection = level.hasNeighborSignal(pos) ? -1.0 : 1.0;
        if (!(blockEntity instanceof HeliRotorTileEntity tileEntity))
            return;
        if (
            tileEntity.getPersistentData().contains("ControlX") &&
            tileEntity.getPersistentData().contains("ControlY") &&
            tileEntity.getPersistentData().contains("ControlZ")
        ) {
            int controlX = tileEntity.getPersistentData().getInt("ControlX");
            int controlY = tileEntity.getPersistentData().getInt("ControlY");
            int controlZ = tileEntity.getPersistentData().getInt("ControlZ");
            controlPos = new BlockPos(controlX, controlY, controlZ);
            BlockEntity controlNode = tileEntity.getLevel().getBlockEntity(controlPos);
            if (controlNode != null && controlNode.getPersistentData().contains("Throttle")) {
                throttle = controlNode.getPersistentData().getInt("Throttle");
                if (throttle < 0)
                    setDirection = -1.0;
            }
        }
        if (tileEntity.getPersistentData().getDouble("Direction") != setDirection) {
            tileEntity.getPersistentData().putDouble("Direction", setDirection);
            tileEntity.setChanged();
        }
    }
}