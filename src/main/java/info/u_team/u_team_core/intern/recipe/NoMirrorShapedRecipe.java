package info.u_team.u_team_core.intern.recipe;

import com.google.gson.JsonObject;

import info.u_team.u_team_core.intern.init.UCoreRecipeSerializers;
import info.u_team.u_team_core.recipeserializer.UShapedRecipeSerializer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

public class NoMirrorShapedRecipe extends ShapedRecipe {
	
	public NoMirrorShapedRecipe(ResourceLocation location, String group, int recipeWidth, int recipeHeigt, NonNullList<Ingredient> ingredients, ItemStack output) {
		super(location, group, recipeWidth, recipeHeigt, ingredients, output);
	}
	
	@Override
	public boolean matches(CraftingContainer container, Level level) {
		for (var i = 0; i <= container.getWidth() - getWidth(); ++i) {
			for (var j = 0; j <= container.getHeight() - getHeight(); ++j) {
				if (matches(container, i, j, false)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return UCoreRecipeSerializers.NO_MIRROR_SHAPED.get();
	}
	
	public static class Serializer extends UShapedRecipeSerializer<NoMirrorShapedRecipe> {
		
		@Override
		public NoMirrorShapedRecipe fromJson(ResourceLocation location, JsonObject json) {
			final var pattern = patternFromJson(GsonHelper.getAsJsonArray(json, "pattern"));
			final var recipeWidth = pattern[0].length();
			final var recipeHeight = pattern.length;
			final var group = GsonHelper.getAsString(json, "group", "");
			final var ingredients = deserializeIngredients(pattern, deserializeKey(GsonHelper.getAsJsonObject(json, "key")), recipeWidth, recipeHeight);
			final var output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
			return new NoMirrorShapedRecipe(location, group, recipeWidth, recipeHeight, ingredients, output);
		}
		
		@Override
		public NoMirrorShapedRecipe fromNetwork(ResourceLocation location, FriendlyByteBuf buffer) {
			final var recipeWidth = buffer.readVarInt();
			final var recipeHeight = buffer.readVarInt();
			final var group = buffer.readUtf(32767);
			final var ingredients = NonNullList.withSize(recipeWidth * recipeHeight, Ingredient.EMPTY);
			for (var k = 0; k < ingredients.size(); ++k) {
				ingredients.set(k, Ingredient.fromNetwork(buffer));
			}
			final var output = buffer.readItem();
			return new NoMirrorShapedRecipe(location, group, recipeWidth, recipeHeight, ingredients, output);
		}
		
		@Override
		public void toNetwork(FriendlyByteBuf buffer, NoMirrorShapedRecipe recipe) {
			buffer.writeVarInt(recipe.getRecipeWidth());
			buffer.writeVarInt(recipe.getRecipeHeight());
			buffer.writeUtf(recipe.getGroup());
			for (final var ingredient : recipe.getIngredients()) {
				ingredient.toNetwork(buffer);
			}
			buffer.writeItem(recipe.getResultItem());
		}
	}
}
