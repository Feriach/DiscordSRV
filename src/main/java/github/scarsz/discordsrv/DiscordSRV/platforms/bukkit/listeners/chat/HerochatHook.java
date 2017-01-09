package github.scarsz.discordsrv.DiscordSRV.platforms.bukkit.listeners.chat;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.Herochat;
import github.scarsz.discordsrv.DiscordSRV.DiscordSRV;
import github.scarsz.discordsrv.DiscordSRV.api.events.GameChatMessageEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @on 11/22/2016
 * @at 3:35 PM
 */

public class HerochatHook implements Listener {

    public HerochatHook() {
        DiscordSRV.getInstance().getHookedPlugins().add("herochat");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMessage(ChannelChatEvent event) {
        // make sure event is allowed
        if (event.getResult() != Chatter.Result.ALLOWED) return;

        // make sure chat channel is registered
        if (!DiscordSRV.getInstance().chatChannelIsLinked(event.getChannel().getName())) return;

        // make sure chat channel is linked to discord channel
        if (DiscordSRV.getInstance().getTextChannelFromChannelName(event.getChannel().getName()) == null) return;

        // make sure message isn't blank
        if (event.getMessage().replace(" ", "").isEmpty()) return;

        //Manager.getInstance().processChatEvent(false, event.getSender().getPlayer(), event.getMessage(), event.getChannel().getName());
        DiscordSRV.getInstance().processEvent(new GameChatMessageEvent(event.getSender().getPlayer().getName(), event.getMessage(), event.getChannel().getName(), event.getSender().getPlayer().getWorld().getName()));
    }

    public static void broadcastMessageToChannel(String channelName, String message, String rawMessage) {
        Channel chatChannel = Herochat.getChannelManager().getChannel(channelName);
        if (chatChannel == null) return; // no suitable channel found
        chatChannel.sendRawMessage(ChatColor.translateAlternateColorCodes('&', DiscordSRV.getInstance().getConfig().getString("ChatChannelHookMessageFormat")
                .replace("%channelcolor%", chatChannel.getColor().toString())
                .replace("%channelname%", chatChannel.getName())
                .replace("%channelnickname%", chatChannel.getNick())
                .replace("%message%", message)));

        // notify players
        List<Player> playersToNotify = new ArrayList<>();
        chatChannel.getMembers().forEach(chatter -> playersToNotify.add(chatter.getPlayer()));
    }

}