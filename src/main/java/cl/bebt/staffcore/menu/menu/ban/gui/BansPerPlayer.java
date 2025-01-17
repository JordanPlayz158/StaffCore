package cl.bebt.staffcore.menu.menu.ban.gui;

import cl.bebt.staffcore.api.StaffCoreAPI;
import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.menu.Menu;
import cl.bebt.staffcore.menu.PlayerMenuUtility;
import cl.bebt.staffcore.menu.menu.ban.list.EditBan;
import cl.bebt.staffcore.sql.queries.BansQuery;
import cl.bebt.staffcore.utils.Items;
import cl.bebt.staffcore.utils.TpPlayers;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class BansPerPlayer extends Menu {
    private final StaffCorePlugin plugin;
    private final Player p;
    private final String banned;

    public BansPerPlayer(PlayerMenuUtility playerMenuUtility, StaffCorePlugin plugin,
                         Player p, String banned) {
        super(playerMenuUtility);
        this.plugin = plugin;
        this.p = p;
        this.banned = banned;
    }

    @Override
    public String getMenuName() {
        if (p.getName().equalsIgnoreCase(banned)) {
            return Utils.chat(Utils.getString("bans.owns", "menu", null));
        } else {
            return Utils.chat(Utils.getString("bans.others", "menu", null).replace("%player%", banned));
        }
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        if (p.hasPermission("staffcore.ban.change")) {
            if (item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "id"), PersistentDataType.INTEGER)) {
                p.closeInventory();
                String jugador = e.getCurrentItem().getItemMeta().getDisplayName();
                if (e.getClick().isLeftClick()) {
                    int id = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "id"), PersistentDataType.INTEGER);
                    new EditBan(StaffCorePlugin.getPlayerMenuUtility(p), StaffCorePlugin.plugin, jugador, id).open();
                } else if (e.getClick().isRightClick()) {
                    TpPlayers.tpToPlayer(p, jugador);
                }
            }
        } else if (e.getCurrentItem().equals(close())) {
            p.closeInventory();
        }

    }

    @Override
    public void setMenuItems() {
        for (int i = 0; i < 10; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, super.bluePanel());
            }
        }

        for (int i = 17; i < 27; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, super.bluePanel());
            }
        }
        JSONObject json = new JSONObject();
        HashMap<Integer, Integer> warns = new HashMap<>();
        int num = 0;
        if (Utils.mysqlEnabled()) {
            ArrayList<Integer> closedReports = BansQuery.getPlayerBans(banned);
            for (Integer closedReport : closedReports) {
                warns.put(num, closedReport);
                num++;
            }
            json = BansQuery.getPlayerBansInfo(banned);
        }
        if (Utils.mysqlEnabled()) {
            for (int i = 0; i <= warns.size(); i++) {
                try {
                    String rawBanInfo = json.get(String.valueOf(i)).toString()
                            .replace("[", "")
                            .replace("]", "");
                    JSONObject banInfo = new JSONObject(rawBanInfo);
                    String exp = banInfo.getString("ExpDate");
                    Date now = new Date();
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
                    ItemStack p_head = Utils.getPlayerHead(banInfo.getString("Name"));
                    ItemMeta meta = p_head.getItemMeta();
                    ArrayList<String> lore = new ArrayList<>();
                    meta.setDisplayName(banInfo.getString("Name"));
                    lore.add(Utils.chat("&7Banned by: " + banInfo.getString("Baner")));
                    lore.add(Utils.chat("&7Reason: &b" + banInfo.getString("Reason")));
                    lore.add(Utils.chat("&7Created date: &c" + banInfo.getString("Date")));
                    lore.add(Utils.chat("&7Expiry date: &c" + banInfo.getString("ExpDate")));
                    if (banInfo.getString("Status").equalsIgnoreCase("open")) {
                        lore.add(Utils.chat("&7Status: &aOpen"));
                        if (Days > 365L) {
                            lore.add(Utils.chat("&7Time left: &4PERMANENT"));
                        } else {
                            lore.add(Utils.chat("&7Time left: &c" + Days + "d " + Hours + "h " + Minutes + "m " + Seconds + "s"));
                        }
                    } else {
                        lore.add(Utils.chat("&7Status: &cClosed"));
                    }
                    if (banInfo.getString("IP_Banned").equalsIgnoreCase("true")) {
                        lore.add(Utils.chat("&7Ip Banned: &aTrue"));
                    } else {
                        lore.add(Utils.chat("&7Ip Banned: &cFalse"));
                    }
                    lore.add(Utils.chat("&7Ban ID:&a #" + i));
                    lore.add(Utils.chat("&aLeft click delete or close"));
                    lore.add(Utils.chat("&aRight click to tp"));
                    meta.setLore(lore);
                    meta.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "id"), PersistentDataType.INTEGER, i);
                    p_head.setItemMeta(meta);
                    inventory.addItem(p_head);
                } catch (ParseException | JSONException ignored) {
                }
            }
        } else {
            ArrayList<Integer> ids = getWarnIds();
            for (int i : ids) {
                Date now = new Date();
                String exp = plugin.bans.getConfig().getString("bans." + i + ".expdate");
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Date d2 = null;
                try {
                    d2 = format.parse(exp);
                } catch (ParseException ignored) {

                }
                long remaining = (d2.getTime() - now.getTime()) / 1000L;
                long Days = TimeUnit.SECONDS.toDays(remaining);
                long Seconds = remaining - TimeUnit.DAYS.toSeconds(Days);
                long Hours = TimeUnit.SECONDS.toHours(Seconds);
                Seconds -= TimeUnit.HOURS.toSeconds(Hours);
                long Minutes = TimeUnit.SECONDS.toMinutes(Seconds);
                Seconds -= TimeUnit.MINUTES.toSeconds(Minutes);
                ItemStack p_head = Utils.getPlayerHead(plugin.bans.getConfig().getString("bans." + i + ".name"));
                ItemMeta meta = p_head.getItemMeta();
                ArrayList<String> lore = new ArrayList<>();
                meta.setDisplayName(plugin.bans.getConfig().get("bans." + i + ".name").toString());
                lore.add(Utils.chat("&7Banned by: " + plugin.bans.getConfig().getString("bans." + i + ".banned_by")));
                lore.add(Utils.chat("&7Reason: &b" + plugin.bans.getConfig().getString("bans." + i + ".reason")));
                lore.add(Utils.chat("&7Created date: &c" + plugin.bans.getConfig().getString("bans." + i + ".date")));
                lore.add(Utils.chat("&7Expiry date: &c" + plugin.bans.getConfig().getString("bans." + i + ".expdate")));
                if (StaffCoreAPI.isStillBanned(i)) {
                    lore.add(Utils.chat("&7Status: &aOpen"));
                    if (Days > 365L) {
                        lore.add(Utils.chat("&7Time left: &4PERMANENT"));
                    } else {
                        lore.add(Utils.chat("&7Time left: &c" + Days + "d " + Hours + "h " + Minutes + "m " + Seconds + "s"));
                    }
                } else {
                    lore.add(Utils.chat("&7Status: &c" + plugin.bans.getConfig().getString("bans." + i + ".status")));
                }
                if (plugin.bans.getConfig().getBoolean("bans." + i + ".IP-Banned")) {
                    lore.add(Utils.chat("&7Ip Banned: &aTrue"));
                } else {
                    lore.add(Utils.chat("&7Ip Banned: &cFalse"));
                }
                lore.add(Utils.chat("&7Ban ID:&a #" + i));
                lore.add(Utils.chat("&aLeft click delete or close"));
                lore.add(Utils.chat("&aRight click to tp"));
                meta.setLore(lore);
                meta.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "id"), PersistentDataType.INTEGER, i);
                p_head.setItemMeta(meta);
                inventory.addItem(p_head);
            }
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, Items.EmptyItem());
            }
        }
    }

    private ArrayList<Integer> getWarnIds() {
        ArrayList<Integer> bansIds = new ArrayList<>();
        for (int i = 0; i < (plugin.bans.getConfig().getInt("current") + plugin.bans.getConfig().getInt("count")); i++) {
            try {
                if (plugin.bans.getConfig().getString("bans." + i + ".name").equalsIgnoreCase(banned)) {
                    bansIds.add(i);
                }
            } catch (NullPointerException ignored) {
            }
        }
        return bansIds;
    }

}
