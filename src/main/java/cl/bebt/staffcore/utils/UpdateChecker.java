package cl.bebt.staffcore.utils;

import cl.bebt.staffcore.StaffCorePlugin;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class UpdateChecker {
    private static final Gson GSON = new Gson();
    private static String USER_AGENT  = "Staff-Core";
    private static final String REQUEST_URL = "https://api.spiget.org/v2/resources/82324/versions/latest";

    private final StaffCorePlugin plugin;

    public UpdateChecker(StaffCorePlugin plugin) {
        this.plugin = plugin;
        USER_AGENT += " " + plugin.getDescription().getVersion();
    }

    public void getLatestVersion(Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL(REQUEST_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.addRequestProperty("User-Agent", USER_AGENT);// Set User-Agent

                // If you're not sure if the request will be successful,
                // you need to check the response code and use #getErrorStream if it returned an error code
                InputStream inputStream = connection.getInputStream();
                String responseBody = new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));

                String version = GSON.fromJson(responseBody, JsonObject.class).get("name").getAsString();

                plugin.latestVersion = version;
                consumer.accept(version);
            } catch (IOException e) {
                Utils.tell(plugin.getServer().getConsoleSender(), "&cAn unknown error occurred while getting the latest version!");
                e.printStackTrace();
            }
        });
    }
}
