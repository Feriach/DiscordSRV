package github.scarsz.discordsrv.DiscordSRV.api.events;

import github.scarsz.discordsrv.DiscordSRV.Manager;
import github.scarsz.discordsrv.DiscordSRV.api.GamePlayerEvent;
import github.scarsz.discordsrv.DiscordSRV.util.DiscordUtil;
import github.scarsz.discordsrv.DiscordSRV.util.PlayerUtil;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @on 11/25/2016
 * @at 10:16 PM
 */
public class GamePlayerQuitEvent extends GamePlayerEvent {

    @Getter private final String message;
    @Getter private final String world;

    public GamePlayerQuitEvent(String playerName, String message, String world) {
        super(playerName);
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

        // Make sure quit messages enabled
        if (!Manager.getInstance().getConfig().getBoolean("MinecraftPlayerLeaveMessageEnabled")) return true;

        // user shouldn't have a quit message from permission
        if (PlayerUtil.hasPermission(getPlayer(), "discordsrv.silentquit")) {
            Manager.getInstance().getPlatform().info("Player " + getPlayer() + " quit with silent quiting permission, not sending a quit message");
            return true;
        }

        // TODO fake join/leaves
        // remove player from status map to help with memory management
        // playerStatusIsOnline.remove(event.getPlayer());

        // player doesn't have silent quit, show quit message
        DiscordUtil.sendMessage(Manager.getInstance().getMainChatChannel(), Manager.getInstance().getConfig().getString("MinecraftPlayerLeaveMessageFormat")
                .replace("%username%", DiscordUtil.escapeMarkdown(getPlayer()))
                //TODO display names .replace("%displayname%", DiscordUtil.stripColor(DiscordUtil.escapeMarkdown(event.getPlayer().getDisplayName())))
        );

        return true;
    }

}

