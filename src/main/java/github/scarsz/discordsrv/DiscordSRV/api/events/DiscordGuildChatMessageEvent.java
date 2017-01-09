package github.scarsz.discordsrv.DiscordSRV.api.events;

import github.scarsz.discordsrv.DiscordSRV.DiscordSRV;
import github.scarsz.discordsrv.DiscordSRV.api.Event;
import lombok.Getter;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @at 11/7/2016
 */
public class DiscordGuildChatMessageEvent extends Event {

    @Getter private final String discordChannelId;
    @Getter private final String gameDestinationChannel;
    @Getter private final String message;
    @Getter private final GuildMessageReceivedEvent rawEvent;
    @Getter private final String senderId;

    public DiscordGuildChatMessageEvent(GuildMessageReceivedEvent rawEvent) {
        this.discordChannelId = rawEvent.getChannel().getId();
        this.gameDestinationChannel = DiscordSRV.getInstance().getChannelNameFromTextChannel(rawEvent.getChannel());
        this.message = rawEvent.getMessage().getRawContent();
        this.rawEvent = rawEvent;
        this.senderId = rawEvent.getAuthor().getId();
    }

}
