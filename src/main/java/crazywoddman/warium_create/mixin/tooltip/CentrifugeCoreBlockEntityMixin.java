package crazywoddman.warium_create.mixin.tooltip;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import net.mcreator.crustychunks.block.entity.CentrifugeCoreBlockEntity;
import net.minecraft.ChatFormatting;
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
    private static final String spacing = "    ";

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {

        boolean validItem = false;
        BlockEntity blockEntity = (BlockEntity)(Object)this;

        if (blockEntity instanceof Container container) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack stack = container.getItem(i);
                if (!stack.isEmpty()) {
                    validItem = stack.is(ItemTags.create(new ResourceLocation("warium_create", "enrichable")));
                    break;
                }
            }
        }

        Level level = blockEntity.getLevel();
        byte isReady = 0;

        if (level != null) {
            BlockEntity aboveEntity = level.getBlockEntity(blockEntity.getBlockPos().above());
            if (aboveEntity != null) {
                isReady = aboveEntity.getPersistentData().getByte("Ready");
            }
        }

        if (isReady != 1 || !validItem)
            return false;

        int enrichmentTime = blockEntity.getPersistentData().getInt("enrichmentTimeGamerule");
        
        tooltip.add(Component.literal(spacing)
            .append(Component.translatable("block.crusty_chunks.centrifuge_core"))
            .append(Component.literal(":"))
            .withStyle(ChatFormatting.AQUA)
        );

        tooltip.add(Component
            .literal(spacing)
            .append(Component.translatable("gamerule.enrichmentTime"))
            .append(Component.literal(":"))
            .withStyle(ChatFormatting.GRAY)
        );

        tooltip.add(Component.literal(spacing)
            .append(Component.literal(" "))
            .append(Component.literal(formatSeconds((enrichmentTime - blockEntity.getPersistentData().getInt("T")) / 4)))
            .withStyle(ChatFormatting.AQUA)
        );

        return true;
    }

    private static String formatSeconds(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}