package github.scarsz.discordsrv.DiscordSRV;

import github.scarsz.discordsrv.DiscordSRV.api.events.DiscordGenericEvent;
import github.scarsz.discordsrv.DiscordSRV.api.events.DiscordGuildChatMessageEvent;
import github.scarsz.discordsrv.DiscordSRV.api.events.DiscordPrivateMessageChatMessageEvent;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @at 11/7/2016
 */
public class DiscordListener extends ListenerAdapter {

    @Override
    public void onGenericEvent(Event event) {
        Manager.getInstance().processEvent(new DiscordGenericEvent(event));
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        Manager.getInstance().processEvent(new DiscordGuildChatMessageEvent(event));
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        Manager.getInstance().processEvent(new DiscordPrivateMessageChatMessageEvent(event));
    }

}
