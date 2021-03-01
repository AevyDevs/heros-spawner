package net.herospvp.herosspawner.listeners;

import net.coreprotect.CoreProtect;
import net.herospvp.herosspawner.HerosSpawner;
import net.herospvp.herosspawner.objects.CustomSpawner;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.UUID;

public class ExplosionListener implements Listener {
    private final HerosSpawner plugin;
    private HashMap<Location, UUID> creeper;

    public ExplosionListener(HerosSpawner plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        this.creeper = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void on(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        this.creeper.put(event.getClickedBlock().getRelative(event.getBlockFace()).getLocation(), event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void on(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) return;
        Location location = event.getEntity().getLocation().getBlock().getLocation().clone();

        if (!creeper.containsKey(location)) return;

        UUID uuid = creeper.get(location);
        String name = Bukkit.getOfflinePlayer(uuid).getName();

        Entity entity = event.getEntity();
        entity.setMetadata("faction", new FixedMetadataValue(plugin, name));
    }

    @EventHandler
    public void onCreeper(EntityExplodeEvent event) {
        if (event.getEntity().getType() != EntityType.CREEPER) return;
        if (!event.getEntity().hasMetadata("faction")) return;

        for (Block block : event.blockList()) {
            if (block == null || block.getType() == Material.AIR) continue;

            String addition = "-";
            CustomSpawner spawner = plugin.getSpawnerHandler().getSpawner(block);
            if (spawner != null) {
                addition = addition.concat(spawner.getEntityType() + "["+spawner.getAmount()+"]");
                plugin.getSpawnerHandler().breakSpawner(spawner);
            }

            CoreProtect.getInstance().getAPI()
                        .logRemoval(event.getEntity().getMetadata("faction").get(0).asString()+addition
                                , block.getLocation(), block.getType(), block.getData());
        }
    }

    @EventHandler
    public void onTnt(EntityExplodeEvent event) {
        if (event.getEntity().getType() == EntityType.CREEPER) return;

        for (Block block : event.blockList()) {
            if (block == null || block.getType() == Material.AIR) continue;

            CustomSpawner spawner = plugin.getSpawnerHandler().getSpawner(block);
            if (spawner == null) continue;

            plugin.getSpawnerHandler().breakSpawner(spawner);
            CoreProtect.getInstance().getAPI()
                    .logRemoval("TNT-"+spawner.getEntityType() + "["+spawner.getAmount()+"]", block.getLocation(), block.getType(), block.getData());
        }
    }
}
