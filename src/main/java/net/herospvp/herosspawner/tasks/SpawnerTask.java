package net.herospvp.herosspawner.tasks;

import com.google.common.collect.Lists;
import net.herospvp.herosspawner.HerosSpawner;
import net.herospvp.herosspawner.objects.CustomEntity;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

public class SpawnerTask extends BukkitRunnable {
    private final HerosSpawner plugin;
    private final List<CustomEntity> toSpawn;

    public SpawnerTask(HerosSpawner plugin) {
        this.plugin = plugin;
        this.toSpawn = Lists.newArrayList();
    }

    @Override
    public void run() {
        if (plugin.getSpawnerHandler().getSpawners().isEmpty()) return;

        plugin.getSpawnerHandler().getSpawners().parallelStream().forEach(spawner -> {
            if (spawner == null || spawner.getLocation() == null || spawner.getLocation().getWorld() == null) return;
            Location spawnerLocation = spawner.getLocation();

            Collection<Entity> entities = spawnerLocation.getWorld().getNearbyEntities(spawner.getLocation(), 10, 10, 10);
            if (entities == null) return;

            Optional<Entity> optionalEntity =
                    entities.stream()
                            .filter(
                                    entity -> entity.getType() == EntityType.PLAYER
                            ).findFirst();

            if (!optionalEntity.isPresent()) {
                return;
            }

            int y=0;
            if (spawner.getEntityType() == EntityType.SILVERFISH) y=1;

            Location location = spawnerLocation.clone().add(1.5, y, 1.5);
            toSpawn.add(new CustomEntity(spawner.getEntityType(), spawner.getAmount(), location));
        });

        int moments = toSpawn.size()/10+1;
        final int[] count = {0};

        new BukkitRunnable() {
            @Override
            public void run() {
                if (toSpawn.isEmpty()) {
                    cancel();
                    return;
                }

                if (count[0] == 10) {
                    cancel();
                    return;
                }

                for (int i=0; i<moments; i++) {
                    if (toSpawn.isEmpty()) {
                        cancel();
                        return;
                    }

                    CustomEntity customEntity = toSpawn.get(0);
                    if (customEntity == null || customEntity.getLocation() == null) {
                        toSpawn.remove(0);
                        continue;
                    }

                    LivingEntity entity = (LivingEntity) customEntity.getLocation().getWorld().spawnEntity(customEntity.getLocation(), customEntity.getType());

                    if (entity.getType() != EntityType.SILVERFISH) {
                        entity.setCustomName(ChatColor.YELLOW + "x" + customEntity.getAmount());
                        entity.setCustomNameVisible(true);
                        entity.setHealth(2);
                        entity.setFireTicks(80);
                    } else {
                        entity.setCustomName(ChatColor.YELLOW + "Silverfish");
                        entity.setCustomNameVisible(true);
                        entity.setHealth(0.1);
                    }

                    toSpawn.remove(0);
                }

                count[0]++;
            }
        }.runTaskTimer(plugin, 2, 5);


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
