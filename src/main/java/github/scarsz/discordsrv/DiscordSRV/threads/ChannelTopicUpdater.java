package github.scarsz.discordsrv.DiscordSRV.threads;

import github.scarsz.discordsrv.DiscordSRV.Manager;
import github.scarsz.discordsrv.DiscordSRV.util.DiscordUtil;
import github.scarsz.discordsrv.DiscordSRV.util.MemUtil;
import net.dv8tion.jda.core.Permission;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @on 11/22/2016
 * @at 1:59 AM
 */
public class ChannelTopicUpdater extends Thread {

    public ChannelTopicUpdater() {
        setName("DiscordSRV - Channel Topic Updater");
    }

    public void run() {
        int rate = Manager.getInstance().getConfig().getInt("ChannelTopicUpdaterRateInSeconds") * 1000;

        while (!isInterrupted())
        {
            try {
                String chatTopic = applyFormatters(Manager.getInstance().getConfig().getString("ChannelTopicUpdaterChatChannelTopicFormat"));
                String consoleTopic = applyFormatters(Manager.getInstance().getConfig().getString("ChannelTopicUpdaterConsoleChannelTopicFormat"));

                if ((Manager.getInstance().getChatChannel() == null && Manager.getInstance().getConsoleChannel() == null) || (chatTopic.isEmpty() && consoleTopic.isEmpty())) interrupt();
                if (Manager.getInstance().getJda() == null || Manager.getInstance().getJda().getSelfUser() == null) continue;

                if (!chatTopic.isEmpty() && Manager.getInstance().getChatChannel() != null && !DiscordUtil.checkPermission(Manager.getInstance().getChatChannel(), Permission.MANAGE_CHANNEL))
                    Manager.getInstance().getPlatform().warning("Unable to update chat channel; no permission to manage channel");
                if (!consoleTopic.isEmpty() && Manager.getInstance().getConsoleChannel() != null && !DiscordUtil.checkPermission(Manager.getInstance().getConsoleChannel(), Permission.MANAGE_CHANNEL))
                    Manager.getInstance().getPlatform().warning("Unable to update console channel; no permission to manage channel");

                if (!chatTopic.isEmpty() && Manager.getInstance().getChatChannel() != null && DiscordUtil.checkPermission(Manager.getInstance().getChatChannel(), Permission.MANAGE_CHANNEL))
                    DiscordUtil.setTextChannelTopic(Manager.getInstance().getChatChannel(), chatTopic);
                if (!consoleTopic.isEmpty() && Manager.getInstance().getConsoleChannel() != null && DiscordUtil.checkPermission(Manager.getInstance().getChatChannel(), Permission.MANAGE_CHANNEL))
                    DiscordUtil.setTextChannelTopic(Manager.getInstance().getConsoleChannel(), consoleTopic);
            } catch (NullPointerException ignored) {}

            try { Thread.sleep(rate); } catch (InterruptedException ignored) {}
        }
    }

    @SuppressWarnings({"SpellCheckingInspection", "ConstantConditions"})
    private String applyFormatters(String input) {
        final Map<String, String> mem = MemUtil.get();

        input = input
                .replace("%playercount%", Integer.toString(Manager.getInstance().getPlatform().queryOnlinePlayers().size()))
                .replace("%playermax%", Integer.toString(Manager.getInstance().getPlatform().queryMaxPlayers()))
                .replace("%date%", new Date().toString())
                .replace("%totalplayers%", Integer.toString(Manager.getInstance().getPlatform().queryTotalPlayers()))
                .replace("%uptimemins%", Long.toString(TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - Manager.getInstance().getStartTime())))
                .replace("%uptimehours%", Long.toString(TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - Manager.getInstance().getStartTime())))
                .replace("%motd%", DiscordUtil.stripColor(Manager.getInstance().getPlatform().queryMotd()))
                .replace("%serverversion%", Manager.getInstance().getPlatform().queryServerVersion())
                .replace("%freememory%", mem.get("freeMB"))
                .replace("%usedmemory%", mem.get("usedMB"))
                .replace("%totalmemory%", mem.get("totalMB"))
                .replace("%maxmemory%", mem.get("maxMB"))
                .replace("%freememorygb%", mem.get("freeGB"))
                .replace("%usedmemorygb%", mem.get("usedGB"))
                .replace("%totalmemorygb%", mem.get("totalGB"))
                .replace("%maxmemorygb%", mem.get("maxGB"))
                .replace("%tps%", Manager.getInstance().getPlatform().queryTps())
        ;

        return input;
    }
}
