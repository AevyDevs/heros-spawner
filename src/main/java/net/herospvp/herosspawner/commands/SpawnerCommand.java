package net.herospvp.herosspawner.commands;

import net.herospvp.heroscore.utils.CommandHandler;
import net.herospvp.herosspawner.objects.SpawnerItem;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

public class SpawnerCommand extends CommandHandler {

    public SpawnerCommand(JavaPlugin plugin) {
        super(plugin, "herospvp.admin", "spawner", false, Collections.singletonList("/spawner <type> <player> <amount>"), false);
    }

    @Override
    public boolean command(CommandSender sender, String[] args) {
        if (!(args.length > 2)) {
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
