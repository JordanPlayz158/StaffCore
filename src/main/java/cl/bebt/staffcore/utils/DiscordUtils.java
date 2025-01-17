package cl.bebt.staffcore.utils;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

public class DiscordUtils {

    public static String AlertsChannel = Utils.getString("discord.type.alerts.webhook_url");

    protected static String AlertsChannelImage = Utils.getString("discord.type.alerts.custom_image_url");

    protected static String AlertsName = Utils.getString("discord.type.alerts.name");

    protected static String AlertsNameImage = Utils.getString("discord.type.alerts.name_image_url");

    protected static String AlertsNameUrl = Utils.getString("discord.type.alerts.name_web_url");

    protected static int AlertsColor = Utils.getInt("discord.type.alerts.color", null);


    protected static String DebugChannel = Utils.getString("discord.type.debug.webhook_url");

    protected static String DebugChannelImage = Utils.getString("discord.type.debug.custom_image_url");

    protected static String DebugName = Utils.getString("discord.type.debug.name");

    protected static String DebugNameImage = Utils.getString("discord.type.debug.name_image_url");

    protected static String DebugNameUrl = Utils.getString("discord.type.debug.name_web_url");

    protected static int DebugColor = Utils.getInt("discord.type.debug.color", null);


    public static void DiscordWebHooksAlerts(ArrayList<String> msg, String title) {
        WebhookClient webhookClient = WebhookClient.withUrl(AlertsChannel);
        WebhookEmbedBuilder webhookEmbedBuilder = new WebhookEmbedBuilder().setAuthor(new WebhookEmbed.EmbedAuthor(AlertsName, AlertsNameImage, AlertsNameUrl));
        webhookEmbedBuilder.setTitle(new WebhookEmbed.EmbedTitle(title, null)).setColor(AlertsColor);
        StringBuilder string = new StringBuilder();
        for (String s : msg) {
            string.append(s).append("\n");
        }
        webhookEmbedBuilder.setDescription(string.toString());
        //webhookEmbedBuilder.addField( new WebhookEmbed.EmbedField( true, "Text Formatting","[Link](https://birdie0.github.io/discord-webhooks-guide/other/discord_markdown.html)" ) );
        webhookEmbedBuilder.setTimestamp(Instant.ofEpochMilli(new Date().getTime()));
        webhookClient.send(webhookEmbedBuilder.build());
        webhookClient.close();
    }

    public static void DiscordWebHooksDebug(Player p, ArrayList<String> msg, String title) {
        WebhookClient webhookClient = WebhookClient.withUrl(DebugChannel);
        WebhookEmbedBuilder webhookEmbedBuilder = new WebhookEmbedBuilder().setAuthor(new WebhookEmbed.EmbedAuthor(DebugName, "https://crafatar.com/avatars/" + p.getUniqueId(), DebugNameUrl));
        webhookEmbedBuilder.setTitle(new WebhookEmbed.EmbedTitle(title, null)).setColor(DebugColor);
        StringBuilder string = new StringBuilder();
        for (String s : msg) {
            string.append(s).append("\n");
        }
        webhookEmbedBuilder.setDescription(string.toString());
        webhookEmbedBuilder.setTimestamp(Instant.ofEpochMilli(new Date().getTime()));
        webhookClient.send(webhookEmbedBuilder.build());
        webhookClient.close();
    }

    protected static String date() {
        Date now = new Date();
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(now);
    }
}
