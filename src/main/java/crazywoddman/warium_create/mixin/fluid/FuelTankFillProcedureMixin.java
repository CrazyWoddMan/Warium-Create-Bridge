package crazywoddman.warium_create.mixin.fluid;

import net.mcreator.crustychunks.procedures.FuelTankFillProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FuelTankFillProcedure.class)
public class FuelTankFillProcedureMixin {

    @Inject(
        method = "execute",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private static void acceptAnyFuel(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        if (entity instanceof LivingEntity living) {
            ItemStack held = living.getMainHandItem();
            held.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(handler -> {
                FluidStack fluidStack = handler.getFluidInTank(0);
                final String[] fuelType = {null};
                if (
                    ForgeRegistries.FLUIDS.getKey(fluidStack.getFluid()).getPath().equals("kerosene") ||
                    fluidStack.getFluid().is(FluidTags.create(new ResourceLocation("forge", "kerosene")))
                ) fuelType[0] = "Kerosene";
                if (
                    ForgeRegistries.FLUIDS.getKey(fluidStack.getFluid()).getPath().equals("diesel") ||
                    fluidStack.getFluid().is(FluidTags.create(new ResourceLocation("forge", "diesel")))
                ) fuelType[0] = "Diesel";
                if (
                    ForgeRegistries.FLUIDS.getKey(fluidStack.getFluid()).getPath().equals("oil") ||
                    fluidStack.getFluid().is(FluidTags.create(new ResourceLocation("forge", "heavy_oil")))
                ) fuelType[0] = "Oil";
                if (
                    ForgeRegistries.FLUIDS.getKey(fluidStack.getFluid()).getPath().equals("petrolium") ||
                    ForgeRegistries.FLUIDS.getKey(fluidStack.getFluid()).getPath().equals("gasoline") ||
                    fluidStack.getFluid().is(FluidTags.create(new ResourceLocation("forge", "gasoline")))
                ) fuelType[0] = "Petrolium";
                if (!fluidStack.isEmpty() && (fuelType != null)) {
                    BlockEntity blockEntity = world.getBlockEntity(BlockPos.containing(x, y, z));
                    if (blockEntity != null) {
                        blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, null).ifPresent(tank -> {
                            if (tank.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE) >= 1000) {
                                tank.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                                blockEntity.getPersistentData().putString("FuelType", fuelType[0]);
                                BlockState state = world.getBlockState(BlockPos.containing(x, y, z));
                                if (world instanceof Level level) {
                                    level.sendBlockUpdated(blockEntity.getBlockPos(), state, state, 3);
                                }

                                if (!living.level().isClientSide() && (!(living instanceof Player) || !((Player) living).isCreative())) {
                                    ItemStack empty = new ItemStack(Items.BUCKET);
                                    living.setItemInHand(InteractionHand.MAIN_HAND, empty);
                                    if (living instanceof Player player) {
                                        player.getInventory().setChanged();
                                    }
                                }

                                if (world instanceof Level level) {
                                    SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.bucket.fill"));
                                    if (sound != null) {
                                        level.playSound(null, blockEntity.getBlockPos(), sound, SoundSource.NEUTRAL, 1.0F, 1.0F);
                                    }
                                }
                                
                                if (entity instanceof Player player && !player.level().isClientSide()) {
                                    player.displayClientMessage(
                                        Component.literal(fuelType[0] + " added!"),
                                        true
                                    );
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