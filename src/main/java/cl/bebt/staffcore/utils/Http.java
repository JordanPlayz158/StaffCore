package cl.bebt.staffcore.utils;


import cl.bebt.staffcore.StaffCorePlugin;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.stream.Collectors;

public class Http {

    private static StaffCorePlugin plugin;

    public Http(StaffCorePlugin plugin) {
        Http.plugin = plugin;
    }

    public static boolean getBoolean(String urlParaVisitar, String bool) {
        boolean isRegistered = false;
        try {
            StringBuilder resultado = new StringBuilder();
            URL url = new URL(urlParaVisitar);
            HttpURLConnection connexion = (HttpURLConnection) url.openConnection();
            connexion.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(connexion.getInputStream()));
            String linea;
            while ((linea = rd.readLine()) != null) {
                resultado.append(linea);
            }
            rd.close();
            JSONObject array = new JSONObject(resultado.toString());
            isRegistered = array.getBoolean(bool);
        } catch (IOException error) {
            error.printStackTrace();
            Utils.tell(Bukkit.getConsoleSender(), "&cCould not get a connection with the server");
        }
        return isRegistered;
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

    public static void registerServer(String urlParaVisitar, String uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                StringBuilder resultado = new StringBuilder();
                URL url = new URL(urlParaVisitar);
                HttpURLConnection connexion = (HttpURLConnection) url.openConnection();
                connexion.setRequestMethod("GET");
                BufferedReader rd = new BufferedReader(new InputStreamReader(connexion.getInputStream()));
                String linea;
                while ((linea = rd.readLine()) != null) {
                    resultado.append(linea);
                }
                rd.close();
                JSONObject array = new JSONObject(resultado.toString());


                array.get("type");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void exportNewServerData(String urlParaVisitar) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                StringBuilder resultado = new StringBuilder();
                URL url = new URL(urlParaVisitar);
                HttpURLConnection connexion = (HttpURLConnection) url.openConnection();
                connexion.setRequestMethod("GET");
                BufferedReader rd = new BufferedReader(new InputStreamReader(connexion.getInputStream()));
                String linea;
                while ((linea = rd.readLine()) != null) {
                    resultado.append(linea);
                }
                rd.close();
                JSONObject array = new JSONObject(resultado.toString());

                array.get("type");
            } catch (IOException ignored) {}
        });
    }

}
