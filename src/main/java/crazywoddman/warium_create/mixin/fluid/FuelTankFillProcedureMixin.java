package crazywoddman.warium_create.mixin.fluid;

import net.mcreator.crustychunks.init.CrustyChunksModItems;
import net.mcreator.crustychunks.procedures.FuelTankFillProcedure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = FuelTankFillProcedure.class, remap = false)
public class FuelTankFillProcedureMixin {

    private static Entity currentEntity;

    @Redirect(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/registries/RegistryObject;get()Ljava/lang/Object;"
        )
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
        
        if (entity instanceof LivingEntity && registryObject == CrustyChunksModItems.DIESEL_BUCKET) {
            LivingEntity livingEntity = (LivingEntity)entity;

            if (getFuelTypeFromItem(entity) != "Unknown")
                return livingEntity.getMainHandItem().getItem();
        }

        return registryObject.get();
    }

    @ModifyArg(
        method = "lambda$execute$0",
        at = @At(
            value = "INVOKE", 
            target = "Lnet/minecraftforge/fluids/FluidStack;<init>(Lnet/minecraft/world/level/material/Fluid;I)V"
        )
    )
    private static Fluid modifyDieselFluid(Fluid fluid) {
        if (getFuelTypeFromItem(currentEntity) != "Unknown")
            if (currentEntity instanceof LivingEntity) {
                
                LivingEntity livingEntity = (LivingEntity)currentEntity;
                IFluidHandlerItem handler = livingEntity.getMainHandItem().getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);

                if (handler != null) {
                    Fluid fluidFromBucket = handler.getFluidInTank(0).getFluid();

                    if (fluidFromBucket != Fluids.EMPTY)
                        return fluidFromBucket;
                }
            }
        return fluid;
    }

    @ModifyConstant(
        method = "execute",
        constant = @Constant(stringValue = "Diesel")
    )
    private static String setType(String value, LevelAccessor world, double x, double y, double z, Entity entity) {
        return getFuelTypeFromItem(entity);
    }

    private static String getFuelTypeFromItem(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            ItemStack stack = livingEntity.getMainHandItem();
            String path = ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath();
            
            if (path.equals("kerosene_bucket") || stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "buckets/kerosene"))))
                return "Kerosene";
            
            if (path.equals("diesel_bucket") || stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "buckets/diesel"))))
                return "Diesel";

            if (path.equals("oil_bucket") || stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "buckets/heavy_oil"))))
                return "Oil";

            if (path.equals("gasoline_bucket") || stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "buckets/gasoline"))))
                return "Petrolium";
        }
        
        return "Unknown";
    }
}