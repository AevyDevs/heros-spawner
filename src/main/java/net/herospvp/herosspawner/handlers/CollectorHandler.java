package net.herospvp.herosspawner.handlers;

import com.google.common.collect.Maps;
import net.herospvp.herosspawner.HerosSpawner;
import net.herospvp.herosspawner.objects.Collector;
import net.prosavage.factionsx.core.FPlayer;
import net.prosavage.factionsx.core.Faction;
import net.prosavage.factionsx.manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class CollectorHandler {
    private Map<Long, Collector> collectors;
    private HerosSpawner plugin;

    public CollectorHandler(HerosSpawner plugin) {
        this.plugin = plugin;
        this.collectors = Maps.newHashMap();

        this.loadAll();
    }

    public Collector getCollector(long factionId) {
        return collectors.get(factionId);
    }

    public void loadAll() {
        for (Player players : Bukkit.getOnlinePlayers()) {
            FPlayer fPlayer = PlayerManager.INSTANCE.getFPlayer(players.getUniqueId());
            if (fPlayer.getFaction().isWilderness()) continue;

            Faction faction = fPlayer.getFaction();

            if (getCollector(faction.getId()) == null) load(faction.getId());
        }
    }

    public void load(long factionId) {
        collectors.put(factionId, new Collector(factionId));
    }

    public void remove(long factionId) {
        collectors.remove(factionId);
    }
}
