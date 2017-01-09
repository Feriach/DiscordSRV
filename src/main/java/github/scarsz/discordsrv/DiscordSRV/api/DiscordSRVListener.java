package github.scarsz.discordsrv.DiscordSRV.api;

import github.scarsz.discordsrv.DiscordSRV.api.events.*;
import lombok.Getter;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @at 11/7/2016
 */
@SuppressWarnings("unused")
public abstract class DiscordSRVListener {

    @Getter private final String name;
    @Getter private final Priority priority;

    public DiscordSRVListener(String name) {
        this(name, Priority.NORMAL);
    }
    public DiscordSRVListener(String name, Priority priority) {
        this.name = name;
        this.priority = priority;
    }

    /**
     * Called upon receiving a raw event from Discord, before the specific API method for the event is called
     * @param event
     */
    public void onDiscordGeneric(DiscordGenericEvent event) {}

    /**
     * Called when a Discord guild message event is being processed for delivery to Minecraft
     * @param event
     */
    public void onDiscordGuildChatMessage(DiscordGuildChatMessageEvent event) {}

    /**
     * Called when a Discord private message event is being processed for delivery to Minecraft
     * @param event
     */
    public void onDiscordPrivateMessageChatMessage(DiscordPrivateMessageChatMessageEvent event) {}
    
    /**
     * Called when a chat event is being processed for delivery to Discord
     * @param event
     */
    public void onGameChatMessage(GameChatMessageEvent event) {}

    /**
     * Called when a death event is being processed for delivery to Discord
     * @param event
     */
    public void onGamePlayerDeath(GamePlayerDeathEvent event) {}

    /**
     * Called when a join event is being processed for delivery to Discord
     * @param event
     */
    public void onGamePlayerJoin(GamePlayerJoinEvent event) {}

    /**
     * Called when a quit event is being processed for delivery to Discord
     * @param event
     */
    public void onGamePlayerQuit(GamePlayerQuitEvent event) {}

    /**
     * Called when a achievement reward event is being processed for delivery to Discord
     * @param event
     */
    public void onGamePlayerAchievementRewarded(GamePlayerAchievementRewardedEvent event) {}

}
