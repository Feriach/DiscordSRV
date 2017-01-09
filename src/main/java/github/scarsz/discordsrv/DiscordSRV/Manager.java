package github.scarsz.discordsrv.DiscordSRV;

import github.scarsz.discordsrv.DiscordSRV.api.DiscordSRVListener;
import github.scarsz.discordsrv.DiscordSRV.api.Event;
import github.scarsz.discordsrv.DiscordSRV.api.Priority;
import github.scarsz.discordsrv.DiscordSRV.objects.AccountLinkManager;
import github.scarsz.discordsrv.DiscordSRV.objects.Config;
import github.scarsz.discordsrv.DiscordSRV.objects.PlatformType;
import github.scarsz.discordsrv.DiscordSRV.objects.UpdateManager;
import github.scarsz.discordsrv.DiscordSRV.platforms.Platform;
import github.scarsz.discordsrv.DiscordSRV.threads.ChannelTopicUpdater;
import github.scarsz.discordsrv.DiscordSRV.util.DiscordUtil;
import lombok.Getter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.yaml.snakeyaml.Yaml;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @at 11/7/2016
 */
public class Manager {

    @Getter private static Manager instance;
    @Getter private static String version = "13.0-SNAPSHOT";
    @Getter private Platform platform;
    @Getter private PlatformType platformType;

    public Manager(Platform platform) {
        instance = this;
        this.platform = platform;
        config = new Config();

        switch (platform.getClass().getSimpleName().replace("Platform", "").toLowerCase()) {
            // in order of least to most hated
            case "bukkit": this.platformType = PlatformType.BUKKIT; break;
            case "bungeecord": this.platformType = PlatformType.BUNGEECORD; break;
            case "sponge": this.platformType = PlatformType.SPONGE; break;
            default: platform.severe("Could not determine platform. Tell Scarsz to fix this cause he's a retard."); this.platformType = PlatformType.BUKKIT;
        }
    }

    @Getter private static final DecimalFormat decimalFormat = new DecimalFormat("#.#");
    @Getter private final Map<String, TextChannel> channels = new LinkedHashMap<>(); //TODO loading
    @Getter private Config config;
    @Getter private Yaml yaml = new Yaml();
    @Getter private final List<String> hookedPlugins = new ArrayList<>();
    @Getter private JDA jda = null;
    @Getter private final long startTime = System.currentTimeMillis();
    @Getter private AccountLinkManager accountLinkManager = new AccountLinkManager(new File(platform.getPluginConfigFile().getParentFile(), "accounts.yml"));
    @Getter private final UpdateManager updateManager = new UpdateManager();
    @Getter private final List<String> randomPhrases = new ArrayList<>();
    @Getter private final List<String> unsubscribedPlayers = new ArrayList<>();
    @Getter private final Map<String, String> colors = new HashMap<>();

    @Getter private TextChannel mainChatChannel; //TODO
    @Getter private TextChannel consoleChannel; //TODO
    @Getter private List<DiscordSRVListener> listeners = new ArrayList<>();
    @Getter private ChannelTopicUpdater channelTopicUpdater = new ChannelTopicUpdater();

