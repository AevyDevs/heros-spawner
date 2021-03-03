package net.herospvp.herosspawner.tasks;

import com.google.common.collect.Queues;
import net.herospvp.herosspawner.HerosSpawner;
import net.herospvp.herosspawner.objects.CustomEntity;
import net.herospvp.herosspawner.objects.SpawnEntity;
import net.herospvp.herosspawner.utils.Workload;
import net.herospvp.herosspawner.utils.WorkloadManager;
import net.herospvp.herosspawner.utils.WorkloadTask;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Optional;
import java.util.Queue;

public class SpawnerTask extends BukkitRunnable {
    private final HerosSpawner plugin;
    private final Queue<WorkloadTask> toSpawn;
    private final WorkloadManager thread;

    public SpawnerTask(HerosSpawner plugin) {
        this.plugin = plugin;
        this.toSpawn = Queues.newConcurrentLinkedQueue();
        this.thread = new WorkloadManager(plugin);
    }

    @Override
    public void run() {
        if (plugin.getSpawnerHandler().getSpawners().isEmpty()) return;

        plugin.getSpawnerHandler().getSpawners().parallelStream().forEach(spawner -> {
            if (spawner == null || spawner.getLocation() == null) return;
            Location spawnerLocation = spawner.getLocation();

            Collection<Entity> entities = spawnerLocation.getWorld().getNearbyEntities(spawner.getLocation(), 10, 10, 10);
            if (entities == null) return;

            // Better way to search, TODO: testing
            Optional<Entity> optionalEntity =
                    entities.stream()
                            .filter(
                                    entity -> entity.getType() == EntityType.PLAYER
                            ).findFirst();

            if (!optionalEntity.isPresent()) {
                return;
            }

            /*
            boolean found = false;

            for (Entity nearbyEntity : entities) {
                if (nearbyEntity.getType() == EntityType.PLAYER) {
                    found = true;
                    break;
                }
            }

            if (!found) return; */
            Location location = spawnerLocation.clone().add(1.5, 0, 1.5);
            toSpawn.add(new SpawnEntity(new CustomEntity(spawner.getEntityType(), spawner.getAmount(), location)));
        });

        Workload workload = new Workload(toSpawn, () -> { });
        plugin.getWorkloadManager().addWorkload(workload);

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
