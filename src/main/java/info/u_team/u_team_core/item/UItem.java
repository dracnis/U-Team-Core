package info.u_team.u_team_core.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class UItem extends Item {
	
	public UItem(Properties properties) {
		this(null, properties);
	}
	
	public UItem(CreativeModeTab creativeTab, Properties properties) {
		super(creativeTab == null ? properties : properties.tab(creativeTab));
	}
	
}
