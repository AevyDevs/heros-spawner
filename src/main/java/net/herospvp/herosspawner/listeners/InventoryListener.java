package net.herospvp.herosspawner.listeners;

import net.herospvp.heroscore.utils.strings.message.Message;
import net.herospvp.heroscore.utils.strings.message.MessageType;
import net.herospvp.herosspawner.HerosSpawner;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {
    private HerosSpawner plugin;

    public InventoryListener(HerosSpawner plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void on(InventoryPickupItemEvent event) {
        if (event.getInventory().getType() == InventoryType.HOPPER && event.getItem().getItemStack().getType() == Material.MOB_SPAWNER) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();

        if (player.hasPermission("herospvp.admin")) return;

        if (item == null || item.getType() != Material.MOB_SPAWNER) {
            if (event.getClick() != ClickType.NUMBER_KEY) {
                return;
            }

            item = player.getInventory().getItem(event.getHotbarButton());
            if (item == null) return;
        }

        if (item.getType() == Material.MOB_SPAWNER && event.getInventory() != null && event.getInventory().getType() != InventoryType.CRAFTING) {
            if (event.getInventory().getType() == InventoryType.CREATIVE || event.getInventory().getType() == InventoryType.PLAYER) return;

            event.setResult(Event.Result.DENY);
            event.setCancelled(true);
            Message.sendMessage(player, MessageType.ERROR, "Spawner", "Gli spawner non possono essere messi nei contenitori! &7&oPuoi solo piazzarli;");
        }
    }
}
