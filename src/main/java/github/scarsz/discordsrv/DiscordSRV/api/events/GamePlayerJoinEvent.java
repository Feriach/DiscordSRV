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
 * @at 11/8/2016
 */
public class GamePlayerJoinEvent extends GamePlayerEvent {

    @Getter private final String message;
    @Getter private final String world;

    public GamePlayerJoinEvent(String playerName, String message, String world) {
        super(playerName);
        this.message = message;
        this.world = world;
    }

    public static GamePlayerJoinEvent fromEvent(Object event) {
        String playerName = null;
        String message = null;
        String world = null;

        try {
            switch (Manager.getInstance().getPlatformType()) {
                case BUKKIT:
                    Object player = event.getClass().getMethod("getEntity").invoke(event);
                    playerName = (String) player.getClass().getMethod("getName").invoke(player);

                    message = (String) event.getClass().getMethod("getJoinMessage").invoke(event);

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
        return new GamePlayerJoinEvent(playerName, message, world);
    }

    @Override
    public boolean perform() {
        if (!super.perform()) return false;

        // make sure join messages enabled
        if (!Manager.getInstance().getConfig().getBoolean("MinecraftPlayerJoinMessageEnabled")) return true;

        // user shouldn't have a quit message from permission
        if (PlayerUtil.hasPermission(getPlayer(), "discordsrv.silentjoin")) {
            Manager.getInstance().getPlatform().info("Player " + getPlayer() + " joined with silent joining permission, not sending a join message");
            return true;
        }

        //TODO assign player's status to online since they don't have silent join platformutils
        // playerStatusIsOnline.put(event.getPlayer(), true);

        // player doesn't have silent join permission, send join message
        DiscordUtil.sendMessage(Manager.getInstance().getMainChatChannel(), Manager.getInstance().getConfig().getString("MinecraftPlayerJoinMessageFormat")
                .replace("%username%", DiscordUtil.escapeMarkdown(getPlayer()))
                //TODO display names .replace("%displayname%", ChatColor.stripColor(DiscordSRV.escapeMarkdown(event.getPlayer().getDisplayName())))
        );

        return true;
    }

}
