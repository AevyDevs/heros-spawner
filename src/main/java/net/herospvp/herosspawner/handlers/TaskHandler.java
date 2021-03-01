package net.herospvp.herosspawner.handlers;

import net.herospvp.herosspawner.HerosSpawner;
import net.herospvp.herosspawner.tasks.SaveTask;
import net.herospvp.herosspawner.tasks.SpawnerTask;

public class TaskHandler {
    private final HerosSpawner plugin;

    public TaskHandler(HerosSpawner plugin) {
        this.plugin = plugin;

        new SaveTask(plugin).runTaskTimerAsynchronously(plugin, 12000, 12000);
        new SpawnerTask(plugin).runTaskTimerAsynchronously(plugin, 200, 200);
    }
}
