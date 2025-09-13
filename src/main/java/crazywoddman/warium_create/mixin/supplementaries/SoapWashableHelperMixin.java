package crazywoddman.warium_create.mixin.supplementaries;

import net.mehvahdjukaar.supplementaries.common.utils.SoapWashableHelper;
import net.mehvahdjukaar.supplementaries.common.utils.BlockPredicate;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import crazywoddman.warium_create.recipe.ColoringRecipe;
import crazywoddman.warium_create.recipe.ColoringRecipeRegistry;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mixin(value = SoapWashableHelper.class, remap = false)
public class SoapWashableHelperMixin {

    @Redirect(
        method = "tryChangingColor",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/function/Supplier;get()Ljava/lang/Object;",
            ordinal = 0
        )
    )
    private static Object redirectSoapBlacklistGet(Supplier<List<String>> instance) {
        List<String> original = instance.get();
        List<String> extended = ColoringRecipeRegistry.getPatternsForSoapBlacklist();

        if (extended.size() > 0)
            original.addAll(extended);
        
        return original;
    }

    @Redirect(
        method = "tryUnoxidise",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/function/Supplier;get()Ljava/lang/Object;",
            ordinal = 0
        )
    )
    private static Object redirectSoapSpecialGet(Supplier<Map<BlockPredicate, ResourceLocation>> instance) {
        Map<BlockPredicate, ResourceLocation> original = instance.get();
        BiMap<BlockPredicate, ResourceLocation> extended = HashBiMap.create();
        
        try {
            for (ColoringRecipe template : ColoringRecipeRegistry.getCachedRecipes()) {
                String base = template.baseIngredient;

                if (base.startsWith("#")) {
                    TagKey<Item> tag = ItemTags.create(ResourceLocation.tryParse(base.substring(1)));
                    ITag<Item> items = ForgeRegistries.ITEMS.tags().getTag(tag);

                    if (!items.isEmpty()) {
                        String firstItemId = ForgeRegistries.ITEMS.getKey(items.iterator().next()).toString();
                        extended.put(BlockPredicate.create(base), ResourceLocation.tryParse(firstItemId));
                    }
                }
            }
        
            return extended;
        } catch (Exception e) {
            System.err.println("Failed to extend soap special: " + e);
            return original;
        }
    }
}