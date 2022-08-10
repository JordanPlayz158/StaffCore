package cl.bebt.staffcore.sql;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.sql.queries.AltsQuery;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLGetter {

    private static StaffCorePlugin plugin;

    public SQLGetter(StaffCorePlugin plugin) {
        SQLGetter.plugin = plugin;
    }

    public static void createTable(String tableName) {
        try (PreparedStatement ps = Mysql.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS sc_" + tableName + " (Name varchar(100),Enabled varchar(100),PRIMARY KEY (Name))")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void createAltsTable() {
        try (PreparedStatement ps = Mysql.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS sc_alts (UUID VARCHAR(36) PRIMARY KEY,Name varchar(100) ,Ips varchar(200), Skin varchar(1000) )")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This will create a ReportReport into the Mysql database
     **/
    public static void createReportTable() {
        try (PreparedStatement ps = Mysql.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS sc_reports (ReportId int NOT NULL AUTO_INCREMENT,Name varchar(20) NOT NULL,Reporter varchar(20) NOT NULL,Reason varchar(100), Date varchar(20), Status varchar(10),PRIMARY KEY (ReportId))")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getServer().getConsoleSender().sendMessage(Utils.chat("&c[&5Staff Core&c] There has been an error with the mysql"));
            plugin.getServer().getConsoleSender().sendMessage(Utils.chat("&c[&5Staff Core&c] Not able to connect to the Database"));
            e.printStackTrace();
        }
    }

    /**
     * This will create the BanTable into the Mysql database.
     **/
    public static void createBansTable() {
        try (PreparedStatement ps = Mysql.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS sc_bans (BanId int NOT NULL AUTO_INCREMENT,Name varchar(20) NOT NULL,Baner varchar(20) NOT NULL,Reason varchar(100)NOT NULL,Date varchar(20)NOT NULL, ExpDate varchar(100)NOT NULL, IP varchar(100)NOT NULL, IP_Banned varchar(10)NOT NULL, Status varchar(10)NOT NULL,PRIMARY KEY (BanId));")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getServer().getConsoleSender().sendMessage(Utils.chat("&c[&5Staff Core&c] There has been an error with the mysql"));
            plugin.getServer().getConsoleSender().sendMessage(Utils.chat("&c[&5Staff Core&c] Not able to connect to the Database"));
            e.printStackTrace();
        }
    }

    /**
     * This will create the BanTable into the Mysql database.
     **/
    public static void createWarnsTable() {
        try (PreparedStatement ps = Mysql.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS sc_warns (WarnId int NOT NULL AUTO_INCREMENT,Name varchar(20) NOT NULL,Warner varchar(20) NOT NULL,Reason varchar(100)NOT NULL,Date varchar(20)NOT NULL, ExpDate varchar(100)NOT NULL, Status varchar(10)NOT NULL,PRIMARY KEY (WarnId));")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getServer().getConsoleSender().sendMessage(Utils.chat("&c[&5Staff Core&c] There has been an error with the mysql"));
            plugin.getServer().getConsoleSender().sendMessage(Utils.chat("&c[&5Staff Core&c] Not able to connect to the Database"));
            e.printStackTrace();
        }
    }

    /**
     * This will create the BanTable into the Mysql database.
     **/
    public static void createStaffCoreSettingsTable() {
        try (PreparedStatement ps = Mysql.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS sc_settings ( ServerName VARCHAR(30) NOT NULL DEFAULT ?, Version VARCHAR(5) NOT NULL DEFAULT '" + plugin.getDescription().getVersion() + "', IsUpdated BOOLEAN NOT NULL DEFAULT true)")) {
            ps.setString(1, Utils.getServer());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getServer().getConsoleSender().sendMessage(Utils.chat("&c[&5Staff Core&c] There has been an error with the mysql"));
            plugin.getServer().getConsoleSender().sendMessage(Utils.chat("&c[&5Staff Core&c] Not able to connect to the Database"));
            e.printStackTrace();
        }
    }

    public static void createTables() {
        createTable("vanish");
        createTable("staff");
        createTable("frozen");
        createTable("staffchat");
        createAltsTable();
        createReportTable();
        createBansTable();
        createWarnsTable();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            createStaffCoreSettingsTable();
            if (!AltsQuery.checkAltsTable()) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    Utils.tellHover(p, "&cHEY STAFFCORE IS MAKING SOME UPDATES IN THE DB", "&aClick to see What's Going on", "https://www.spigotmc.org/resources/staff-core.104420/");
                }
                AltsQuery.MigrateAltsTable();
            }
        });

    }

}
