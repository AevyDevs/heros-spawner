package net.herospvp.herosspawner.tasks;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import net.herospvp.herosspawner.HerosSpawner;
import net.herospvp.herosspawner.objects.CustomEntity;
import net.herospvp.herosspawner.objects.CustomSpawner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.Collection;
import java.util.List;

public class SpawnerTask extends BukkitRunnable {
    private final HerosSpawner plugin;
    private final List<CustomEntity> toSpawn;

    public SpawnerTask(HerosSpawner plugin) {
        this.plugin = plugin;
        this.toSpawn = Lists.newArrayList();
    }

    @Override
    public void run() {
        for (CustomSpawner spawner : plugin.getSpawnerHandler().getSpawners()) {
            Collection<Entity> entities = spawner.getLocation().getWorld().getNearbyEntities(spawner.getLocation(), 10, 10, 10);
            if (entities == null) continue;

            boolean found = false;

            for (Entity nearbyEntity : entities) {
                if (nearbyEntity.getType() == EntityType.PLAYER) {
                    found = true;
                    break;
                }
            }

            if (!found) return;
            Location location = spawner.getLocation().clone().add(1.5, 0, 1.5);
            toSpawn.add(new CustomEntity(spawner.getEntityType(), spawner.getAmount(), location));
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            for (CustomEntity customEntity : toSpawn) {
                LivingEntity entity = (LivingEntity) customEntity.getLocation().getWorld().spawnEntity(customEntity.getLocation(), customEntity.getType());
                entity.setCustomName(ChatColor.YELLOW + "x" + customEntity.getAmount());
                entity.setCustomNameVisible(true);
                entity.setHealth(2);
                entity.setFireTicks(80);
            }
            toSpawn.clear();
        });
    }
}
