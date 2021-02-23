package net.herospvp.herosspawner.handlers;

import com.google.common.collect.Maps;
import net.herospvp.database.Musician;
import net.herospvp.database.items.Notes;
import net.herospvp.database.items.Papers;
import net.herospvp.heroscore.utils.LocationUtils;
import net.herospvp.heroscore.utils.strings.Debug;
import net.herospvp.heroscore.utils.strings.message.Message;
import net.herospvp.heroscore.utils.strings.message.MessageType;
import net.herospvp.herosspawner.HerosSpawner;
import net.herospvp.herosspawner.objects.CustomSpawner;
import net.herospvp.herosspawner.objects.SpawnerItem;
import net.herospvp.herosspawner.utils.FactionUtils;
import net.prosavage.factionsx.core.CustomRole;
import net.prosavage.factionsx.core.FPlayer;
import net.prosavage.factionsx.core.Faction;
import net.prosavage.factionsx.manager.GridManager;
import net.prosavage.factionsx.manager.PlayerManager;
import net.prosavage.factionsx.util.SpecialAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SpawnerHandler {
    private final Notes notes;
    private final Musician musician;

    private final HerosSpawner plugin;

    private final Map<Location, CustomSpawner> spawners;
    private int maxId;

    public SpawnerHandler(HerosSpawner plugin) {
        this.spawners = Maps.newConcurrentMap();
        this.maxId = 0;
        this.plugin = plugin;

        this.musician = plugin.getMusician();
        this.notes = new Notes("spawners");

        this.musician.update(startup());
        this.musician.update(loadAll());
    }

    public CustomSpawner getSpawner(Location location) {
        Location blockLoc = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getZ());
        return spawners.get(blockLoc);
    }

    public CustomSpawner getSpawner(Block block) {
        Location blockLoc = block.getLocation();
        return spawners.get(blockLoc);
    }

    public void addAmount(Player player, Block block) {
        CreatureSpawner spawnerBlock = (CreatureSpawner) block.getState();

        Set<Integer> slots = new HashSet<>();
        int count = -1;
        int amount = 0;
        for (ItemStack content : player.getInventory().getContents()) {
            count++;
            if (content == null || content.getType() == Material.AIR) continue;

            EntityType entityType = SpawnerItem.getType(content);
            if (entityType == null || spawnerBlock.getSpawnedType() != entityType) continue;

            amount += content.getAmount();
            slots.add(count);
        }

        if (amount == 0) {
            return;
        }

        for (Integer slot : slots) {
            player.getInventory().setItem(slot, null);
        }

        CustomSpawner customSpawner = getSpawner(block);
        customSpawner.setAmount(customSpawner.getAmount()+amount);
    }

    public void place(Player player, ItemStack item, Block block) {
        FPlayer fPlayer = PlayerManager.INSTANCE.getFPlayer(player.getUniqueId());

        Debug.send("heros-spawner", "placing mobspawner [Player:{0}, Item:{1}, Loc<{2}>]", player.getName(), item.getType(),
                LocationUtils.getLiteStringFromLocation(block.getLocation()));

        if (!player.hasPermission("herospvp.admin")) {
            Faction factionLoc = GridManager.INSTANCE.getFactionAt(block.getChunk());
            if (fPlayer.getFaction().isWilderness()) {
                Message.sendMessage(player, MessageType.ERROR, "Spawner", "Devi essere in una fazione per poter piazzare gli spawner!");
                return;
            }

            if (!FactionUtils.isMod(fPlayer)) {
                Message.sendMessage(player, MessageType.ERROR, "Spawner", "Devi essere almeno mod per poter piazzare gli spawner!");
                return;
            }

            if (factionLoc.isSystemFaction() || factionLoc.getId() != fPlayer.getFaction().getId()) {
                Message.sendMessage(player, MessageType.ERROR, "Spawner", "Puoi piazzare gli spawner solo nei claim della tua fazione!");
                return;
            }
        }

        CustomSpawner spawner = new CustomSpawner(maxId++, fPlayer.getFaction().getId(), SpawnerItem.getType(item), 1, block.getLocation(), false);
        spawners.put(block.getLocation(), spawner);
        Debug.send("heros-spawner", "put in the map, contains: {0}", spawners.containsKey(block.getLocation()));;
    }

    private Papers startup() {
        return (connection, instrument) -> {
            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = connection.prepareStatement(
                        notes.createTable(new String[]{"ID int NOT NULL", "FACTIONID long NOT NULL", "ENTITY varchar(20)", "AMOUNT int", "LOCATION varchar(255) NOT NULL"})
                );
                preparedStatement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                instrument.close(preparedStatement);
            }
        };
    }

    public void save() {
        musician.update(saveAll());
    }

    private Papers loadAll() {
        return (connection, instrument) -> {
            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = connection.prepareStatement(
                        notes.selectAll()
                );
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt("ID");
                    if (id>maxId) maxId=id;

                    long factionId = resultSet.getLong("FACTIONID");
                    EntityType type = EntityType.valueOf(resultSet.getString("ENTITY"));
                    Integer amount = resultSet.getInt("AMOUNT");
                    Location location = LocationUtils.getLiteLocationFromString(resultSet.getString("LOCATION"));

                    CustomSpawner spawner = new CustomSpawner(id, factionId, type, amount, location, true);
                    spawners.put(location, spawner);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                instrument.close(preparedStatement);
            }
        };
    }

    private Papers saveAll() {
        return (connection, instrument) -> {
            PreparedStatement preparedStatement = null;

            try {
                if (spawners.isEmpty()) return;

                for (CustomSpawner spawner : spawners.values()) {
                    Debug.send("heros-spawner", "spawner: [ID:{0}, Loc:<{1}>, Amount:{2}]", spawner.getId(), LocationUtils.getLiteStringFromLocation(spawner.getLocation()), spawner.getAmount());
                    if (spawner.isSaved()) {
                        preparedStatement = connection.prepareStatement(
                                notes.update(new String[] { "AMOUNT" } , new Object[] {spawner.getAmount()}, "ID", spawner.getAmount())
                        );
                    } else {
                        preparedStatement = connection.prepareStatement(
                                notes.insert(new String[]{ "ID", "FACTIONID", "ENTITY", "AMOUNT", "LOCATION" },
                                        new Object[]{
                                                spawner.getId(),
                                                spawner.getFactionId(),
                                                spawner.getEntityType().name(),
                                                spawner.getAmount(),
                                                LocationUtils.getLiteStringFromLocation(spawner.getLocation())
                                })
                        );
                        spawner.setSaved(true);
                    }
                    preparedStatement.executeUpdate();
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                instrument.close(preparedStatement);
            }
        };
    }
}
