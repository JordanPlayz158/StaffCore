package cl.bebt.staffcore.menu.menu.ban.list;

import cl.bebt.staffcore.api.StaffCoreAPI;
import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.menu.PaginatedMenu;
import cl.bebt.staffcore.menu.PlayerMenuUtility;
import cl.bebt.staffcore.sql.queries.BansQuery;
import cl.bebt.staffcore.utils.TpPlayers;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class OpenBansMenu extends PaginatedMenu {
    private final StaffCorePlugin plugin;

    public OpenBansMenu(PlayerMenuUtility playerMenuUtility, StaffCorePlugin plugin) {
        super(playerMenuUtility);
        this.plugin = plugin;
    }

    public String getMenuName() {
        return Utils.chat("&cOpen Bans:");
    }

    public int getSlots() {
        return 54;
    }

    public void handleMenu(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        HashMap<Integer, Integer> bans = new HashMap<>();
        int num = 0;
        if (Utils.mysqlEnabled()) {
            ArrayList<Integer> openBans = BansQuery.getOpenBans();
            for (Integer openBan : openBans) {
                num++;
                bans.put(num, openBan);
            }
        } else {
            for (int id = 0; id <= Utils.count("bans"); ) {
                id++;
                try {
                    if (plugin.bans.getConfig().getString("bans." + id + ".status").equals("open")) {
                        num++;
                        bans.put(num, id);
                    }

                } catch (NullPointerException ignored) {
                }
            }
        }
        if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "open"), PersistentDataType.STRING)) {
            p.closeInventory();
            String jugador = e.getCurrentItem().getItemMeta().getDisplayName();
            if (e.getClick().isLeftClick()) {
                int i = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "open-id"), PersistentDataType.INTEGER);
                new EditBan(StaffCorePlugin.getPlayerMenuUtility(p), StaffCorePlugin.plugin, jugador, i).open();
            } else if (e.getClick().isRightClick()) {
                TpPlayers.tpToPlayer(p, jugador);
            }
        } else if (e.getCurrentItem().equals(close())) {
            p.closeInventory();
            if (e.getClick().isLeftClick()) {
                new BanManager(StaffCorePlugin.getPlayerMenuUtility(p), plugin).open();
            }
        } else if (e.getCurrentItem().equals(back())) {
            if (page == 0) {
                Utils.tell(p, Utils.getString("menu.already_in_first_page", "lg", "sv"));
            } else {
                page--;
                p.closeInventory();
                open();
            }
        } else if (e.getCurrentItem().equals(next())) {
            if (index + 1 <= bans.size()) {
                page++;
                p.closeInventory();
                open();
            } else {
                Utils.tell(p, Utils.getString("menu.already_in_last_page", "lg", "sv"));
            }
        }
    }

    public void setMenuItems() {
        addMenuBorder();
        HashMap<Integer, Integer> bans = new HashMap<>();
        int num = 0;
        JSONObject json = new JSONObject();
        if (Utils.mysqlEnabled()) {
            ArrayList<Integer> openBans = BansQuery.getOpenBans();
            for (Integer openBan : openBans) {
                num++;
                bans.put(num, openBan);
            }
            json = BansQuery.getOpenBanInfo();
        } else {
            for (int id = 0; id <= Utils.count("bans"); ) {
                id++;
                try {
                    if (plugin.bans.getConfig().getString("bans." + id + ".status").equals("open")) {
                        num++;
                        bans.put(num, id);
                    }

                } catch (NullPointerException ignored) {
                }
            }
        }
        if (bans != null && !bans.isEmpty())
            for (int i = 1; i <= getMaxItemsPerPage(); i++) {
                try {
                    index = getMaxItemsPerPage() * page + i;
                    if (index > bans.size())
                        break;
                    if (bans.get(index) != null) {
                        if (Utils.mysqlEnabled()) {
                            String rawBanInfo = json.get(String.valueOf(bans.get(index))).toString()
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
                            lore.add(Utils.chat("&7Ban ID:&a #" + bans.get(index)));
                            lore.add(Utils.chat("&aLeft click delete or close"));
                            lore.add(Utils.chat("&aRight click to tp"));
                            meta.setLore(lore);
                            meta.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "open-id"), PersistentDataType.INTEGER, bans.get(index));
                            meta.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "open"), PersistentDataType.STRING, "open");
                            p_head.setItemMeta(meta);
                            inventory.addItem(p_head);
                        } else {
                            Date now = new Date();
                            String exp = plugin.bans.getConfig().getString("bans." + bans.get(index) + ".expdate");
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
                            ItemStack p_head = Utils.getPlayerHead(plugin.bans.getConfig().getString("bans." + bans.get(index) + ".name"));
                            ItemMeta meta = p_head.getItemMeta();
                            ArrayList<String> lore = new ArrayList<>();
                            meta.setDisplayName(plugin.bans.getConfig().get("bans." + bans.get(index) + ".name").toString());
                            lore.add(Utils.chat("&7Banned by: " + plugin.bans.getConfig().getString("bans." + bans.get(index) + ".banned_by")));
                            lore.add(Utils.chat("&7Reason: &b" + plugin.bans.getConfig().getString("bans." + bans.get(index) + ".reason")));
                            lore.add(Utils.chat("&7Created date: &c" + plugin.bans.getConfig().getString("bans." + bans.get(index) + ".date")));
                            lore.add(Utils.chat("&7Expiry date: &c" + plugin.bans.getConfig().getString("bans." + bans.get(index) + ".expdate")));
                            if (StaffCoreAPI.isStillBanned(bans.get(index))) {
                                lore.add(Utils.chat("&7Status: &aOpen"));
                                if (Days > 365L) {
                                    lore.add(Utils.chat("&7Time left: &4PERMANENT"));
                                } else {
                                    lore.add(Utils.chat("&7Time left: &c" + Days + "d " + Hours + "h " + Minutes + "m " + Seconds + "s"));
                                }
                            } else {
                                lore.add(Utils.chat("&7Status: &c" + plugin.bans.getConfig().getString("bans." + bans.get(index) + ".status")));
                            }
                            if (plugin.bans.getConfig().getBoolean("bans." + bans.get(index) + ".IP-Banned")) {
                                lore.add(Utils.chat("&7Ip Banned: &aTrue"));
                            } else {
                                lore.add(Utils.chat("&7Ip Banned: &cFalse"));
                            }
                            lore.add(Utils.chat("&7Ban ID:&a #" + bans.get(index)));
                            lore.add(Utils.chat("&aLeft click delete or close"));
                            lore.add(Utils.chat("&aRight click to tp"));
                            meta.setLore(lore);
                            meta.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "open-id"), PersistentDataType.INTEGER, bans.get(index));
                            meta.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "open"), PersistentDataType.STRING, "open");
                            p_head.setItemMeta(meta);
                            inventory.addItem(p_head);
                        }
                    }
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
            }
    }
}
