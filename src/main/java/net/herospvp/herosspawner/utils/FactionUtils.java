package net.herospvp.herosspawner.utils;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.prosavage.factionsx.core.CustomRole;
import net.prosavage.factionsx.core.FPlayer;
import org.bukkit.Location;

public class FactionUtils {

    public static boolean isMod(FPlayer fPlayer) {
        CustomRole role = fPlayer.getRole();
        String tag = role.getRoleTag().toLowerCase();
        return tag.equals("mod") || tag.equals("admin") || fPlayer.isLeader();
    }

}
