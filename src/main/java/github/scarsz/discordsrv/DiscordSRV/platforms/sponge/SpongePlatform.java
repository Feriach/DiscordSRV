package github.scarsz.discordsrv.DiscordSRV.platforms.sponge;

import com.google.inject.Inject;
import github.scarsz.discordsrv.DiscordSRV.DiscordSRV;
import github.scarsz.discordsrv.DiscordSRV.platforms.Platform;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @at 11/7/2016
 */
@Plugin(
        id = "discordsrv",
        name = "DiscordSRV",
        version = "13.0",
        description = "The most powerful, configurable, open-source Discord bridge plugin out there.",
        url = "https://github.com/Scarsz/DiscordSRV",
        authors = { "Scarsz", "Androkai" }
)
public abstract class SpongePlatform implements Platform {

    @Inject
    private Logger logger;

    public DiscordSRV discordSRV = new DiscordSRV(this);
    public static SpongePlatform instance = null;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        instance = this;
        discordSRV.initialize();
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event, @Getter("getTargetEntity") Player player) {
        // The text message could be configurable, check the docs on how to do so!
        player.sendMessage(Text.of(TextColors.AQUA, TextStyles.BOLD, "Hi " + player.getName()));
    }

    @Override
    public String queryTps() {
        return DiscordSRV.getDecimalFormat().format(Sponge.getServer().getTicksPerSecond());
    }
}
