package net.herospvp.herosspawner.listeners;

import net.herospvp.heroscore.utils.strings.message.Message;
import net.herospvp.heroscore.utils.strings.message.MessageType;
import net.herospvp.herosspawner.HerosSpawner;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class HeadListener implements Listener {
    private HerosSpawner plugin;

    public HeadListener(HerosSpawner plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void on(BlockPlaceEvent event) {
        ItemStack itemStack = event.getItemInHand();
        if (itemStack.getType() != Material.SKULL_ITEM) return;

        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().hasLore()) {
            Message.sendMessage(event.getPlayer(), MessageType.WARNING, "Heads", "Non puoi piazzare questo item! &7&oControlla se lo puoi vendere");
            event.setCancelled(true);
        }
    }
}
