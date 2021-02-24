package net.herospvp.herosspawner.listeners;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.event.FactionCreateEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import net.herospvp.herosspawner.HerosSpawner;
import net.herospvp.herosspawner.objects.CustomSpawner;
import org.bukkit.Bukkit;
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
            if ((spawner.getFactionId() + "").equals(event.getFaction().getId())) {
                plugin.getSpawnerHandler().breakSpawner(spawner);
            }
        }

        plugin.getCollectorHandler().remove(event.getFaction().getId());
    }

    @EventHandler
    public void on(FactionCreateEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            plugin.getCollectorHandler().load(Factions.getInstance().getByTag(event.getFactionTag()).getId());
        }, 10);

    }


}