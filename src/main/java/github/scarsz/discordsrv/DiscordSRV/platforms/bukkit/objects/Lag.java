package github.scarsz.discordsrv.DiscordSRV.platforms.bukkit.objects;

import github.scarsz.discordsrv.DiscordSRV.DiscordSRV;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @on 11/22/2016
 * @at 2:58 AM
 */
public class Lag implements Runnable {

    private static int tick_count = 0;
    private static long[] ticks = new long[600];

    public static String getTPSString() {
        return DiscordSRV.getDecimalFormat().format(getTps());
    }

    private static double getTps() {
        double reportedTps = getTps(100);
        return reportedTps >= 19.5 ? 20 : reportedTps;
    }

    private static double getTps(int ticks) {
        if (tick_count < ticks) return 20.0D;
        int target = (tick_count - 1 - ticks) % Lag.ticks.length;
        long elapsed = System.currentTimeMillis() - Lag.ticks[target];
        return ticks / (elapsed / 1000.0D);
    }

    public void run() {
        ticks[(tick_count % ticks.length)] = System.currentTimeMillis();
        tick_count += 1;
    }

}