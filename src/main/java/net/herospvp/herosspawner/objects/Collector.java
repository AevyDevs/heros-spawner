package net.herospvp.herosspawner.objects;

import com.google.common.collect.Maps;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.integration.Econ;
import net.herospvp.heroscore.utils.items.ItemBuilder;
import net.herospvp.heroscore.utils.strings.StringUtils;
import net.herospvp.heroscore.utils.strings.message.Message;
import net.herospvp.heroscore.utils.strings.message.MessageType;
import net.herospvp.herosspawner.utils.FactionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Map;

public class Collector {
    private final String factionId;
    private final Map<Material, Double> drops;
    private Inventory inventory;

    public Collector(String factionId) {
        this.factionId = factionId;

        this.drops = Maps.newHashMap();
        this.clear(true);

        this.createInventory();
    }

    public void sell(Player player, Material material) {
        Faction faction = Factions.getInstance().getFactionById(factionId+"");

        double amount = SpawnerDrop.getPrice(material)*drops.get(material);
        if (amount == 0) return;

        boolean outpost = FactionUtils.isOutpostFaction(faction);
        if (outpost) amount *= 1.5;

        Econ.depositFactionBalance(faction, amount);

        Message.sendMessage(player, MessageType.WARNING, "Collector", "Hai aggiunto &6$&e{0} &falla banca della fazione{1}",
                amount+"", outpost ? "&7 (x1.5)" : "");

        removeDrop(material);
        this.updateInventory();
    }

    public void sellAll(Player player) {
        Faction faction = Factions.getInstance().getFactionById(factionId+"");

        final double[] total = {0};

        drops.entrySet().parallelStream().forEach(entry -> {
            Material key = entry.getKey();
            Double value = entry.getValue();

            if (key != Material.TNT) {
                Econ.depositFactionBalance(faction, (SpawnerDrop.getPrice(key) * value));
                total[0] += value;
            }
        });

        if (total[0] == 0) return;
        boolean outpost = FactionUtils.isOutpostFaction(faction);
        if (outpost) total[0] *= 1.5;

        Message.sendMessage(player, MessageType.WARNING, "Collector", "Hai venduto un totale di &6$&e{0}{1}", total[0]+"", outpost ? " &7(x1.5)" : "");

        clear(false);
        this.updateInventory();
    }

    public void openInventory(Player player) {
        this.updateInventory();
        player.openInventory(inventory);
    }

    public void addDrop(Material material, int amount) {
        drops.put(material, drops.get(material)+amount);
        updateInventory();
    }

    public void addTnt(Player player) {
        int amount = drops.get(Material.TNT).intValue();
        if (amount == 0) return;

        Factions.getInstance().getFactionById(String.valueOf(factionId)).addTnt(amount);
        removeDrop(Material.TNT);
        updateInventory();

        Message.sendMessage(player, MessageType.WARNING, "Collector",
                "Hai depositato &e{0} &fTNT nella banca della fazione", amount+"");
    }

    private void removeDrop(Material material) {
        drops.put(material, (double) 0);
    }

    private void clear(boolean withTnt) {
        for (SpawnerDrop value : SpawnerDrop.values()) {
            if (!withTnt) if (value.getDropType() == Material.TNT) continue;
            drops.put(value.getDropType(), (double) 0);
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
            double amount = drops.get(value.getDropType());
            String rounded = String.format("%.0f", amount);

            if (value.getEntityType() == EntityType.SILVERFISH) continue;

            if (value.getEntityType() == EntityType.CREEPER) {
                inventory.setItem(16, new ItemBuilder(value.getDropType())
                        .setLore(
                                "&6▎ &fQuantità: &e" + rounded,
                                "",
                                "&f&oClicca per depositare le tnt",
                                "&f&onella banca della fazione &e&o/f tnt"
                        )
                        .toItemStack());
                continue;
            }

            double money = amount*SpawnerDrop.getPrice(value.getDropType());

            Faction faction = Factions.getInstance().getFactionById(factionId);
            boolean outpost = FactionUtils.isOutpostFaction(faction);
            if (outpost) money *= 1.5;

            inventory.setItem(i, new ItemBuilder(value.getDropType()).setLore(
                    "&6▎ &fQuantità: &e"+ rounded,
                    "&6▎ &fVendita: &6$&e" + StringUtils.formatNumber(money) + (outpost ? " &7(x1.5)" : ""),
                    "",
                    "&f&oClicca per depositare la vendita",
                    "&f&onella banca della fazione &e&o/f bank"
            ).toItemStack());
            i++;
        }

        inventory.setItem(21, new ItemBuilder(Material.HOPPER).setName("&eVendi tutto").toItemStack());
    }
}
