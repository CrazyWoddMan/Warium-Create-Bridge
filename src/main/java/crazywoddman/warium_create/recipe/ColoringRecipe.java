package crazywoddman.warium_create.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class ColoringRecipe implements Recipe<Container> {
    public final ResourceLocation id;
    public final String baseIngredient;
    public final List<String> itemIngredients;
    public final List<FluidIngredient> fluidIngredients;
    public final String resultItem;

    public ColoringRecipe(
        ResourceLocation id,
        String baseIngredient, 
        List<String> itemIngredients,
        List<FluidIngredient> fluidIngredients,
        String resultItem
    ) {
        this.id = id;
        this.baseIngredient = baseIngredient;
        this.itemIngredients = itemIngredients != null ? itemIngredients : new ArrayList<>();
        this.fluidIngredients = fluidIngredients != null ? fluidIngredients : new ArrayList<>();
        this.resultItem = resultItem;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return WariumCreateRecipeTypes.COLORING.getSerializer();
    }

    @Override
    public RecipeType<?> getType() {
        return WariumCreateRecipeTypes.COLORING.getType();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.create();
    }

    public String getBaseIngredientPattern() {
        return baseIngredient;
    }

    public List<String> getItemIngredientPatterns() {
        return itemIngredients;
    }

    public List<FluidIngredient> getFluidIngredients() {
        return fluidIngredients;
    }

    public String getResultItem() {
        return resultItem;
    }

    // Внутренний класс для флюидов
    public static class FluidIngredient {
        private final String fluidPattern;
        private final int amount;

        public FluidIngredient(String fluidPattern, int amount) {
            this.fluidPattern = fluidPattern;
            this.amount = amount;
        }

        public String getFluidPattern() {
            return fluidPattern;
        }

        public int getAmount() {
            return amount;
        }

        public FluidStack createFluidStack(String color) {
            try {
                String processedPattern = fluidPattern.replace("{color}", color);
                ResourceLocation fluidLocation = ResourceLocation.parse(processedPattern);
                if (ForgeRegistries.FLUIDS.containsKey(fluidLocation))
                    return new FluidStack(ForgeRegistries.FLUIDS.getValue(fluidLocation), amount);
            } catch (Exception e) {
                System.err.println("Failed to create fluid stack from pattern: " + fluidPattern + " with color: " + color);
            }

            return FluidStack.EMPTY;
        }
    }

    public static class Serializer implements RecipeSerializer<ColoringRecipe> {
        
        @Override
        public ColoringRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            String baseIngredient = "";
            List<String> itemIngredients = new ArrayList<>();
            List<FluidIngredient> fluidIngredients = new ArrayList<>();

            if (json.has("ingredients")) {
                JsonArray ingredientsArray = GsonHelper.getAsJsonArray(json, "ingredients");
                
                for (int i = 0; i < ingredientsArray.size(); i++) {
                    JsonElement element = ingredientsArray.get(i);
                    JsonObject ingredientObj = element.getAsJsonObject();
                    
                    if (ingredientObj.has("fluid")) {
                        String fluidPattern = GsonHelper.getAsString(ingredientObj, "fluid");
                        int amount = GsonHelper.getAsInt(ingredientObj, "amount", 1000);
                        fluidIngredients.add(new FluidIngredient(fluidPattern, amount));
                    } else {
                        String ingredientPattern = "";

                        if (ingredientObj.has("item"))
                            ingredientPattern = GsonHelper.getAsString(ingredientObj, "item");
                        else if (ingredientObj.has("tag"))
                            ingredientPattern = "#" + GsonHelper.getAsString(ingredientObj, "tag");
                        
                        if (!ingredientPattern.isEmpty()) {
                            if (i == 0)
                                baseIngredient = ingredientPattern;
                            else
                                itemIngredients.add(ingredientPattern);
                        }
                    }
                }
            }

            String resultItem = "";

            if (json.has("result"))
                resultItem = GsonHelper.getAsString(
                    GsonHelper.getAsJsonObject(json, "result"),
                    "item"
                );

            return new ColoringRecipe(recipeId, baseIngredient, itemIngredients, fluidIngredients, resultItem);
        }

        @Override
        public ColoringRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            String baseIngredient = buffer.readUtf();
            int itemCount = buffer.readVarInt();
            List<String> itemIngredients = new ArrayList<>();

            for (int i = 0; i < itemCount; i++)
                itemIngredients.add(buffer.readUtf());
            
            int fluidCount = buffer.readVarInt();
            List<FluidIngredient> fluidIngredients = new ArrayList<>();

            for (int i = 0; i < fluidCount; i++) {
                String fluidPattern = buffer.readUtf();
                int amount = buffer.readVarInt();
                fluidIngredients.add(new FluidIngredient(fluidPattern, amount));
            }
            
            String resultItem = buffer.readUtf();

            return new ColoringRecipe(recipeId, baseIngredient, itemIngredients, fluidIngredients, resultItem);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ColoringRecipe recipe) {
            buffer.writeUtf(recipe.baseIngredient);
            buffer.writeVarInt(recipe.itemIngredients.size());

            for (String pattern : recipe.itemIngredients)
                buffer.writeUtf(pattern);
            
            buffer.writeVarInt(recipe.fluidIngredients.size());

            for (FluidIngredient fluidIngredient : recipe.fluidIngredients) {
                buffer.writeUtf(fluidIngredient.fluidPattern);
                buffer.writeVarInt(fluidIngredient.amount);
            }
            
            buffer.writeUtf(recipe.resultItem);
        }
    }
}