package net.herospvp.herosspawner.listeners;

import net.herospvp.heroscore.utils.strings.Debug;
import net.herospvp.herosspawner.HerosSpawner;
import net.herospvp.herosspawner.objects.CustomSpawner;
import net.herospvp.herosspawner.objects.SpawnerItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void on(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.MOB_SPAWNER) return;

        CustomSpawner spawner = plugin.getSpawnerHandler().getSpawner(event.getBlock());
        plugin.getSpawnerHandler().remove(event.getPlayer(), spawner);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void on(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock().getType() != Material.MOB_SPAWNER) return;

        EntityType type = SpawnerItem.getType(event.getItem());
        if (type == null) return;

        CreatureSpawner spawner = (CreatureSpawner) event.getClickedBlock().getState();

        if (type == spawner.getSpawnedType()) {
            plugin.getSpawnerHandler().addAmount(event.getPlayer(), event.getClickedBlock());
            event.setCancelled(true);
        }
    }
}