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

import javax.security.auth.login.LoginException;
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
    @Getter private Platform platform;
    @Getter private PlatformType platformType;

    public Manager(Platform platform) {
        Manager.instance = this;
        this.platform = platform;
        config = new Config();

        switch (platform.getClass().getSimpleName().replace("Platform", "").toLowerCase()) {
            // in order of least to most hated
            case "bukkit": this.platformType = PlatformType.BUKKIT; break;
            case "bungeecord": this.platformType = PlatformType.BUNGEECORD; break;
            case "sponge": this.platformType = PlatformType.SPONGE; break;
            default: platform.severe("Could not determine platform. Tell Scarsz to fix this case he's a retard."); this.platformType = PlatformType.BUKKIT;
        }
    }

    @Getter private static final DecimalFormat decimalFormat = new DecimalFormat("#.#");
    @Getter private final Map<String, TextChannel> channels = new HashMap<>();
    @Getter private Config config;
    @Getter private final List<String> hookedPlugins = new ArrayList<>();
    @Getter private JDA jda = null;
    @Getter private final long startTime = System.currentTimeMillis();
    @Getter private AccountLinkManager accountLinkManager;
    @Getter private final Map<String, UUID> linkingCodes = new HashMap<>();
    @Getter private final UpdateManager updateManager = new UpdateManager();

    @Getter private TextChannel chatChannel; //TODO
    @Getter private TextChannel consoleChannel; //TODO
    @Getter private List<DiscordSRVListener> listeners = new ArrayList<>();
    @Getter private ChannelTopicUpdater channelTopicUpdater = new ChannelTopicUpdater();

    public void initialize() {
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
                            platform.warning("[JDA] " + o);
                            break;
                        case FATAL:
                            platform.severe("[JDA] " + o);
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

        // start channel topic updater if not already
        if (channelTopicUpdater.getState() == Thread.State.NEW) channelTopicUpdater.start();
        else {
            if (channelTopicUpdater != null) channelTopicUpdater.interrupt();
            channelTopicUpdater = new ChannelTopicUpdater();
            channelTopicUpdater.start();
        }

        // print the things the bot can see
        for (Guild guild : jda.getGuilds()) {
            platform.info("Found guild " + guild);
            for (Channel channel : guild.getTextChannels()) {
                platform.info("- " + channel);
            }
        }

        // check & get location info
        chatChannel = getTextChannelFromChannelName(config.getString("DiscordMainChatChannel"));
        consoleChannel = jda.getTextChannelById(config.getString("DiscordConsoleChannelId"));

        if (chatChannel == null) platform.warning("Specified chat channel from channels.json could not be found (is it's name set to \"" + config.getString("DiscordMainChatChannel") + "\"?)");
        if (consoleChannel == null) platform.warning("Specified console channel from config could not be found");
        if (chatChannel == null && consoleChannel == null) {
            platform.severe("Chat and console channels are both unavailable, plugin will not work properly");
            return;
        }

        // send startup message if enabled
        if (config.getBoolean("DiscordChatChannelServerStartupMessageEnabled")) DiscordUtil.sendMessage(chatChannel, config.getString("DiscordChatChannelServerStartupMessage"));


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
                    if (isCanceled != event.isCanceled() && config.getBoolean("Debug")) platform.debug("Event " + event.getClass().getSimpleName() + " canceled by " + listener.getName());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        // at this point, all listeners have been informed of the event. the for loop ends directly
        // after the last MONITOR priority, so code after this is technically also MONITOR priority

        // don't process event if a listener canceled it
        if (event.isCanceled()) return;

        // perform event
        event.perform();
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
