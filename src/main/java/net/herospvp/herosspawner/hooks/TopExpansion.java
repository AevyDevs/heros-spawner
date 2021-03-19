package net.herospvp.herosspawner.hooks;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.herospvp.heroscore.utils.strings.StringUtils;
import net.herospvp.herosspawner.HerosSpawner;
import net.herospvp.herosspawner.objects.FactionTop;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class TopExpansion extends PlaceholderExpansion {
    private final HerosSpawner plugin;

    public TopExpansion(HerosSpawner plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ftop";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Niketion";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier){
        if(identifier.startsWith("top_name_")){
            int pos = Integer.parseInt(identifier.split("_")[2]);
            for (FactionTop faction : plugin.getTopHandler().getFactions()) {
                if (faction.getPosition() == pos) {
                    return faction.getName();
                }
            }
            return "N/A";
        }

        if(identifier.startsWith("top_value_")){
            int pos = Integer.parseInt(identifier.split("_")[2]);
            for (FactionTop faction : plugin.getTopHandler().getFactions()) {
                if (faction.getPosition() == pos) {
                    return StringUtils.formatNumber(plugin.getTopHandler().getTop().get(faction));
                }
            }
            return "N/A";
        }

        if (identifier.startsWith("position")) {
            if (!player.isOnline()) return "";
            Faction faction = FPlayers.getInstance().getByPlayer(Bukkit.getPlayer(player.getUniqueId())).getFaction();
            if (faction.isSystemFaction()) return "N/A";

            FactionTop factionTop =  plugin.getTopHandler().getFactionInfo(faction.getTag());
            if (factionTop == null) return "N/A";
            return factionTop.getPosition()+"";
        }

        return "";
    }
}
