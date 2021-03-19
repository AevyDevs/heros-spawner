package net.herospvp.herosspawner.objects;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import net.herospvp.heroscore.utils.strings.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

import java.util.Map;

@Getter
public class FactionTop {
    private final String name;
    private final Map<EntityType, Integer> spawners;
    @Setter private int position;

    public FactionTop(String name) {
        this.name = name;
        this.spawners = Maps.newHashMap();

        for (SpawnerDrop value : SpawnerDrop.values()) {
            if (value == SpawnerDrop.SILVERFISH) continue;
            spawners.put(value.getEntityType(), 0);
        }
    }

    public void addValue(EntityType type, int amount) {
        spawners.put(type, spawners.get(type)+amount);
    }

    public String format() {
        StringBuilder sb = new StringBuilder("");
        sb.append(ChatColor.YELLOW).append(name).append("\n");

        spawners.forEach((type, integer) -> sb
                .append(ChatColor.GOLD)
                .append("▎ ")
                .append(ChatColor.WHITE)
                .append(StringUtils.capitalize(type.name()).replace("_", " "))
                .append(": ")
                .append(ChatColor.YELLOW)
                .append(integer)
                .append("\n")
        );
        sb.setLength(sb.length()-1);
        return sb.toString();
    }
}
