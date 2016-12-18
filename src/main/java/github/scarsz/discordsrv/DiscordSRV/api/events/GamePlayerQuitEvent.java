package github.scarsz.discordsrv.DiscordSRV.api.events;

import github.scarsz.discordsrv.DiscordSRV.Manager;
import github.scarsz.discordsrv.DiscordSRV.api.GamePlayerEvent;

import java.lang.reflect.InvocationTargetException;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @on 11/25/2016
 * @at 10:16 PM
 */
public class GamePlayerQuitEvent extends GamePlayerEvent {

    private final String playerName;
    public String getPlayerName() {
        return playerName;
    }

    private final String message;
    public String getMessage() {
        return message;
    }

    private final String world;
    public String getWorld() {
        return world;
    }

    public GamePlayerQuitEvent(String playerName, String message, String world) {
        this.playerName = playerName;
        this.message = message;
        this.world = world;
    }

    public static GamePlayerQuitEvent fromEvent(Object event) {
        String playerName = null;
        String message = null;
        String world = null;

        try {
            switch (Manager.getInstance().getPlatformType()) {
                case BUKKIT:
                    Object player = event.getClass().getMethod("getPlayer").invoke(event);
                    playerName = (String) player.getClass().getMethod("getName").invoke(player);

                    message = (String) event.getClass().getMethod("getQuitMessage").invoke(event);

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
        return new GamePlayerQuitEvent(playerName, message, world);
    }

    @Override
    public boolean perform() {
        if (!super.perform()) return false;



        return true;
    }

}

