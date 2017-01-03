package github.scarsz.discordsrv.DiscordSRV.api.events;

import github.scarsz.discordsrv.DiscordSRV.Manager;
import github.scarsz.discordsrv.DiscordSRV.api.GamePlayerEvent;
import github.scarsz.discordsrv.DiscordSRV.util.DiscordUtil;
import github.scarsz.discordsrv.DiscordSRV.util.PlayerUtil;
import lombok.Getter;
import net.dv8tion.jda.core.entities.TextChannel;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

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

    public GameChatMessageEvent(String playerName, String message, String channel, String world) {
        super(playerName);
        this.message = message;
        this.channel = channel;
        this.world = world;
    }

    public static GameChatMessageEvent fromEvent(Object event, String channel) {
        String playerName = null;
        String message = null;
        String world = null;

        try {
            switch (Manager.getInstance().getPlatformType()) {
                case BUKKIT:
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
        return new GameChatMessageEvent(playerName, message, channel, world);
    }

    @Override
    public boolean perform() {
        if (!super.perform()) return false;

        // ReportCanceledChatEvents debug message
        if (Manager.getInstance().getConfig().getBoolean("ReportCanceledChatEvents")) Manager.getInstance().getPlatform().info("Chat message received, canceled: " + isCanceled());

        // return if player doesn't have permission
        if (!PlayerUtil.hasPermission(getPlayer(), "discordsrv.chat") && !(PlayerUtil.isOp(getPlayer()) || PlayerUtil.hasPermission(getPlayer(), "discordsrv.admin"))) {
            if (Manager.getInstance().getConfig().getBoolean("EventDebug")) Manager.getInstance().getPlatform().info("User " + getPlayer() + " sent a message but it was not delivered to Discord due to lack of permission");
            return true;
        }

        // TODO plugin hooks
//        // return if mcMMO is enabled and message is from party or admin chat
//        if (Bukkit.getPluginManager().isPluginEnabled("mcMMO") && (ChatAPI.isUsingPartyChat(sender) || ChatAPI.isUsingAdminChat(sender))) return;

        // return if event canceled
        if (Manager.getInstance().getConfig().getBoolean("DontSendCanceledChatEvents") && isCanceled()) return true;

        // return if should not send in-game chat
        if (!Manager.getInstance().getConfig().getBoolean("DiscordChatChannelMinecraftToDiscord")) return true;

        // return if user is unsubscribed from Discord and config says don't send those peoples' messages
        if (Manager.getInstance().getUnsubscribedPlayers().contains(getPlayer()) && !Manager.getInstance().getConfig().getBoolean("MinecraftUnsubscribedMessageForwarding")) return true;

        // return if doesn't match prefix filter
        if (!message.startsWith(Manager.getInstance().getConfig().getString("DiscordChatChannelPrefix"))) return true;

        String userPrimaryGroup = PlayerUtil.getPrimaryGroup(getPlayer());
        boolean hasGoodGroup = !"".equals(userPrimaryGroup.replace(" ", ""));

        String format = hasGoodGroup ? Manager.getInstance().getConfig().getString("MinecraftChatToDiscordMessageFormat") : Manager.getInstance().getConfig().getString("MinecraftChatToDiscordMessageFormatNoPrimaryGroup");
        String discordMessage = format
                .replaceAll("&([0-9a-qs-z])", "")
                .replace("%message%", DiscordUtil.stripColor(message))
                .replace("%primarygroup%", PlayerUtil.getPrimaryGroup(getPlayer()))
                //TODO display name .replace("%displayname%", DiscordUtil.stripColor(DiscordUtil.escapeMarkdown(sender.getDisplayName())))
                .replace("%username%", DiscordUtil.stripColor(DiscordUtil.escapeMarkdown(getPlayer())))
                .replace("%world%", getWorld())
                //TODO plugin hooks .replace("%worldalias%", DiscordUtil.stripColor(MultiverseCoreHook.getWorldAlias(sender.getWorld().getName())))
                .replace("%time%", new Date().toString())
                .replace("%date%", new Date().toString())
        ;

        TextChannel targetChannel = Manager.getInstance().getJda().getTextChannelById(channel);
        discordMessage = DiscordUtil.convertMentionsFromNames(discordMessage, targetChannel.getGuild());

        if (channel == null) DiscordUtil.sendMessage(Manager.getInstance().getMainChatChannel(), discordMessage);
        else DiscordUtil.sendMessage(targetChannel, discordMessage);

        return true;
    }

}
