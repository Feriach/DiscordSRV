package github.scarsz.discordsrv.DiscordSRV.api.events;

import github.scarsz.discordsrv.DiscordSRV.Manager;
import github.scarsz.discordsrv.DiscordSRV.api.GamePlayerEvent;

import java.lang.reflect.InvocationTargetException;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @at 11/7/2016
 */
public class GameChatMessageEvent extends GamePlayerEvent {

    private final String message;
    public final String getMessage() {
        return message;
    }

    private final String channel;
    public final String getChannel() {
        return channel;
    }

    public GameChatMessageEvent(String playerName, String message, String channel) {
        setPlayer(playerName);
        this.message = message;
        this.channel = channel;
    }

    public static GameChatMessageEvent fromEvent(Object event, String channel) {
        String playerName = null;
        String message = null;

        try {
            switch (Manager.getInstance().getPlatformType()) {
                case BUKKIT:
                    Object player = event.getClass().getMethod("getPlayer").invoke(event);
                    playerName = (String) player.getClass().getMethod("getName").invoke(player);

                    message = (String) event.getClass().getMethod("getMessage").invoke(event);
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
        return new GameChatMessageEvent(playerName, message, channel);
    }

}
