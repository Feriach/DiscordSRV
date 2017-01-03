package github.scarsz.discordsrv.DiscordSRV.api;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @on 11/25/2016
 * @at 10:26 PM
 */
public abstract class GamePlayerEvent extends Event {

    public GamePlayerEvent(String player) {
        this.player = player;
    }

    private String player;
    public String getPlayer() {
        return player;
    }
    protected void setPlayer(String player) {
        this.player = player;
    }

}
