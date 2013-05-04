package com.rocketmail.alextodaro.TeleItems;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TeleListener implements Listener {

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR
				|| event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack is = event.getPlayer().getItemInHand();
			if (is.equals(Main.teleMap)) {
				event.setCancelled(true);
				Player player = event.getPlayer();
				if (player.hasPermission("ti.map.use")) {
					player.sendMessage(ChatColor.GREEN
							+ "Click on an item on the top to teleport!");
					player.openInventory(Main.locInv);
				} else {
					player.sendMessage("You do not have permission to do that!");
				}
			}
		}

		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (event.getClickedBlock().getType() == Material.WALL_SIGN
					|| event.getClickedBlock().getType() == Material.SIGN_POST
					&& event.getClickedBlock().getState() instanceof Sign) {
				Sign s = (Sign) event.getClickedBlock().getState();
				if (ChatColor.stripColor(s.getLine(1)).equals("[Teleporter]")) {
					Player player = event.getPlayer();
					if (player.hasPermission("ti.sign.use")) {
						player.getInventory().addItem(Main.teleMap);
						player.sendMessage(ChatColor.GREEN
								+ "Click with this map to teleport from place to place!");
					} else {
						player.sendMessage(ChatColor.RED
								+ "You do not have permission to do that!");
					}
				}

			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (!event.getPlayer().hasPlayedBefore()
				&& event.getPlayer().hasPermission("ti.map.login")) {
			event.getPlayer().getInventory().addItem(Main.teleMap);
			event.getPlayer()
					.sendMessage(
							ChatColor.GREEN
									+ "Click with this map to teleport from place to place!");
		}
	}

	@EventHandler
	public void onSignWrite(SignChangeEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		if (block.getState() instanceof Sign) {
			Sign sign = (Sign) block.getState();
			if (event.getLine(1).equalsIgnoreCase("[teleporter]")) {
				if (player.hasPermission("ti.sign.create")) {

					event.setLine(1, ChatColor.GREEN + "[Teleporter]");
					event.setLine(0, "");
					sign.setLine(1, ChatColor.GREEN + "[Teleporter]");
					sign.update();

					player.sendMessage(ChatColor.GREEN
							+ "Sign created successfully!");
				} else {
					player.sendMessage(ChatColor.RED
							+ "You do not have permission to do that!");
					sign.getBlock().breakNaturally();
				}
			}
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Inventory invOpen = event.getInventory();
		if (invOpen.getName().equals(Main.locInv.getName())) {
			int slot = event.getSlot();
			event.setCancelled(true);
			Integer i = Integer.valueOf(slot);
			Location teleLoc = Main.itemLocations.get(i);
			if(teleLoc != null){
				player.teleport(teleLoc);
			}
		}

	}

}
