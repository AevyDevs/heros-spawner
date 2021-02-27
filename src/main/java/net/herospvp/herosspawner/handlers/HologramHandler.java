package net.herospvp.herosspawner.handlers;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.collect.Maps;
import net.herospvp.heroscore.utils.strings.StringUtils;
import net.herospvp.herosspawner.HerosSpawner;
import net.herospvp.herosspawner.objects.CustomSpawner;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.Map;

public class HologramHandler {
    private HerosSpawner plugin;
    private Map<Integer, Hologram> holograms;

    public HologramHandler(HerosSpawner plugin) {
        this.plugin = plugin;
        this.holograms = Maps.newHashMap();
    }

    public void load(Collection<CustomSpawner> spawners) {
        purge();

        Bukkit.getScheduler().runTask(plugin, () -> {
            for (CustomSpawner spawner : spawners) {
                createHologram(spawner);
            }
        });
    }

    public void createHologram(CustomSpawner spawner) {
        if (spawner == null || spawner.getLocation() == null || spawner.getLocation().getWorld() == null) return;

        Hologram hologram = HologramsAPI.createHologram(plugin, spawner.getLocation().clone().add(0.5,1.5,0.5));

        hologram.appendTextLine(StringUtils.c("&e&l「&r&e " + StringUtils.capitalize(spawner.getEntityType().name()) + " &6x" + spawner.getAmount() + "&e&l 」"));
        holograms.put(spawner.getId(), hologram);
    }

    public void updateHologram(CustomSpawner spawner) {
        Hologram hologram = holograms.get(spawner.getId());

        hologram.appendTextLine(StringUtils.c("&e&l「&r&e " + StringUtils.capitalize(spawner.getEntityType().name()) + " &6x" + spawner.getAmount() + "&e&l 」"));
        hologram.removeLine(0);
    }

    public void removeHologram(int spawnerId) {
        holograms.get(spawnerId).delete();
    }

    public void purge() {
        for (Hologram hologram : HologramsAPI.getHolograms(plugin)) {
            hologram.delete();
        }
    }
}
