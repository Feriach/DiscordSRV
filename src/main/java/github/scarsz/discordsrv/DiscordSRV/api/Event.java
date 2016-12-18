package github.scarsz.discordsrv.DiscordSRV.api;

import github.scarsz.discordsrv.DiscordSRV.Manager;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @on 11/22/2016
 * @at 3:18 PM
 */
public abstract class Event extends Cancelable {

    public boolean perform() {
        String classFromStackTrace = Thread.currentThread().getStackTrace()[2].getClassName();
        if (!classFromStackTrace.contains(Manager.class.getName())) {
            Manager.getInstance().getPlatform().severe("Class " + classFromStackTrace + " attempted to manually perform an event. (you're not allowed to do that)");
            return false;
        }
        return true;
    }

}
