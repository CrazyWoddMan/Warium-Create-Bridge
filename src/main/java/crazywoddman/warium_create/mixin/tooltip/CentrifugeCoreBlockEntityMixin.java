package crazywoddman.warium_create.mixin.tooltip;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;

import crazywoddman.warium_create.util.WariumCreateTooltipHelper;
import net.mcreator.crustychunks.block.entity.CentrifugeCoreBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

@Mixin(CentrifugeCoreBlockEntity.class)
public abstract class CentrifugeCoreBlockEntityMixin implements IHaveGoggleInformation {

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {

        boolean validItem = false;
        int itemsAmount = 0;
        BlockEntity blockEntity = (BlockEntity)(Object)this;

        if (blockEntity instanceof Container container) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack stack = container.getItem(i);
                if (!stack.isEmpty()) {
                    validItem = stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("warium_create", "enrichable")));
                    itemsAmount = stack.getCount();
                    break;
                }
            }
        }

        Level level = blockEntity.getLevel();
        byte isReady = 0;

        if (level != null) {
            BlockEntity aboveEntity = level.getBlockEntity(blockEntity.getBlockPos().above());
            if (aboveEntity != null)
                isReady = aboveEntity.getPersistentData().getByte("Ready");
        }

        if (isReady != 1 || !validItem)
            return false;

        CompoundTag data = blockEntity.getPersistentData();
        int enrichmentTime = data.getInt("enrichmentTimeGamerule");
        int timeLeft = (enrichmentTime - data.getInt("T")) / 4;
        int timeLeftover = enrichmentTime / 4 * (itemsAmount - 1);
        
        tooltip.add(Component.literal("    ")
            .append(Component.translatable("block.crusty_chunks.centrifuge_core"))
            .append(Component.literal(":"))
            .withStyle(ChatFormatting.AQUA)
        );

        tooltip.add(Component
            .literal("    ")
            .append(Component.translatable("gamerule.enrichmentTime"))
            .append(Component.literal(":"))
            .withStyle(ChatFormatting.GRAY)
        );

        tooltip.add(Component.literal("     ")
            .append(Component.literal(WariumCreateTooltipHelper.formatSeconds(timeLeft)))
            .append(Component.literal(timeLeftover > 0 ? (" + " + WariumCreateTooltipHelper.formatSeconds(timeLeftover)) : "").withStyle(ChatFormatting.DARK_GRAY))
            .withStyle(ChatFormatting.AQUA)
        );

        return true;
    }
}