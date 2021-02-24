package net.herospvp.herosspawner.listeners;

import net.herospvp.herosspawner.HerosSpawner;
import net.herospvp.herosspawner.objects.CustomSpawner;
import net.prosavage.factionsx.event.FactionCreateEvent;
import net.prosavage.factionsx.event.FactionDisbandEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FactionListener implements Listener {
    private HerosSpawner plugin;

    public FactionListener(HerosSpawner plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void on(FactionDisbandEvent event) {
        for (CustomSpawner spawner : plugin.getSpawnerHandler().getSpawners()) {
            if (spawner.getFactionId() == event.getFaction().getId()) {
                plugin.getSpawnerHandler().breakSpawner(spawner);
            }
        }

        plugin.getCollectorHandler().remove(event.getFaction().getId());
    }

    @EventHandler
    public void on(FactionCreateEvent event) {
        plugin.getCollectorHandler().load(event.getFaction().getId());
    }


}
