package net.herospvp.herosspawner;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import lombok.Getter;
import net.herospvp.database.Director;
import net.herospvp.database.Musician;
import net.herospvp.herosspawner.commands.SpawnerCommand;
import net.herospvp.herosspawner.handlers.SpawnerHandler;
import net.herospvp.herosspawner.handlers.TaskHandler;
import net.herospvp.herosspawner.listeners.SpawnerListener;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Getter
public final class HerosSpawner extends JavaPlugin implements Listener{

    // Database cores
    private Director director;
    private Musician musician;

    private TaskHandler taskHandler;
    private SpawnerHandler spawnerHandler;

    private WorldGuardPlugin worldGuardPlugin;

    @Override
    public void onEnable() {
        // Config
        saveDefaultConfig();

        // Database
        this.initializeDatabase();

        this.spawnerHandler = new SpawnerHandler(this);
        this.taskHandler = new TaskHandler(this);

        new SpawnerCommand(this);

        new SpawnerListener(this);
    }

    @Override
    public void onDisable() {
        spawnerHandler.save();
    }

    /**
     * Initialize the database with values in the config.yml
     */
    private void initializeDatabase() {
        this.director = new Director();

        String password = getConfig().getString("db.password");
        net.herospvp.database.items.Instrument instrument = new net.herospvp.database.items.Instrument(
                null, getConfig().getString("db.ip"), getConfig().getString("db.port"),
                getConfig().getString("db.database"), getConfig().getString("db.user"), password.equals("null") ? null : password,
                getConfig().getString("db.url"), getConfig().getString("db.driver"), null, true,
                getConfig().getInt("db.max-pool-size")
        );
        instrument.assemble();

        this.director.addInstrument("heros-spawners", instrument);
        this.musician = new Musician(director, instrument, getConfig().getBoolean("db.debug"));
    }
}
