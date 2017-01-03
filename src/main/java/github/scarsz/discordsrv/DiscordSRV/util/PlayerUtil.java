package github.scarsz.discordsrv.DiscordSRV.util;

import github.scarsz.discordsrv.DiscordSRV.Manager;
import github.scarsz.discordsrv.DiscordSRV.util.platformutils.BukkitUtil;

public class PlayerUtil {

    public static boolean hasPermission(String player, String permission) {
        switch (Manager.getInstance().getPlatformType()) {
            case BUKKIT: return BukkitUtil.playerHasPermission(player, permission);
            case BUNGEECORD: return false; //TODO
            case SPONGE: return false; //TODO
            default: return false;
        }
    }

    public static boolean isOp(String player) {
        switch (Manager.getInstance().getPlatformType()) {
            case BUKKIT: return BukkitUtil.playerIsOp(player);
            case BUNGEECORD: return false; //TODO
            case SPONGE: return false; //TODO
            default: return false;
        }
    }

    public static String getPrimaryGroup(String player) {
        switch (Manager.getInstance().getPlatformType()) {
            case BUKKIT: return BukkitUtil.getPrimaryGroup(player);
            case BUNGEECORD: return " "; //TODO
            case SPONGE: return " "; //TODO
            default: return " ";
        }
    }

}
