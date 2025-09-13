package crazywoddman.warium_create.recipe;

import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import crazywoddman.warium_create.WariumCreate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@EventBusSubscriber(modid = WariumCreate.MODID, bus = EventBusSubscriber.Bus.FORGE)
public class ColoringRecipeRegistry {
    
    public static final List<ColoringRecipe> cachedRecipes = new ArrayList<>();
    private static final boolean supplementariesLoaded = ModList.get().isLoaded("supplementaries");

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onServerStarted(ServerStartedEvent event) {
        updateCacheAndGenerateRecipes(event.getServer());
    }

    public static void updateCacheAndGenerateRecipes(MinecraftServer server) {
        RecipeManager recipeManager = server.getRecipeManager();
        
        cachedRecipes.clear();
        for (Recipe<?> recipe : recipeManager.getRecipes())
            if (recipe instanceof ColoringRecipe coloringRecipe)
                cachedRecipes.add(coloringRecipe);

        if (cachedRecipes.isEmpty())
            return;

        Collection<Recipe<?>> existingRecipes = recipeManager.getRecipes();
        List<Recipe<?>> allRecipes = new ArrayList<>(existingRecipes);
        int recipesAdded = 0;

        for (ColoringRecipe template : cachedRecipes) {
            ResourceLocation soapRecipeId = ResourceLocation.fromNamespaceAndPath(
                template.getId().getNamespace(),
                template.getId().getPath() + "_cleaning"
            );

            Recipe<?> soapRecipe = createSoapCleanRecipe(soapRecipeId, template);
            if (soapRecipe != null) {
                allRecipes.add(soapRecipe);
                recipesAdded++;
            }
            
            for (DyeColor color : DyeColor.values()) {
                if (!template.getFluidIngredients().isEmpty()) {
                    ResourceLocation mixingRecipeId = ResourceLocation.fromNamespaceAndPath(
                        template.getId().getNamespace(),
                        template.getId().getPath() + "_" + color.getName() + "_mixing"
                    );

                    Recipe<?> mixingRecipe = createMixingRecipe(mixingRecipeId, template, color);

                    if (mixingRecipe != null) {
                        allRecipes.add(mixingRecipe);
                        recipesAdded++;
                    }
                }

                if (!template.getItemIngredientPatterns().isEmpty()) {
                    ResourceLocation deployingRecipeId = ResourceLocation.fromNamespaceAndPath(
                        template.getId().getNamespace(),
                        template.getId().getPath() + "_" + color.getName() + "_deploying"
                    );

                    Recipe<?> deployingRecipe = createDeployingRecipe(deployingRecipeId, template, color);

                    if (deployingRecipe != null) {
                        allRecipes.add(deployingRecipe);
                        recipesAdded++;
                    }
                }
            }
        }

        if (recipesAdded > 0) {
            recipeManager.replaceRecipes(allRecipes);
            System.out.println("Generated " + recipesAdded + " mixing/deploying recipes from " + cachedRecipes.size() + " ColoringRecipes");

            if (supplementariesLoaded)
                System.out.println("Supplementaries detected - crafting recipes with soap included");
        }
    }

    private static Recipe<?> createMixingRecipe(ResourceLocation recipeId, ColoringRecipe template, DyeColor color) {
        try {
            String colorName = color.getName();
            ResourceLocation outputLocation = ResourceLocation.parse(template.getResultItem().replace("{color}", colorName));

            if (!ForgeRegistries.ITEMS.containsKey(outputLocation))
                return null;

            Ingredient baseIngredient = createIngredientExcludingColor(template.getBaseIngredientPattern(), color);

            if (baseIngredient.isEmpty())
                return null;
            
            FluidStack fluidStack = FluidStack.EMPTY;

            for (ColoringRecipe.FluidIngredient fluidIngredient : template.getFluidIngredients()) {
                fluidStack = fluidIngredient.createFluidStack(colorName);
                if (fluidStack.isEmpty())
                    return null;
            }

            return new ProcessingRecipeBuilder<>(MixingRecipe::new, recipeId)
                .withItemIngredients(baseIngredient)
                .withFluidIngredients(FluidIngredient.fromFluidStack(fluidStack))
                .output(new ItemStack(ForgeRegistries.ITEMS.getValue(outputLocation)))
                .duration(100)
                .build();
                
        } catch (Exception e) {
            System.err.println("Failed to create mixing recipe " + recipeId + ": " + e.getMessage());
            return null;
        }
    }

    private static Recipe<?> createDeployingRecipe(ResourceLocation recipeId, ColoringRecipe template, DyeColor color) {
        try {
            String colorName = color.getName();
            ResourceLocation outputLocation = ResourceLocation.parse(template.getResultItem().replace("{color}", colorName));

            if (!ForgeRegistries.ITEMS.containsKey(outputLocation))
                return null;
            
            Ingredient baseIngredient = createIngredientExcludingColor(template.getBaseIngredientPattern(), color);
            
            if (baseIngredient.isEmpty())
                return null;
            
            Ingredient toolIngredient = createIngredientFromPattern(template.getItemIngredientPatterns().get(0), colorName);
            
            if (toolIngredient.isEmpty())
                return null;
    
            return new ProcessingRecipeBuilder<>(DeployerApplicationRecipe::new, recipeId)
                .withItemIngredients(baseIngredient, toolIngredient)
                .output(new ItemStack(ForgeRegistries.ITEMS.getValue(outputLocation)))
                .build();
            
        } catch (Exception e) {
            System.err.println("Failed to create deploying recipe " + recipeId + ": " + e.getMessage());
            return null;
        }
    }

