package net.herospvp.herosspawner.listeners;

import net.herospvp.herosspawner.HerosSpawner;
import net.herospvp.herosspawner.objects.SpawnerDrop;
import net.prosavage.factionsx.core.Faction;
import net.prosavage.factionsx.manager.FactionManager;
import net.prosavage.factionsx.manager.GridManager;
import net.prosavage.factionsx.persist.data.Factions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

public class EntityListener implements Listener {
    private HerosSpawner plugin;

    public EntityListener(HerosSpawner plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void on(EntityDeathEvent event) {
        Faction faction = GridManager.INSTANCE.getFactionAt(event.getEntity().getLocation().getChunk());
        if (event.getEntity().getCustomName() == null) return;

        if (SpawnerDrop.getDrop(event.getEntityType()) == null) return;

        event.getDrops().clear();

        int amount = Integer.parseInt(event.getEntity().getCustomName().replace("x", ""));
        plugin.getCollectorHandler().getCollector(faction.getId()).addDrop(SpawnerDrop.getDrop(event.getEntityType()), amount);
    }
}
