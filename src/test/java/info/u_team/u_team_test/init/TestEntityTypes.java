package info.u_team.u_team_test.init;

import info.u_team.u_team_test.entity.EntityBetterEnderPearl;
import net.minecraft.entity.EntityType;

public class TestEntityTypes {
	
	public static final EntityType<EntityBetterEnderPearl> better_enderpearl = null;//UEntityType.Builder.<EntityBetterEnderPearl> create("better_enderpearl", EntityBetterEnderPearl::new, EntityClassification.MISC).size(0.25F, 0.25F).setTrackingRange(128).setUpdateInterval(20).setShouldReceiveVelocityUpdates(true).setCustomClientFactory(EntityBetterEnderPearl::new).build();
	
	public static void construct() {
//		EntityTypeRegistry.register(TestMod.modid, TestEntityTypes.class);
	}
	
}
