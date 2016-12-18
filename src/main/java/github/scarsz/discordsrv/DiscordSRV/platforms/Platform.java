package github.scarsz.discordsrv.DiscordSRV.platforms;

import com.google.common.io.Resources;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @at 11/7/2016
 */
public interface Platform {

    /**
     * Get the File object representing this platform's File for storing DiscordSRV's config
     * @return the config file object
     */
    File getPluginConfigFile();

    /**
     * Get the given resource as an InputStream
     * @param name
     * @return
     */
    default InputStream getResourceAsStream(String name) {
        try {
            return new FileInputStream(new File(Resources.getResource(name).getFile()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Get the given resource as a String
     * @param name
     * @return
     */
    default String getResourceAsString(String name) {
        try {
            return IOUtils.toString(getResourceAsStream(name), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Trigger an info message on the platform's logger
     * @param message the message to be triggered
     */
    void info(String message);
    /**
     * Trigger a warning message on the platform's logger
     * @param message the message to be triggered
     */
    void warning(String message);
    /**
     * Trigger a severe message on the platform's logger
     * @param message the message to be triggered
     */
    void severe(String message);
    /**
     * Trigger a debug message on the platform's logger
     * @param message the message to be triggered
     */
    void debug(String message);

    /**
     * Query for the platform's enabled addons
     * @return alphabetical list of enabled addon names & versions
     */
    List<String> queryAddons();
    /**
     * Query for the platform's maximum player count
     * @return integer representing the platform's maximum player count
     */
    int queryMaxPlayers();
    /**
     * Query for the platform's Motto of the Day (MOTD)
     * @return String containing the server software version
     */
    String queryMotd();
    /**
     * TODO
     * @return
     */
    List<String> queryOnlinePlayers();
    /**
     * Query for the platform's server software version
     * @return String containing the server software version
     */
    String queryServerVersion();
    /**
     * TODO
     * @return
     */
    int queryTotalPlayers();
    /**
     * TODO
     * @return
     */
    String queryTps();

}
