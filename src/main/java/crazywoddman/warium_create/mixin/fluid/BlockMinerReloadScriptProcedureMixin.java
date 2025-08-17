package crazywoddman.warium_create.mixin.fluid;

import net.mcreator.crustychunks.procedures.BlockMinerReloadScriptProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
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
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockMinerReloadScriptProcedure.class)
public class BlockMinerReloadScriptProcedureMixin {
    
    @Inject(
        method = "execute",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private static void acceptAnyFuel(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        if (entity instanceof LivingEntity living) {
            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack held = living.getItemInHand(hand);
                held.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(handler -> {
                    FluidStack fluidStack = handler.getFluidInTank(0);
                    if (!fluidStack.isEmpty()) {
                        BlockEntity blockEntity = world.getBlockEntity(BlockPos.containing(x, y, z));
                        if (blockEntity != null) {
                            blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, null).ifPresent(tank -> {
                                int filled = tank.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                                if (filled > 0) {
                                    if (!living.level().isClientSide() && (!(living instanceof Player) || !((Player) living).isCreative())) {
                                        living.setItemInHand(hand, new ItemStack(Items.BUCKET));
                                        if (living instanceof Player player) {
                                            player.getInventory().setChanged();
                                        }
                                    }
                                    if (world instanceof Level _level) {
                                        BlockPos pos = BlockPos.containing(x, y, z);
                                        double fuel = blockEntity.getPersistentData().getDouble("Fuel");
                                        float pitch = (float)(0.5 + fuel / 2000.0);
                                        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.bucket.fill"));
                                        if (!_level.isClientSide()) {
                                            _level.playSound(null, pos, sound, SoundSource.NEUTRAL, 2.0F, pitch);
                                        } else {
                                            _level.playLocalSound(x, y, z, sound, SoundSource.NEUTRAL, 2.0F, pitch, false);
                                        }
                                    }
                                    if (!world.isClientSide()) {
                                        BlockPos pos = BlockPos.containing(x, y, z);
                                        BlockState state = world.getBlockState(pos);
                                        if (world instanceof Level _levelx) {
                                            _levelx.sendBlockUpdated(pos, state, state, 3);
                                        }
                                    }
                                    ci.cancel();
                                }
                            });
                        }
                    }
                });
            }
        }
    }
}