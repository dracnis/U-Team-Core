package info.u_team.u_team_core.intern.init;

import info.u_team.u_team_core.UCoreMod;
import info.u_team.u_team_core.intern.loot.SetBlockEntityNBTLootFunction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.IEventBus;

public class UCoreLootFunctions {
	
	public static final LootItemFunctionType SET_BLOCKENTITY_NBT = new LootItemFunctionType(new SetBlockEntityNBTLootFunction.Serializer());
	
	private static void registerLootFunction(Register<Block> event) {
		Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(UCoreMod.MODID, "set_blockentity_nbt"), SET_BLOCKENTITY_NBT);
	}
	
	public static void registerMod(IEventBus bus) {
		bus.addGenericListener(Block.class, UCoreLootFunctions::registerLootFunction);
	}
}
