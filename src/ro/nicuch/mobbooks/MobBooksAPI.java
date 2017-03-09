package ro.nicuch.mobbooks;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MobBooksAPI {
	private final MobBooks plugin;

	public MobBooksAPI(final MobBooks plugin) {
		this.plugin = plugin;
	}

	public ItemStack getFilter(final String filterName) {
		return this.plugin.getSettings().getItemStack("filters." + filterName, new ItemStack(Material.WRITTEN_BOOK));
	}

	public boolean hasFilter(final String filterName) {
		return this.plugin.getSettings().isSet("filters." + filterName);
	}

	public void createFilter(final String filterName, final ItemStack book) {
		this.plugin.getSettings().set("filters." + filterName, book);
		this.plugin.saveSettings();
	}

	public void removeFilter(final String filterName) {
		this.plugin.getSettings().set("filters." + filterName, null);
		this.plugin.saveSettings();
	}
}
