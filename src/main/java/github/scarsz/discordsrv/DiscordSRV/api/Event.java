package github.scarsz.discordsrv.DiscordSRV.api;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @on 11/22/2016
 * @at 3:18 PM
 */
public abstract class Event extends Cancelable {

    // I'm retarded
//    /**
//     * Perform the event as the manager. For internal use only.
//     * @return true if event was executed successfully, false for errors like external usage
//     */
//    public boolean perform() {
//        String classFromStackTrace = Thread.currentThread().getStackTrace()[2].getClassName();
//        if (!classFromStackTrace.contains(Manager.class.getName())) {
//            Manager.getInstance().getPlatform().severe("Class " + classFromStackTrace + " attempted to manually perform an event. (you're not allowed to do that)");
//            return false;
//        }
//        return true;
//    }

}