    public void initialize() {
        // show initializing message
        platform.info("Initializing DiscordSRV Manager v" + version);

        // send the config File to the Config & init
        config.configFile = platform.getPluginConfigFile();
        config.initialize();

        // check for updates
        updateManager.checkIfUpdateIsAvailable();

        //TODO CKC thank yous
        //TODO random phrases

        // shutdown JDA if it was already running (plugin reload? 🤦)
        if (jda != null) jda.shutdown(false);

        // set JDA message logging if it hasn't been already
        if (SimpleLog.LEVEL != SimpleLog.Level.OFF) {
            SimpleLog.LEVEL = SimpleLog.Level.OFF;
            SimpleLog.addListener(new SimpleLog.LogListener() {
                @Override
                public void onLog(SimpleLog simpleLog, SimpleLog.Level level, Object o) {
                    switch (level) {
                        case INFO:
                            platform.info("[JDA] " + o);
                            break;
                        case WARNING:
                            platform.warning("[JDA WARNING] " + o);
                            break;
                        case FATAL:
                            platform.severe("[JDA ERROR] " + o);
                            break;
                    }
                }
                @Override
                public void onError(SimpleLog simpleLog, Throwable throwable) {}
            });
        }

        // build JDA
        try {
            JDABuilder builder = new JDABuilder(AccountType.BOT)
                    .setToken(config.getString("BotToken")) // set bot token
                    .addListener(new DiscordListener()) // register Discord listener
                    .setAutoReconnect(true) // automatically reconnect to Discord if shit happens
                    .setAudioEnabled(false) // we don't use audio and this not being disabled causes major codec problems on some systems
                    .setBulkDeleteSplittingEnabled(false) // has to be off for JDA not to bitch
                    .setStatus(OnlineStatus.ONLINE); // set bot as online

            // set game status
            if (config.getString("DiscordGameStatus") != null && !config.getString("DiscordGameStatus").isEmpty())
                builder.setGame(Game.of(config.getString("DiscordGameStatus")));

            jda = builder.buildBlocking(); // build JDA
        } catch (LoginException | InterruptedException | RateLimitedException e) {
            e.printStackTrace();
        }

        // print the things the bot can see
        for (Guild guild : jda.getGuilds()) {
            platform.info("Found guild " + guild);
            for (Channel channel : guild.getTextChannels()) {
                platform.info("- " + channel);
            }
        }

        // show warning if bot wasn't in any guilds
        if (jda.getGuilds().size() == 0) {
            platform.severe("The bot is not a part of any Discord guilds. Follow the installation instructions.");
            return;
        }

        // check & get location info
        mainChatChannel = new ArrayList<>(channels.values()).get(0);
        consoleChannel = jda.getTextChannelById(config.getString("DiscordConsoleChannelId"));

        if (mainChatChannel == null) platform.warning("Specified chat channel from channels.json could not be found (is it's name set to \"" + config.getString("DiscordMainChatChannel") + "\"?)");
        if (consoleChannel == null) platform.warning("Specified console channel from config could not be found");
        if (mainChatChannel == null && consoleChannel == null) {
            platform.severe("Chat and console channels are both unavailable, plugin will not work properly");
            return;
        }
        if (mainChatChannel.getId().equals(consoleChannel.getId())) {
            platform.severe("Main chat channel has the same channel ID as the console channel, do you seriously want to stream your console to your chat channel?");
        }

        // send startup message if enabled
        if (config.getBoolean("DiscordChatChannelServerStartupMessageEnabled")) DiscordUtil.sendMessage(mainChatChannel, config.getString("DiscordChatChannelServerStartupMessage"));

        // start channel topic updater if not already
        if (channelTopicUpdater != null && channelTopicUpdater.getState() == Thread.State.NEW) channelTopicUpdater.start();
        else {
            if (channelTopicUpdater != null) channelTopicUpdater.interrupt();
            channelTopicUpdater = new ChannelTopicUpdater();
            channelTopicUpdater.start();
        }
    }

    public void shutdown() {
        platform.info("Manager shutting down...");
        long shutdownStartTime = System.currentTimeMillis();

        jda.getPresence().setStatus(OnlineStatus.INVISIBLE);
        jda.shutdown(false);

        if (channelTopicUpdater != null) channelTopicUpdater.interrupt();

        if (accountLinkManager != null) accountLinkManager.save();

        platform.info("Shutdown completed in " + (System.currentTimeMillis() - shutdownStartTime) + "ms");
    }

    public void processEvent(Event event) {
        platform.debug("Event " + event + ":");
        for (Priority priority : Priority.values()) {
            platform.debug("Processing priority " + priority);
            for (DiscordSRVListener listener : listeners) {
                if (listener.getPriority() != priority) continue;
                System.out.println("Performing listener " + listener.getName());

                try {
                    Method method = null;
                    for (Method iteratedMethod : listener.getClass().getMethods()) {
                        if (iteratedMethod.getName().equals("on" + event.getClass().getSimpleName().replace("Event", "")) && iteratedMethod.getParameterCount() == 1)
                            method = iteratedMethod;
                    }
                    if (method == null) continue;

                    boolean isCanceled = event.isCanceled();
                    method.invoke(listener, event);
                    if (isCanceled != event.isCanceled()) platform.debug("Event " + event.getClass().getSimpleName() + " canceled by " + listener.getName());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        // at this point, all listeners have been informed of the event. the for loop ends directly
        // after the last MONITOR priority, so code after this is technically also MONITOR priority
        // but there's nothing to see here
    }

    public void addListener(DiscordSRVListener listener) {
        listeners.add(listener);
        platform.info("Listener \"" + listener.getName() + "\" [" + listener.getClass() + "] registered");
    }
    public void removeListener(DiscordSRVListener listener) {
        listeners.remove(listener);
        platform.info("Listener \"" + listener.getName() + "\" [" + listener.getClass() + "] unregistered");
    }

    //TODO rename getTextChannelFromChannelName
    public TextChannel getTextChannelFromChannelName(String channelName) {
        return channels.get(channelName);
    }
    public String getChannelNameFromTextChannel(TextChannel channel) {
        for (Map.Entry<String, TextChannel> entry : channels.entrySet())
            if (entry.getValue().getId().equals(channel.getId()))
                return entry.getKey();
        return null;
    }

    public boolean chatChannelIsLinked(String channelName) {
        return channels.containsKey(channelName);
    }
    public boolean discordChannelIsLinked(TextChannel channel) {
        return channels.containsValue(channel);
    }

}
