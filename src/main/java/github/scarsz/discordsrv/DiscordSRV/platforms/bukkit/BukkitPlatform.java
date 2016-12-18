package github.scarsz.discordsrv.DiscordSRV.platforms.bukkit;

import github.scarsz.discordsrv.DiscordSRV.Manager;
import github.scarsz.discordsrv.DiscordSRV.platforms.Platform;
import github.scarsz.discordsrv.DiscordSRV.platforms.bukkit.listeners.DeathListener;
import github.scarsz.discordsrv.DiscordSRV.platforms.bukkit.listeners.chat.*;
import github.scarsz.discordsrv.DiscordSRV.platforms.bukkit.objects.BukkitDiscordSRVListener;
import github.scarsz.discordsrv.DiscordSRV.platforms.bukkit.objects.CancelationDetector;
import github.scarsz.discordsrv.DiscordSRV.platforms.bukkit.objects.Lag;
import lombok.Getter;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @at 11/7/2016
 */
public class BukkitPlatform extends JavaPlugin implements Platform, Listener {

    @Getter private static BukkitPlatform instance = null;

    /*
         /$$$$$$             /$$                          /$$$$$$
        |_  $$_/            | $$                         /$$__  $$
          | $$   /$$$$$$$  /$$$$$$    /$$$$$$   /$$$$$$ | $$  \__//$$$$$$   /$$$$$$$  /$$$$$$
          | $$  | $$__  $$|_  $$_/   /$$__  $$ /$$__  $$| $$$$   |____  $$ /$$_____/ /$$__  $$
          | $$  | $$  \ $$  | $$    | $$$$$$$$| $$  \__/| $$_/    /$$$$$$$| $$      | $$$$$$$$
          | $$  | $$  | $$  | $$ /$$| $$_____/| $$      | $$     /$$__  $$| $$      | $$_____/
         /$$$$$$| $$  | $$  |  $$$$/|  $$$$$$$| $$      | $$    |  $$$$$$$|  $$$$$$$|  $$$$$$$
        |______/|__/  |__/   \___/   \_______/|__/      |__/     \_______/ \_______/ \_______/
     */

    public File getPluginConfigFile() {
        return new File(getDataFolder(), "config.yml");
    }
    public InputStream getResourceAsStream(String name) {
        return getResource(name);
    }
    public void info(String message) {
        getLogger().info(message);
    }
    public void warning(String message) {
        getLogger().warning(message);
    }
    public void severe(String message) {
        getLogger().severe(message);
    }
    public void debug(String message) {
        getLogger().info("DEBUG | " + message);
    }

