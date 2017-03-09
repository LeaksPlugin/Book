package ro.nicuch.mobbooks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.util.StringUtil;

import net.citizensnpcs.api.CitizensAPI;
import net.milkbowl.vault.permission.Permission;

public class CommandClass implements CommandExecutor, TabCompleter {
	private final MobBooks plugin;

	public CommandClass(final MobBooks plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		if (args.length > 0) {
			switch (args[0]) {
			case "about":
				this.sendAbout(sender);
				break;
			case "reload":
				if (this.plugin.getPermission().has(sender, "mobb.command.reload")) {
					this.plugin.reloadSettings();
					sender.sendMessage(this.plugin.getMessage("lang.pluginReloaded"));
				} else
					sender.sendMessage(this.plugin.getMessage("lang.noPermission"));
				break;
			case "set":
				if (this.plugin.getPermission().has(sender, "mobb.command.set")) {
					if (hasBookInHand((Player) sender)) {
						if (CitizensAPI.getDefaultNPCSelector().getSelected(sender) != null) {
							this.plugin.getSettings().set(
									"save." + CitizensAPI.getDefaultNPCSelector().getSelected(sender).getId(),
									getBookFromHand((Player) sender));
							this.plugin.saveSettings();
							sender.sendMessage(this.plugin.getMessage("lang.setMenuForNPC").replaceFirst("%npc%",
									CitizensAPI.getDefaultNPCSelector().getSelected(sender).getFullName()));
						} else
							sender.sendMessage(this.plugin.getMessage("lang.noNPCSelected"));
					} else
						sender.sendMessage(this.plugin.getMessage("lang.noBookInHand"));
				} else
					sender.sendMessage(this.plugin.getMessage("lang.noPermission"));
				break;
			case "remove":
				if (this.plugin.getPermission().has(sender, "mobb.command.remove")) {
					if (CitizensAPI.getDefaultNPCSelector().getSelected(sender) != null) {
						if (this.plugin.getSettings()
								.isSet("save." + CitizensAPI.getDefaultNPCSelector().getSelected(sender).getId())) {
							this.plugin.getSettings().set(
									"save." + CitizensAPI.getDefaultNPCSelector().getSelected(sender).getId(), null);
							this.plugin.saveSettings();
						}
						sender.sendMessage(this.plugin.getMessage("lang.removeMenuForNPC")
								.replaceFirst("%menu%", args[1]).replaceFirst("%npc%",
										CitizensAPI.getDefaultNPCSelector().getSelected(sender).getFullName()));
					} else
						sender.sendMessage(this.plugin.getMessage("lang.noNPCSelected"));
				} else
					sender.sendMessage(this.plugin.getMessage("lang.noPermission"));
				break;
			case "getbook":
				if (this.plugin.getPermission().has(sender, "mobb.command.getbook")) {
					if (CitizensAPI.getDefaultNPCSelector().getSelected(sender) != null) {
						if (this.plugin.getSettings()
								.isSet("save." + CitizensAPI.getDefaultNPCSelector().getSelected(sender).getId())) {
							final ItemStack book = this.plugin.getSettings().getItemStack(
									"save." + CitizensAPI.getDefaultNPCSelector().getSelected(sender).getId());
							if (book != null) {
								((Player) sender).getInventory().addItem(book);
								sender.sendMessage("lang.bookRecived");
							} else
								sender.sendMessage(this.plugin.getMessage("lang.notValidBook"));
						} else
							sender.sendMessage(this.plugin.getMessage("lang.noBookForNPC").replaceFirst("%npc%",
									CitizensAPI.getDefaultNPCSelector().getSelected(sender).getFullName()));
					} else
						sender.sendMessage(this.plugin.getMessage("lang.noNPCSelected"));
				} else
					sender.sendMessage(this.plugin.getMessage("lang.noPermission"));
				break;
			case "openbook":
				if (this.plugin.getPermission().has(sender, "mobb.command.getbook")) {
					if (hasBookInHand((Player) sender)) {
						this.openBook((Player) sender, this.getBookFromHand((Player) sender));
					} else
						sender.sendMessage(this.plugin.getMessage("lang.noBookInHand"));
				} else
					sender.sendMessage(this.plugin.getMessage("lang.noPermission"));
				break;
			case "filter":
				if (args.length > 1) {
					switch (args[1]) {
					case "set":
						if (this.plugin.getPermission().has(sender, "mobb.command.filter.set")) {
							if (args.length > 2) {
								if (hasBookInHand((Player) sender)) {
									this.plugin.getAPI().createFilter(args[2], this.getBookFromHand((Player) sender));
									sender.sendMessage(this.plugin.getMessage("lang.filterSaved")
											.replaceAll("%filter_name%", args[2]));
								} else
									sender.sendMessage(this.plugin.getMessage("lang.noBookInHand"));
							} else
								sender.sendMessage(this.plugin.getMessage("lang.usage.filter.set"));
						} else
							sender.sendMessage(this.plugin.getMessage("lang.noPermission"));
						break;
					case "remove":
						if (this.plugin.getPermission().has(sender, "mobb.command.filter.remove")) {
							if (args.length > 2) {
								this.plugin.getAPI().removeFilter(args[2]);
								sender.sendMessage(this.plugin.getMessage("lang.filterRemoved")
										.replaceAll("%filter_name%", args[2]));
							} else
								sender.sendMessage(this.plugin.getMessage("lang.usage.filter.remove"));
						} else
							sender.sendMessage(this.plugin.getMessage("lang.noPermission"));
						break;
					case "getbook":
						if (this.plugin.getPermission().has(sender, "mobb.command.filter.getbook")) {
							if (args.length > 2) {
								if (this.plugin.getAPI().hasFilter(args[2])) {
									final ItemStack book = this.plugin.getAPI().getFilter(args[2]);
									if (book != null) {
										((Player) sender).getInventory().addItem(book);
										sender.sendMessage(this.plugin.getMessage("lang.bookRecived"));
									} else
										sender.sendMessage(this.plugin.getMessage("lang.notValidFilterBook"));
								} else
									sender.sendMessage(this.plugin.getMessage("lang.noBookForFilter"));
							} else
								sender.sendMessage(this.plugin.getMessage("lang.usage.filter.getbook"));
						} else
							sender.sendMessage(this.plugin.getMessage("lang.noPermission"));
						break;
					default:
						this.sendFilterHelp(sender);
						break;
					}
				} else
					this.sendFilterHelp(sender);
				break;
			default:
				if (this.plugin.getPermission().has(sender, "mobb.command.help"))
					this.sendHelp(sender);
				else
					this.sendAbout(sender);
				break;
			}
		} else {
			if (this.plugin.getPermission().has(sender, "mobb.command.help"))
				this.sendHelp(sender);
			else
				this.sendAbout(sender);
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String label,
			final String[] args) {
		final List<String> completions = new ArrayList<String>();
		final List<String> commands = new ArrayList<String>();
		final Permission perm = this.plugin.getPermission();
		if (args.length == 1) {
			if (perm.has(sender, "mobb.command.set"))
				commands.add("set");
			if (perm.has(sender, "mobb.command.remove"))
				commands.add("remove");
			if (perm.has(sender, "mobb.command.getbook"))
				commands.add("getbook");
			if (perm.has(sender, "mobb.command.openbook"))
				commands.add("openbook");
			if (perm.has(sender, "mobb.command.filter"))
				commands.add("filter");
			StringUtil.copyPartialMatches(args[0], commands, completions);
		} else if (args.length == 2) {
			if (args[0].equals("filter")) {
				if (perm.has(sender, "mobb.command.filter.set"))
					commands.add("set");
				if (perm.has(sender, "mobb.command.filter.remove"))
					commands.add("remove");
				if (perm.has(sender, "mobb.command.filter.getbook"))
					commands.add("getbook");
			}
			StringUtil.copyPartialMatches(args[1], commands, completions);
		}
		Collections.sort(completions);
		return completions;
	}

	private void sendFilterHelp(final CommandSender sender) {
		sender.sendMessage("§6===========================");
		sender.sendMessage("");
		sender.sendMessage(this.plugin.getMessageNoHeader("lang.help.filter.set"));
		sender.sendMessage(this.plugin.getMessageNoHeader("lang.help.filter.remove"));
		sender.sendMessage(this.plugin.getMessageNoHeader("lang.help.filter.getbook"));
		sender.sendMessage("");
		sender.sendMessage("§6===========================");
	}

	private boolean hasBookInHand(final Player player) {
		final ItemStack item = player.getInventory().getItemInMainHand();
		if (item == null)
			return false;
		if (!item.getType().equals(Material.WRITTEN_BOOK))
			return false;
		return true;
	}

	private ItemStack getBookFromHand(final Player player) {
		return player.getInventory().getItemInMainHand().clone();
	}

	private void openBook(final Player player, final ItemStack book) {
		final BookMeta meta = (BookMeta) book.getItemMeta();
		final ItemStack item = new ItemStack(Material.BOOK_AND_QUILL);
		item.setItemMeta(meta);
		player.getInventory().setItemInMainHand(item);
	}

	private void sendAbout(final CommandSender sender) {
		sender.sendMessage("§6===========================");
		sender.sendMessage("");
		sender.sendMessage("§b> §aMobBooks §b<");
		sender.sendMessage("§bVersion: " + this.plugin.getDescription().getVersion());
		sender.sendMessage("§bAuhtor: §cnicuch");
		sender.sendMessage("");
		sender.sendMessage("§6===========================");
	}

	private void sendHelp(final CommandSender sender) {
		sender.sendMessage("§6===========================");
		sender.sendMessage("");
		sender.sendMessage(this.plugin.getMessageNoHeader("lang.help.about"));
		sender.sendMessage(this.plugin.getMessageNoHeader("lang.help.set"));
		sender.sendMessage(this.plugin.getMessageNoHeader("lang.help.remove"));
		sender.sendMessage(this.plugin.getMessageNoHeader("lang.help.reload"));
		sender.sendMessage(this.plugin.getMessageNoHeader("lang.help.getbook"));
		sender.sendMessage(this.plugin.getMessageNoHeader("lang.help.openbook"));
		sender.sendMessage(this.plugin.getMessageNoHeader("lang.help.filter.set"));
		sender.sendMessage(this.plugin.getMessageNoHeader("lang.help.filter.remove"));
		sender.sendMessage(this.plugin.getMessageNoHeader("lang.help.filter.getbook"));
		sender.sendMessage("");
		sender.sendMessage("§6===========================");
	}
}
