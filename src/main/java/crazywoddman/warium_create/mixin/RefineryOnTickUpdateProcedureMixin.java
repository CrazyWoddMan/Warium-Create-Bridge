package crazywoddman.warium_create.mixin;

import net.mcreator.crustychunks.init.CrustyChunksModItems;
import net.mcreator.crustychunks.procedures.RefineryOnTickUpdateProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RefineryOnTickUpdateProcedure.class)
public class RefineryOnTickUpdateProcedureMixin {
    private static final ThreadLocal<Item> previousItem = new ThreadLocal<>();

    @Redirect(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"
        )
    )
    private static Item redirectGetItem(ItemStack stack) {
        TagKey<Item> tag = TagKey.create(Registries.ITEM, ResourceLocation.parse("warium_create:refinery_input"));
        if (stack.is(tag))
            return CrustyChunksModItems.SHALE_OIL.get();
        return stack.getItem();
    }

    @Inject(
        method = "execute",
        at = @At("HEAD"),
        remap = false
    )
    private static void beforeExecute(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        world
        .getBlockEntity(BlockPos.containing(x, y, z))
        .getCapability(ForgeCapabilities.ITEM_HANDLER, null)
        .ifPresent(cap -> {
            if (cap instanceof IItemHandlerModifiable modifiable) {
                ItemStack stack = modifiable.getStackInSlot(0);
                previousItem.set(stack.getItem());
            }
        });
    }

    @Inject(
        method = "execute",
        at = @At("TAIL"),
        remap = false
    )
    private static void afterExecute(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        world
        .getBlockEntity(BlockPos.containing(x, y, z))
        .getCapability(ForgeCapabilities.ITEM_HANDLER, null)
        .ifPresent(cap -> {
            if (cap instanceof IItemHandlerModifiable modifiable) {
                ItemStack stack = modifiable.getStackInSlot(0);
                Item prev = previousItem.get();
                if (stack.isEmpty() && prev instanceof BucketItem && prev != Items.BUCKET) {
                    modifiable.setStackInSlot(0, new ItemStack(Items.BUCKET));
                }
            }
            });
        previousItem.remove();
    }
}