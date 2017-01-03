package github.scarsz.discordsrv.DiscordSRV.api.events;

import github.scarsz.discordsrv.DiscordSRV.Manager;
import github.scarsz.discordsrv.DiscordSRV.api.GamePlayerEvent;
import github.scarsz.discordsrv.DiscordSRV.util.DiscordUtil;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @on 12/11/2016
 * @at 3:19 PM
 */
public class GamePlayerAchievementRewardedEvent extends GamePlayerEvent {

    @Getter private final String achievement;
    @Getter private final String world;

    public GamePlayerAchievementRewardedEvent(String achievement, String playerName, String world) {
        super(playerName);
        this.achievement = achievement;
        this.world = world;
    }

    public static GamePlayerAchievementRewardedEvent fromEvent(Object event) {
        String achievement = null;
        String playerName = null;
        String world = null;

        try {
            switch (Manager.getInstance().getPlatformType()) {
                case BUKKIT:
                    Object player = event.getClass().getMethod("getEntity").invoke(event);
                    playerName = (String) player.getClass().getMethod("getName").invoke(player);

                    Object worldObject = player.getClass().getMethod("getWorld").invoke(player);
                    world = (String) worldObject.getClass().getMethod("getName").invoke(worldObject);

                    achievement = event.getClass().getMethod("getAchievement").invoke(event).getClass().getSimpleName().toLowerCase();
                    List<String> achievementNameParts = new LinkedList<>();
                    for (String s : achievement.split("_"))
                        achievementNameParts.add(s.substring(0, 1).toUpperCase() + s.substring(1));
                    achievement = String.join(" ", achievementNameParts);

                    break;
                case BUNGEECORD:
                    //TODO
                    break;
                case SPONGE:
                    //TODO
                    break;
                default:
                    return null;
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return new GamePlayerAchievementRewardedEvent(playerName, world, achievement);
    }

    @Override
    public boolean perform() {
        if (!super.perform()) return false;

        // return if achievement messages are disabled
        if (!Manager.getInstance().getConfig().getBoolean("MinecraftPlayerAchievementMessagesEnabled")) return true;

        // turn "SHITTY_ACHIEVEMENT_NAME" into "Shitty Achievement Name"
        List<String> achievementNameParts = new LinkedList<>();
        for (String s : getAchievement().toLowerCase().split("_"))
            achievementNameParts.add(s.substring(0, 1).toUpperCase() + s.substring(1));
        String achievementName = String.join(" ", achievementNameParts);

        DiscordUtil.sendMessage(Manager.getInstance().getMainChatChannel(), DiscordUtil.stripColor(Manager.getInstance().getConfig().getString("MinecraftPlayerAchievementMessagesFormat")
                .replace("%username%", getPlayer())
                //TODO display names .replace("%displayname%", event.getPlayer().getDisplayName())
                .replace("%world%", getWorld())
                .replace("%achievement%", achievementName)
        ));

        return true;
    }

}