    private static Ingredient createIngredientFromPattern(String pattern, String color) {
        if (pattern == null || pattern.isEmpty())
            return Ingredient.EMPTY;

        String processedPattern = pattern.replace("{color}", color);
        
        try {
            if (processedPattern.startsWith("#")) {
                String tagName = processedPattern.substring(1);
                ResourceLocation tagLocation = ResourceLocation.parse(tagName);
                return Ingredient.fromValues(Stream.of(
                    new Ingredient.TagValue(ItemTags.create(tagLocation))
                ));
            } else {
                ResourceLocation itemLocation = ResourceLocation.parse(processedPattern);
                if (ForgeRegistries.ITEMS.containsKey(itemLocation))
                    return Ingredient.of(new ItemStack(ForgeRegistries.ITEMS.getValue(itemLocation)));
            }
        } catch (Exception e) {
            System.err.println("Failed to create ingredient from pattern: " + pattern + " with color: " + color);
        }
        
        return Ingredient.EMPTY;
    }

    private static Ingredient createIngredientExcludingColor(String pattern, DyeColor excludeColor) {
        if (pattern == null || pattern.isEmpty())
            return Ingredient.EMPTY;
            
        try {
            if (pattern.startsWith("#")) {
                Ingredient tagIngredient = Ingredient.fromValues(Stream.of(
                    new Ingredient.TagValue(ItemTags.create(ResourceLocation.parse(pattern.substring(1))))
                ));
                
                List<ItemStack> validItems = new ArrayList<>();
                
                for (ItemStack stack : tagIngredient.getItems()) {
                    String itemPath = ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath();

                    if (!itemPath.contains(excludeColor.getName()))
                        validItems.add(stack);
                }
                
                if (validItems.isEmpty())
                    return tagIngredient;
                
                return Ingredient.of(validItems.toArray(new ItemStack[0]));
            } else {
                ResourceLocation itemLocation = ResourceLocation.parse(pattern);
                if (ForgeRegistries.ITEMS.containsKey(itemLocation))
                    return Ingredient.of(new ItemStack(ForgeRegistries.ITEMS.getValue(itemLocation)));
            }
        } catch (Exception e) {
            System.err.println("Failed to create ingredient excluding color from pattern: " + pattern);
        }
        
        return Ingredient.EMPTY;
    }

    private static Recipe<?> createSoapCleanRecipe(ResourceLocation recipeId, ColoringRecipe template) {
        if (!supplementariesLoaded)
            return null;

        try {
            String basePattern = template.getBaseIngredientPattern();
            if (!basePattern.startsWith("#"))
                return null;
            
            ResourceLocation tagLocation = ResourceLocation.parse(basePattern.substring(1));
            ITag<Item> items = ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(tagLocation));
            
            if (items.isEmpty())
                return null;
            
            Item result = items.iterator().next();
            List<ItemStack> validIngredients = new ArrayList<>();

            for (Item item : items)
                if (item != result)
                    validIngredients.add(new ItemStack(item));
            
            if (validIngredients.isEmpty())
                return null;

            List<Ingredient> ingredients = new ArrayList<>();
            ingredients.add(Ingredient.of(validIngredients.toArray(new ItemStack[0])));
            ResourceLocation soapLocation = ResourceLocation.fromNamespaceAndPath("supplementaries", "soap");

            if (!ForgeRegistries.ITEMS.containsKey(soapLocation))
                return null;
            
            ingredients.add(Ingredient.of(new ItemStack(ForgeRegistries.ITEMS.getValue(soapLocation))));

            return new ShapelessRecipe(
                recipeId,
                "misc",
                CraftingBookCategory.MISC,
                new ItemStack(result),
                NonNullList.of(Ingredient.EMPTY, ingredients.toArray(new Ingredient[0]))
            );

        } catch (Exception e) {
            System.err.println("Failed to create soap cleaning recipe " + recipeId + ": " + e.getMessage());
            return null;
        }
    }

    public static List<String> getPatternsForSoapBlacklist() {
        List<String> patterns = new ArrayList<>();

        for (ColoringRecipe recipe : cachedRecipes) {
            String resultItem = recipe.getResultItem();

            if (!resultItem.isEmpty()) {
                String pattern = resultItem
                    .replace("_{color}_", "")
                    .replace("_{color}", "")
                    .replace("{color}_", "")
                    .replace("{color}", "");
                patterns.add(pattern);
            }
        }
        
        return patterns;
    }

    public static List<ColoringRecipe> getCachedRecipes() {
        return cachedRecipes;
    }
}