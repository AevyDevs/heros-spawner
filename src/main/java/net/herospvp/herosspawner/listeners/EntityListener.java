package net.herospvp.herosspawner.listeners;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import net.herospvp.heroscore.utils.strings.StringUtils;
import net.herospvp.herosspawner.HerosSpawner;
import net.herospvp.herosspawner.objects.Collector;
import net.herospvp.herosspawner.objects.SpawnerDrop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class EntityListener implements Listener {
    private HerosSpawner plugin;

    public EntityListener(HerosSpawner plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(EntityDeathEvent event) {
        Faction faction = Board.getInstance().getFactionAt(new FLocation(event.getEntity().getLocation()));
        if (event.getEntity().getCustomName() == null) return;

        if (SpawnerDrop.getDrop(event.getEntityType()) == null) return;

        event.getDrops().clear();

        int amount = 1;
        String displayNameStripped = ChatColor.stripColor(event.getEntity().getCustomName()).replace("x", "");
        if (StringUtils.isNumber(displayNameStripped)) {
            amount = Integer.parseInt(displayNameStripped);
        }

        EntityType entityType = event.getEntityType();
        ItemStack drop = SpawnerDrop.getDrop(event.getEntityType());
        if (drop == null) return;

        // Silverfish check
        if (entityType == EntityType.SILVERFISH) {
            event.getDrops().add(drop);
            return;
        }

        if (faction.isWilderness()) return;
        Collector collector = plugin.getCollectorHandler().getCollector(faction.getId());
        if (collector == null) return;

        collector.addDrop(drop.getType(), amount);
    }
}
