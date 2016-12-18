package github.scarsz.discordsrv.DiscordSRV.platforms.bukkit.objects;

import github.scarsz.discordsrv.DiscordSRV.api.DiscordSRVListener;
import github.scarsz.discordsrv.DiscordSRV.api.events.DiscordGuildChatMessageEvent;
import org.bukkit.Bukkit;

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

    @Override
    public void onDiscordGuildChatMessage(DiscordGuildChatMessageEvent event) {
        if (event.getGameDestinationChannel() == null) {
            Bukkit.broadcastMessage(event.getMessage());
        }
        //TODO if channel not null
    }

}
