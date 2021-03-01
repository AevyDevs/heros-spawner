package net.herospvp.herosspawner.handlers;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.massivecraft.factions.*;
import lombok.SneakyThrows;
import net.herospvp.database.lib.Musician;
import net.herospvp.database.lib.items.Notes;
import net.herospvp.database.lib.items.Papers;
import net.herospvp.heroscore.utils.LocationUtils;
import net.herospvp.heroscore.utils.strings.Debug;
import net.herospvp.heroscore.utils.strings.message.Message;
import net.herospvp.heroscore.utils.strings.message.MessageType;
import net.herospvp.herosspawner.HerosSpawner;
import net.herospvp.herosspawner.objects.CustomSpawner;
import net.herospvp.herosspawner.objects.SpawnerItem;
import net.herospvp.herosspawner.utils.FactionUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class SpawnerHandler {
    private final Notes notes;
    private final Musician musician;

    private final HerosSpawner plugin;

    private final Map<Location, CustomSpawner> spawners;
    private final Set<Integer> toRemove;
    private int maxId;

    public SpawnerHandler(HerosSpawner plugin) {
        this.spawners = Maps.newConcurrentMap();
        this.toRemove = Sets.newConcurrentHashSet();

        this.maxId = 0;
        this.plugin = plugin;

        this.musician = plugin.getMusician();
        this.notes = new Notes("spawners");

        this.musician.offer(startup());
    }

    public Collection<CustomSpawner> getSpawners() {
        return spawners.values();
    }

    public CustomSpawner getSpawner(Location location) {
        Location blockLoc = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getZ());
        return spawners.get(blockLoc);
    }

    public CustomSpawner getSpawner(Block block) {
        Location blockLoc = block.getLocation();
        return spawners.get(blockLoc);
    }

    public boolean breakSpawner(Player player, CustomSpawner spawner) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);

        if (!player.hasPermission("herospvp.admin")) {
            if (player.getItemInHand() == null || !(player.getItemInHand().getType() == Material.DIAMOND_PICKAXE && player.getItemInHand()
                    .getEnchantmentLevel(Enchantment.SILK_TOUCH) != 0)) {
                Message.sendMessage(player, MessageType.WARNING, "Spawner", "Devi avere un piccone &esilk touch &fper poter raccogliere lo spawner!");
                return false;
            }

            Faction factionLoc = Board.getInstance().getFactionAt(new FLocation(spawner.getLocation()));
            if (player.getGameMode() == GameMode.CREATIVE) {
                Message.sendMessage(player, MessageType.ERROR, "Spawner", "Non puoi rompere spawner in &ecreative mode&f!");
                return false;
            }

            if (factionLoc.isSystemFaction() || !factionLoc.getId().equals(fPlayer.getFaction().getId())) {
                Message.sendMessage(player, MessageType.ERROR, "Spawner", "Puoi rompere gli spawner solo nei claim della tua fazione!");
                return false;
            }

            if (!FactionUtils.isMod(fPlayer)) {
                Message.sendMessage(player, MessageType.ERROR, "Spawner", "Devi essere almeno mod per poter raccogliere gli spawner!");
                return false;
            }
        }

        this.breakSpawner(spawner);
        return true;
    }

    public void breakSpawner(CustomSpawner customSpawner) {
        spawners.remove(customSpawner.getLocation());
        toRemove.add(customSpawner.getId());
        plugin.getHologramHandler().removeHologram(customSpawner.getId());

        customSpawner.getLocation().getWorld().dropItem(customSpawner.getLocation(), new SpawnerItem(customSpawner.getEntityType()).build(customSpawner.getAmount()));

        Debug.send("heros-spawner", "remove spawner ID:{0}", customSpawner.getId());
    }

    public void addAmount(Player player, Block block) {
        CreatureSpawner spawnerBlock = (CreatureSpawner) block.getState();

        Debug.send("heros-spawner", "spawnercreature: {0}", spawnerBlock.getSpawnedType().name());

        int amount = 0;
        for (ItemStack content : player.getInventory().getContents()) {
            if (content == null || content.getType() == Material.AIR) continue;

            EntityType entityType = SpawnerItem.getType(content);
            if (entityType == null || spawnerBlock.getSpawnedType() != entityType) continue;

            amount += content.getAmount();
            player.getInventory().remove(content);
            Debug.send("heros-spawner", "found spawner, amount actual: {0}", amount);
        }

        if (amount == 0) {
            return;
        }

        CustomSpawner customSpawner = getSpawner(block);
        customSpawner.setAmount(customSpawner.getAmount()+amount);
        plugin.getHologramHandler().updateHologram(customSpawner);
    }

    public boolean place(Player player, ItemStack item, Block block) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);

        Debug.send("heros-spawner", "placing mobspawner [Player:{0}, Item:{1}, Loc<{2}>]", player.getName(), item.getType(),
                LocationUtils.getLiteStringFromLocation(block.getLocation()));

        if (!player.hasPermission("herospvp.admin")) {
            Faction factionLoc = Board.getInstance().getFactionAt(new FLocation(block.getLocation()));
            if (fPlayer.getFaction().isWilderness()) {
                Message.sendMessage(player, MessageType.ERROR, "Spawner", "Devi essere in una fazione per poter piazzare gli spawner!");
                return false;
            }

            if (!FactionUtils.isMod(fPlayer)) {
                Message.sendMessage(player, MessageType.ERROR, "Spawner", "Devi essere almeno mod per poter piazzare gli spawner!");
                return false;
            }

            if (factionLoc.isSystemFaction() || !factionLoc.getId().equals(fPlayer.getFaction().getId())) {
                Message.sendMessage(player, MessageType.ERROR, "Spawner", "Puoi piazzare gli spawner solo nei claim della tua fazione!");
                return false;
            }
        }

        maxId = maxId+1;
        CustomSpawner spawner = new CustomSpawner(maxId, fPlayer.getFaction().getId(), SpawnerItem.getType(item), 1, block.getLocation(), false);

        CreatureSpawner spawnerBlock = (CreatureSpawner) block.getState();
        spawnerBlock.setSpawnedType(spawner.getEntityType());
        block.getState().update(true);

        spawners.put(block.getLocation(), spawner);
        plugin.getHologramHandler().createHologram(spawner);

        Debug.send("heros-spawner", "put in the map, contains: {0}", spawners.containsKey(block.getLocation()));
        return true;
    }

    public void purge() {
        musician.offer(((connection, instrument) -> {
            spawners.clear();
            plugin.getHologramHandler().purge();

            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = connection.prepareStatement(
                        "DROP TABLE " + notes.getTable() + ";"
                );
                preparedStatement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                instrument.close(preparedStatement);
            }
        }));
    }

    private Papers startup() {
        return (connection, instrument) -> {
            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = connection.prepareStatement(
                        notes.createTable(new String[]{"ID int NOT NULL", "FACTIONID long NOT NULL", "ENTITY varchar(20) NOT NULL", "AMOUNT int NOT NULL", "LOCATION varchar(255) NOT NULL"})
                );
                preparedStatement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                instrument.close(preparedStatement);
            }
        };
    }

    public void loadAll(Consumer<Collection<CustomSpawner>> result) {
        this.musician.offer((connection, instrument) -> {
            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = connection.prepareStatement(
                        notes.selectAll()
                );
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt("ID");
                    if (id > maxId) maxId = id;

                    String factionId = String.valueOf(resultSet.getLong("FACTIONID"));
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
                result.accept(spawners.values());
            }
        });
    }

    @SneakyThrows
    public void saveAll() {
        musician.offer((connection, instrument) -> {
            if (spawners.isEmpty()) return;

            PreparedStatement updateStatement = null, insertStatement = null, deleteStatement = null;
            try {

                insertStatement = connection.prepareStatement(
                        notes.pendingInsert(new String[]{ "ID", "FACTIONID", "ENTITY", "AMOUNT", "LOCATION" })
                );

                updateStatement = connection.prepareStatement(
                        notes.pendingUpdate(new String[] { "AMOUNT" } , "ID")
                );

                deleteStatement = connection.prepareStatement(
                        "DELETE FROM " + notes.getTable() + " WHERE ID = ?;"
                );

                for (CustomSpawner spawner : spawners.values()) {

                    Debug.send("heros-spawner", "spawner saving: [ID:{0}, Loc:<{1}>, Amount:{2}, Type:{3}]",
                            spawner.getId(), LocationUtils.getLiteStringFromLocation(spawner.getLocation()), spawner.getAmount(), spawner.getEntityType().name());

                    if (spawner.isSaved()) {
                        updateStatement.setInt(1, spawner.getAmount());
                        updateStatement.setInt(2, spawner.getId());
                        updateStatement.addBatch();
                    } else {
                        insertStatement.setInt(1, spawner.getId());
                        insertStatement.setString(2, spawner.getFactionId());
                        insertStatement.setString(3, spawner.getEntityType().name());
                        insertStatement.setInt(4, spawner.getAmount());
                        insertStatement.setString(5, LocationUtils.getLiteStringFromLocation(spawner.getLocation()));
                        spawner.setSaved(true);
                        insertStatement.addBatch();
                    }

                }

                for (Integer id : toRemove) {
                    deleteStatement.setInt(1, id);
                    deleteStatement.addBatch();
                }

                deleteStatement.executeBatch();
                updateStatement.executeBatch();
                insertStatement.executeBatch();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                instrument.close(deleteStatement);
                instrument.close(updateStatement);
                instrument.close(insertStatement);
            }
        });

        while (!musician.getBlockingQueue().isEmpty()) {
            Thread.sleep(50);
        }
    }
}
