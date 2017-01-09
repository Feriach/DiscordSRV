package github.scarsz.discordsrv.DiscordSRV.platforms.bukkit.objects;

import github.scarsz.discordsrv.DiscordSRV.Manager;
import github.scarsz.discordsrv.DiscordSRV.api.DiscordSRVListener;
import github.scarsz.discordsrv.DiscordSRV.api.events.*;
import github.scarsz.discordsrv.DiscordSRV.util.DiscordUtil;
import github.scarsz.discordsrv.DiscordSRV.util.PlayerUtil;
import lombok.Getter;
import net.dv8tion.jda.core.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.*;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @at 11/7/2016
 */
public class BukkitDiscordSRVListener extends DiscordSRVListener {

    public BukkitDiscordSRVListener() {
        super("DiscordSRV - Bukkit Platform");
    }

    @Getter private Map<String, Boolean> playerStatusIsOnline = new HashMap<>();

    @Override
    public void onDiscordGeneric(DiscordGenericEvent event) {
        Manager.getInstance().getPlatform().debug("Generic event received from Discord: " + event);
    }

    @Override
    public void onDiscordGuildChatMessage(DiscordGuildChatMessageEvent event) {
        if (event.getGameDestinationChannel() == null) {
            Bukkit.broadcastMessage(event.getMessage());
        }
        //TODO if channel not null
    }

    @Override
    public void onDiscordPrivateMessageChatMessage(DiscordPrivateMessageChatMessageEvent event) {
        // this method should only be handling linking codes
        if (!event.getMessage().matches("[0-9][0-9][0-9][0-9]")) return;

        if (Manager.getInstance().getAccountLinkManager().getCodes().containsKey(event.getMessage())) {
            Manager.getInstance().getAccountLinkManager().link(Manager.getInstance().getAccountLinkManager().getCodes().get(event.getMessage()), event.getSenderId());
            Manager.getInstance().getAccountLinkManager().getCodes().remove(event.getMessage());
            event.getPrivateChannel().sendMessage("Your Discord account has been linked to Game ID " + Manager.getInstance().getAccountLinkManager().getGameIdOfDiscordId(event.getSenderId())).queue();
            if (Bukkit.getPlayer(Manager.getInstance().getAccountLinkManager().getGameIdOfDiscordId(event.getSenderId())).isOnline())
                Bukkit.getPlayer(Manager.getInstance().getAccountLinkManager().getGameIdOfDiscordId(event.getSenderId())).sendMessage(ChatColor.AQUA + "Your UUID has been linked to Discord ID " + event.getSenderName());
        } else {
            event.getPrivateChannel().sendMessage("I don't know of such a code, try again.").queue();
        }
    }

    @Override
    public void onGameChatMessage(GameChatMessageEvent event) {
        // ReportCanceledChatEvents debug message
        if (Manager.getInstance().getConfig().getBoolean("ReportCanceledChatEvents")) Manager.getInstance().getPlatform().info("Chat message received, canceled: " + event.isCanceled());

        // return if player doesn't have permission
        if (!PlayerUtil.hasPermission(event.getPlayer(), "discordsrv.chat") && !(PlayerUtil.isOp(event.getPlayer()) || PlayerUtil.hasPermission(event.getPlayer(), "discordsrv.admin"))) {
            if (Manager.getInstance().getConfig().getBoolean("EventDebug")) Manager.getInstance().getPlatform().info("User " + event.getPlayer() + " sent a message but it was not delivered to Discord due to lack of permission");
            return;
        }

        // TODO plugin hooks
//        // return if mcMMO is enabled and message is from party or admin chat
//        if (Bukkit.getPluginManager().isPluginEnabled("mcMMO") && (ChatAPI.isUsingPartyChat(sender) || ChatAPI.isUsingAdminChat(sender))) return;

        // return if event canceled
        if (Manager.getInstance().getConfig().getBoolean("DontSendCanceledChatEvents") && event.isCanceled()) return;

        // return if should not send in-game chat
        if (!Manager.getInstance().getConfig().getBoolean("DiscordChatChannelMinecraftToDiscord")) return;

        // return if user is unsubscribed from Discord and config says don't send those peoples' messages
        if (Manager.getInstance().getUnsubscribedPlayers().contains(event.getPlayer()) && !Manager.getInstance().getConfig().getBoolean("MinecraftUnsubscribedMessageForwarding")) return;

        // return if doesn't match prefix filter
        if (!event.getMessage().startsWith(Manager.getInstance().getConfig().getString("DiscordChatChannelPrefix"))) return;

        String userPrimaryGroup = PlayerUtil.getPrimaryGroup(event.getPlayer());
        boolean hasGoodGroup = !"".equals(userPrimaryGroup.replace(" ", ""));

        String format = hasGoodGroup ? Manager.getInstance().getConfig().getString("MinecraftChatToDiscordMessageFormat") : Manager.getInstance().getConfig().getString("MinecraftChatToDiscordMessageFormatNoPrimaryGroup");
        String discordMessage = format
                .replaceAll("&([0-9a-qs-z])", "")
                .replace("%message%", DiscordUtil.stripColor(event.getMessage()))
                .replace("%primarygroup%", PlayerUtil.getPrimaryGroup(event.getPlayer()))
                //TODO display name .replace("%displayname%", DiscordUtil.stripColor(DiscordUtil.escapeMarkdown(sender.getDisplayName())))
                .replace("%username%", DiscordUtil.stripColor(DiscordUtil.escapeMarkdown(event.getPlayer())))
                .replace("%world%", event.getWorld())
                //TODO plugin hooks .replace("%worldalias%", DiscordUtil.stripColor(MultiverseCoreHook.getWorldAlias(sender.getWorld().getName())))
                .replace("%time%", new Date().toString())
                .replace("%date%", new Date().toString())
                ;

        TextChannel targetChannel = Manager.getInstance().getTextChannelFromChannelName(event.getChannel());
        discordMessage = DiscordUtil.convertMentionsFromNames(discordMessage, targetChannel.getGuild());

        if (event.getChannel() == null) DiscordUtil.sendMessage(Manager.getInstance().getMainChatChannel(), discordMessage);
        else DiscordUtil.sendMessage(targetChannel, discordMessage);

    }

