package net.herospvp.herosspawner.handlers;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import net.herospvp.herosspawner.HerosSpawner;
import net.herospvp.herosspawner.objects.FactionTop;
import net.herospvp.herosspawner.objects.SpawnerDrop;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

@Setter
public class TopHandler {
    private final HerosSpawner plugin;
    private final Map<EntityType, Integer> values;
    @Getter private final Map<FactionTop, Double> cache;
    @Getter private Map<FactionTop, Double> top;
    private final Set<FactionTop> factions;

    @Getter private boolean blocked;

    public TopHandler(HerosSpawner plugin) {
        this.plugin = plugin;
        this.cache = Maps.newConcurrentMap();

        this.values = Maps.newHashMap();
        this.top = Maps.newLinkedHashMap();
        this.factions = Sets.newHashSet();

        Arrays.stream(SpawnerDrop.values())
                .forEach(value -> this.values.put(
                        value.getEntityType(),
                        plugin.getConfig().getInt("spawners." + value.getEntityType().name())
                ));
    }

    public void addValue(String name, EntityType type, int amount) {
        for (FactionTop faction : factions) {
            if (faction.getName().equals(name)) {
                faction.addValue(type, amount);
                cache.put(faction, cache.get(faction)+(values.get(type)*amount));
                return;
            }
        }

        FactionTop factionTop = new FactionTop(name);
        factionTop.addValue(type, amount);
        factions.add(factionTop);

        if (!cache.containsKey(factionTop)) {
            cache.put(factionTop, Double.valueOf(values.get(type))*amount);
        }
        else cache.put(factionTop, cache.get(factionTop)+(values.get(type)*amount));
    }

    public void clear() {
        this.cache.clear();
        this.top.clear();
    }

    public FactionTop getFactionInfo(String name) {
        for (FactionTop faction : factions) {
            if (faction.getName().equals(name)) {
                return faction;
            }
        }
        return null;
    }
}
