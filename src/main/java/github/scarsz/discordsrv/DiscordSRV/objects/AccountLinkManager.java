package github.scarsz.discordsrv.DiscordSRV.objects;

import github.scarsz.discordsrv.DiscordSRV.Manager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @on 11/22/2016
 * @at 3:38 PM
 */
public class AccountLinkManager {

    private final File accountsFile;
    private final HashMap<UUID, String> linkedAccounts = new HashMap<>();

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
            mapFromFile.forEach((uuid, s) -> linkedAccounts.put(UUID.fromString(uuid), s));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void save() {
        try {
            HashMap<String, String> linkedAccountsStringMap = new HashMap<>();
            linkedAccounts.forEach((uuid, s) -> linkedAccountsStringMap.put(String.valueOf(uuid), s));
            FileUtils.writeStringToFile(accountsFile, Manager.getInstance().getYaml().dump(linkedAccountsStringMap), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDiscordIdOfUuid(UUID uuid) {
        return linkedAccounts.containsKey(uuid) ? linkedAccounts.get(uuid) : null;
    }
    public UUID getUuidOfDiscordId(String discordId) {
        UUID foundUuid = null;
        for (Map.Entry<UUID, String> entry : linkedAccounts.entrySet())
            if (entry.getValue().equals(discordId)) foundUuid = entry.getKey();
        return foundUuid;
    }

    public void link(UUID uuid, String discordId) {
        if (linkedAccounts.containsKey(uuid)) linkedAccounts.remove(uuid);
        linkedAccounts.put(uuid, discordId);
        save();

        String minecraftDiscordAccountLinkedConsoleCommand = Manager.getInstance().getConfig().getString("MinecraftDiscordAccountLinkedConsoleCommand");
        if (!minecraftDiscordAccountLinkedConsoleCommand.equals("")) {
            Manager.getInstance().getPlatform().runCommand(minecraftDiscordAccountLinkedConsoleCommand
                    .replace("%minecraftplayername%", Manager.getInstance().getPlatform().transformUuidToPlayerName(uuid.toString()))
                    .replace("%minecraftuuid%", uuid.toString())
                    .replace("%discordid%", discordId)
                    .replace("%discordname%", Manager.getInstance().getMainChatChannel().getGuild().getMemberById(discordId).getEffectiveName())
            );
        }
    }

    public void unlink(UUID uuid) {
        linkedAccounts.remove(uuid);
    }
    public void unlink(String discordId) {
        linkedAccounts.entrySet().stream().filter(entry -> entry.getValue().equals(discordId)).forEach(entry -> linkedAccounts.remove(entry.getKey()));
    }

}
