package github.scarsz.discordsrv.DiscordSRV.platforms.bukkit.listeners;

import github.scarsz.discordsrv.DiscordSRV.Manager;
import github.scarsz.discordsrv.DiscordSRV.api.events.GamePlayerJoinEvent;
import github.scarsz.discordsrv.DiscordSRV.api.events.GamePlayerQuitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @on 12/17/2016
 * @at 5:24 PM
 */
public class JoinQuitListener implements Listener {

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event) {
        Manager.getInstance().processEvent(GamePlayerJoinEvent.fromEvent(event));
    }

    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent event) {
        Manager.getInstance().processEvent(GamePlayerQuitEvent.fromEvent(event));
    }

//TODO contemplate whether or not I want this
//    @EventHandler
//    public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
//        if (isFakeJoin(event.getMessage()) && event.getPlayer().hasPermission("vanish.fakeannounce") && Manager.getInstance().getConfig().getBoolean("MinecraftPlayerJoinMessageEnabled")) {
//            // Player has permission to fake join messages
//
//            // Set player's status if they don't already have one
//            if (!playerStatusIsOnline.containsKey(event.getPlayer().getUniqueId())) playerStatusIsOnline.put(event.getPlayer().getUniqueId(), false);
//
//            // Make sure player's status isn't already "online" and isn't a forced command
//            // (player is already online AND command is not forced)
//            if (playerStatusIsOnline.get(event.getPlayer().getUniqueId()) && !isForceFakeJoin(event.getMessage())) return;
//
//            // Set status as online
//            playerStatusIsOnline.put(event.getPlayer().getUniqueId(), true);
//
//            // Send fake join message
//            DiscordUtil.sendMessage(Manager.getInstance().getMainChatChannel(), Manager.getInstance().getConfig().getString("MinecraftPlayerJoinMessageFormat")
//                    .replace("%username%", DiscordUtil.escapeMarkdown(event.getPlayer().getName()))
//                    .replace("%displayname%", ChatColor.stripColor(DiscordUtil.escapeMarkdown(event.getPlayer().getDisplayName())))
//            );
//        } else if (isFakeQuit(event.getMessage()) && event.getPlayer().hasPermission("vanish.fakeannounce") && Manager.getInstance().getConfig().getBoolean("MinecraftPlayerLeaveMessageEnabled")) {
//            // Player has permission to fake quit messages
//
//            // Set player's status if they don't already have one
//            if (!playerStatusIsOnline.containsKey(event.getPlayer().getUniqueId())) playerStatusIsOnline.put(event.getPlayer().getUniqueId(), true);
//
//            // Make sure player's status isn't already "offline" and isn't a forced command
//            // (player is already offline AND command is not forced)
//            if (!playerStatusIsOnline.get(event.getPlayer().getUniqueId()) && !isForceFakeQuit(event.getMessage())) return;
//
//            // Set status as online
//            playerStatusIsOnline.put(event.getPlayer().getUniqueId(), false);
//
//            // Send fake quit message
//            DiscordUtil.sendMessage(Manager.getInstance().getMainChatChannel(), Manager.getInstance().getConfig().getString("MinecraftPlayerLeaveMessageFormat")
//                    .replace("%username%", DiscordUtil.escapeMarkdown(event.getPlayer().getName()))
//                    .replace("%displayname%", ChatColor.stripColor(DiscordUtil.escapeMarkdown(event.getPlayer().getDisplayName())))
//            );
//        }
//    }
//
//    private static boolean isFakeJoin(String message) {
//        return message.startsWith("/v fj") || message.startsWith("/vanish fj") || message.startsWith("/v fakejoin") || message.startsWith("/vanish fakejoin");
//    }
//    private static boolean isFakeQuit(String message) {
//        return message.startsWith("/v fq") || message.startsWith("/vanish fq") || message.startsWith("/v fakequit") || message.startsWith("/vanish fakequit");
//    }
//    private static boolean isForceFakeJoin(String message) {
//        return isFakeJoin(message) && (message.endsWith(" f") || message.endsWith(" force"));
//    }
//    private static boolean isForceFakeQuit(String message) {
//        return isFakeQuit(message) && (message.endsWith(" f") || message.endsWith(" force"));
//    }

}
