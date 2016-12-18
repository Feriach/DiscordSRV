package github.scarsz.discordsrv.DiscordSRV.platforms.bukkit.listeners.chat;

import github.scarsz.discordsrv.DiscordSRV.Manager;
import github.scarsz.discordsrv.DiscordSRV.api.events.GameChatMessageEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @on 11/22/2016
 * @at 3:34 PM
 */
public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Manager.getInstance().processEvent(GameChatMessageEvent.fromEvent(event, null));
    }

}
