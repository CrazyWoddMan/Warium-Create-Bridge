package crazywoddman.warium_create.mixin.tooltip;

import net.mcreator.crustychunks.procedures.CentrifugeCoreTickProcedure;
import net.mcreator.crustychunks.init.CrustyChunksModGameRules;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CentrifugeCoreTickProcedure.class)
public class CentrifugeCoreTickProcedureMixin {
    @Inject(
        method = "execute",
        at = @At("HEAD"),
        remap = false
    )
    private static void warium$cacheEnrichmentTime(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        if (!world.isClientSide()) {
            int enrichmentTime = world.getLevelData().getGameRules().getInt(CrustyChunksModGameRules.ENRICHMENT_TIME);
            BlockEntity be = world.getBlockEntity(net.minecraft.core.BlockPos.containing(x, y, z));
            if (be != null) {
                be.getPersistentData().putInt("enrichmentTimeGamerule", enrichmentTime);
            }
        }
    }
}