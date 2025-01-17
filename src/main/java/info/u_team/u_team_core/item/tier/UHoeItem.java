package info.u_team.u_team_core.item.tier;

import info.u_team.u_team_core.api.item.ExtendedTier;
import info.u_team.u_team_core.api.item.ExtendedTier.Tools;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.HoeItem;

public class UHoeItem extends HoeItem {
	
	public UHoeItem(Properties properties, ExtendedTier tier) {
		this(null, properties, tier);
	}
	
	public UHoeItem(CreativeModeTab creativeTab, Properties properties, ExtendedTier tier) {
		super(tier, (int) tier.getAttackDamage(Tools.HOE), tier.getAttackSpeed(Tools.HOE), creativeTab == null ? properties : properties.tab(creativeTab));
	}
	
}
