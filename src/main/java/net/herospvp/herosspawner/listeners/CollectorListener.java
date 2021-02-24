package net.herospvp.herosspawner.listeners;

import net.herospvp.herosspawner.HerosSpawner;
import net.prosavage.factionsx.core.FPlayer;
import net.prosavage.factionsx.core.Faction;
import net.prosavage.factionsx.manager.PlayerManager;
import net.prosavage.factionsx.persist.data.Players;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CollectorListener implements Listener {
    private final HerosSpawner plugin;

    public CollectorListener(HerosSpawner plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        FPlayer fPlayer = PlayerManager.INSTANCE.getFPlayer(event.getPlayer().getUniqueId());
        if (fPlayer.getFaction().isWilderness()) return;

        Faction faction = fPlayer.getFaction();
        if (plugin.getCollectorHandler().getCollector(faction.getId()) == null) {
            plugin.getCollectorHandler().load(faction.getId());
        }
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        if (event.getInventory() == null|| event.getInventory().getTitle() == null) return;

        if (event.getInventory().getTitle().contains("Collector")) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);

            ItemStack item = event.getCurrentItem();
            if (item == null) return;

            if (item.getType() == Material.STAINED_GLASS_PANE) return;
            if (item.getType() == Material.HOPPER) return;
            if (item.getType() == Material.TNT) return;

            plugin.getCollectorHandler().getCollector(PlayerManager.INSTANCE.getFPlayer(event.getWhoClicked().getUniqueId()).getFaction().getId()).sell(item.getType());
        }
    }

}
