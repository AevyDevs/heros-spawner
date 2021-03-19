package net.herospvp.herosspawner.tasks;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import net.herospvp.heroscore.utils.strings.StringUtils;
import net.herospvp.herosspawner.HerosSpawner;
import net.herospvp.herosspawner.handlers.TopHandler;
import net.herospvp.herosspawner.hooks.TopExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TopTask extends BukkitRunnable {
    private final HerosSpawner plugin;

    public TopTask(HerosSpawner plugin) {
        this.plugin = plugin;
        new TopExpansion(plugin).register();
    }

    @Override
    public void run() {
        TopHandler handler = plugin.getTopHandler();
        handler.setBlocked(true);
        handler.clear();

        Bukkit.broadcastMessage(StringUtils.c("&6(( &e&l&oTop &6)) &fInizio calcolo della &etop&f... "));

        if (!plugin.getSpawnerHandler().getSpawners().isEmpty()) {
            plugin.getSpawnerHandler()
                    .getSpawners()
                    .forEach(spawner -> {
                        Faction faction = Factions.getInstance().getFactionById(spawner.getFactionId());
                        if (faction == null) return;
                        if (spawner.getEntityType() == EntityType.SILVERFISH) return;

                        handler.addValue(faction.getTag(), spawner.getEntityType(), spawner.getAmount());
                    });

            if (!handler.getCache().isEmpty()) {
                handler.setTop(handler.getCache()
                        .entrySet()
                        .stream()
                        .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (e1, e2) -> e1,
                                LinkedHashMap::new)
                        ));

                final int[] pos = {1};
                handler.getTop().forEach((factionTop, aDouble) -> {
                    factionTop.setPosition(pos[0]);
                    pos[0]++;
                });
            }
        }

        handler.setBlocked(false);
        Bukkit.broadcastMessage(StringUtils.c("&6(( &e&l&oTop &6)) &eTop &fcalcolata con successo! "));
    }
}
