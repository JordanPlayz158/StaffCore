package cl.bebt.staffcore.utils;


import cl.bebt.staffcore.StaffCorePlugin;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

    public static String getHead(String urlParaVisitar, String p) {
        String head = "";
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
            if (array.getString("type").equals("success")) {
                head = array.getString("value");
                StaffCorePlugin.playerSkins.put(p, head);
            }
        } catch (IOException error) {
            error.printStackTrace();
            Utils.tell(Bukkit.getConsoleSender(), "&cCould not get a connection with the server");
        }
        return head;
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
