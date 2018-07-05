package info.u_team.u_team_core.entity;

import info.u_team.u_team_core.api.registry.IUEntityEntry;
import net.minecraftforge.fml.common.registry.*;

/**
 * Entity API<br>
 * -> Basic Entity Entry
 * 
 * @date 05.07.2018
 * @author HyCraftHD
 */
public class UEntityEntry implements IUEntityEntry {
	
	protected EntityEntryBuilder<?> builder;
	
	public UEntityEntry(EntityEntryBuilder<?> builder) {
		this.builder = builder;
	}
	
	@Override
	public EntityEntry getEntry() {
		return builder.build();
	}
	
	@Override
	public String getName() {
		return builder.toString();
	}
	
}
