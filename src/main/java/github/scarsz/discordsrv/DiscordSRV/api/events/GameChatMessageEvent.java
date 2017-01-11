package github.scarsz.discordsrv.DiscordSRV.api.events;

import github.scarsz.discordsrv.DiscordSRV.DiscordSRV;
import github.scarsz.discordsrv.DiscordSRV.api.GamePlayerEvent;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @at 11/7/2016
 */
public class GameChatMessageEvent extends GamePlayerEvent {

    @Getter private final String message;
    @Getter private final String channel;
    @Getter private final String world;

    public GameChatMessageEvent(boolean canceled, String playerName, String message, String channel, String world) {
        super(playerName);
        setCanceled(canceled);
        this.message = message;
        this.channel = channel;
        this.world = world;
    }

    public static GameChatMessageEvent fromEvent(Object event, String channel) {
        boolean canceled = false;
        String playerName = null;
        String message = null;
        String world = null;

        try {
            switch (DiscordSRV.getInstance().getPlatformType()) {
                case BUKKIT:
                    canceled = (boolean) event.getClass().getMethod("isCancelled").invoke(event, null);

                    Object player = event.getClass().getMethod("getPlayer").invoke(event);
                    playerName = (String) player.getClass().getMethod("getName").invoke(player);

                    message = (String) event.getClass().getMethod("getMessage").invoke(event);

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
        return new GameChatMessageEvent(canceled, playerName, message, channel, world);
    }

}
