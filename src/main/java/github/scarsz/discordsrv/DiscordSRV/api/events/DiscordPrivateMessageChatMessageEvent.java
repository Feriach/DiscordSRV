package github.scarsz.discordsrv.DiscordSRV.api.events;

import github.scarsz.discordsrv.DiscordSRV.api.Event;
import lombok.Getter;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @at 11/7/2016
 */
public class DiscordPrivateMessageChatMessageEvent extends Event {

    @Getter private final String message;
    @Getter private final PrivateChannel privateChannel;
    @Getter private final PrivateMessageReceivedEvent rawEvent;
    @Getter private final String senderId;
    @Getter private final String senderName;

    public DiscordPrivateMessageChatMessageEvent(PrivateMessageReceivedEvent rawEvent) {
        this.message = rawEvent.getMessage().getRawContent();
        this.privateChannel = rawEvent.getChannel();
        this.rawEvent = rawEvent;
        this.senderId = rawEvent.getAuthor().getId();
        this.senderName = rawEvent.getAuthor().getName();
    }

}
