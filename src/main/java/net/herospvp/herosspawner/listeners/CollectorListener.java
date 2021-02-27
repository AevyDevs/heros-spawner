package net.herospvp.herosspawner.listeners;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import net.herospvp.herosspawner.HerosSpawner;
import net.herospvp.herosspawner.objects.Collector;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class CollectorListener implements Listener {
    private final HerosSpawner plugin;

    public CollectorListener(HerosSpawner plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
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

            Collector collector = plugin.getCollectorHandler()
                    .getCollector(FPlayers.getInstance().getByPlayer((Player) event.getWhoClicked()).getFaction().getId());

            Player player = (Player) event.getWhoClicked();
            switch (item.getType()) {
                case STAINED_GLASS_PANE: {
                    return;
                }
                case HOPPER: {
                    collector.sellAll(player);
                    return;
                }
                case TNT: {
                    collector.addTnt(player);
                    return;
                }
            }

            collector.sell(player, item.getType());
        }
    }

}
