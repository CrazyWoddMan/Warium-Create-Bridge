package crazywoddman.warium_create.mixin;

import net.mcreator.valkyrienwarium.procedures.Throttle1PressProcedure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import crazywoddman.warium_create.Config;

import org.spongepowered.asm.mixin.injection.Constant;

@Mixin(Throttle1PressProcedure.class)
public class Throttle1PressProcedureMixin {

    @ModifyConstant(
        method = "execute",
        constant = @Constant(intValue = 10),
        remap = false
    )
    private static int moreNegativeThrottle(int maxThrottle) {
        return Config.SERVER.maxThrottle.get();
    }
}