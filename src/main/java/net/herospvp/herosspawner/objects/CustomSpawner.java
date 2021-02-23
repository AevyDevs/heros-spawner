package net.herospvp.herosspawner.objects;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;

@Getter @Setter
public class CustomSpawner {
    private final Integer id;
    private final long factionId;
    private final EntityType entityType;
    private final Location location;
    private Integer amount;
    private boolean saved;

    public CustomSpawner(Integer id, long factionId, EntityType entityType, Integer amount, Location location, boolean saved) {
        this.id = id;
        this.factionId = factionId;
        this.entityType = entityType;
        this.amount = amount;
        this.location = location;
        this.saved = saved;
    }


}