    @Override
    public void onGamePlayerDeath(GamePlayerDeathEvent event) {
        // return if death messages are disabled
        if (!Manager.getInstance().getConfig().getBoolean("MinecraftPlayerDeathMessageEnabled")) return;

        DiscordUtil.sendMessage(Manager.getInstance().getMainChatChannel(), DiscordUtil.stripColor(Manager.getInstance().getConfig().getString("MinecraftPlayerDeathMessageFormat")
                .replace("%username%", event.getPlayer())
                .replace("%displayname%", DiscordUtil.stripColor(DiscordUtil.escapeMarkdown(PlayerUtil.getDisplayName(event.getPlayer()))))
                .replace("%world%", event.getWorld())
                .replace("%deathmessage%", DiscordUtil.escapeMarkdown(event.getMessage()))
        ));
    }

    @Override
    public void onGamePlayerJoin(GamePlayerJoinEvent event) {
        // If player is OP & update is available tell them
        if ((PlayerUtil.isOp(event.getPlayer()) || PlayerUtil.hasPermission(event.getPlayer(), "discordsrv.admin")) && Manager.getInstance().getUpdateManager().isUpdateAvailable()) {
            PlayerUtil.sendMessage(event.getPlayer(), ChatColor.AQUA + "An update to DiscordSRV is available. Download it at http://dev.bukkit.org/bukkit-plugins/discordsrv/");
        }

        // make sure join messages enabled
        if (!Manager.getInstance().getConfig().getBoolean("MinecraftPlayerJoinMessageEnabled")) return;

        // user shouldn't have a quit message from permission
        if (PlayerUtil.hasPermission(event.getPlayer(), "discordsrv.silentjoin")) {
            Manager.getInstance().getPlatform().info("Player " + event.getPlayer() + " joined with silent joining permission, not sending a join message");
            return;
        }

        //TODO assign player's status to online since they don't have silent join platformutils
        playerStatusIsOnline.put(event.getPlayer(), true);

        // player doesn't have silent join permission, send join message
        DiscordUtil.sendMessage(Manager.getInstance().getMainChatChannel(), Manager.getInstance().getConfig().getString("MinecraftPlayerJoinMessageFormat")
                .replace("%username%", DiscordUtil.escapeMarkdown(event.getPlayer()))
                .replace("%displayname%", DiscordUtil.stripColor(DiscordUtil.escapeMarkdown(PlayerUtil.getDisplayName(event.getPlayer()))))
        );
    }

    @Override
    public void onGamePlayerQuit(GamePlayerQuitEvent event) {
        // Make sure quit messages enabled
        if (!Manager.getInstance().getConfig().getBoolean("MinecraftPlayerLeaveMessageEnabled")) return;

        // No quit message, user shouldn't have one from permission
        if (PlayerUtil.hasPermission(event.getPlayer(), "discordsrv.silentjoin")) {
            Manager.getInstance().getPlatform().info("Player " + event.getPlayer() + " quit with silent quiting permission, not sending a quit message");
            return;
        }

        // Remove player from status map to help with memory management
        playerStatusIsOnline.remove(event.getPlayer());

        // Player doesn't have silent quit, show quit message
        DiscordUtil.sendMessage(Manager.getInstance().getMainChatChannel(), Manager.getInstance().getConfig().getString("MinecraftPlayerLeaveMessageFormat")
                .replace("%username%", DiscordUtil.escapeMarkdown(event.getPlayer()))
                .replace("%displayname%", DiscordUtil.stripColor(DiscordUtil.escapeMarkdown(PlayerUtil.getDisplayName(event.getPlayer()))))
        );
    }

    @Override
    public void onGamePlayerAchievementRewarded(GamePlayerAchievementRewardedEvent event) {
        // return if achievement messages are disabled
        if (!Manager.getInstance().getConfig().getBoolean("MinecraftPlayerAchievementMessagesEnabled")) return;

        DiscordUtil.sendMessage(Manager.getInstance().getMainChatChannel(), DiscordUtil.stripColor(Manager.getInstance().getConfig().getString("MinecraftPlayerAchievementMessagesFormat")
                .replace("%username%", event.getPlayer())
                .replace("%displayname%", DiscordUtil.stripColor(DiscordUtil.escapeMarkdown(PlayerUtil.getDisplayName(event.getPlayer()))))
                .replace("%world%", event.getWorld())
                .replace("%achievement%", event.getAchievement())
        ));
    }

}
