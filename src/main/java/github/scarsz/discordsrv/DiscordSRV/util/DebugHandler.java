package github.scarsz.discordsrv.DiscordSRV.util;

import github.scarsz.discordsrv.DiscordSRV.Manager;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @on 12/17/2016
 * @at 6:49 PM
 */
public class DebugHandler {

    public static String run() {
        List<String> info = new LinkedList<>();

        info.add("DiscordSRV debug report - generated " + new Date());
        info.add("");

        if (Manager.getInstance().getRandomPhrases().size() > 0) {
            info.add(Manager.getInstance().getRandomPhrases().get(new Random().nextInt(Manager.getInstance().getRandomPhrases().size())));
            info.add("");
        }

        info.add("Server MOTD: " + DiscordUtil.stripColor(Manager.getInstance().getPlatform().queryMotd()));
        info.add("Server players: " + Manager.getInstance().getPlatform().queryOnlinePlayers().size() + "/" + Manager.getInstance().getPlatform().queryMaxPlayers());
        info.add("Server addons: " + Manager.getInstance().getPlatform().queryAddons());
        info.add("");
        info.add("Config version: " + Manager.getInstance().getConfig().getString("ConfigVersion"));
        info.add("Plugin version: " + Manager.getVersion());
        info.add("Version: " + Manager.getInstance().getPlatform().queryServerVersion());
        info.add("");

        // system properties
        info.add("System properties:");
        ManagementFactory.getRuntimeMXBean().getSystemProperties().forEach((key, value) -> info.add(key + "= " + value));
        info.add("");

        // total number of processors or cores available to the JVM
        info.add("Available processors (cores): " + Runtime.getRuntime().availableProcessors());
        info.add("");

        // memory
        info.add("Free memory (MB): " + Runtime.getRuntime().freeMemory() / 1024 / 1024);
        info.add("Maximum memory (MB): " + (Runtime.getRuntime().maxMemory() == Long.MAX_VALUE ? "no limit" : Runtime.getRuntime().maxMemory() / 1024 / 1024));
        info.add("Total memory available to JVM (MB): " + Runtime.getRuntime().totalMemory() / 1024 / 1024);
        info.add("");

        // drive space
        File[] roots = File.listRoots();
        for (File root : roots) {
            info.add("File system " + root.getAbsolutePath());
            info.add("- Total space (MB): " + root.getTotalSpace() / 1024 / 1024);
            info.add("- Free space (MB): " + root.getFreeSpace() / 1024 / 1024);
            info.add("- Usable space (MB): " + root.getUsableSpace() / 1024 / 1024);
        }
        info.add("");

        // config.yml
        info.add("parsed config");
        info.addAll(Manager.getInstance().getConfig().getEntrySet().stream().filter(s -> !s.getKey().equals("BotToken")).map(s -> s + ": " + s.getValue()).collect(Collectors.toList()));
        info.add("");

        // channels
        info.add("channels");
        info.add(String.valueOf(Manager.getInstance().getChannels()));
        info.add("");

        // channel permissions
        info.add("channel permissions");
        List<TextChannel> channelsToShowPermissionInfoOf = new ArrayList<>();
        Manager.getInstance().getChannels().values().forEach(channelsToShowPermissionInfoOf::add);
        if (Manager.getInstance().getConsoleChannel() != null) channelsToShowPermissionInfoOf.add(Manager.getInstance().getConsoleChannel());
        for (TextChannel textChannel : channelsToShowPermissionInfoOf) {
            List<String> permissions = new ArrayList<>();
            if (DiscordUtil.checkPermission(textChannel, Permission.MESSAGE_READ)) permissions.add("read");
            if (DiscordUtil.checkPermission(textChannel, Permission.MESSAGE_WRITE)) permissions.add("write");
            if (DiscordUtil.checkPermission(textChannel, Permission.MANAGE_CHANNEL)) permissions.add("manage channel");
            if (DiscordUtil.checkPermission(textChannel, Permission.MESSAGE_MANAGE)) permissions.add("manage messages");
            info.add(textChannel + ": " + permissions);
        }
        info.add("");

        // channels.json
        info.add("channels.json");
        try {
            FileReader fr = new FileReader(new File(Manager.getInstance().getPlatform().getPluginConfigFile().getParentFile(), "channels.json"));
            BufferedReader br = new BufferedReader(fr);
            boolean done = false;
            while (!done)
            {
                String line = br.readLine();
                if (line != null) {
                    if (!line.startsWith("/")) info.add(line);
                } else
                    done = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        info.add("");

        // discordsrv info
        info.add("discordsrv info");
        info.add("consoleChannel: " + Manager.getInstance().getConsoleChannel());
        info.add("mainChatChannel: " + Manager.getInstance().getMainChatChannel());
        info.add("unsubscribedPlayers: " + Manager.getInstance().getUnsubscribedPlayers());
        info.add("colors: " + Manager.getInstance().getColors());
        info.add("threads: " + Arrays.asList(
                "channelTopicUpdater -> alive: " + (Manager.getInstance().getChannelTopicUpdater() != null && Manager.getInstance().getChannelTopicUpdater().isAlive())
        ));
        info.add("updateIsAvailable: " + Manager.getInstance().getUpdateManager().isUpdateAvailable());
        info.add("hookedPlugins: " + Manager.getInstance().getHookedPlugins());
        info.add("");

        // latest.log lines
        try {
            FileReader fr = new FileReader(new File(new File("."), "logs/latest.log"));
            BufferedReader br = new BufferedReader(fr);
            info.add("Lines for DiscordSRV from latest.log:");
            boolean done = false;
            while (!done)
            {
                String line = br.readLine();
                if (line == null) done = true;
                if (line != null && line.toLowerCase().contains("discordsrv")) info.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // upload data
        String data = String.join("\n", info);
        try {
            StringBuilder sb = new StringBuilder();
            try {
                URLConnection conn = new URL("http://hastebin.com/documents").openConnection();
                conn.setRequestProperty("User-Agent", "DiscordSRV");
                conn.setDoOutput(true);
                byte[] outputInBytes = data.getBytes("UTF-8");
                OutputStream os = conn.getOutputStream();
                os.write(outputInBytes);
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) sb.append(inputLine);
                in.close();
                return "http://hastebin.com/" + sb.toString().split("\"")[3];
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        } catch (RuntimeException e) {
            // fucking hastebin
            Manager.getInstance().getPlatform().warning("Failed uploading debug report to Hastebin (" + e.getMessage() + ")");

            File debugReportFile = new File("DiscordSRVDebugReport.txt");
            try {
                FileUtils.writeStringToFile(debugReportFile, data, Charset.defaultCharset());
                Manager.getInstance().getPlatform().warning("The debug report has been dumped to " + debugReportFile.getName() + " in the server's main folder");
                return "<server folder>/" + debugReportFile.getName();
            } catch (IOException e1) {
                // shit has completely hit the fan
                Manager.getInstance().getPlatform().warning("Additionally, we couldn't dump the debug report to a file. Thus, here's the debug report in the console:\n" + data);
                return "the console";
            }
        }
    }

}