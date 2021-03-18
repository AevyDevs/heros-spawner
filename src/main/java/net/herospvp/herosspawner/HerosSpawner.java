package net.herospvp.herosspawner;

import lombok.Getter;
import lombok.SneakyThrows;
import net.herospvp.database.DatabaseLib;
import net.herospvp.database.lib.Director;
import net.herospvp.database.lib.Musician;
import net.herospvp.database.lib.items.Instrument;
import net.herospvp.herosspawner.commands.CollectorCommand;
import net.herospvp.herosspawner.commands.SpawnerCommand;
import net.herospvp.herosspawner.commands.TopCommand;
import net.herospvp.herosspawner.handlers.*;
import net.herospvp.herosspawner.listeners.*;
import net.herospvp.herosspawner.tasks.TopTask;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class HerosSpawner extends JavaPlugin implements Listener{

    // Database cores
    private Director director;
    private Musician musician;

    private TaskHandler taskHandler;
    private HologramHandler hologramHandler;
    private SpawnerHandler spawnerHandler;
    private CollectorHandler collectorHandler;
    private TopHandler topHandler;

    @Override
    public void onEnable() {
        // Config
        saveDefaultConfig();

        // Database
        this.initializeDatabase();

        this.spawnerHandler = new SpawnerHandler(this);
        this.hologramHandler = new HologramHandler(this);
        this.taskHandler = new TaskHandler(this);
        this.collectorHandler = new CollectorHandler(this);
        this.topHandler = new TopHandler(this);
        spawnerHandler.loadAll(result -> {
            this.hologramHandler.load(result);
            new TopTask(this)
                    .runTaskTimerAsynchronously(this, 1, 20*60*30);
        });

        new SpawnerCommand(this);
        new CollectorCommand(this);
        new TopCommand(this);

        new SpawnerListener(this);
        new FactionListener(this);
        new EntityListener(this);
        new CollectorListener(this);
        new ExplosionListener(this);
        new HeadListener(this);
        new InventoryListener(this);
    }

    @SneakyThrows
    @Override
    public void onDisable() {
        spawnerHandler.saveAll();
    }

    /**
     * Initialize the database with values in the config.yml
     */
    private void initializeDatabase() {
        this.director = getPlugin(DatabaseLib.class).getDirector();

        String password = getConfig().getString("db.password");
        Instrument instrument = new Instrument(
                getConfig().getString("db.ip"), getConfig().getString("db.port"),
                getConfig().getString("db.database"), getConfig().getString("db.user"), password.equals("null") ? null : password,
                getConfig().getString("db.url"), getConfig().getString("db.driver"), null, true,
                getConfig().getInt("db.max-pool-size")
        );
        this.director.addInstrument("heros-spawners", instrument);
        this.musician = new Musician(director, instrument, getConfig().getBoolean("db.debug"));
    }
}
