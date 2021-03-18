package net.herospvp.herosspawner.commands;

import net.herospvp.heroscore.utils.CommandsHandler;
import net.herospvp.heroscore.utils.strings.StringUtils;
import net.herospvp.heroscore.utils.strings.message.Message;
import net.herospvp.heroscore.utils.strings.message.MessageType;
import net.herospvp.herosspawner.HerosSpawner;
import net.herospvp.herosspawner.handlers.TopHandler;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

public class TopCommand extends CommandsHandler {
    private final HerosSpawner plugin;

    public TopCommand(@NotNull HerosSpawner plugin) {
        super(plugin, null, "top", false, Collections.singletonList("ftop"), false);
        this.plugin = plugin;
    }

    @Override
    public boolean command(CommandSender sender, String[] args) {
        TopHandler handler = plugin.getTopHandler();
        if (handler.isBlocked()) {
            Message.sendMessage(sender, MessageType.ERROR, "Top", "Attendi qualche secondo, sto ancora calcolando la top!");
            return true;
        }

        if (handler.getTop().isEmpty()) {
            Message.sendMessage(sender, MessageType.WARNING, "Top", "La top è attualmente vuota... che ne dici di iniziare a dominare tu?");
            return true;
        }

        final int[] i = {1};

        sender.sendMessage("");
        Message.sendMessage(sender, MessageType.WARNING, false, "Top", "Classifica spawners:");
        handler.getTop().entrySet().stream()
                .limit(10)
                .forEach((entry -> {
                    TextComponent message = new TextComponent(ChatColor.GOLD + "▎ "
                            + ChatColor.WHITE + i[0] + ". " + ChatColor.AQUA + ChatColor.UNDERLINE + entry.getKey().getName()
                            + ChatColor.WHITE + " ‣ " + ChatColor.GOLD + "$" + ChatColor.YELLOW + StringUtils.formatNumber(entry.getValue()));
                    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(entry.getKey().format()).create()));
                    i[0]++;
                    sender.sendMessage(message);
        }));
        sender.sendMessage("");
        sender.sendMessage(StringUtils.c("&8&oLa classifica viene calcolata ogni &7&o30 minuti&8&o!"));
        return true;
    }

    @Override
    public List<String> tabComplete(String[] args) {
        return null;
    }

    private String formatter(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        return formatter.format(amount);
    }
}
