package net.herospvp.herosspawner.utils;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;

public class FactionUtils {

    public static boolean hasSpawnerPerm(FPlayer fPlayer) {
        return fPlayer.getFaction().getAccess(fPlayer, PermissableAction.SPAWNER) != Access.DENY;
    }

}
