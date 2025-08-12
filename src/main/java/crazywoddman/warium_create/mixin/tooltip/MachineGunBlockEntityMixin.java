package crazywoddman.warium_create.mixin.tooltip;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import net.mcreator.crustychunks.block.entity.MachineGunBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(MachineGunBlockEntity.class)
public abstract class MachineGunBlockEntityMixin implements IHaveGoggleInformation {

    private static final String spacing = "    ";

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        BlockEntity blockEntity = (BlockEntity)(Object)this;

        if (blockEntity.getPersistentData().getByte("Loaded") == 0) {
            return false;
        }

        tooltip.add(Component
            .literal(spacing)
            .append(Component.translatable("item.crusty_chunks.machine_gun_box"))
            .append(Component.literal(":"))
            .withStyle(ChatFormatting.GRAY)
        );
        tooltip.add(Component.literal(spacing)
            .append(Component.literal(" "))
            .append(Component.literal(String.valueOf(blockEntity.getPersistentData().getInt("Ammo"))))
            .append(Component.literal(" "))
            .append(Component.translatable("item.crusty_chunks.large_bullet"))
            .withStyle(ChatFormatting.AQUA)
        );

        return true;
    }
}