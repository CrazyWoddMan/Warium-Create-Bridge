package crazywoddman.warium_create.mixin;

import net.mcreator.crustychunks.procedures.FireboxUpdateTickProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.core.NonNullList;

@Mixin(FireboxUpdateTickProcedure.class)
public class FireboxUpdateTickProcedureMixin {

    @Inject(
        method = "execute",
        at = @At("HEAD"),
        remap = false
    )
    private static void setHeatLevel(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        BlockPos pos = BlockPos.containing(x, y, z);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        BlockState state = world.getBlockState(pos);

        if (blockEntity != null && state != null && state.hasProperty(BlazeBurnerBlock.HEAT_LEVEL)) {
            NonNullList<ItemStack> stacks = ((FireboxBlockEntityAccessor) blockEntity).getStacks();
            boolean hasCoal = false;

            for (ItemStack stack : stacks) {
                if (!stack.isEmpty() && (stack.getItem() == Items.COAL || stack.getItem() == Items.COAL)) {
                    hasCoal = true;
                    break;
                }
            }

            HeatLevel heat = Config.SERVER.fireboxHeat.get();
            HeatLevel getheat = state.getValue(BlazeBurnerBlock.HEAT_LEVEL);

            if (hasCoal) {
                if (getheat != heat)
                    world.setBlock(pos, state.setValue(BlazeBurnerBlock.HEAT_LEVEL, heat), 3);
            } else if (getheat == heat)
                world.setBlock(pos, state.setValue(BlazeBurnerBlock.HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.NONE), 3);
        }
    }
}