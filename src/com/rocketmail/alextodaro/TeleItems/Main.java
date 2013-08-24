package com.rocketmail.alextodaro.TeleItems;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	public File locationFile;
	public YamlConfiguration locations;
	public int size;
	public static Inventory locInv;
	public static HashMap<Integer, Location> itemLocations = new HashMap<Integer, Location>();
	public static ItemStack teleMap = new ItemStack(2256);

	public void saveLocationFile() {
		Bukkit.getServer().getPluginManager()
				.registerEvents(new TeleListener(), this);

		ItemMeta tmim = teleMap.getItemMeta();
		List<String> tmLoreList = new ArrayList<String>();
		tmLoreList.add("Handy Teleporter!");
		tmim.setLore(tmLoreList);
		tmim.setDisplayName("Teleporter");
		teleMap.setItemMeta(tmim);

		try {
			locations.save(locationFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reloadLocations() {
		try {
			locationFile = new File(getDataFolder(), "locations.yml");
			if (!locationFile.exists()) {
				locationFile.getParentFile().mkdirs();
				locationFile.createNewFile();
			}
			locations = YamlConfiguration.loadConfiguration(locationFile);
			if (!locations.contains("inventorysize")) {
				locations.set("inventorysize", 1);
			}
			if (!locations.contains("inventoryname")) {
				locations.set("inventoryname", "Locations");
			}
			if (!locations.contains("items")) {
				locations.createSection("items");
			}
			ConfigurationSection items = locations
					.getConfigurationSection("items");
			for (int size = (locations.getInt("inventorysize") * 9), i = 0; i < size; i++) {
				if (!items.contains("" + i)) {
					ConfigurationSection sec = items.createSection("" + i);
					sec.set("id", "0");
					sec.set("name", "");
					sec.set("location.x", "0");
					sec.set("location.y", "0");
					sec.set("location.z", "0");
					sec.set("location.world", "");
					sec.set("description", "");
				}
			}

			saveLocationFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		size = locations.getInt("inventorysize") * 9;
		locInv = Bukkit.createInventory(null, size,
				locations.getString("inventoryname"));
		ConfigurationSection items = locations.getConfigurationSection("items");
		for (int i = 0; i < size; i++) {
			ConfigurationSection sec = items.getConfigurationSection("" + i);
			int id = Integer.valueOf(sec.getString("id"));
			String name = sec.getString("name");
			int x = Integer.valueOf(sec.getString("location.x"));
			int y = Integer.valueOf(sec.getString("location.y"));
			int z = Integer.valueOf(sec.getString("location.z"));
			String world = sec.getString("location.world");
			String lores = sec.getString("description");
			if (id != 0 && Material.getMaterial(id) != null
					&& name.length() > 0) {
				ItemStack is = new ItemStack(id);
				ItemMeta im = is.getItemMeta();
				im.setDisplayName(name);
				List<String> lorelist = new ArrayList<String>();
				if (lores.length() > 0) {
					for (String s : lores.split(",")) {
						lorelist.add(s);
					}
				}
				im.setLore(lorelist);
				is.setItemMeta(im);
				locInv.setItem(i, is);
				World w = Bukkit.getWorld(world);
				getLogger().info(
						"Loading location " + i + ", " + w.getName() + ", " + x
								+ ", " + y + ", " + z);
				itemLocations.put(Integer.valueOf(i), new Location(w, x, y, z));
			}

		}
	}

	public void onEnable() {
		reloadLocations();

	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (label.equalsIgnoreCase("ti")) {
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("reload")) {
					if (sender.hasPermission("ti.reload")) {
						sender.sendMessage("Reloadingpeeeeee..");
						reloadLocations();
						return true;
					}
					sender.sendMessage(ChatColor.RED
							+ "You do not have permission to do that!");
					return true;
				}
			}
		}
		return false;
	}
}
