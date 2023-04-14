package cl.bebt.staffcore.utils;


import cl.bebt.staffcore.StaffCorePlugin;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Http {

    private static StaffCorePlugin plugin;
    private static Logger logger;

    public Http(StaffCorePlugin plugin) {
        Http.plugin = plugin;
        logger = Http.plugin.getLogger();
    }

    public static String getHead(String p) {
        try {
            String profile = getURLContent("https://api.mojang.com/users/profiles/minecraft/" + p);
            final Gson gson = new Gson();
            JsonObject profileJson = gson.fromJson(profile, JsonObject.class);

            String uid = profileJson.get("id").getAsString();

            String signature = getURLContent("https://sessionserver.mojang.com/session/minecraft/profile/" + uid);
            JsonObject signatureJson = gson.fromJson(signature, JsonObject.class);

            return signatureJson.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();
        } catch (FileNotFoundException e) {
            logger.fine("Player head not found for '" + p + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getURLContent(String urlStr, @Nullable String userAgent) throws FileNotFoundException {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (userAgent != null) {
                connection.addRequestProperty("User-Agent", userAgent);// Set User-Agent
            }

            // If you're not sure if the request will be successful,
            // you need to check the response code and use #getErrorStream if it returned an error code
            InputStream inputStream = connection.getInputStream();
            return new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getURLContent(String urlStr) throws FileNotFoundException {
        return getURLContent(urlStr, null);
    }

}
