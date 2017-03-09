package ro.nicuch.mobbooks;

import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import me.clip.placeholderapi.PlaceholderAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.minecraft.server.v1_11_R1.PacketDataSerializer;
import net.minecraft.server.v1_11_R1.PacketPlayOutCustomPayload;

public class EventClass implements Listener {
	private final MobBooks plugin;

	public EventClass(final MobBooks plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void event(final NPCRightClickEvent event) {
		ItemStack book = this.plugin.getSettings().getItemStack("save." + event.getNPC().getId());
		final BookNPCRightClickEvent e = new BookNPCRightClickEvent(event.getClicker(), event.getNPC(), book);
		this.plugin.getServer().getPluginManager().callEvent(e);
		if (e.isCancelled())
			return;
		book = e.getBook();
		if (book == null)
			return;
		if (e.usePlaceHolders())
			this.openBook(event.getClicker(), this.placeholderHook(event.getClicker(), book));
		else
			this.openBook(event.getClicker(), book);
	}

	private void openBook(final Player player, final ItemStack book) {
		int slot = player.getInventory().getHeldItemSlot();
		ItemStack old = player.getInventory().getItem(slot);
		player.getInventory().setItem(slot, book);
		ByteBuf buf = Unpooled.buffer(256);
		buf.setByte(0, (byte) 0);
		buf.writerIndex(1);
		PacketPlayOutCustomPayload packet = new PacketPlayOutCustomPayload("MC|BOpen", new PacketDataSerializer(buf));
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		player.getInventory().setItem(slot, old);
	}

	private ItemStack placeholderHook(final Player player, final ItemStack item) {
		if (!this.plugin.isPlaceHolderEnabled())
			return item;
		final BookMeta meta = (BookMeta) item.getItemMeta();
		meta.setPages(PlaceholderAPI.setPlaceholders(player, meta.getPages()));
		item.setItemMeta(meta);
		return item;
	}
}
