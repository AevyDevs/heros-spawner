package net.herospvp.herosspawner.listeners;

import net.herospvp.heroscore.utils.strings.Debug;
import net.herospvp.herosspawner.HerosSpawner;
import net.herospvp.herosspawner.objects.SpawnerItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class SpawnerListener implements Listener {
    private HerosSpawner plugin;

    public SpawnerListener(HerosSpawner plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void on(BlockPlaceEvent event) {
        if (event.getItemInHand().getType() != Material.MOB_SPAWNER) return;

        if (event.getBlockAgainst().getType() == Material.MOB_SPAWNER) {
            Debug.send("heros-spawner", "add amount block");
            plugin.getSpawnerHandler().addAmount(event.getPlayer(), event.getBlockAgainst());
            event.setCancelled(true);
            return;
        }

        plugin.getSpawnerHandler().place(event.getPlayer(), event.getItemInHand(), event.getBlockPlaced());
    }

    @EventHandler
    public void onMammt(PlayerDropItemEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getSpawnerHandler().save();
        });
    }
}
