package crazywoddman.warium_create.mixin.supplementaries;

import net.mehvahdjukaar.supplementaries.common.items.crafting.SpecialRecipeDisplays;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import crazywoddman.warium_create.recipe.ColoringRecipeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mixin(value = SpecialRecipeDisplays.class, remap = false)
public class SpecialRecipeDisplaysMixin {

    @Redirect(
        method = "createSoapCleanRecipe",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/function/Supplier;get()Ljava/lang/Object;",
            ordinal = 0
        )
    )
    private static Object redirectSoapBlacklistGet(Supplier<List<String>> instance) {
        List<String> original = instance.get();
        List<String> extended = new ArrayList<>(original);
        extended.addAll(ColoringRecipeRegistry.getPatternsForSoapBlacklist());

        return extended;
    }
}