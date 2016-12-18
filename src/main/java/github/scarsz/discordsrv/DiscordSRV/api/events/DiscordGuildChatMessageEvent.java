package github.scarsz.discordsrv.DiscordSRV.api.events;

import github.scarsz.discordsrv.DiscordSRV.Manager;
import github.scarsz.discordsrv.DiscordSRV.api.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @at 11/7/2016
 */
public class DiscordGuildChatMessageEvent extends Event {

    private final String discordChannelId;
    private final String gameDestinationChannel;
    private final String message;
    private final GuildMessageReceivedEvent rawEvent;
    private final String senderId;

    public DiscordGuildChatMessageEvent(GuildMessageReceivedEvent rawEvent) {
        this.discordChannelId = rawEvent.getChannel().getId();
        this.gameDestinationChannel = Manager.getInstance().getChannelNameFromTextChannel(rawEvent.getChannel());
        this.message = rawEvent.getMessage().getRawContent();
        this.rawEvent = rawEvent;
        this.senderId = rawEvent.getAuthor().getId();
    }

    public String getDiscordChannelId() {
        return discordChannelId;
    }
    public String getGameDestinationChannel() {
        return gameDestinationChannel;
    }
    public String getMessage() {
        return message;
    }
    public GuildMessageReceivedEvent getRawEvent() {
        return rawEvent;
    }
    public String getSenderId() {
        return senderId;
    }

}
