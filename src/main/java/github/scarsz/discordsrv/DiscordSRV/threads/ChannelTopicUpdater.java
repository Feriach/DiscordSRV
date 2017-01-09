package github.scarsz.discordsrv.DiscordSRV.threads;

import github.scarsz.discordsrv.DiscordSRV.DiscordSRV;
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
        int rate = DiscordSRV.getInstance().getConfig().getInt("ChannelTopicUpdaterRateInSeconds") * 1000;

        // make sure rate isn't less than every second because of rate limitations
        // even then, a channel topic update /every second/ is still pushing it
        if (rate < 1000) rate = 1000;

        while (!isInterrupted())
        {
            try {
                String chatTopic = applyFormatters(DiscordSRV.getInstance().getConfig().getString("ChannelTopicUpdaterChatChannelTopicFormat"));
                String consoleTopic = applyFormatters(DiscordSRV.getInstance().getConfig().getString("ChannelTopicUpdaterConsoleChannelTopicFormat"));

                if ((DiscordSRV.getInstance().getMainChatChannel() == null && DiscordSRV.getInstance().getConsoleChannel() == null) || (chatTopic.isEmpty() && consoleTopic.isEmpty())) interrupt();
                if (DiscordSRV.getInstance().getJda() == null || DiscordSRV.getInstance().getJda().getSelfUser() == null) continue;

                if (!chatTopic.isEmpty() && DiscordSRV.getInstance().getMainChatChannel() != null && !DiscordUtil.checkPermission(DiscordSRV.getInstance().getMainChatChannel(), Permission.MANAGE_CHANNEL))
                    DiscordSRV.getInstance().getPlatform().warning("Unable to update chat channel; no permission to manage channel");
                if (!consoleTopic.isEmpty() && DiscordSRV.getInstance().getConsoleChannel() != null && !DiscordUtil.checkPermission(DiscordSRV.getInstance().getConsoleChannel(), Permission.MANAGE_CHANNEL))
                    DiscordSRV.getInstance().getPlatform().warning("Unable to update console channel; no permission to manage channel");

                if (!chatTopic.isEmpty() && DiscordSRV.getInstance().getMainChatChannel() != null && DiscordUtil.checkPermission(DiscordSRV.getInstance().getMainChatChannel(), Permission.MANAGE_CHANNEL))
                    DiscordUtil.setTextChannelTopic(DiscordSRV.getInstance().getMainChatChannel(), chatTopic);
                if (!consoleTopic.isEmpty() && DiscordSRV.getInstance().getConsoleChannel() != null && DiscordUtil.checkPermission(DiscordSRV.getInstance().getMainChatChannel(), Permission.MANAGE_CHANNEL))
                    DiscordUtil.setTextChannelTopic(DiscordSRV.getInstance().getConsoleChannel(), consoleTopic);
            } catch (NullPointerException ignored) {}

            try { Thread.sleep(rate); } catch (InterruptedException ignored) {}
        }
    }

    @SuppressWarnings({"SpellCheckingInspection", "ConstantConditions"})
    private String applyFormatters(String input) {
        final Map<String, String> mem = MemUtil.get();

        input = input
                .replace("%playercount%", Integer.toString(DiscordSRV.getInstance().getPlatform().queryOnlinePlayers().size()))
                .replace("%playermax%", Integer.toString(DiscordSRV.getInstance().getPlatform().queryMaxPlayers()))
                .replace("%date%", new Date().toString())
                .replace("%totalplayers%", Integer.toString(DiscordSRV.getInstance().getPlatform().queryTotalPlayers()))
                .replace("%uptimemins%", Long.toString(TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - DiscordSRV.getInstance().getStartTime())))
                .replace("%uptimehours%", Long.toString(TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - DiscordSRV.getInstance().getStartTime())))
                .replace("%motd%", DiscordUtil.stripColor(DiscordSRV.getInstance().getPlatform().queryMotd()))
                .replace("%serverversion%", DiscordSRV.getInstance().getPlatform().queryServerVersion())
                .replace("%freememory%", mem.get("freeMB"))
                .replace("%usedmemory%", mem.get("usedMB"))
                .replace("%totalmemory%", mem.get("totalMB"))
                .replace("%maxmemory%", mem.get("maxMB"))
                .replace("%freememorygb%", mem.get("freeGB"))
                .replace("%usedmemorygb%", mem.get("usedGB"))
                .replace("%totalmemorygb%", mem.get("totalGB"))
                .replace("%maxmemorygb%", mem.get("maxGB"))
                .replace("%tps%", DiscordSRV.getInstance().getPlatform().queryTps())
        ;

        return input;
    }
}
