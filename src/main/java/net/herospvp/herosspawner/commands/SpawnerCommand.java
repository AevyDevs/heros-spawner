package net.herospvp.herosspawner.commands;

import net.herospvp.heroscore.utils.CommandsHandler;
import net.herospvp.herosspawner.HerosSpawner;
import net.herospvp.herosspawner.objects.SpawnerItem;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SpawnerCommand extends CommandsHandler {
    private final HerosSpawner plugin;

    public SpawnerCommand(HerosSpawner plugin) {
        super(plugin, "herospvp.admin", "spawner", false, Collections.singletonList("/spawner [checks/purge/<type>] <player> <amount>"), false);
        this.plugin = plugin;
    }

    @Override
    public boolean command(CommandSender sender, String[] args) {
        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "purge": {
                    plugin.getSpawnerHandler().purge();
                    return true;
                }
                case "checks": {
                    plugin.getSpawnerHandler().checks(() -> {});
                    return true;
                }
                default: {
                    return false;
                }
            }
        }

        if (args.length <= 2) {
            return false;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            target.sendMessage("Player non trovato");
            return true;
        }

        target.getInventory().addItem(new SpawnerItem(EntityType.valueOf(args[0].toUpperCase())).build(Integer.parseInt(args[2])));
        return false;
    }

    @Override
    public List<String> tabComplete(String[] args) {
        return null;
    }
}
