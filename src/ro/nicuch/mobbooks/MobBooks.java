package ro.nicuch.mobbooks;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.permission.Permission;

public class MobBooks extends JavaPlugin {
	private YamlConfiguration SETTINGS;
	private YamlConfiguration DEFAULTS;
	private Permission PERMISSION;
	private boolean placeholder;
	private MobBooksAPI api;

	@Override
	public void onEnable() {
		this.reloadSettings();
		final PluginManager manager = this.getServer().getPluginManager();
		if (!manager.isPluginEnabled("Citizens")) {
			this.getLogger().warning("Citizens not enabled, plugin disabled!");
			this.setEnabled(false);
		}
		if (!manager.isPluginEnabled("Vault")) {
			this.getLogger().warning("Vault not enabled, plugin disabled!");
			this.setEnabled(false);
		}
		if (!manager.isPluginEnabled("PlaceholderAPI")) {
			this.getLogger().info("PlaceholderAPI not found!");
		} else {
			this.getLogger().info("PlaceholderAPI found, try hooking!");
			this.placeholder = true;
		}
		this.PERMISSION = this.getServer().getServicesManager().getRegistration(Permission.class).getProvider();
		this.getServer().getPluginManager().registerEvents(new EventClass(this), this);
		final CommandClass cmd = new CommandClass(this);
		this.getCommand("mobb").setExecutor(cmd);
		this.getCommand("mobb").setTabCompleter(cmd);
		this.api = new MobBooksAPI(this);
	}

	public MobBooksAPI getAPI() {
		return this.api;
	}

	@Override
	public void onDisable() {
		this.saveSettings();
	}

	public YamlConfiguration getSettings() {
		return this.SETTINGS;
	}

	private YamlConfiguration getDefSettings() {
		return this.DEFAULTS;
	}

	@SuppressWarnings("deprecation")
	public void reloadSettings() {
		final File cfgFile = new File(this.getDataFolder() + File.separator + "config.yml");
		if (!cfgFile.exists())
			this.saveResource("config.yml", false);
		this.SETTINGS = YamlConfiguration.loadConfiguration(cfgFile);
		try {
			this.DEFAULTS = YamlConfiguration.loadConfiguration(this.getResource("config.yml"));
		} catch (final Exception e) {
			this.getLogger().warning("The default configuration didn/'t load, don/'t know what happened!");
			this.getLogger()
					.warning("This can generate NullPointerException if the config file is not properly configured!");
		}
		if (this.SETTINGS.getInt("version", 0) != 2) {
			try {
				this.SETTINGS.save(
						new File(this.getDataFolder() + File.separator + System.currentTimeMillis() + "_config.yml"));
			} catch (final Exception e) {
				e.printStackTrace();
				this.getLogger().warning("Maybe you don/'t have enought space left on disk?");
			}
			final ConfigurationSection saves = this.SETTINGS.getConfigurationSection("save");
			final ConfigurationSection filters = this.SETTINGS.getConfigurationSection("filters");
			this.saveResource("config.yml", true);
			this.SETTINGS = YamlConfiguration.loadConfiguration(cfgFile);
			if (saves != null)
				this.SETTINGS.set("save", saves);
			if (filters != null)
				this.SETTINGS.set("filters", filters);
			this.saveSettings();
			this.getLogger().info("New config.yml file generated because config version was changed.");
		}
	}

	public void saveSettings() {
		try {
			this.SETTINGS.save(new File(this.getDataFolder() + File.separator + "config.yml"));
		} catch (final Exception e) {
			e.printStackTrace();
			this.getLogger().warning("Maybe you don/'t have enought space left on disk?");
		}
	}

	public Permission getPermission() {
		return this.PERMISSION;
	}

	public boolean isPlaceHolderEnabled() {
		return this.placeholder;
	}

	public String getMessage(final String path) {
		return ChatColor.translateAlternateColorCodes('&',
				this.getSettings().getString("lang.header", this.getDefSettings().getString("lang.header")))
				+ ChatColor.translateAlternateColorCodes('&',
						this.getSettings().getString(path, this.getDefSettings().getString(path)));
	}

	public String getMessageNoHeader(final String path) {
		return ChatColor.translateAlternateColorCodes('&',
				this.getSettings().getString(path, this.getDefSettings().getString(path)));
	}
}
