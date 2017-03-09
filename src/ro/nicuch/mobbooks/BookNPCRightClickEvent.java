package ro.nicuch.mobbooks;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import net.citizensnpcs.api.npc.NPC;

public class BookNPCRightClickEvent extends Event implements Cancellable {
	private final static HandlerList handlers = new HandlerList();
	private final Player player;
	private final NPC npc;
	private ItemStack book;
	private boolean usePlaceHolders = true;
	private boolean cancel;

	public BookNPCRightClickEvent(final Player player, final NPC npc, final ItemStack book) {
		this.player = player;
		this.npc = npc;
		this.book = book;
	}

	public Player getPlayer() {
		return this.player;
	}

	public NPC getNPC() {
		return this.npc;
	}

	public ItemStack getBook() {
		return this.book;
	}

	public boolean usePlaceHolders() {
		return this.usePlaceHolders;
	}

	public void setPlaceHoldersUse(final boolean usePlaceHolders) {
		this.usePlaceHolders = usePlaceHolders;
	}

	public void setBook(final ItemStack book) {
		this.book = book;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(final boolean cancel) {
		this.cancel = cancel;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
