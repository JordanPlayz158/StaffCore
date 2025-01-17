package cl.bebt.staffcore.sql.queries;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.sql.Mysql;
import cl.bebt.staffcore.sql.SQLGetter;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.Bukkit;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BansQuery {

    /**
     * This will create a Ban into the Bans Table.
     **/
    public static void createBan(String banned, String baner, String Reason, String Date, String ExpDate, String IP, String IP_Banned, String Status) {
        int intento = 0;
        while (intento < 4) {
            try {
                PreparedStatement statement = Mysql.getConnection().prepareStatement("INSERT INTO sc_bans (Name, Baner, Reason, Date, ExpDate, IP, IP_Banned, Status) VALUES (?,?,?,?,?,?,?,?)");
                statement.setString(1, banned);
                statement.setString(2, baner);
                statement.setString(3, Reason);
                statement.setString(4, Date);
                statement.setString(5, ExpDate);
                statement.setString(6, IP);
                statement.setString(7, IP_Banned);
                statement.setString(8, Status);
                statement.executeUpdate();
                break;
            } catch (SQLException throwable) {
                StaffCorePlugin.plugin.getServer().getConsoleSender().sendMessage(Utils.chat("&c[&5Staff Core&c] There has been an error with the mysql"));
                StaffCorePlugin.plugin.getServer().getConsoleSender().sendMessage(Utils.chat("&c[&5Staff Core&c] Creating the sc_bans table into the database"));
                SQLGetter.createBansTable();
                throwable.printStackTrace();
                intento++;
            }
        }
    }

    public static JSONObject getBanInfo(int id) {
        JSONObject json = new JSONObject();
        try {
            PreparedStatement statement = Mysql.getConnection().prepareStatement("SELECT * FROM sc_bans WHERE BanId LIKE " + id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                json.put("error", false);
                json.put("BanId", rs.getString("BanId"));
                json.put("Name", rs.getString("Name"));
                json.put("Banner", rs.getString("Baner"));
                json.put("Reason", rs.getString("Reason"));
                json.put("Date", rs.getString("Date"));
                json.put("ExpDate", rs.getString("ExpDate"));
                json.put("IP", rs.getString("IP"));
                json.put("IP_Banned", rs.getString("IP_Banned"));
                json.put("Status", rs.getString("Status"));
            }
        } catch (SQLException error) {
            error.printStackTrace();
            json.put("error", true);
        }
        return json;
    }

    public static JSONObject getOpenBanInfo() {
        JSONObject json = new JSONObject();
        try {
            PreparedStatement statement = Mysql.getConnection().prepareStatement("SELECT * FROM sc_bans WHERE Status LIKE 'open' ORDER BY BanId");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int BanId = rs.getInt("BanId");
                String Name = rs.getString("Name");
                String Baner = rs.getString("Baner");
                String Reason = rs.getString("Reason");
                String Date = rs.getString("Date");
                String ExpDate = rs.getString("ExpDate");
                String IP = rs.getString("IP");
                String IP_Banned = rs.getString("IP_Banned");
                String Status = rs.getString("Status");

                JSONArray senderArray = new JSONArray();
                JSONObject senderDetail = new JSONObject();
                senderDetail.put("Name", Name);
                senderDetail.put("Baner", Baner);
                senderDetail.put("Reason", Reason);
                senderDetail.put("Date", Date);
                senderDetail.put("ExpDate", ExpDate);
                senderDetail.put("IP", IP);
                senderDetail.put("IP_Banned", IP_Banned);
                Date now = new Date();
                Date exp_date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(rs.getString("ExpDate"));
                if (now.after(exp_date)) {
                    senderDetail.put("Status", "closed");
                    Bukkit.getScheduler().runTaskAsynchronously(StaffCorePlugin.plugin, () -> closeBan(BanId));
                } else {
                    senderDetail.put("Status", Status);
                }

                senderArray.put(senderDetail);
                json.put(String.valueOf(BanId), senderArray);
            }
        } catch (SQLException | ParseException error) {
            error.printStackTrace();
        }
        return json;
    }

    public static JSONObject getClosedBanInfo() {
        JSONObject json = new JSONObject();
        try {
            PreparedStatement statement = Mysql.getConnection().prepareStatement("SELECT * FROM sc_bans WHERE Status LIKE 'closed' ORDER BY BanId");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int BanId = rs.getInt("BanId");
                String Name = rs.getString("Name");
                String Baner = rs.getString("Baner");
                String Reason = rs.getString("Reason");
                String Date = rs.getString("Date");
                String ExpDate = rs.getString("ExpDate");
                String IP = rs.getString("IP");
                String IP_Banned = rs.getString("IP_Banned");
                String Status = rs.getString("Status");

                JSONArray senderArray = new JSONArray();
                JSONObject senderDetail = new JSONObject();
                senderDetail.put("Name", Name);
                senderDetail.put("Baner", Baner);
                senderDetail.put("Reason", Reason);
                senderDetail.put("Date", Date);
                senderDetail.put("ExpDate", ExpDate);
                senderDetail.put("IP", IP);
                senderDetail.put("IP_Banned", IP_Banned);
                senderDetail.put("Status", Status);
                senderArray.put(senderDetail);
                json.put(String.valueOf(BanId), senderArray);
            }
        } catch (SQLException error) {
            error.printStackTrace();
        }
        return json;
    }

    public static JSONObject getPlayerBansInfo(String banned) {
        JSONObject json = new JSONObject();
        try {
            PreparedStatement statement = Mysql.getConnection().prepareStatement("SELECT * FROM sc_bans WHERE Name LIKE ? order by BanId");
            statement.setString(1, banned);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int BanId = rs.getInt("BanId");
                String Name = rs.getString("Name");
                String Baner = rs.getString("Baner");
                String Reason = rs.getString("Reason");
                String Date = rs.getString("Date");
                String ExpDate = rs.getString("ExpDate");
                String IP = rs.getString("IP");
                String IP_Banned = rs.getString("IP_Banned");
                String Status = rs.getString("Status");

                JSONArray senderArray = new JSONArray();
                JSONObject senderDetail = new JSONObject();
                senderDetail.put("Name", Name);
                senderDetail.put("Baner", Baner);
                senderDetail.put("Reason", Reason);
                senderDetail.put("Date", Date);
                senderDetail.put("ExpDate", ExpDate);
                senderDetail.put("IP", IP);
                senderDetail.put("IP_Banned", IP_Banned);
                senderDetail.put("Status", Status);
                java.util.Date now = new Date();
                Date exp_date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(rs.getString("ExpDate"));
                if (now.after(exp_date)) {
                    senderDetail.put("Status", "closed");
                    Bukkit.getScheduler().runTaskAsynchronously(StaffCorePlugin.plugin, () -> closeBan(BanId));
                } else {
                    senderDetail.put("Status", Status);
                }

                senderArray.put(senderDetail);
                json.put(String.valueOf(BanId), senderArray);
            }
        } catch (SQLException | ParseException error) {
            error.printStackTrace();
        }
        return json;
    }

    public static List<String> getBannedPlayers() {
        List<String> players = new ArrayList<>();
        try {
            PreparedStatement statement = Mysql.getConnection().prepareStatement("SELECT Name FROM sc_bans");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String aDBName = rs.getString(1);
                if (!players.contains(aDBName)) {
                    players.add(aDBName);
                }
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return players;
    }

    public static ArrayList<Integer> getPlayerBans(String banned) {
        ArrayList<Integer> playerWarns = new ArrayList<>();
        try {
            PreparedStatement statement = Mysql.getConnection().prepareStatement("SELECT BanId FROM sc_bans WHERE Name = ? order by BanId");
            statement.setString(1, banned);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                playerWarns.add(rs.getInt("BanId"));
            }
        } catch (SQLException error) {
            error.printStackTrace();
        }
        return playerWarns;
    }

    public static ArrayList<Integer> getOpenBans() {
        ArrayList<Integer> openBans = new ArrayList<>();
        try {
            PreparedStatement statement = Mysql.getConnection().prepareStatement("SELECT BanId FROM sc_bans WHERE Status LIKE 'open' group by BanId order by BanId");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                openBans.add(rs.getInt("BanId"));
            }
        } catch (SQLException error) {
            error.printStackTrace();
        }
        return openBans;
    }

    public static ArrayList<Integer> getClosedBans() {
        ArrayList<Integer> closedBans = new ArrayList<>();
        try {
            PreparedStatement statement = Mysql.getConnection().prepareStatement("SELECT BanId FROM sc_bans WHERE Status LIKE 'closed' group by BanId order by BanId");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                closedBans.add(rs.getInt("BanId"));
            }
        } catch (SQLException error) {
            error.printStackTrace();
        }
        return closedBans;
    }

    public static Integer getCurrentBans() {
        int currentBans = 0;
        try {
            PreparedStatement statement = Mysql.getConnection().prepareStatement("SELECT count(*) AS CurrentBans FROM sc_bans");
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                currentBans = rs.getInt("CurrentBans");
            }
        } catch (SQLException error) {
            error.printStackTrace();
        }
        return currentBans;
    }

    public static ArrayList<Integer> getBanIds(String p) {
        ArrayList<Integer> BanIDs = new ArrayList<>();
        try {
            PreparedStatement statement = Mysql.getConnection().prepareStatement("SELECT * FROM sc_bans");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String players = rs.getString(2);
                if (players.equalsIgnoreCase(p)) {
                    BanIDs.add(rs.getInt(1));
                }
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return BanIDs;
    }

    public static void closeBan(int id) {
        int intento = 0;
        while (intento < 4) {
            try {
                PreparedStatement statement = Mysql.getConnection().prepareStatement("UPDATE sc_bans SET Status = 'closed' WHERE BanId=?");
                statement.setInt(1, id);
                statement.executeUpdate();
                break;
            } catch (SQLException ignored) {
                intento++;
            }
        }
    }

    public static void deleteBan(int id) {
        int intento = 0;
        while (intento < 4) {
            try {
                PreparedStatement statement = Mysql.getConnection().prepareStatement("DELETE FROM sc_bans WHERE sc_bans.BanId = ?");
                statement.setInt(1, id);
                statement.executeUpdate();
                break;
            } catch (SQLException ignored) {
                intento++;
            }
        }
    }

    public static Integer wipePlayerBans(String name) {
        int bans = 0;
        try {
            PreparedStatement statement1 = Mysql.getConnection().prepareStatement("SELECT * FROM sc_bans WHERE Name LIKE ?");
            statement1.setString(1, name);
            ResultSet rs1 = statement1.executeQuery();
            while (rs1.next()) {
                bans++;
            }
            PreparedStatement statement2 = Mysql.getConnection().prepareStatement("DELETE FROM sc_bans WHERE Name LIKE ?");
            statement2.setString(1, name);
            statement2.executeUpdate();

        } catch (SQLException ignored) {

        }
        return bans;
    }

    public static Boolean isStillBanned(int id) {
        try {
            PreparedStatement statement = Mysql.getConnection().prepareStatement("SELECT ExpDate, Status FROM sc_bans WHERE BanId=?");
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Date now = new Date();
                Date exp_date = (new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")).parse(rs.getString("ExpDate"));
                if (now.after(exp_date)) {
                    closeBan(id);
                    return false;
                }
                return rs.getString("Status").equalsIgnoreCase("closed");
            } else {
                return false;
            }
        } catch (SQLException | ParseException error) {
            error.printStackTrace();
            return false;
        }
    }

    public static Boolean isStillBanned(String name, String IP) {
        int bans = 0;
        try {
            PreparedStatement statement = Mysql.getConnection().prepareStatement("SELECT BanId, Name, ExpDate, Status, IP, IP_Banned FROM sc_bans WHERE Name=? OR IP =?");
            statement.setString(1, name);
            statement.setString(2, IP);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                if (rs.getString("Status").equalsIgnoreCase("open")) {
                    if (rs.getString("IP_Banned").equalsIgnoreCase("true")) {
                        if (rs.getString("IP").equalsIgnoreCase(IP)) {
                            bans++;
                        }
                    } else {
                        if (rs.getString("Name").equalsIgnoreCase(name)) {
                            bans++;
                        }
                    }
                }
            }
        } catch (SQLException error) {
            error.printStackTrace();
            return false;
        }
        return bans != 0;
    }

    public static int getBannedId(String name, String IP) {
        int banId = 0;
        try {
            PreparedStatement statement = Mysql.getConnection().prepareStatement("SELECT BanId, Name, ExpDate, Status, IP, IP_Banned FROM sc_bans WHERE Name=? OR IP =? ORDER BY BanId");
            statement.setString(1, name);
            statement.setString(2, IP);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                if (rs.getString("Status").equalsIgnoreCase("open")) {
                    if (rs.getString("IP_Banned").equalsIgnoreCase("true")) {
                        if (rs.getString("IP").equalsIgnoreCase(IP)) {
                            banId = rs.getInt("BanId");
                        }
                    } else {
                        if (rs.getString("Name").equalsIgnoreCase(name)) {
                            banId = rs.getInt("BanId");
                        }
                    }
                } else {
                    Date now = new Date();
                    Date exp_date = (new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")).parse(rs.getString("ExpDate"));
                    if (!now.after(exp_date)) {
                        if (rs.getString("Name").equalsIgnoreCase(name) || rs.getString("IP").equalsIgnoreCase(IP)) {
                            banId = rs.getInt("BanId");
                        }
                    } else {
                        banId = rs.getInt("BanId");
                    }
                }


            }
        } catch (SQLException | ParseException error) {
            error.printStackTrace();
        }
        return banId;
    }

}
