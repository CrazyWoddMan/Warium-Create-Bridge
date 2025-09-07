package crazywoddman.warium_create.mixin.create;

import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(remap = false, value = BasinCategory.class)
public class BasinCategoryMixin {
    
    private static final TagKey<Item> BLAZE_CAKE_TAG = TagKey.create(
        Registries.ITEM, 
        ResourceLocation.fromNamespaceAndPath("create", "blaze_burner_fuel/special")
    );
    
    @Inject(
        method = "setRecipe",
        at = @At("TAIL")
    )
    private void addCustomBlazeCakeSlot(IRecipeLayoutBuilder builder, BasinRecipe recipe, IFocusGroup focuses, CallbackInfo ci) {
        HeatCondition requiredHeat = recipe.getRequiredHeat();

        if (!requiredHeat.testBlazeBurner(HeatLevel.KINDLED))
            builder
            .addSlot(RecipeIngredientRole.CATALYST, 153, 81)
            .addIngredients(Ingredient.of(BLAZE_CAKE_TAG));
    }
}