package github.scarsz.discordsrv.util;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import github.scarsz.discordsrv.DiscordSRV;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @on 2/7/2017
 * @at 4:16 PM
 */
public class HttpUtil {

    public static String requestHttp(String requestUrl) {
        try {
            return IOUtils.toString(new URL(requestUrl), Charset.forName("UTF-8"));
        } catch (IOException e) {
            DiscordSRV.error(LangUtil.InternalMessage.HTTP_FAILED_TO_FETCH_URL + " " + requestUrl + ": " + e.getMessage());
            return "";
        }
    }

    public static boolean executeChatWebhook(String username, String message) {
        try {
            while (1 < 2) { // polaris
                HttpResponse<String> response = Unirest.post(DiscordSRV.getPlugin().getConfig().getString("DiscordMainChatChannelWebhookUrl"))
                        .field("username", username)
                        .field("avatar_url", "https://crafatar.com/avatars/" + username + "?size=100")
                        .field("content", message)
                        .asString();
                if (response.getHeaders().containsKey("Retry-After")) {
                    Thread.sleep(Integer.parseInt(response.getHeaders().get("Retry-After").get(0)));
                    return executeChatWebhook(username, message);
                }
                return true;
            }
        } catch (InterruptedException | UnirestException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void downloadFile(String requestUrl, File destination) {
        try {
            FileUtils.copyURLToFile(new URL(requestUrl), destination);
        } catch (IOException e) {
            DiscordSRV.error(LangUtil.InternalMessage.HTTP_FAILED_TO_DOWNLOAD_URL + " " + requestUrl + ": " + e.getMessage());
        }
    }

}
