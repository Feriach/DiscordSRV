package github.scarsz.discordsrv.DiscordSRV.util.platformutils;

import github.scarsz.discordsrv.DiscordSRV.Manager;
import org.bukkit.Bukkit;

public class BukkitUtil {

    public static boolean playerHasPermission(String player, String permission) {
        return Bukkit.getPlayerExact(player).hasPermission(permission);
    }

    public static boolean playerIsOp(String player) {
        return Bukkit.getPlayerExact(player).isOp();
    }

    public static String getPrimaryGroup(String player) {
        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) return " ";

        // TODO vault
//        RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> service = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
//
//        if (service == null) return " ";
//        try {
//            String primaryGroup = service.getProvider().getPrimaryGroup(player);
//            if (!primaryGroup.equals("default")) return primaryGroup;
//        } catch (Exception ignored) { }

        return " ";
    }

}
