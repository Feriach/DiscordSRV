package github.scarsz.discordsrv.DiscordSRV.platforms.bukkit.listeners;

import github.scarsz.discordsrv.DiscordSRV.DiscordSRV;
import github.scarsz.discordsrv.DiscordSRV.api.events.GamePlayerDeathEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @on 11/28/2016
 * @at 8:57 PM
 */
public class DeathListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        DiscordSRV.getInstance().processEvent(GamePlayerDeathEvent.fromEvent(event));
    }

}
