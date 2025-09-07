package crazywoddman.warium_create.mixin.supplementaries;

import net.mehvahdjukaar.supplementaries.common.items.crafting.SoapClearRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import crazywoddman.warium_create.recipe.ColoringRecipeRegistry;

import java.util.List;
import java.util.function.Supplier;

@Mixin(value = SoapClearRecipe.class, remap = false)
public class SoapClearRecipeMixin {

    @Redirect(
        method = "matches",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/function/Supplier;get()Ljava/lang/Object;",
            ordinal = 0
        )
    )
    private Object redirectSoapBlacklistGet(Supplier<List<String>> instance) {
        List<String> original = instance.get();
        List<String> extended = ColoringRecipeRegistry.getPatternsForSoapBlacklist();
        
        return extended.size() > 0 ? original.addAll(extended) : original;
    }
}