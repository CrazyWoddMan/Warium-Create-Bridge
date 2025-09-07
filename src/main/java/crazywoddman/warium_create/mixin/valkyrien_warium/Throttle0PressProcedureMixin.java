package crazywoddman.warium_create.mixin.valkyrien_warium;

import net.mcreator.valkyrienwarium.procedures.Throttle0PressProcedure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import crazywoddman.warium_create.Config;

import org.spongepowered.asm.mixin.injection.Constant;

@Mixin(remap = false, value = Throttle0PressProcedure.class)
public class Throttle0PressProcedureMixin {

    @ModifyConstant(
        method = "execute",
        constant = @Constant(intValue = -1)
    )
    private static int moreNegativeThrottle(int minThrottle) {
        return Config.SERVER.minThrottle.get();
    }
}