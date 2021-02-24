package net.herospvp.herosspawner.objects;

import com.google.common.collect.Maps;
import com.sk89q.commandbook.bans.Ban;
import net.herospvp.heroscore.utils.items.ItemBuilder;
import net.prosavage.factionsx.core.Faction;
import net.prosavage.factionsx.manager.FactionManager;
import net.prosavage.factionsx.persist.data.Factions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class Collector {
    private final long factionId;
    private final Map<Material, Long> drops;
    private Inventory inventory;

    public Collector(long factionId) {
        this.factionId = factionId;

        this.drops = Maps.newHashMap();
        this.clear();

        this.createInventory();
    }

    public void sell(Material material) {
        Faction.Bank bank = FactionManager.INSTANCE.getFaction(factionId).getBank();
        bank.setAmount(bank.getAmount() + (SpawnerDrop.getPrice(material)*drops.get(material)));
        removeDrop(material);
        updateInventory();
    }

    public void openInventory(Player player) {
        this.updateInventory();
        player.openInventory(inventory);
    }

    public void addDrop(Material material, int amount) {
        drops.put(material, drops.get(material)+amount);
        updateInventory();
    }

    private void removeDrop(Material material) {
        drops.put(material, (long) 0);
    }

    private void clear() {
        for (SpawnerDrop value : SpawnerDrop.values()) {
            drops.put(value.getDropType(), (long) 0);
        }
    }

    private void createInventory() {
        this.inventory = Bukkit.createInventory(null, 3*9, "Collector");
        for (int i=0; i<27; i++) {
            inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("&f").toItemStack());
        }

        this.updateInventory();
    }

    private void updateInventory() {
        int i=10;
        for (SpawnerDrop value : SpawnerDrop.values()) {
            if (value.getEntityType() == EntityType.CREEPER) {
                inventory.setItem(16, new ItemBuilder(value.getDropType()).setLore(drops.get(Material.TNT)+"").toItemStack());
                continue;
            }

            inventory.setItem(i, new ItemBuilder(value.getDropType()).setLore(drops.get(value.getDropType())+"").toItemStack());
            i++;
        }

        inventory.setItem(21, new ItemBuilder(Material.HOPPER).setName("&eVendi tutto").toItemStack());
    }
}
