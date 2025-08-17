package crazywoddman.warium_create.mixin.fluid;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin {
    @Inject(
        method = "initCapabilities",
        at = @At("RETURN"),
        cancellable = true,
        remap = false
    )
    private void addFluidHandler(ItemStack stack, CompoundTag nbt, CallbackInfoReturnable<ICapabilityProvider> cir) {
        // Только для ведер Warium (по namespace)
        String regName = "";
        if (stack != null && stack.getItem() != null) {
            var key = BuiltInRegistries.ITEM.getKey(stack.getItem());
            if (key != null) regName = key.getNamespace();
        }
        if (regName.equals("crusty_chunks")) {
            cir.setReturnValue(new ICapabilityProvider() {
                private final LazyOptional<IFluidHandlerItem> handler = LazyOptional.of(() -> new FluidBucketWrapper(stack));
                @Override
                public <T> LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap, net.minecraft.core.Direction side) {
                    if (cap == ForgeCapabilities.FLUID_HANDLER_ITEM) {
                        return handler.cast();
                    }
                    return LazyOptional.empty();
                }
            });
        }
    }
}