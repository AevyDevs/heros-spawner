package net.herospvp.herosspawner.tasks;

import lombok.AllArgsConstructor;
import net.herospvp.herosspawner.HerosSpawner;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
public class SaveTask extends BukkitRunnable {
    private final HerosSpawner plugin;

    @Override
    public void run() {
        plugin.getSpawnerHandler().save();
    }
}
