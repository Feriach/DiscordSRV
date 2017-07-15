package github.scarsz.discordsrv;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

@Plugin(
        id = "discordsrv",
        name = "Discordsrv",
        description = "The most powerful, configurable, widely loved, open-source Discord bridge plugin out there.",
        url = "https://github.com/Scarsz/DiscordSRV",
        authors = {
                "Scarsz",
                "Androkai"
        }
)
public class DiscordSRVSponge {

    @Inject
    private Logger logger;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.info("Hello world!");
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event, @Getter("getTargetEntity") Player player) {
        // The text message could be configurable, check the docs on how to do so!
        player.sendMessage(Text.of(TextColors.AQUA + "" + TextStyles.BOLD + "Hi " + player.getName()));
    }

}
