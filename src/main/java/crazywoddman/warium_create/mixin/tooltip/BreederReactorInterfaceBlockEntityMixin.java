package crazywoddman.warium_create.mixin.tooltip;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;

import crazywoddman.warium_create.util.WariumCreateTooltipHelper;
import net.mcreator.crustychunks.block.entity.BreederReactorInterfaceBlockEntity;
import net.mcreator.crustychunks.init.CrustyChunksModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;

import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(BreederReactorInterfaceBlockEntity.class)
public abstract class BreederReactorInterfaceBlockEntityMixin implements IHaveGoggleInformation {

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {

        boolean validItem = false;
        int itemsAmount = 0;
        BlockEntity blockEntity = (BlockEntity)(Object)this;

        if (blockEntity instanceof Container container) {

            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack stack = container.getItem(i);

                if (!stack.isEmpty()) {
                    validItem = stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("warium_create", "reactor_acceptable")));
                    itemsAmount = stack.getCount();
                    break;
                }
            }
        }

        if (!validItem)
            return false;

        Level level = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();

        boolean structureComplete =
                level != null
                && level.getBlockState(pos.below(2)).getBlock() == CrustyChunksModBlocks.BREEDER_REACTOR_PORT.get()
                && level.getBlockState(pos.below(1)).getBlock() == CrustyChunksModBlocks.BREEDER_REACTOR_CORE.get()
                && getReady(level, pos.below(1).north(2))
                && getReady(level, pos.below(1).south(2))
                && getReady(level, pos.below(1).west(2))
                && getReady(level, pos.below(1).east(2))

                && getReady(level, pos.north(2))
                && getReady(level, pos.south(2))
                && getReady(level, pos.west(2))
                && getReady(level, pos.east(2));

        if (!structureComplete)
            return false;

        int enrichmentTime = blockEntity.getPersistentData().getInt("enrichmentTimeGamerule");
        int timeLeft = (enrichmentTime * 2 - blockEntity.getPersistentData().getInt("T")) / 4;
        int timeLeftover = enrichmentTime / 2 * (itemsAmount - 1);

        tooltip.add(Component
            .literal("    ")
            .append(Component.translatable("block.crusty_chunks.breeder_reactor_interface"))
            .append(Component.literal(":"))
            .withStyle(ChatFormatting.AQUA)
        );

        tooltip.add(Component
            .literal("    ")
            .append(Component.translatable("warium_create.breeder_reactor.processtime"))
            .append(Component.literal(": "))
            .withStyle(ChatFormatting.GRAY)
        );

        tooltip.add(Component
            .literal("     ")
            .append(Component.literal(WariumCreateTooltipHelper.formatSeconds(timeLeft)).withStyle(ChatFormatting.AQUA))
            .append(Component.literal(timeLeftover > 0 ? (" + " + WariumCreateTooltipHelper.formatSeconds(timeLeftover)) : "").withStyle(ChatFormatting.DARK_GRAY))
        );

        return true;
    }

    private static boolean getReady(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && blockEntity.getPersistentData().getBoolean("Ready");
    }
}