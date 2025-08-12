package crazywoddman.warium_create.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ItemTooltip extends BlockItem {
    public ItemTooltip(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        String descKey = this.getDescriptionId() + ".tooltip";
        tooltip.add(Component.translatable(descKey).withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, world, tooltip, flag);
    }
}