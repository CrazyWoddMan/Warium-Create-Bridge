package crazywoddman.warium_create.mixin.tooltip;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import net.mcreator.crustychunks.block.entity.AutocannonDrumBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(AutocannonDrumBlockEntity.class)
public abstract class AutocannonDrumBlockEntityMixin implements IHaveGoggleInformation {

    private static final String spacing = "    ";

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        BlockEntity blockEntity = (BlockEntity)(Object)this;

        int ammo = blockEntity.getPersistentData().getInt("Ammo");
        String ammoType = blockEntity.getPersistentData().getString("AmmoType");

        if (ammo <= 0) {
            return false;
        }

        tooltip.add(Component
            .literal(spacing)
            .append(Component.translatable("block.crusty_chunks.autocannon_drum"))
            .append(Component.literal(":"))
            .withStyle(ChatFormatting.GRAY)
        );
        tooltip.add(Component.literal(spacing)
            .append(Component.literal(" "))
            .append(Component.literal(String.valueOf(ammo)))
            .append(Component.literal(" "))
            .append(Component.translatable(ammoType.equals("Small") ? "item.crusty_chunks.huge_bullet" : "item.crusty_chunks.small_shell"))
            .withStyle(ChatFormatting.AQUA)
        );

        return true;
    }
}