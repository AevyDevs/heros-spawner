package net.herospvp.herosspawner.tasks;

import com.google.common.collect.Queues;
import net.herospvp.herosspawner.HerosSpawner;
import net.herospvp.herosspawner.objects.CustomEntity;
import net.herospvp.herosspawner.objects.SpawnEntity;
import net.herospvp.herosspawner.utils.WorkloadThread;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;

public class SpawnerTask extends BukkitRunnable {
    private final HerosSpawner plugin;
    private final ArrayDeque<SpawnEntity> toSpawn;
    private final WorkloadThread thread;

    public SpawnerTask(HerosSpawner plugin) {
        this.plugin = plugin;
        this.toSpawn = Queues.newArrayDeque();
        this.thread = new WorkloadThread();
    }

    @Override
    public void run() {
        if (plugin.getSpawnerHandler().getSpawners().isEmpty()) return;

        plugin.getSpawnerHandler().getSpawners().parallelStream().forEach(spawner -> {
            if (spawner == null) return;

            Collection<Entity> entities = spawner.getLocation().getWorld().getNearbyEntities(spawner.getLocation(), 10, 10, 10);
            if (entities == null) return;

            boolean found = false;

            for (Entity nearbyEntity : entities) {
                if (nearbyEntity.getType() == EntityType.PLAYER) {
                    found = true;
                    break;
                }
            }

            if (!found) return;
            Location location = spawner.getLocation().clone().add(1.5, 0, 1.5);
            toSpawn.add(new SpawnEntity(new CustomEntity(spawner.getEntityType(), spawner.getAmount(), location)));
        });

        thread.setWorkloadDeque(toSpawn);
        Bukkit.getScheduler().runTask(plugin, thread);
        /*Bukkit.getScheduler().runTask(plugin, () -> {
            for (CustomEntity customEntity : toSpawn) {
                LivingEntity entity = (LivingEntity) customEntity.getLocation().getWorld().spawnEntity(customEntity.getLocation(), customEntity.getType());
                entity.setCustomName(ChatColor.YELLOW + "x" + customEntity.getAmount());
                entity.setCustomNameVisible(true);
                entity.setHealth(2);
                entity.setFireTicks(80);
            }
            toSpawn.clear();
        });*/
    }

    public static String toString(Queue<?> queue) {
        String string = "{";
        for (Object o : queue) {
            if (o == null) string = string.concat(null + ", ");
            else string = string.concat(o.toString()+", ");
        }
        string = string.concat("}");
        return string;
    }
}
