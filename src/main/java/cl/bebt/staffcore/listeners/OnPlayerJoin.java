package cl.bebt.staffcore.listeners;

import cl.bebt.staffcore.api.StaffCoreAPI;
import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.sql.queries.AltsQuery;
import cl.bebt.staffcore.sql.queries.BansQuery;
import cl.bebt.staffcore.sql.queries.FreezeQuery;
import cl.bebt.staffcore.utils.FreezePlayer;
import cl.bebt.staffcore.utils.SetFly;
import cl.bebt.staffcore.utils.SetStaffItems;
import cl.bebt.staffcore.utils.uuid.UUIDGetter;
import cl.bebt.staffcore.utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OnPlayerJoin implements Listener {

    private static StaffCorePlugin plugin;

    public OnPlayerJoin(StaffCorePlugin plugin) {
        OnPlayerJoin.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    void onPlayerPreJoin(AsyncPlayerPreLoginEvent e) {
        String IP = String.valueOf(e.getAddress());
        IP = IP.replace("/", "");
        if (Utils.mysqlEnabled()) {
            try {
                String finalIP = IP;
                if (!AltsQuery.PlayerExists(e.getName())) {
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> AltsQuery.createAlts(e.getName(), finalIP, UUIDGetter.getUUID(e.getName()).toString()));
                } else {
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        List<String> ips = Utils.makeList(AltsQuery.getAlts(e.getName()));
                        AltsQuery.addIps(e.getName(), Utils.stringify(ips, finalIP));
                    });
                }
            } catch (NullPointerException | IndexOutOfBoundsException Exception) {
                Exception.printStackTrace();
            }
            if (BansQuery.isStillBanned(e.getName(), IP)) {
                int id = BansQuery.getBannedId(e.getName(), IP);
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, KickBannedPlayerSql(BansQuery.getBanInfo(id)));
            }
        } else {
            try {
                List<String> ips = plugin.alts.getConfig().getStringList("alts." + e.getName());
                int size = plugin.alts.getConfig().getStringList("alts." + e.getName()).size();
                if (size > 0) {
                    if (!ips.contains(IP))
                        ips.add(IP);
                } else {
                    ips.add(IP);
                }
                plugin.alts.getConfig().set("alts." + e.getName(), ips);
                plugin.alts.saveConfig();
            } catch (NullPointerException | IndexOutOfBoundsException Exception) {
                Exception.printStackTrace();
            }
            for (int i = 1; i <= Utils.count("bans"); i++) {
                try {
                    if (Objects.equals(plugin.bans.getConfig().getString("bans." + i + ".name"), e.getName()) &&
                            Objects.equals(plugin.bans.getConfig().getString("bans." + i + ".status"), "open") &&
                            StaffCoreAPI.isStillBanned(i)) {
                        e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, KickBannedPlayer(i));
                        break;
                    }
                    if (plugin.bans.getConfig().getBoolean("bans." + i + ".IP-Banned") &&
                            Objects.equals(plugin.bans.getConfig().getString("bans." + i + ".IP"), IP) &&
                            Objects.equals(plugin.bans.getConfig().getString("bans." + i + ".status"), "open") &&
                            StaffCoreAPI.isStillBanned(i)) {
                        e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, KickBannedPlayer(i));
                        break;
                    }
                } catch (NullPointerException ignored) {
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    void onPlayerJoinEvent(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (Utils.currentPlayerWarns(p.getName()) != 0 && Utils.getBoolean("warns.notify")) {
            ComponentBuilder cb = new ComponentBuilder(Utils.chat(Utils.getString("warns.join_msg", "lg", null)));
            TextComponent dis = new TextComponent(Utils.chat(Utils.getString("warns.notify", "lg", "staff").replace("%amount%", String.valueOf(Utils.currentPlayerWarns(p.getName())))));
            dis.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, cb.create()));
            dis.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/warningns"));
            p.spigot().sendMessage(dis);
        }
        try {
            PersistentDataContainer PlayerData = p.getPersistentDataContainer();
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                if (Utils.mysqlEnabled()) {
                    if (FreezeQuery.isFrozen(p.getName()).equals("true")) {
                        if (!p.getPersistentDataContainer().has(new NamespacedKey(plugin, "frozen"), PersistentDataType.STRING))
                            FreezePlayer.freeze(p, "CONSOLE", true);
                    } else {
                        if (p.getPersistentDataContainer().has(new NamespacedKey(plugin, "frozen"), PersistentDataType.STRING)) {
                            FreezePlayer.freeze(p, "CONSOLE", false);
                        }
                    }
                }
            });
            if (PlayerData.has(new NamespacedKey(plugin, "flying"), PersistentDataType.STRING)) {
                new SetFly(p, true);
            } else if (!PlayerData.has(new NamespacedKey(plugin, "flying"), PersistentDataType.STRING) &&
                    !PlayerData.has(new NamespacedKey(plugin, "staff"), PersistentDataType.STRING) &&
                    !PlayerData.has(new NamespacedKey(plugin, "vanished"), PersistentDataType.STRING)) {
                new SetFly(p, false);
            }
            if (PlayerData.has(new NamespacedKey(plugin, "staff"), PersistentDataType.STRING)) {
                SetStaffItems.On(p);
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!plugin.getDescription().getVersion().equals(plugin.latestVersion)) {
                    if (p.hasPermission("staffcore.staff")) {
                        Utils.tellHover(p, plugin.getConfig().getString("server_prefix") +
                                        "&cYou are using an StaffCore older version",
                                "&aClick to download the version: " + plugin.latestVersion,
                                "https://www.spigotmc.org/resources/staff-core.104420/");
                    }
                }
                if (player != p) {
                    if (PlayerData.has(new NamespacedKey(plugin, "vanished"), PersistentDataType.STRING)) {
                        if (player.hasPermission("staffcore.vanish.see") ||
                                player.getPersistentDataContainer().has(new NamespacedKey(plugin, "vanished"), PersistentDataType.STRING) ||
                                player.getPersistentDataContainer().has(new NamespacedKey(plugin, "staff"), PersistentDataType.STRING)) {
                            player.showPlayer(plugin, p);
                        } else {
                            player.hidePlayer(plugin, p);
                        }
                    }
                    if (player.getPersistentDataContainer().has(new NamespacedKey(plugin, "vanished"), PersistentDataType.STRING)) {
                        if (p.hasPermission("staffcore.vanish.see") ||
                                PlayerData.has(new NamespacedKey(plugin, "vanished"), PersistentDataType.STRING) ||
                                PlayerData.has(new NamespacedKey(plugin, "staff"), PersistentDataType.STRING)) {
                            p.showPlayer(plugin, player);
                        } else {
                            p.hidePlayer(plugin, player);
                        }
                    }
                }
            }
        } catch (NoSuchMethodError ignored) {
        }
        if (Utils.getBoolean("discord.type.debug.enabled_debugs.commands")) {
            ArrayList<String> dc = new ArrayList<>();
            dc.add("**Player:** " + p.getName());
            dc.add("**Reason:** " + e.getJoinMessage());
            Utils.sendDiscordDebugMsg(e.getPlayer(), "⚠ Player Join ⚠", dc);
        }
    }

    String KickBannedPlayerSql(JSONObject json) {
        try {
            String reason = json.getString("Reason");
            Date now = new Date();
            String created = json.getString("Date");
            String exp = json.getString("ExpDate");
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String banner = json.getString("Banner");
            String banned = json.getString("Name");
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
            for (String msg : Utils.getStringList("ban.join", "alerts")) {
                msg = msg.replace("%baner%", banner);
                msg = msg.replace("%banned%", banned);
                msg = msg.replace("%reason%", reason);
                if (Days >= 365L) {
                    msg = msg.replace("%time_left%", "&4PERMANENT");
                } else {
                    msg = msg.replace("%time_left%", Days + "d " + Hours + "h " + Minutes + "m " + Seconds + "s");
                }
                if (json.getString("IP_Banned").equals("true")) {
                    msg = msg.replace("%IP_BANED%", "&atrue");
                } else {
                    msg = msg.replace("%IP_BANED%", "&cfalse");
                }
                msg = msg.replace("%exp_date%", exp);
                msg = msg.replace("%date%", created);
                ban_msg = ban_msg + msg + "\n";
            }
            return Utils.chat(ban_msg);
        } catch (ParseException | NullPointerException error) {
            error.printStackTrace();
            return null;
        }
    }

    String KickBannedPlayer(int Id) {
        try {
            String p = plugin.bans.getConfig().getString("bans." + Id + ".banned_by");
            String banned = plugin.bans.getConfig().getString("bans." + Id + ".name");
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
            for (String msg : Utils.getStringList("ban.join", "alerts")) {
                msg = msg.replace("%baner%", p);
                msg = msg.replace("%banned%", banned);
                msg = msg.replace("%reason%", reason);
                if (Days >= 365L) {
                    msg = msg.replace("%time_left%", "&4PERMANENT");
                } else {
                    msg = msg.replace("%time_left%", Days + "d " + Hours + "h " + Minutes + "m " + Seconds + "s");
                }
                if (plugin.bans.getConfig().getBoolean("bans." + Id + ".IP-Banned")) {
                    msg = msg.replace("%IP_BANED%", "&atrue");
                } else {
                    msg = msg.replace("%IP_BANED%", "&cfalse");
                }
                msg = msg.replace("%exp_date%", exp);
                msg = msg.replace("%date%", created);
                ban_msg = ban_msg + msg + "\n";
            }
            return Utils.chat(ban_msg);
        } catch (ParseException | NullPointerException error) {
            error.printStackTrace();
            return null;
        }
    }
}
