package crazywoddman.warium_create.mixin.fluid;

import net.mcreator.crustychunks.init.CrustyChunksModItems;
import net.mcreator.crustychunks.procedures.FuelTankFillProcedure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = FuelTankFillProcedure.class)
public class FuelTankFillProcedureMixin {

    private static Entity currentEntity;

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
        currentEntity = entity;
        
        if (entity != null && entity instanceof LivingEntity livingEntity && (registryObject.equals(CrustyChunksModItems.DIESEL_BUCKET) || registryObject.equals(CrustyChunksModItems.KEROSENE_BUCKET))) {
            ItemStack stack = livingEntity.getMainHandItem();

            if (getFuelType(stack).equals(registryObject.equals(CrustyChunksModItems.DIESEL_BUCKET) ? "Diesel" : "Kerosene"))
                return stack.getItem();
        }

        return registryObject.get();
    }

    @ModifyArg(
        method = {
            "lambda$execute$0",
            "lambda$execute$1"
        },
        at = @At(
            value = "INVOKE", 
            target = "Lnet/minecraftforge/fluids/FluidStack;<init>(Lnet/minecraft/world/level/material/Fluid;I)V"
        ),
        remap = false
    )
    private static Fluid modifyDieselFluid(Fluid fluid) {
        if (currentEntity != null && currentEntity instanceof LivingEntity livingEntity) {
            ItemStack stack = livingEntity.getMainHandItem();

            if (!getFuelType(stack).isEmpty()) {
                IFluidHandlerItem handler = livingEntity
                    .getMainHandItem()
                    .getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM)
                    .orElse(null);

                if (handler != null) {
                    Fluid fluidFromBucket = handler.getFluidInTank(0).getFluid();

                    if (fluidFromBucket != Fluids.EMPTY)
                        return fluidFromBucket;
                }
            }
        }

        return fluid;
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

    private static String getFuelType(ItemStack stack) {
        String path = ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath();

        if (stack != null && !path.isEmpty()) {
            
            if (path.equals("kerosene_bucket") || stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "buckets/kerosene"))))
                return "Kerosene";
            
            else if (path.equals("diesel_bucket") || stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "buckets/diesel"))))
                return "Diesel";
        }
        
        return "";
    }
}