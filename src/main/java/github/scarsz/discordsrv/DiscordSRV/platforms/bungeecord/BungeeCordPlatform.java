package github.scarsz.discordsrv.DiscordSRV.platforms.bungeecord;

import github.scarsz.discordsrv.DiscordSRV.DiscordSRV;
import github.scarsz.discordsrv.DiscordSRV.platforms.Platform;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @at 11/7/2016
 */
public abstract class BungeeCordPlatform extends Plugin implements Platform {

    public DiscordSRV discordSRV = new DiscordSRV(this);
    public static BungeeCordPlatform instance = null;

    @Override
    public void onEnable() {
        instance = this;
        discordSRV.initialize();
    }

    @Override
    public void onDisable() {

    }

}
