package net.herospvp.herosspawner.handlers;

import com.google.common.collect.Maps;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import net.herospvp.herosspawner.HerosSpawner;
import net.herospvp.herosspawner.objects.Collector;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class CollectorHandler {
    private Map<String, Collector> collectors;
    private HerosSpawner plugin;

    public CollectorHandler(HerosSpawner plugin) {
        this.plugin = plugin;
        this.collectors = Maps.newHashMap();

        this.loadAll();
    }

    public Collector getCollector(String factionId) {
        return collectors.get(factionId);
    }

    public void loadAll() {
        for (Player players : Bukkit.getOnlinePlayers()) {
            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(players);
            if (fPlayer.getFaction().isWilderness()) continue;

            Faction faction = fPlayer.getFaction();

            if (getCollector(faction.getId()) == null) load(faction.getId());
        }
    }

    public void load(String factionId) {
        collectors.put(factionId, new Collector(factionId));
    }

    public void remove(String factionId) {
        collectors.remove(factionId);
    }
}
