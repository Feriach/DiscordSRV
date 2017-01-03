package github.scarsz.discordsrv.DiscordSRV.api;

import lombok.Getter;
import lombok.Setter;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @on 11/25/2016
 * @at 10:42 PM
 */
public abstract class Cancelable {

    /**
     * @param canceled whether or not the event should be canceled
     * @return whether or not the event is canceled
     */
    @Getter @Setter protected boolean canceled = false;

}
