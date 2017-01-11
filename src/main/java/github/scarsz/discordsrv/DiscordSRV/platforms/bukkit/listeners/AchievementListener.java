package github.scarsz.discordsrv.DiscordSRV.platforms.bukkit.listeners;

import github.scarsz.discordsrv.DiscordSRV.DiscordSRV;
import github.scarsz.discordsrv.DiscordSRV.api.events.GamePlayerAchievementRewardedEvent;
import github.scarsz.discordsrv.DiscordSRV.util.DiscordUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;

import java.util.LinkedList;
import java.util.List;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @on 12/11/2016
 * @at 3:09 PM
 */
public class AchievementListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void PlayerAchievementAwardedEvent(PlayerAchievementAwardedEvent event) {
        // return if achievement messages are disabled
        if (!DiscordSRV.getInstance().getConfig().getBoolean("MinecraftPlayerAchievementMessagesEnabled")) return;

        // return if achievement or player objects are fucking knackered
        if (event == null || event.getAchievement() == null || event.getPlayer() == null) return;

        // turn "SHITTY_ACHIEVEMENT_NAME" into "Shitty Achievement Name"
        List<String> achievementNameParts = new LinkedList<>();
        for (String s : event.getAchievement().toString().toLowerCase().split("_")) achievementNameParts.add(s.substring(0, 1).toUpperCase() + s.substring(1));
        String achievementName = String.join(" ", achievementNameParts);

        DiscordUtil.sendMessage(DiscordSRV.getInstance().getMainChatChannel(), ChatColor.stripColor(DiscordSRV.getInstance().getConfig().getString("MinecraftPlayerAchievementMessagesFormat")
                .replace("%username%", event.getPlayer().getName())
                .replace("%displayname%", event.getPlayer().getDisplayName())
                .replace("%world%", event.getPlayer().getWorld().getName())
                .replace("%achievement%", achievementName)
        ));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerAchievementAwarded(PlayerAchievementAwardedEvent event) {
        DiscordSRV.getInstance().processEvent(GamePlayerAchievementRewardedEvent.fromEvent(event));
    }

}