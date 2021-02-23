package net.herospvp.herosspawner.handlers;

import net.herospvp.herosspawner.HerosSpawner;
import net.herospvp.herosspawner.tasks.SaveTask;

public class TaskHandler {
    private final HerosSpawner plugin;

    public TaskHandler(HerosSpawner plugin) {
        this.plugin = plugin;

        new SaveTask(plugin).runTaskTimerAsynchronously(plugin, 20 , 20*60*3);
    }
}
