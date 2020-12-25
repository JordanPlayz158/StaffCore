package cl.bebt.staffcore.listeners;

import cl.bebt.staffcore.API.API;
import cl.bebt.staffcore.main;
import cl.bebt.staffcore.sql.SQLGetter;
import cl.bebt.staffcore.utils.*;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class onPlayerJoin implements Listener {
    private static final SQLGetter data = main.plugin.data;

    private static main plugin;

    public onPlayerJoin(main plugin) {
        onPlayerJoin.plugin = plugin;
    }

    @EventHandler
    void onPlayerPreJoin(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        String IP = String.valueOf(e.getAddress());
        IP = IP.replace("/", "");
        int currents = BanPlayer.id();
        if (utils.mysqlEnabled()) {
            try {
                if (!SQLGetter.PlayerExists("alts", p.getName())) {
                    SQLGetter.createAlts(p.getName(), IP);
                } else {
                    List<String> ips = utils.makeList(SQLGetter.getAlts(p.getName()));
                    SQLGetter.addIps(p.getName(), utils.stringify(ips, IP));
                }
            } catch (NullPointerException|IndexOutOfBoundsException Exception) {
                Exception.printStackTrace();
            }
            for (int i = 1; i <= currents; i++) {
                try {
                    if (SQLGetter.getBaned(i, "Name").equals(p.getName()) &&
                            SQLGetter.getBaned(i, "Status").equals("open") &&
                            API.isStillBaned(i).booleanValue()) {
                        e.disallow(PlayerLoginEvent.Result.KICK_OTHER, KickBanedPlayerSql(i));
                        break;
                    }
                    if (SQLGetter.getBanedIp(i).equals(IP) &&
                            SQLGetter.getBaned(i, "Status").equals("open") && SQLGetter.getBaned(i, "IP_Baned").equals("true") &&
                            API.isStillBaned(i).booleanValue()) {
                        e.disallow(PlayerLoginEvent.Result.KICK_OTHER, KickBanedPlayerSql(i));
                        break;
                    }
                } catch (NullPointerException nullPointerException) {}
            }
        } else {
            try {
                List<String> ips = plugin.alts.getConfig().getStringList("alts." + p.getName());
                int size = plugin.alts.getConfig().getStringList("alts." + p.getName()).size();
                if (size > 0) {
                    if (!ips.contains(IP))
                        ips.add(IP);
                } else {
                    ips.add(IP);
                }
                plugin.alts.getConfig().set("alts." + p.getName(), ips);
                plugin.alts.saveConfig();
            } catch (NullPointerException|IndexOutOfBoundsException Exception) {
                Exception.printStackTrace();
            }
            for (int i = 1; i <= currents; i++) {
                try {
                    if (Objects.equals(plugin.bans.getConfig().getString("bans." + i + ".name"), p.getName()) &&
                            Objects.equals(plugin.bans.getConfig().getString("bans." + i + ".status"), "open") &&
                            API.isStillBaned(i).booleanValue()) {
                        e.disallow(PlayerLoginEvent.Result.KICK_OTHER, KickBanedPlayer(i));
                        break;
                    }
                    if (plugin.bans.getConfig().getBoolean("bans." + i + ".IP-Baned") &&
                            Objects.equals(plugin.bans.getConfig().getString("bans." + i + ".IP"), IP) &&
                            Objects.equals(plugin.bans.getConfig().getString("bans." + i + ".status"), "open") &&
                            API.isStillBaned(i).booleanValue()) {
                        e.disallow(PlayerLoginEvent.Result.KICK_OTHER, KickBanedPlayer(i));
                        break;
                    }
                } catch (NullPointerException nullPointerException) {}
            }
        }
    }

    @EventHandler
    void onPlayerJoinEvent(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        PersistentDataContainer PlayerData = p.getPersistentDataContainer();
        if (p.hasPermission("staffcore.vanish") &&
                plugin.getConfig().getBoolean("staff.vanish_on_join")) {
            SetVanish.setVanish(p, Boolean.valueOf(true));
            utils.tell(p, plugin.getConfig().getString("staff.staff_prefix") + plugin.getConfig().getString("staff.vanished"));
        }
        if (utils.currentPlayerWarns(p.getName()) != 0 && utils.getBoolean("warns.notify").booleanValue()) {
            String msg = utils.getString("warns.alerts.notify");
            msg = msg.replace("%amount%", "" + utils.currentPlayerWarns(p.getName()));
            ComponentBuilder cb = new ComponentBuilder(utils.chat("&7Click to open your Warns"));
            TextComponent dis = new TextComponent(utils.chat(utils.getString("server_prefix") + msg));
            dis.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, cb.create()));
            dis.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/warningns"));
            p.spigot().sendMessage((BaseComponent)dis);
        }
        if (utils.mysqlEnabled()) {
            if (SQLGetter.isTrue(p, "frozen").equals("true")) {
                if (!p.getPersistentDataContainer().has(new NamespacedKey((Plugin)plugin, "frozen"), PersistentDataType.STRING))
                    FreezePlayer.FreezePlayer(p, "CONSOLE", Boolean.valueOf(true));
            } else if (SQLGetter.isTrue(p, "frozen").equals("false") &&
                    p.getPersistentDataContainer().has(new NamespacedKey((Plugin)plugin, "frozen"), PersistentDataType.STRING)) {
                FreezePlayer.FreezePlayer(p, "CONSOLE", Boolean.valueOf(false));
            }
            if (SQLGetter.isTrue(p, "staff").equals("true")) {
                if (!p.getPersistentDataContainer().has(new NamespacedKey((Plugin)plugin, "staff"), PersistentDataType.STRING)) {
                    SetStaffItems.On(p);
                } else {
                    p.setAllowFlight(true);
                    p.setFlying(true);
                }
            } else if (SQLGetter.isTrue(p, "staff").equals("false") &&
                    p.getPersistentDataContainer().has(new NamespacedKey((Plugin)plugin, "staff"), PersistentDataType.STRING)) {
                SetStaffItems.Off(p);
            }
            if (SQLGetter.isTrue(p, "vanish").equals("true")) {
                if (!p.getPersistentDataContainer().has(new NamespacedKey((Plugin)plugin, "vanished"), PersistentDataType.STRING))
                    SetVanish.setVanish(p, Boolean.valueOf(true));
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    if (!player.getPersistentDataContainer().has(new NamespacedKey((Plugin)plugin, "vanished"), PersistentDataType.STRING) || SQLGetter.isTrue(player, "vanish").equals("false")) {
                        if (p.hasPermission("staffcore.vanish.see"))
                            return;
                        player.hidePlayer((Plugin)plugin, p);
                        continue;
                    }
                    player.showPlayer((Plugin)plugin, p);
                    player.sendMessage(utils.chat(plugin.getConfig().getString("staff.staff_prefix") + p.getDisplayName() + " &3(&dVanished&3)"));
                    utils.PlaySound(player, "vanished_join");
                }
            }
            if (SQLGetter.isTrue(p, "vanish").equals("false")) {
                if (!p.getPersistentDataContainer().has(new NamespacedKey((Plugin)plugin, "vanished"), PersistentDataType.STRING))
                    SetVanish.setVanish(p, Boolean.valueOf(false));
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    if (player.getPersistentDataContainer().has(new NamespacedKey((Plugin)plugin, "vanished"), PersistentDataType.STRING) ||
                            SQLGetter.isTrue(player, "vanish").equals("true")) {
                        if (p.hasPermission("staffcore.vanish.see"))
                            return;
                        p.hidePlayer((Plugin)plugin, player);
                    }
                }
            }
            if (SQLGetter.isTrue(p, "flying").equals("true"))
                if (!p.getPersistentDataContainer().has(new NamespacedKey((Plugin)plugin, "flying"), PersistentDataType.STRING)) {
                    SetFly.SetFly(p, Boolean.valueOf(true));
                } else {
                    p.setAllowFlight(true);
                    p.setFlying(true);
                }
            if (SQLGetter.isTrue(p, "staffchat").equals("true"))
                p.getPersistentDataContainer().set(new NamespacedKey((Plugin)plugin, "staffchat"), PersistentDataType.STRING, "staffchat");
            if (SQLGetter.isTrue(p, "staffchat").equals("false"))
                p.getPersistentDataContainer().remove(new NamespacedKey((Plugin)plugin, "staffchat"));
        } else {
            if (PlayerData.has(new NamespacedKey((Plugin)plugin, "flying"), PersistentDataType.STRING)) {
                SetFly.SetFly(p, Boolean.valueOf(true));
            } else if (!PlayerData.has(new NamespacedKey((Plugin)plugin, "flying"), PersistentDataType.STRING) &&
                    !PlayerData.has(new NamespacedKey((Plugin)plugin, "staff"), PersistentDataType.STRING) &&
                    !PlayerData.has(new NamespacedKey((Plugin)plugin, "vanished"), PersistentDataType.STRING)) {
                SetFly.SetFly(p, Boolean.valueOf(false));
            }
            if (PlayerData.has(new NamespacedKey((Plugin)plugin, "vanished"), PersistentDataType.STRING) || PlayerData.has(new NamespacedKey((Plugin)plugin, "staff"), PersistentDataType.STRING)) {
                p.setAllowFlight(true);
                p.setFlying(true);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.hasPermission("staffcore.vanish.see") && !player.hasPermission("staffcore.vanish")) {
                        if (!player.getPersistentDataContainer().has(new NamespacedKey((Plugin)plugin, "staff"), PersistentDataType.STRING)) {
                            player.hidePlayer((Plugin)plugin, p);
                            continue;
                        }
                        p.showPlayer((Plugin)plugin, player);
                        continue;
                    }
                    if (player.getPersistentDataContainer().has(new NamespacedKey((Plugin)plugin, "staff"), PersistentDataType.STRING) || player
                            .getPersistentDataContainer().has(new NamespacedKey((Plugin)plugin, "vanished"), PersistentDataType.STRING) || player
                            .hasPermission("staffcore.vanish.see")) {
                        p.showPlayer((Plugin)plugin, player);
                        player.showPlayer((Plugin)plugin, p);
                        player.sendMessage(utils.chat(plugin.getConfig().getString("staff.staff_prefix") + p.getDisplayName() + " &3(&dVanished&3)"));
                        utils.PlaySound(player, "vanished_join");
                        continue;
                    }
                    player.hidePlayer((Plugin)plugin, p);
                }
            } else {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (p.hasPermission("staffcore.vanish.see") && p.hasPermission("staffcore.vanish")) {
                        if (player.getPersistentDataContainer().has(new NamespacedKey((Plugin)plugin, "staff"), PersistentDataType.STRING) || player.getPersistentDataContainer().has(new NamespacedKey((Plugin)plugin, "vanished"), PersistentDataType.STRING)) {
                            p.showPlayer((Plugin)plugin, player);
                            player.showPlayer((Plugin)plugin, p);
                        }
                        continue;
                    }
                    if (player.getPersistentDataContainer().has(new NamespacedKey((Plugin)plugin, "staff"), PersistentDataType.STRING) || player.getPersistentDataContainer().has(new NamespacedKey((Plugin)plugin, "vanished"), PersistentDataType.STRING)) {
                        p.hidePlayer((Plugin)plugin, player);
                        continue;
                    }
                    p.showPlayer((Plugin)plugin, player);
                }
            }
        }
    }

    String KickBanedPlayerSql(int Id) {
        try {
            String reason = SQLGetter.getBaned(Id, "Reason");
            Date now = new Date();
            String created = SQLGetter.getBaned(Id, "Date");
            String exp = SQLGetter.getBaned(Id, "ExpDate");
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String baner = SQLGetter.getBaned(Id, "Baner");
            String baned = SQLGetter.getBaned(Id, "Name");
            Date d2 = null;
            d2 = format.parse(exp);
            long remaining = (d2.getTime() - now.getTime()) / 1000L;
            long Days = TimeUnit.SECONDS.toDays(remaining);
            long Seconds = remaining - TimeUnit.DAYS.toSeconds(Days);
            long Hours = TimeUnit.SECONDS.toHours(Seconds);
            Seconds -= TimeUnit.HOURS.toSeconds(Hours);
            long Minutes = TimeUnit.SECONDS.toMinutes(Seconds);
            Seconds -= TimeUnit.MINUTES.toSeconds(Minutes);
            String ban_msg = "\n";
            for (String msg : main.plugin.getConfig().getStringList("ban.join_baned")) {
                msg = msg.replace("%baner%", baner);
                msg = msg.replace("%baned%", baned);
                msg = msg.replace("%reason%", reason);
                if (Days >= 365L) {
                    msg = msg.replace("%time_left%", "&4PERMANENT");
                } else {
                    msg = msg.replace("%time_left%", Days + "d " + Hours + "h " + Minutes + "m " + Seconds + "s");
                }
                if (SQLGetter.getBaned(Id, "IP_Baned").equals("true")) {
                    msg = msg.replace("%IP_BANED%", "&atrue");
                } else if (SQLGetter.getBaned(Id, "IP_Baned").equals("false")) {
                    msg = msg.replace("%IP_BANED%", "&cfalse");
                }
                msg = msg.replace("%exp_date%", exp);
                msg = msg.replace("%date%", created);
                ban_msg = ban_msg + msg + "\n";
            }
            return utils.chat(ban_msg);
        } catch (ParseException|NullPointerException ignored) {
            ignored.printStackTrace();
            return null;
        }
    }

    String KickBanedPlayer(int Id) {
        try {
            String p = plugin.bans.getConfig().getString("bans." + Id + ".baned_by");
            String baned = plugin.bans.getConfig().getString("bans." + Id + ".name");
            String reason = plugin.bans.getConfig().getString("bans." + Id + ".reason");
            Date now = new Date();
            String created = plugin.bans.getConfig().getString("bans." + Id + ".date");
            String exp = plugin.bans.getConfig().getString("bans." + Id + ".expdate");
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date d2 = null;
            d2 = format.parse(exp);
            long remaining = (d2.getTime() - now.getTime()) / 1000L;
            long Days = TimeUnit.SECONDS.toDays(remaining);
            long Seconds = remaining - TimeUnit.DAYS.toSeconds(Days);
            long Hours = TimeUnit.SECONDS.toHours(Seconds);
            Seconds -= TimeUnit.HOURS.toSeconds(Hours);
            long Minutes = TimeUnit.SECONDS.toMinutes(Seconds);
            Seconds -= TimeUnit.MINUTES.toSeconds(Minutes);
            String ban_msg = "\n";
            for (String msg : main.plugin.getConfig().getStringList("ban.join_baned")) {
                msg = msg.replace("%baner%", p);
                msg = msg.replace("%baned%", baned);
                msg = msg.replace("%reason%", reason);
                if (Days >= 365L) {
                    msg = msg.replace("%time_left%", "&4PERMANENT");
                } else {
                    msg = msg.replace("%time_left%", Days + "d " + Hours + "h " + Minutes + "m " + Seconds + "s");
                }
                if (plugin.bans.getConfig().getBoolean("bans." + Id + ".IP-Baned")) {
                    msg = msg.replace("%IP_BANED%", "&atrue");
                } else {
                    msg = msg.replace("%IP_BANED%", "&cfalse");
                }
                msg = msg.replace("%exp_date%", exp);
                msg = msg.replace("%date%", created);
                ban_msg = ban_msg + msg + "\n";
            }
            return utils.chat(ban_msg);
        } catch (ParseException|NullPointerException ignored) {
            ignored.printStackTrace();
            return null;
        }
    }
}
