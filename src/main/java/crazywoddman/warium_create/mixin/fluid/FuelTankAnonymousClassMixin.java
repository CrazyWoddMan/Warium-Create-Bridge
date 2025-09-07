package crazywoddman.warium_create.mixin.fluid;

import net.minecraft.world.level.material.Fluid;
import crazywoddman.warium_create.util.FluidTransferContext;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(
    targets = {
        "net.mcreator.crustychunks.procedures.FuelTankModuleOnTickUpdateProcedure$9",
        "net.mcreator.crustychunks.procedures.FuelTankInputTickProcedure$5",
        "net.mcreator.crustychunks.procedures.FuelTankTickProcedure$12"
    },
    remap = false
)
public class FuelTankAnonymousClassMixin {

    @ModifyArg(
        method = "lambda$fillTankSimulate$0",
        at = @At(
            value = "INVOKE", 
            target = "Lnet/minecraftforge/fluids/FluidStack;<init>(Lnet/minecraft/world/level/material/Fluid;I)V"
        )
    )
    private static Fluid modifyKeroseneFluid(Fluid kerosene) {
        Fluid realFluid = FluidTransferContext.getFluid();
        return realFluid != null ? realFluid : kerosene;
    }
}