    public List<String> queryAddons() {
        List<String> plugins = new ArrayList<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) plugins.add(plugin.toString());
        Collections.sort(plugins);
        return plugins;
    }
    public int queryMaxPlayers() {
        return Bukkit.getMaxPlayers();
    }
    public String queryMotd() {
        return Bukkit.getMotd();
    }
    public List<String> queryOnlinePlayers() {
        List<String> onlinePlayers = Bukkit.getOnlinePlayers().stream().map((Function<Player, String>) HumanEntity::getName).collect(Collectors.toList());
        //TODO check if player is vanished
        return onlinePlayers;
    }
    public String queryServerVersion() {
        return Bukkit.getBukkitVersion();
    }
    public String queryTps() {
        return Lag.getTPSString();
    }
    public int queryTotalPlayers() {
        return Bukkit.getWorlds().size() != 0 ? new File(Bukkit.getWorlds().get(0).getWorldFolder().getAbsolutePath(), "/playerdata").listFiles(f -> f.getName().endsWith(".dat")).length : 0;
    }

    /*
         /$$$$$$$  /$$             /$$      /$$$$$$
        | $$__  $$| $$            | $$     /$$__  $$
        | $$  \ $$| $$  /$$$$$$  /$$$$$$  | $$  \__//$$$$$$   /$$$$$$  /$$$$$$/$$$$
        | $$$$$$$/| $$ |____  $$|_  $$_/  | $$$$   /$$__  $$ /$$__  $$| $$_  $$_  $$
        | $$____/ | $$  /$$$$$$$  | $$    | $$_/  | $$  \ $$| $$  \__/| $$ \ $$ \ $$
        | $$      | $$ /$$__  $$  | $$ /$$| $$    | $$  | $$| $$      | $$ | $$ | $$
        | $$      | $$|  $$$$$$$  |  $$$$/| $$    |  $$$$$$/| $$      | $$ | $$ | $$
        |__/      |__/ \_______/   \___/  |__/     \______/ |__/      |__/ |__/ |__/
     */

    private CancelationDetector<AsyncPlayerChatEvent> cancelationDetector = new CancelationDetector<>(AsyncPlayerChatEvent.class);

    @Override
    public void onEnable() {
        instance = this;

        // check if the person is trying to use the plugin on Thermos without updating to ASM5
        try {
            File specialSourceFile = new File("libraries/net/md-5/SpecialSource/1.7-SNAPSHOT/SpecialSource-1.7-SNAPSHOT.jar");
            if (specialSourceFile.exists() && DigestUtils.md5Hex(FileUtils.readFileToByteArray(specialSourceFile)).equalsIgnoreCase("096777a1b6098130d6c925f1c04050a3")) {
                getLogger().warning("");
                getLogger().warning("");
                getLogger().warning("You're attempting to use DiscordSRV on Thermos without applying the SpecialSource/ASM5 fix.");
                getLogger().warning("DiscordSRV WILL NOT work without it on Thermos. Blame the Thermos developers for having outdated libraries.");
                getLogger().warning("");
                getLogger().warning("Instructions for updating to ASM5:");
                getLogger().warning("1. Navigate to the libraries/net/md-5/SpecialSource/1.7-SNAPSHOT folder of the server");
                getLogger().warning("2. Delete the SpecialSource-1.7-SNAPSHOT.jar jar file");
                getLogger().warning("3. Download SpecialSource v1.7.4 from http://central.maven.org/maven2/net/md-5/SpecialSource/1.7.4/SpecialSource-1.7.4.jar");
                getLogger().warning("4. Copy the jar file to the libraries/net/md-5/SpecialSource/1.7-SNAPSHOT folder");
                getLogger().warning("5. Rename the jar file you just copied to SpecialSource-1.7-SNAPSHOT.jar");
                getLogger().warning("6. Restart the server");
                getLogger().warning("");
                getLogger().warning("");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Manager(this);
        Manager.getInstance().initialize();
        Manager.getInstance().addListener(new BukkitDiscordSRVListener());

        // clear past tasks in scheduler if any
        Bukkit.getServer().getScheduler().cancelTasks(this);

        // start TPS monitor
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Lag(), 100L, 1L);

        // in-game chat events
        if (checkIfPluginEnabled("herochat") && getConfig().getBoolean("HeroChatHook")) {
            getLogger().info("Enabling Herochat hook");
            getServer().getPluginManager().registerEvents(new HerochatHook(), this);
        } else if (checkIfPluginEnabled("legendchat") && getConfig().getBoolean("LegendChatHook")) {
            getLogger().info("Enabling LegendChat hook");
            getServer().getPluginManager().registerEvents(new LegendChatHook(), this);
        } else if (checkIfPluginEnabled("LunaChat") && getConfig().getBoolean("LunaChatHook")) {
            getLogger().info("Enabling LunaChat hook");
            getServer().getPluginManager().registerEvents(new LunaChatHook(), this);
        } else if (checkIfPluginEnabled("Towny") && checkIfPluginEnabled("TownyChat") && getConfig().getBoolean("TownyChatHook")) {
            getLogger().info("Enabling TownyChat hook");
            getServer().getPluginManager().registerEvents(new TownyChatHook(), this);
        } else if (checkIfPluginEnabled("venturechat") && getConfig().getBoolean("VentureChatHook")) {
            getLogger().info("Enabling VentureChat hook");
            getServer().getPluginManager().registerEvents(new VentureChatHook(), this);
        } else {
            getLogger().info("No compatible chat plugins found that have their hooks enabled");
            getServer().getPluginManager().registerEvents(new ChatListener(), this);
        }

        // in-game death events
        getServer().getPluginManager().registerEvents(new DeathListener(), this);

        // enable reporting plugins that have canceled chat events
        if (Manager.getInstance().getConfig().getBoolean("ReportCanceledChatEvents")) {
            getLogger().info("Chat event cancelation detector has been enabled");
            cancelationDetector.addListener((plugin, event) -> info(event.getClass().getName() + " cancelled by " + plugin));
        }
    }

    @Override
    public void onDisable() {
        // shutdown manager
        Manager.getInstance().shutdown();

        // stop cancelation detector
        cancelationDetector.close();
    }

    private boolean checkIfPluginEnabled(String pluginName) {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins())
            if (plugin.getName().equalsIgnoreCase(pluginName)) return true;
        return false;
    }

}
