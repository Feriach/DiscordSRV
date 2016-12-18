package github.scarsz.discordsrv.DiscordSRV.platforms.bukkit.listeners;

import github.scarsz.discordsrv.DiscordSRV.Manager;
import github.scarsz.discordsrv.DiscordSRV.util.DiscordUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @on 12/17/2016
 * @at 5:24 PM
 */
public class JoinLeaveListener implements Listener {

    private Map<UUID, Boolean> playerStatusIsOnline = new HashMap<>();

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event) {
        // If player is OP & update is available tell them
        if ((event.getPlayer().isOp() || event.getPlayer().hasPermission("discordsrv.admin")) && Manager.getInstance().getUpdateManager().isUpdateAvailable()) {
            event.getPlayer().sendMessage(ChatColor.AQUA + "An update to DiscordSRV is available. Download it at http://dev.bukkit.org/bukkit-plugins/discordsrv/");
        }

        // Make sure join messages enabled
        if (!Manager.getInstance().getConfig().getBoolean("MinecraftPlayerJoinMessageEnabled")) return;

        // Check if player has permission to not have join messages
        if (event.getPlayer().hasPermission("discordsrv.silentjoin")) {
            Manager.getInstance().getPlatform().info("Player " + event.getPlayer().getName() + " joined with silent joining permission, not sending a join message");
            return;
        }

        // Assign player's status to online since they don't have silent join permissions
        playerStatusIsOnline.put(event.getPlayer().getUniqueId(), true);

        // Player doesn't have silent join permission, send join message
        DiscordUtil.sendMessage(Manager.getInstance().getChatChannel(), Manager.getInstance().getConfig().getString("MinecraftPlayerJoinMessageFormat")
                .replace("%username%", DiscordUtil.escapeMarkdown(event.getPlayer().getName()))
                .replace("%displayname%", ChatColor.stripColor(DiscordUtil.escapeMarkdown(event.getPlayer().getDisplayName())))
        );
    }
    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent event) {
        // Make sure quit messages enabled
        if (!Manager.getInstance().getConfig().getBoolean("MinecraftPlayerLeaveMessageEnabled")) return;

        // No quit message, user shouldn't have one from permission
        if (event.getPlayer().hasPermission("discordsrv.silentquit")) {
            Manager.getInstance().getPlatform().info("Player " + event.getPlayer().getName() + " quit with silent quiting permission, not sending a quit message");
            return;
        }

        // Remove player from status map to help with memory management
        playerStatusIsOnline.remove(event.getPlayer().getUniqueId());

        // Player doesn't have silent quit, show quit message
        DiscordUtil.sendMessage(Manager.getInstance().getChatChannel(), Manager.getInstance().getConfig().getString("MinecraftPlayerLeaveMessageFormat")
                .replace("%username%", DiscordUtil.escapeMarkdown(event.getPlayer().getName()))
                .replace("%displayname%", ChatColor.stripColor(DiscordUtil.escapeMarkdown(event.getPlayer().getDisplayName())))
        );
    }
    @EventHandler
    public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        if (isFakeJoin(event.getMessage()) && event.getPlayer().hasPermission("vanish.fakeannounce") && Manager.getInstance().getConfig().getBoolean("MinecraftPlayerJoinMessageEnabled")) {
            // Player has permission to fake join messages

            // Set player's status if they don't already have one
            if (!playerStatusIsOnline.containsKey(event.getPlayer().getUniqueId())) playerStatusIsOnline.put(event.getPlayer().getUniqueId(), false);

            // Make sure player's status isn't already "online" and isn't a forced command
            // (player is already online AND command is not forced)
            if (playerStatusIsOnline.get(event.getPlayer().getUniqueId()) && !isForceFakeJoin(event.getMessage())) return;

            // Set status as online
            playerStatusIsOnline.put(event.getPlayer().getUniqueId(), true);

            // Send fake join message
            DiscordUtil.sendMessage(Manager.getInstance().getChatChannel(), Manager.getInstance().getConfig().getString("MinecraftPlayerJoinMessageFormat")
                    .replace("%username%", DiscordUtil.escapeMarkdown(event.getPlayer().getName()))
                    .replace("%displayname%", ChatColor.stripColor(DiscordUtil.escapeMarkdown(event.getPlayer().getDisplayName())))
            );
        } else if (isFakeQuit(event.getMessage()) && event.getPlayer().hasPermission("vanish.fakeannounce") && Manager.getInstance().getConfig().getBoolean("MinecraftPlayerLeaveMessageEnabled")) {
            // Player has permission to fake quit messages

            // Set player's status if they don't already have one
            if (!playerStatusIsOnline.containsKey(event.getPlayer().getUniqueId())) playerStatusIsOnline.put(event.getPlayer().getUniqueId(), true);

            // Make sure player's status isn't already "offline" and isn't a forced command
            // (player is already offline AND command is not forced)
            if (!playerStatusIsOnline.get(event.getPlayer().getUniqueId()) && !isForceFakeQuit(event.getMessage())) return;

            // Set status as online
            playerStatusIsOnline.put(event.getPlayer().getUniqueId(), false);

            // Send fake quit message
            DiscordUtil.sendMessage(Manager.getInstance().getChatChannel(), Manager.getInstance().getConfig().getString("MinecraftPlayerLeaveMessageFormat")
                    .replace("%username%", DiscordUtil.escapeMarkdown(event.getPlayer().getName()))
                    .replace("%displayname%", ChatColor.stripColor(DiscordUtil.escapeMarkdown(event.getPlayer().getDisplayName())))
            );
        }
    }

    private static boolean isFakeJoin(String message) {
        return message.startsWith("/v fj") || message.startsWith("/vanish fj") || message.startsWith("/v fakejoin") || message.startsWith("/vanish fakejoin");
    }
    private static boolean isFakeQuit(String message) {
        return message.startsWith("/v fq") || message.startsWith("/vanish fq") || message.startsWith("/v fakequit") || message.startsWith("/vanish fakequit");
    }
    private static boolean isForceFakeJoin(String message) {
        return isFakeJoin(message) && (message.endsWith(" f") || message.endsWith(" force"));
    }
    private static boolean isForceFakeQuit(String message) {
        return isFakeQuit(message) && (message.endsWith(" f") || message.endsWith(" force"));
    }

}
