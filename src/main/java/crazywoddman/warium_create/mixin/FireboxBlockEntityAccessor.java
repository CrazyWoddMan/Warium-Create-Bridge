package crazywoddman.warium_create.mixin;

import net.mcreator.crustychunks.block.entity.FireboxBlockEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(remap = false, value = FireboxBlockEntity.class)
public interface FireboxBlockEntityAccessor {
    @Accessor("stacks")
    NonNullList<ItemStack> getStacks();
}