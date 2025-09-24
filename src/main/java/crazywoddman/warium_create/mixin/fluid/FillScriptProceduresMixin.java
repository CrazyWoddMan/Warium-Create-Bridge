package crazywoddman.warium_create.mixin.fluid;

import net.mcreator.crustychunks.block.entity.BlockMinerBlockEntity;
import net.mcreator.crustychunks.block.entity.OilFireboxBlockEntity;
import net.mcreator.crustychunks.procedures.BlockMinerReloadScriptProcedure;
import net.mcreator.crustychunks.procedures.KeroseneFillScriptProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
    value = {
        KeroseneFillScriptProcedure.class,
        BlockMinerReloadScriptProcedure.class
    }
)
public class FillScriptProceduresMixin {

    @Redirect(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/registries/RegistryObject;get()Ljava/lang/Object;"
        ),
        remap = false
    )
    private static Object redirectBucket(
        RegistryObject<?> registryObject, 
        LevelAccessor world, 
        double x, 
        double y, 
        double z, 
        Entity entity
    ) {
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            ItemStack stack = livingEntity.getMainHandItem();
            BlockEntity blockEntity = world.getBlockEntity(BlockPos.containing(x, y, z));

            if (stack != null && blockEntity != null) {
                String fuelName = "";

                if (blockEntity instanceof OilFireboxBlockEntity)
                    fuelName = "kerosene";
                else if (blockEntity instanceof BlockMinerBlockEntity)
                    fuelName = "diesel";

                if (!fuelName.isEmpty()) {
                    boolean isAcceptableId = ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath().equals(fuelName + "_bucket");
                    boolean isAcceptableTag = stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "buckets/" + fuelName)));

                    if (isAcceptableTag || isAcceptableId)
                        return stack.getItem();
                }
            }
        }

        return registryObject.get();
    }

    @Redirect(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;setItemInHand(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;)V"
        )
    )
    private static void redirectSetItemInHand(LivingEntity entity, InteractionHand hand, ItemStack stack) {
        if (entity instanceof Player player && !player.isCreative())
            entity.setItemInHand(hand, stack);
    }
}