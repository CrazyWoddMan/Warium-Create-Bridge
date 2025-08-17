package crazywoddman.warium_create.mixin;

import net.mcreator.valkyrienwarium.procedures.Throttle0PressProcedure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import crazywoddman.warium_create.Config;

import org.spongepowered.asm.mixin.injection.Constant;

@Mixin(Throttle0PressProcedure.class)
public class Throttle0PressProcedureMixin {

    @ModifyConstant(
        method = "execute",
        constant = @Constant(intValue = -1),
        remap = false
    )
    private static int moreNegativeThrottle(int minThrottle) {
        return Config.SERVER.minThrottle.get();
    }
}