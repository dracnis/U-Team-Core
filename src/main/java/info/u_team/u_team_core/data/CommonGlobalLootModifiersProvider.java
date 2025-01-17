package info.u_team.u_team_core.data;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;

import info.u_team.u_team_core.util.CastUtil;
import info.u_team.u_team_core.util.TriConsumer;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.IGlobalLootModifier;

public abstract class CommonGlobalLootModifiersProvider extends CommonProvider {
	
	protected boolean replace;
	
	public CommonGlobalLootModifiersProvider(GenerationData data) {
		super(data);
	}
	
	@Override
	public void run(HashCache cache) throws IOException {
		final Map<String, Tuple<GlobalLootModifierSerializer<?>, JsonObject>> serializers = new TreeMap<>();
		
		registerGlobalLootModifiers((modifier, serializerSupplier, instance) -> {
			final GlobalLootModifierSerializer<IGlobalLootModifier> serializer = CastUtil.uncheckedCast(serializerSupplier.get());
			serializers.put(modifier, new Tuple<>(serializer, serializer.write(instance)));
		});
		
		final List<String> entries = serializers.entrySet().stream().map(entry -> {
			final var name = entry.getKey();
			final var tuple = entry.getValue();
			final var json = tuple.getB();
			
			json.addProperty("type", tuple.getA().getRegistryName().toString());
			
			try {
				write(cache, json, resolveModData().resolve("loot_modifiers").resolve(name + ".json"));
			} catch (final IOException ex) {
				LOGGER.error(marker, "Could not write data.", ex);
			}
			return new ResourceLocation(modid, name);
		}).map(ResourceLocation::toString).collect(Collectors.toList());
		
		final var json = new JsonObject();
		json.addProperty("replace", replace);
		json.add("entries", GSON.toJsonTree(entries));
		
		try {
			write(cache, json, path.resolve("data").resolve("forge").resolve("loot_modifiers").resolve("global_loot_modifiers.json"));
		} catch (final IOException ex) {
			LOGGER.error(marker, "Could not write data.", ex);
		}
	}
	
	protected void replacing() {
		replace = true;
	}
	
	protected abstract void registerGlobalLootModifiers(TriConsumer<String, Supplier<? extends GlobalLootModifierSerializer<? extends IGlobalLootModifier>>, ? super IGlobalLootModifier> consumer);
	
	@Override
	public String getName() {
		return "Global-Loot-Modifiers";
	}
	
}
