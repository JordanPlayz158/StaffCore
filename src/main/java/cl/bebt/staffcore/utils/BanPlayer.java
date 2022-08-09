package cl.bebt.staffcore.utils;

import cl.bebt.staffcore.MSGChanel.SendMsg;
import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.sql.Queries.AltsQuery;
import cl.bebt.staffcore.sql.Queries.BansQuery;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BanPlayer {

    public static void unBan(CommandSender p, int Id) {
        String player = "";
        if (p instanceof Player) {
            player = p.getName();
        } else {
            player = "CONSOLE";
        }
        String reason = null;
        String created = null;
        String exp = null;
        String baner = null;
        String banned = null;
        String status = "unbanned";
        if (utils.mysqlEnabled()) {
            JSONObject json = BansQuery.getBanInfo(Id);
            if (!json.getBoolean("error")) {
                reason = json.getString("Reason");
                created = json.getString("Date");
                exp = json.getString("ExpDate");
                baner = json.getString("Banner");
                banned = json.getString("Name");
                BansQuery.deleteBan(Id);
            }
        } else {
            StaffCorePlugin.plugin.bans.reloadConfig();
            reason = StaffCorePlugin.plugin.bans.getConfig().getString("bans." + Id + ".reason");
            created = StaffCorePlugin.plugin.bans.getConfig().getString("bans." + Id + ".date");
            exp = StaffCorePlugin.plugin.bans.getConfig().getString("bans." + Id + ".expdate");
            baner = StaffCorePlugin.plugin.bans.getConfig().getString("bans." + Id + ".banned_by");
            banned = StaffCorePlugin.plugin.bans.getConfig().getString("bans." + Id + ".name");
            StaffCorePlugin.plugin.bans.getConfig().set("bans." + Id, null);
            StaffCorePlugin.plugin.bans.getConfig().set("current", currentBans());
            StaffCorePlugin.plugin.bans.saveConfig();
        }
        SendMsg.sendBanChangeAlert(Id, p.getName(), baner, banned, reason, exp, created, status, utils.getString("bungeecord.server"));
        for (Player people : Bukkit.getOnlinePlayers()) {
            if (people.hasPermission("staffcore.staff") || utils.getBoolean("alerts.ban")) {
                utils.PlaySound(people, "un_ban");
                for (String key : utils.getStringList("ban.change", "alerts")) {
                    key = key.replace("%changed_by%", player);
                    key = key.replace("%baner%", baner);
                    key = key.replace("%banned%", banned);
                    key = key.replace("%id%", String.valueOf(Id));
                    key = key.replace("%reason%", reason);
                    key = key.replace("%create_date%", created);
                    key = key.replace("%exp_date%", exp);
                    key = key.replace("%ban_status%", "UNBANED");
                    utils.tell(people, key);
                }
            }
        }
    }

    public static int currentBans() {
        try {
            if (utils.mysqlEnabled()) {
                return BansQuery.getCurrentBans();
            } else {
                ConfigurationSection inventorySection = StaffCorePlugin.plugin.bans.getConfig().getConfigurationSection("bans");
                return inventorySection.getKeys(false).size();
            }
        } catch (NullPointerException ignored) {
            return 0;
        }

    }

    public static void BanPlayer(CommandSender p, String banned, String reason) {
        createBan((Player) p, banned, reason, 283605, "d", true);
    }


    public static void BanCooldown(CommandSender sender, String banned, String reason, long amount, String time) {
        if (time.equalsIgnoreCase("s") || time.equalsIgnoreCase("m") ||
                time.equalsIgnoreCase("h") || time.equalsIgnoreCase("d")) {
            createBan((Player) sender, banned, reason, amount, time, false);
        } else {
            utils.tell(sender, utils.getString("wrong_usage", "lg", "staff").replace("%command%", "ban " + banned));
        }
    }

    private static void createBan(Player p, String banned, String reason, long amount, String time, Boolean permanent) {
        int id = (StaffCorePlugin.plugin.bans.getConfig().getInt("count") + 1);
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        String IP = null;
        if (Bukkit.getPlayer(banned) instanceof Player) {
            Player banned_player = Bukkit.getPlayer(banned);
            IP = banned_player.getAddress().getAddress().toString();
            IP = IP.replace("/", "");
        } else {
            try {
                //TODO CREATE A SYSTEM TO OPTIMIZE THIS:
                if (utils.mysqlEnabled()) {
                    List<String> ips = utils.makeList(AltsQuery.getAlts(banned));
                    IP = ips.get(0);
                } else {
                    List<? extends String> ips = StaffCorePlugin.plugin.alts.getConfig().getStringList("alts." + banned);
                    IP = ips.subList(ips.size() - 1, ips.size()).toString();
                }
                IP = IP.replace("[", "");
                IP = IP.replace("]", "");
            } catch (NullPointerException | IndexOutOfBoundsException ignored) {
                utils.tell(p, utils.getString("never_seen", "lg", "staff").replace("%player%", banned));
            }
        }
        if (IP != null) {
            switch (time) {
                case "s":
                    cal.add(Calendar.SECOND, (int) amount);
                    break;
                case "m":
                    cal.add(Calendar.MINUTE, (int) amount);
                    break;
                case "h":
                    cal.add(Calendar.HOUR, (int) amount);
                    break;
                case "d":
                    cal.add(Calendar.DAY_OF_MONTH, (int) amount);
                    break;
            }
            Date ExpDate = cal.getTime();
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            if (utils.mysqlEnabled()) {
                if (p.getPersistentDataContainer().has(new NamespacedKey(StaffCorePlugin.plugin, "ban-ip"), PersistentDataType.STRING)) {
                    BansQuery.createBan(banned, p.getName(), reason, format.format(now), format.format(ExpDate), IP, "true", "open");
                } else {
                    BansQuery.createBan(banned, p.getName(), reason, format.format(now), format.format(ExpDate), IP, "false", "open");
                }
            }
            if (StaffCorePlugin.plugin.bans.getConfig().contains("count")) {
                StaffCorePlugin.plugin.bans.getConfig().set("count", id);
                StaffCorePlugin.plugin.bans.getConfig().set("bans." + id + ".name", banned);
                StaffCorePlugin.plugin.bans.getConfig().set("bans." + id + ".banned_by", p.getName());
                StaffCorePlugin.plugin.bans.getConfig().set("bans." + id + ".reason", reason);
                StaffCorePlugin.plugin.bans.getConfig().set("bans." + id + ".date", format.format(now));
                StaffCorePlugin.plugin.bans.getConfig().set("bans." + id + ".expdate", format.format(ExpDate));
                if (p.getPersistentDataContainer().has(new NamespacedKey(StaffCorePlugin.plugin, "ban-ip"), PersistentDataType.STRING)) {
                    StaffCorePlugin.plugin.bans.getConfig().set("bans." + id + ".IP-Banned", true);
                } else {
                    StaffCorePlugin.plugin.bans.getConfig().set("bans." + id + ".IP-Banned", false);
                }
                StaffCorePlugin.plugin.bans.getConfig().set("bans." + id + ".IP", IP);
                StaffCorePlugin.plugin.bans.getConfig().set("bans." + id + ".status", "open");
                StaffCorePlugin.plugin.bans.getConfig().set("current", currentBans());
                StaffCorePlugin.plugin.bans.saveConfig();
            }
            boolean Ip = p.getPersistentDataContainer().has(new NamespacedKey(StaffCorePlugin.plugin, "ban-ip"), PersistentDataType.STRING);
            SendMsg.sendBanAlert(p.getName(), banned, reason, permanent, Ip, amount, time, format.format(ExpDate), format.format(now), utils.getString("bungeecord.server"));
            for (Player people : Bukkit.getOnlinePlayers()) {
                if (utils.getBoolean("alerts.ban") || people.hasPermission("staffcore.staff")) {
                    utils.PlaySound(people, "ban_alerts");
                    for (String key : utils.getStringList("ban.alerts", "alerts")) {
                        key = key.replace("%baner%", p.getName());
                        key = key.replace("%banned%", banned);
                        key = key.replace("%reason%", reason);
                        if (permanent) {
                            key = key.replace("%amount%", "&4PERMANENT");
                            key = key.replace("%time%", "");
                        } else {
                            key = key.replace("%amount%", String.valueOf(amount));
                            key = key.replace("%time%", time);
                        }
                        if (Ip) {
                            key = key.replace("%IP_BANED%", "&atrue");
                        } else {
                            key = key.replace("%IP_BANED%", "&cfalse");
                        }
                        key = key.replace("%exp_date%", format.format(ExpDate));
                        key = key.replace("%date%", format.format(now));
                        utils.tell(people, key);
                    }
                }
            }
            if (Bukkit.getPlayer(banned) != null) {
                String ban_msg = "\n";
                for (String msg : utils.getStringList("ban.msg", "alerts")) {
                    msg = msg.replace("%baner%", p.getName());
                    msg = msg.replace("%banned%", banned);
                    msg = msg.replace("%reason%", reason);
                    if (permanent) {
                        msg = msg.replace("%amount%", "&4PERMANENT");
                        msg = msg.replace("%time%", "");
                    } else {
                        msg = msg.replace("%amount%", String.valueOf(amount));
                        msg = msg.replace("%time%", time);
                    }
                    if (p.getPersistentDataContainer().has(new NamespacedKey(StaffCorePlugin.plugin, "ban-ip"), PersistentDataType.STRING)) {
                        msg = msg.replace("%IP_BANED%", "&atrue");
                    } else {
                        msg = msg.replace("%IP_BANED%", "&cfalse");
                    }
                    msg = msg.replace("%exp_date%", format.format(ExpDate));
                    msg = msg.replace("%date%", format.format(now));
                    ban_msg = ban_msg + msg + "\n";
                }
                utils.PlayParticle(Bukkit.getPlayer(banned), "ban");
                if (StaffCorePlugin.plugin.getConfig().getBoolean("wipe.wipe_on_ban")) {
                    wipePlayer.WipeOnBan(StaffCorePlugin.plugin, banned);
                }
                String finalBan_msg = ban_msg;
                Bukkit.getPlayer(banned).kickPlayer(utils.chat(finalBan_msg));
            }
        }
    }
}
