package net.herospvp.herosspawner.utils;

import com.benzimmer123.outpost.api.objects.OutpostArena;
import com.benzimmer123.outpost.data.OutpostData;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;

public class FactionUtils {

    public static boolean hasSpawnerPerm(FPlayer fPlayer) {
        return fPlayer.getFaction().getAccess(fPlayer, PermissableAction.SPAWNER) != Access.DENY;
    }

    public static String getOutpostFactionTag() {
        for (OutpostArena outpost : OutpostData.getInstance().getOutposts()) {
            if (outpost.getName().equalsIgnoreCase("default")) {
                if (outpost.getController() == null || outpost.getController().getOwner() == null) return null;
                return outpost.getController().getOwner().getTeamName();
            }
        }
        return null;
    }

    public static boolean isOutpostFaction(Faction faction) {
        String name = getOutpostFactionTag();
        if (name == null) return false;

        return name.equalsIgnoreCase(faction.getTag());
    }

}
