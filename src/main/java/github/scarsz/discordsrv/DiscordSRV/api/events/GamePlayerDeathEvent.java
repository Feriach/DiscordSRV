package github.scarsz.discordsrv.DiscordSRV.api.events;

import github.scarsz.discordsrv.DiscordSRV.DiscordSRV;
import github.scarsz.discordsrv.DiscordSRV.api.GamePlayerEvent;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @at 11/8/2016
 */
public class GamePlayerDeathEvent extends GamePlayerEvent {

    @Getter private final String message;
    @Getter private final String world;

    public GamePlayerDeathEvent(String playerName, String message, String world) {
        super(playerName);
        this.message = message;
        this.world = world;
    }

    public static GamePlayerDeathEvent fromEvent(Object event) {
        String playerName = null;
        String message = null;
        String world = null;

        try {
            switch (DiscordSRV.getInstance().getPlatformType()) {
                case BUKKIT:
                    Object player = event.getClass().getMethod("getEntity").invoke(event);
                    playerName = (String) player.getClass().getMethod("getName").invoke(player);

                    message = (String) event.getClass().getMethod("getDeathMessage").invoke(event);

                    Object worldObject = player.getClass().getMethod("getWorld").invoke(player);
                    world = (String) worldObject.getClass().getMethod("getName").invoke(worldObject);
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
        return new GamePlayerDeathEvent(playerName, message, world);
    }

}
