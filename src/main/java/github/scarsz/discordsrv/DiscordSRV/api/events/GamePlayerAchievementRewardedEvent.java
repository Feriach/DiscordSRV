package github.scarsz.discordsrv.DiscordSRV.api.events;

import github.scarsz.discordsrv.DiscordSRV.DiscordSRV;
import github.scarsz.discordsrv.DiscordSRV.api.GamePlayerEvent;
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
            switch (DiscordSRV.getInstance().getPlatformType()) {
                case BUKKIT:
                    Object player = event.getClass().getMethod("getEntity").invoke(event);
                    playerName = (String) player.getClass().getMethod("getName").invoke(player);

                    Object worldObject = player.getClass().getMethod("getWorld").invoke(player);
                    world = (String) worldObject.getClass().getMethod("getName").invoke(worldObject);

                    achievement = event.getClass().getMethod("getAchievement").invoke(event).getClass().getSimpleName().toLowerCase();
                    // turn "SHITTY_ACHIEVEMENT_NAME" into "Shitty Achievement Name"
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

}
