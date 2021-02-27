package net.herospvp.herosspawner.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

@Getter @AllArgsConstructor
public class CustomEntity {
    private final EntityType type;
    private final int amount;
    private final Location location;
}
