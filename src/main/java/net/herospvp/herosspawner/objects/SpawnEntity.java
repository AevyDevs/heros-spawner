package net.herospvp.herosspawner.objects;

import lombok.AllArgsConstructor;
import net.herospvp.herosspawner.utils.Workload;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;

@AllArgsConstructor
public class SpawnEntity implements Workload {
    private final CustomEntity customEntity;

    @Override
    public void compute() {
        LivingEntity entity = (LivingEntity) customEntity.getLocation().getWorld().spawnEntity(customEntity.getLocation(), customEntity.getType());
        entity.setCustomName(ChatColor.YELLOW + "x" + customEntity.getAmount());
        entity.setCustomNameVisible(true);
        entity.setHealth(2);
        entity.setFireTicks(80);
    }
}