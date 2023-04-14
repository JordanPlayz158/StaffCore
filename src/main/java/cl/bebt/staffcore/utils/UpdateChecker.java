package cl.bebt.staffcore.utils;

import cl.bebt.staffcore.StaffCorePlugin;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

import java.io.FileNotFoundException;
import java.util.function.Consumer;

public class UpdateChecker {
    private static final Gson GSON = new Gson();
    private static String USER_AGENT  = "Staff-Core";
    private static final String REQUEST_URL = "https://api.spiget.org/v2/resources/104420/versions/latest";

    private final StaffCorePlugin plugin;

    public UpdateChecker(StaffCorePlugin plugin) {
        this.plugin = plugin;
        USER_AGENT += " " + plugin.getDescription().getVersion();
    }

    public void getLatestVersion(Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String version;
            try {
                version = GSON.fromJson(Http.getURLContent(REQUEST_URL, USER_AGENT), JsonObject.class).get("name").getAsString();
            } catch (FileNotFoundException e) {
                // Assume the plugin is up to date if the URL 404s but inform the user of the inability/error when checking for plugin update
                plugin.getLogger().info("Unable to check the plugin's latest version due to 404, assuming up to date");
                plugin.getLogger().fine(e.getMessage());
                version = plugin.getDescription().getVersion();
            }

            plugin.latestVersion = version;
            consumer.accept(version);
        });
    }
}
