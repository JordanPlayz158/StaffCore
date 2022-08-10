package cl.bebt.staffcore.utils;


import cl.bebt.staffcore.StaffCorePlugin;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.stream.Collectors;

public class Http {

    private static StaffCorePlugin plugin;

    public Http(StaffCorePlugin plugin) {
        Http.plugin = plugin;
    }

    public static String getHead(String p) {
        try {
            String profile = getURLContent("https://api.mojang.com/users/profiles/minecraft/" + p);
            final Gson gson = new Gson();
            JsonObject profileJson = gson.fromJson(profile, JsonObject.class);

            String uid = profileJson.get("id").getAsString();

            String signature = getURLContent("https://sessionserver.mojang.com/session/minecraft/profile/" + uid);
            JsonObject signatureJson = gson.fromJson(signature, JsonObject.class);
            String value = signatureJson.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();
            String decoded = new String(Base64.getDecoder().decode(value));

            JsonObject decodedSignatureJson = gson.fromJson(decoded,JsonObject.class);
            String texturesJson = gson.toJson(decodedSignatureJson);

            byte[] skinByte = texturesJson.getBytes();

            return new String(Base64.getEncoder().encode(skinByte));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getURLContent(String urlStr, @Nullable String userAgent) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if(userAgent != null) {
                connection.addRequestProperty("User-Agent", userAgent);// Set User-Agent
            }

            // If you're not sure if the request will be successful,
            // you need to check the response code and use #getErrorStream if it returned an error code
            InputStream inputStream = connection.getInputStream();
            return new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getURLContent(String urlStr) {
        return getURLContent(urlStr, null);
    }

}
