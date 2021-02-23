package net.herospvp.herosspawner.objects;

import lombok.Getter;
import net.herospvp.heroscore.utils.items.ItemBuilder;
import net.herospvp.heroscore.utils.strings.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class SpawnerItem {
    @Getter private final EntityType type;
    private final ItemStack itemStack;

    public SpawnerItem(EntityType type) {
        this.type = type;
        this.itemStack = new ItemBuilder(Material.MOB_SPAWNER).setName(ChatColor.YELLOW + StringUtils.capitalize(type.name()) + " Spawner").toItemStack();
    }

    public ItemStack build(int amount) {
        this.itemStack.setAmount(amount);
        return itemStack;
    }

    public static EntityType getType(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasDisplayName() || itemStack.getType() != Material.MOB_SPAWNER ||
                !itemStack.getItemMeta().getDisplayName().contains("Spawner")) {
            return null;
        }

        return EntityType.valueOf(ChatColor.stripColor(itemStack.getItemMeta().getDisplayName().toUpperCase().split(" ")[0]));
    }
}
