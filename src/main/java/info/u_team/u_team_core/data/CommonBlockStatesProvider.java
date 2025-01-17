package info.u_team.u_team_core.data;

import java.io.IOException;
import java.util.function.Function;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.common.base.Preconditions;

import info.u_team.u_team_core.UCoreMod;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;

public abstract class CommonBlockStatesProvider extends BlockStateProvider {
	
	protected final Marker marker;
	
	protected final GenerationData data;
	protected final String modid;
	protected final DataGenerator generator;
	
	public CommonBlockStatesProvider(GenerationData data) {
		super(data.getGenerator(), data.getModid(), data.getExistingFileHelper());
		this.data = data;
		modid = data.getModid();
		generator = data.getGenerator();
		marker = MarkerManager.getMarker(getName());
	}
	
	@Override
	public void run(HashCache cache) throws IOException {
		models().generatedModels.clear();
		registerModels0(cache);
		models().generatedModels.values().forEach(model -> {
			try {
				final var location = model.getLocation();
				CommonProvider.write(cache, model.toJson(), generator.getOutputFolder().resolve("assets/" + location.getNamespace() + "/models/" + location.getPath() + ".json"));
			} catch (final IOException ex) {
				CommonProvider.LOGGER.error(marker, "Could not write data.", ex);
			}
		});
	}
	
	// We need to overide registerModels, but this method is marked final...
	private void registerModels0(HashCache cache) {
		registeredBlocks.clear();
		registerStatesAndModels();
		
		registeredBlocks.forEach((block, generatedState) -> {
			try {
				final var location = Preconditions.checkNotNull(block.getRegistryName());
				CommonProvider.write(cache, generatedState.toJson(), generator.getOutputFolder().resolve("assets/" + location.getNamespace() + "/blockstates/" + location.getPath() + ".json"));
			} catch (final IOException ex) {
				CommonProvider.LOGGER.error(marker, "Could not write data.", ex);
			}
		});
	}
	
	@Override
	public String getName() {
		return "Block-States | Block-Models";
	}
	
	// Block state methods
	protected void facingBlock(Block block, ModelFile modelFile) {
		facingBlock(block, modelFile, 0);
	}
	
	protected void facingBlock(Block block, ModelFile modelFile, int angleOffset) {
		facingBlock(block, $ -> modelFile, angleOffset);
	}
	
	protected void facingBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
		getVariantBuilder(block).forAllStates(state -> {
			final var direction = state.getValue(BlockStateProperties.FACING);
			return ConfiguredModel.builder() //
					.modelFile(modelFunc.apply(state)) //
					.rotationX(direction == Direction.DOWN ? 90 : direction == Direction.UP ? 270 : 0) //
					.rotationY(direction.getAxis().isVertical() ? 0 : (((int) direction.toYRot()) + angleOffset + 180) % 360) //
					.build(); //
		});
	}
	
	// Block model methods
	protected BlockModelBuilder cubeFacing(String name, ResourceLocation front, ResourceLocation side) {
		return cubeFacing(name, front, side, side);
	}
	
	protected BlockModelBuilder cubeFacing(String name, ResourceLocation front, ResourceLocation side, ResourceLocation particle) {
		return models().getBuilder(name) //
				.parent(new UncheckedModelFile(new ResourceLocation(UCoreMod.MODID, "block/facing"))) //
				.texture("particle", particle) //
				.texture("front", front) //
				.texture("side", side);
	}
	
	protected BlockModelBuilder cubeFacingBottomTop(String name, ResourceLocation front, ResourceLocation bottom, ResourceLocation top, ResourceLocation side) {
		return cubeFacingBottomTop(name, front, bottom, top, side, side);
	}
	
	protected BlockModelBuilder cubeFacingBottomTop(String name, ResourceLocation front, ResourceLocation bottom, ResourceLocation top, ResourceLocation side, ResourceLocation particle) {
		return models().getBuilder(name) //
				.parent(new UncheckedModelFile(new ResourceLocation(UCoreMod.MODID, "block/facing_bottom_top"))) //
				.texture("particle", particle) //
				.texture("front", front) //
				.texture("bottom", bottom) //
				.texture("top", top) //
				.texture("side", side);
	}
	
	// Utility methods
	protected String getPath(Block block) {
		return block.getRegistryName().getPath();
	}
}
