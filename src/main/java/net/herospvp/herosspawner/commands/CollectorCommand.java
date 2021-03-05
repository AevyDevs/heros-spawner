package net.herospvp.herosspawner.commands;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import net.herospvp.heroscore.utils.CommandsHandler;
import net.herospvp.heroscore.utils.strings.message.Message;
import net.herospvp.heroscore.utils.strings.message.MessageType;
import net.herospvp.herosspawner.HerosSpawner;
import net.herospvp.herosspawner.utils.FactionUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CollectorCommand extends CommandsHandler {
    private final HerosSpawner plugin;

    public CollectorCommand(HerosSpawner plugin) {
        super(plugin, "herospvp.admin", "collector", true, Collections.singletonList("/collector"), false);
        this.plugin = plugin;
    }

    @Override
    public boolean command(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        if (fPlayer.getFaction().isWilderness()) {
            Message.sendMessage(sender, MessageType.WARNING, "Collector", "Non hai una fazione!");
            return true;
        }

        if (!FactionUtils.hasSpawnerPerm(fPlayer)) {
            Message.sendMessage(sender, MessageType.WARNING, "Collector", "Non hai il permesso!");
            return true;
        }

        plugin.getCollectorHandler().getCollector(fPlayer.getFaction().getId()).openInventory(player);
        return true;
    }

    @Override
    public List<String> tabComplete(String[] args) {
        return null;
    }
}
