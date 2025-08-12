package crazywoddman.warium_create.item;

import net.mcreator.crustychunks.procedures.PlayerRadiationDoseProcedure;
import net.mcreator.crustychunks.procedures.Rad05TickItemProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class YellowcakeItem extends Item {
    public YellowcakeItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        entity.getPersistentData().putDouble("Radiation", entity.getPersistentData().getDouble("Radiation") + 500.0);
        PlayerRadiationDoseProcedure.execute(world, entity);
        return super.finishUsingItem(stack, world, entity);
    }

    @Override
    public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
      super.inventoryTick(itemstack, world, entity, slot, selected);
      Rad05TickItemProcedure.execute(entity, itemstack);
   }
}