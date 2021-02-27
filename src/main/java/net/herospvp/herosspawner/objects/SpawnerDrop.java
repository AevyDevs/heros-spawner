package net.herospvp.herosspawner.objects;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.Arrays;

@Getter
public enum SpawnerDrop {
    ZOMBIE(Material.ROTTEN_FLESH, EntityType.ZOMBIE, 0.5),
    SKELETON(Material.BONE, EntityType.SKELETON, 0.7),
    IRON_GOLEM(Material.IRON_INGOT, EntityType.IRON_GOLEM, 1),
    CREEPER(Material.TNT, EntityType.CREEPER, 0),
    SNOW_GOLEM(Material.PUMPKIN, EntityType.SNOWMAN, 1.3),
    VILLAGER(Material.EMERALD, EntityType.VILLAGER, 1.6);

    private final Material dropType;
    private final EntityType entityType;
    private final double money;

    SpawnerDrop(Material dropType, EntityType entityType, double money) {
        this.dropType = dropType;
        this.entityType = entityType;
        this.money = money;
    }

    public static Material getDrop(EntityType entityType) {
        for (SpawnerDrop value : values()) {
            if (entityType == value.entityType) {
                return value.dropType;
            }
        }
        return null;
    }

    public static double getPrice(Material material) {
        for (SpawnerDrop value : values()) {
            if (value.getDropType() == material) {
                return value.getMoney();
            }
        }
        return 0;
    }
}
