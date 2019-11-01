package info.u_team.u_team_core.intern.data.provider;

import java.util.function.Consumer;

import info.u_team.u_team_core.UCoreMain;
import info.u_team.u_team_core.data.CommonRecipesProvider;
import info.u_team.u_team_core.intern.init.UCoreRecipeSerializers;
import net.minecraft.data.*;

public class UCoreRecipesProvider extends CommonRecipesProvider {
	
	public UCoreRecipesProvider(DataGenerator generator) {
		super(generator);
	}
	
	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
		CustomRecipeBuilder.func_218656_a(UCoreRecipeSerializers.CRAFTING_SPECIAL_ITEMDYE).build(consumer, UCoreMain.MODID + ":custom_dyeable_item");
	}
	
}