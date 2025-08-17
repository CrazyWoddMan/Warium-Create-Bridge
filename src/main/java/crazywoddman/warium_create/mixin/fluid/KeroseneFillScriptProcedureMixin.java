package crazywoddman.warium_create.mixin.fluid;

import net.mcreator.crustychunks.procedures.KeroseneFillScriptProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeroseneFillScriptProcedure.class)
public class KeroseneFillScriptProcedureMixin {

    @Inject(
        method = "execute",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private static void acceptAnyKerosene(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        if (entity instanceof LivingEntity living) {
            ItemStack held = living.getMainHandItem();
            held.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(handler -> {
                FluidStack fluidStack = handler.getFluidInTank(0);
                boolean isAcceptableId = ForgeRegistries.FLUIDS.getKey(fluidStack.getFluid()).getPath().equals("kerosene");
                boolean isAcceptableTag = fluidStack.getFluid().is(FluidTags.create(new ResourceLocation("forge", "kerosene")));
                if (!fluidStack.isEmpty() && !(isAcceptableId || isAcceptableTag)) {
                    BlockEntity blockEntity = world.getBlockEntity(BlockPos.containing(x, y, z));
                    if (blockEntity != null) {
                        double currentFuel = blockEntity.getPersistentData().getDouble("Fuel");
                        if (currentFuel <= 4000.0 && fluidStack.getAmount() >= 1000) {
                            blockEntity.getPersistentData().putDouble("Fuel", currentFuel + 1000.0);
                            BlockState state = world.getBlockState(blockEntity.getBlockPos());
                            if (world instanceof Level level) {
                                level.sendBlockUpdated(blockEntity.getBlockPos(), state, state, 3);
                            }

                            if (world instanceof Level level) {
                                SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.bucket.fill"));
                                if (sound != null) {
                                    level.playSound(null, blockEntity.getBlockPos(), sound, SoundSource.NEUTRAL, 2.0F, 1.0F);
                                }
                            }

                            if (!living.level().isClientSide() && (!(living instanceof Player) || !((Player) living).isCreative())) {
                                ItemStack empty = new ItemStack(Items.BUCKET);
                                living.setItemInHand(InteractionHand.MAIN_HAND, empty);
                                if (living instanceof Player player) {
                                    player.getInventory().setChanged();
                                }
                            }
                            ci.cancel();
                        }
                    }
                }
            });
        }
    }
}