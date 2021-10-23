package info.u_team.u_team_core.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import info.u_team.u_team_core.api.sync.DataHolder;
import info.u_team.u_team_core.intern.init.UCoreNetwork;
import info.u_team.u_team_core.intern.network.DataHolderMenuMessage;
import info.u_team.u_team_core.screen.UMenuContainerScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

/**
 * A basic menu with synchronization capabilities that implements the {@link FluidContainerMenu}.
 *
 * @author HyCraftHD
 */
public abstract class UContainerMenu extends FluidContainerMenu {
	
	private final List<DataHolder> dataHolderToClient;
	private final List<DataHolder> dataHolderToServer;
	
	/**
	 * Creates a new container. Must be implemented by a sub class to be used.
	 *
	 * @param menuType Menu type
	 * @param containerId Container id
	 */
	protected UContainerMenu(MenuType<?> menuType, int containerId) {
		super(menuType, containerId);
		dataHolderToClient = new ArrayList<>();
		dataHolderToServer = new ArrayList<>();
	}
	
	/**
	 * Default player interaction with this container to true
	 */
	@Override
	public boolean stillValid(Player player) {
		return true;
	}
	
	/**
	 * Adds a new {@link DataHolder} that will automatically synchronize values from the server to the client.
	 *
	 * @param holder Data holder
	 * @return The supplied data holder
	 */
	protected <E extends DataHolder> E addDataHolderToClient(E holder) {
		dataHolderToClient.add(holder);
		return holder;
	}
	
	/**
	 * Adds a new {@link DataHolder} that will synchronize values from the client to the server. <br />
	 * <br />
	 * To synchronize values automatically the screen must implement {@link UMenuContainerScreen}. If not you must manually call
	 * {@link #broadcastChangesToServer()} every time you update values on the client that should be synchronized to the
	 * server.
	 *
	 * @param holder Data holder
	 * @return The supplied data holder
	 */
	protected <E extends DataHolder> E addDataHolderToServer(E holder) {
		dataHolderToServer.add(holder);
		return holder;
	}
	
	/**
	 * Sends all menu data to the client.
	 */
	@Override
	public void sendAllDataToRemote() {
		super.sendAllDataToRemote();
		
		if (getSynchronizerPlayer() != null) {
			for (var index = 0; index < dataHolderToClient.size(); index++) {
				final var dataHolder = dataHolderToClient.get(index);
				UCoreNetwork.NETWORK.send(PacketDistributor.PLAYER.with(this::getSynchronizerPlayer), new DataHolderMenuMessage(containerId, index, dataHolder.get()));
			}
		}
	}
	
	/**
	 * Broadcast changed data
	 */
	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
		
		if (getSynchronizerPlayer() != null) {
			checkForChanges(dataHolderToClient, (index, dataHolder) -> {
				UCoreNetwork.NETWORK.send(PacketDistributor.PLAYER.with(this::getSynchronizerPlayer), new DataHolderMenuMessage(containerId, index, dataHolder.get()));
			});
		}
	}
	
	/**
	 * Broadcast data from the client to the server if changed. Needs to be called everytime you want to send changed data
	 * from the client to the server. Will be called automatically every tick if the screens base class is
	 * {@link UMenuContainerScreen}.
	 *
	 * @see #addDataHolderToServer(DataHolder)
	 */
	public void broadcastChangesToServer() {
		checkForChanges(dataHolderToServer, (index, dataHolder) -> {
			UCoreNetwork.NETWORK.send(PacketDistributor.SERVER.noArg(), new DataHolderMenuMessage(containerId, index, dataHolder.get()));
		});
	}
	
	/**
	 * Called by the packet handler to update the values on the right side.
	 * 
	 * @param side Side that should set the values
	 * @param index Index of the data holder in the list
	 * @param dataHolderBuffer The buffer that should be set
	 */
	public final void setDataHolder(LogicalSide side, int index, FriendlyByteBuf dataHolderBuffer) {
		if (side == LogicalSide.CLIENT) {
			dataHolderToClient.get(index).set(dataHolderBuffer);
		} else if (side == LogicalSide.SERVER) {
			dataHolderToServer.get(index).set(dataHolderBuffer);
		}
	}
	
	/**
	 * Checks the supplied data holder list and calls the consumer if something changed and should be updated.
	 * 
	 * @param dataHolders List of data holders
	 * @param consumer Consumer that should handle the change
	 */
	private void checkForChanges(List<DataHolder> dataHolders, BiConsumer<Integer, DataHolder> consumer) {
		for (var index = 0; index < dataHolders.size(); index++) {
			final var dataHolder = dataHolders.get(index);
			if (dataHolder.checkAndClearUpdateFlag()) {
				consumer.accept(index, dataHolder);
			}
		}
	}
	
}
