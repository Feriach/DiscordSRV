package github.scarsz.discordsrv.DiscordSRV.objects;

import github.scarsz.discordsrv.DiscordSRV.Manager;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @on 11/22/2016
 * @at 3:38 PM
 */
public class AccountLinkManager {

    private final File accountsFile;

    @Getter private final HashMap<String, String> codes = new HashMap<>();
    private final HashMap<String, String> linkedAccounts = new HashMap<>();

    public AccountLinkManager(File file) {
        accountsFile = file;
        load();
    }

//    @EventHandler
//    public void onPluginDisableEvent(PluginDisableEvent event) {
//        // if the plugin is being disabled, save the links to file
//        if (event.getPlugin().getName().equals("DiscordSRV")) save();
//    }

    public void load() {
        if (!accountsFile.exists()) return;
        linkedAccounts.clear();

        try {
            TreeMap<String, String> mapFromFile = Manager.getInstance().getYaml().loadAs(FileUtils.readFileToString(accountsFile, Charset.defaultCharset()), TreeMap.class);
            mapFromFile.forEach(linkedAccounts::put);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void save() {
        try {
            HashMap<String, String> linkedAccountsStringMap = new HashMap<>();
            linkedAccounts.forEach(linkedAccountsStringMap::put);
            FileUtils.writeStringToFile(accountsFile, Manager.getInstance().getYaml().dump(linkedAccountsStringMap), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDiscordIdOfGameId(String id) {
        return linkedAccounts.containsKey(id) ? linkedAccounts.get(id) : null;
    }
    public String getGameIdOfDiscordId(String discordId) {
        String foundId = null;
        for (Map.Entry<String, String> entry : linkedAccounts.entrySet())
            if (entry.getValue().equals(discordId)) foundId = entry.getKey();
        return foundId;
    }

    public void link(String gameId, String discordId) {
        if (linkedAccounts.containsKey(gameId)) linkedAccounts.remove(gameId);
        linkedAccounts.put(gameId, discordId);
        save();

        String minecraftDiscordAccountLinkedConsoleCommand = Manager.getInstance().getConfig().getString("MinecraftDiscordAccountLinkedConsoleCommand");
        if (!minecraftDiscordAccountLinkedConsoleCommand.equals("")) {
            Manager.getInstance().getPlatform().runCommand(minecraftDiscordAccountLinkedConsoleCommand
                    .replace("%minecraftplayername%", Manager.getInstance().getPlatform().transformGameIdToPlayerName(gameId))
                    .replace("%minecraftuuid%", gameId)
                    .replace("%discordid%", discordId)
                    .replace("%discordname%", Manager.getInstance().getMainChatChannel().getGuild().getMemberById(discordId).getEffectiveName())
            );
        }
    }

    public void unlinkGameId(String gameId) {
        linkedAccounts.remove(gameId);
    }
    public void unlinkDiscordId(String discordId) {
        linkedAccounts.entrySet().stream().filter(entry -> entry.getValue().equals(discordId)).forEach(entry -> linkedAccounts.remove(entry.getKey()));
    }

}
