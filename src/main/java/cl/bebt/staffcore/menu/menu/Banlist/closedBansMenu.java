package cl.bebt.staffcore.menu.menu.Banlist;

import cl.bebt.staffcore.API.StaffCoreAPI;
import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.menu.PaginatedMenu;
import cl.bebt.staffcore.menu.PlayerMenuUtility;
import cl.bebt.staffcore.sql.Queries.BansQuery;
import cl.bebt.staffcore.utils.TpPlayers;
import cl.bebt.staffcore.utils.utils;
import org.bukkit.Material;
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

public class closedBansMenu extends PaginatedMenu {
    private final StaffCorePlugin plugin;

    public closedBansMenu(PlayerMenuUtility playerMenuUtility, StaffCorePlugin plugin) {
        super(playerMenuUtility);
        this.plugin = plugin;
    }

    public String getMenuName() {
        return utils.chat("&cClosed Bans");
    }

    public int getSlots() {
        return 54;
    }

    public void handleMenu(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        HashMap<Integer, Integer> bans = new HashMap<>();
        int num = 0;
        if (utils.mysqlEnabled()) {
            ArrayList<Integer> closedBans = BansQuery.getClosedBans();
            for (Integer closedBan : closedBans) {
                num++;
                bans.put(num, closedBan);
            }
        } else {
            for (int id = 0; id <= utils.count("bans"); ) {
                id++;
                try {
                    if (plugin.bans.getConfig().getString("bans." + id + ".status").equals("close")) {
                        num++;
                        bans.put(num, id);
                    }

                } catch (NullPointerException ignored) {
                }
            }
        }
        if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(this.plugin, "closed"), PersistentDataType.STRING)) {
            p.closeInventory();
            String jugador = e.getCurrentItem().getItemMeta().getDisplayName();
            if (e.getClick().isLeftClick()) {
                int i = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(this.plugin, "closed-id"), PersistentDataType.INTEGER);
                new EditBan(StaffCorePlugin.getPlayerMenuUtility(p), StaffCorePlugin.plugin, jugador, i).open();
            } else if (e.getClick().isRightClick()) {
                TpPlayers.tpToPlayer(p, jugador);
            }
        } else if (e.getCurrentItem().getType().equals(Material.BARRIER)) {
            if (e.getClick().isLeftClick()) {
                p.closeInventory();
                new BanManager(StaffCorePlugin.getPlayerMenuUtility(p), this.plugin).open();
                e.setCancelled(true);
            } else if (e.getClick().isRightClick()) {
                p.closeInventory();
            }
        } else if (e.getCurrentItem().equals(back())) {
            if (page == 0) {
                utils.tell(p, utils.getString("menu.already_in_first_page", "lg", "sv"));
            } else {
                page--;
                p.closeInventory();
                open();
            }
        } else if (e.getCurrentItem().equals(next())) {
            e.setCancelled(true);
            if (index + 1 <= bans.size()) {
                page++;
                open();
            } else {
                utils.tell(p, utils.getString("menu.already_in_last_page", "lg", "sv"));
            }
        }
    }

    public void setMenuItems() {
        addMenuBorder();
        HashMap<Integer, Integer> bans = new HashMap<>();
        int num = 0;
        JSONObject json = new JSONObject();
        if (utils.mysqlEnabled()) {
            ArrayList<Integer> closedBans = BansQuery.getClosedBans();
            for (Integer closedBan : closedBans) {
                num++;
                bans.put(num, closedBan);
            }
            json = BansQuery.getClosedBanInfo();
        } else {
            for (int id = 0; id <= utils.count("bans"); ) {
                id++;
                try {
                    if (plugin.bans.getConfig().getString("bans." + id + ".status").equals("closed")) {
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
                    this.index = getMaxItemsPerPage() * this.page + i;
                    if (this.index > bans.size())
                        break;
                    if (bans.get(this.index) != null) {
                        if (utils.mysqlEnabled()) {
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
                            ItemStack p_head = utils.getPlayerHead(banInfo.getString("Name"));
                            ItemMeta meta = p_head.getItemMeta();
                            ArrayList<String> lore = new ArrayList<>();
                            meta.setDisplayName(banInfo.getString("Name"));
                            lore.add(utils.chat("&7Banned by: " + banInfo.getString("Baner")));
                            lore.add(utils.chat("&7Reason: &b" + banInfo.getString("Reason")));
                            lore.add(utils.chat("&7Created date: &c" + banInfo.getString("Date")));
                            lore.add(utils.chat("&7Expiry date: &c" + banInfo.getString("ExpDate")));
                            if (banInfo.getString("Status").equalsIgnoreCase("open")) {
                                lore.add(utils.chat("&7Status: &aOpen"));
                                if (Days > 365L) {
                                    lore.add(utils.chat("&7Time left: &4PERMANENT"));
                                } else {
                                    lore.add(utils.chat("&7Time left: &c" + Days + "d " + Hours + "h " + Minutes + "m " + Seconds + "s"));
                                }
                            } else {
                                lore.add(utils.chat("&7Status: &cClosed"));
                            }
                            if (banInfo.getString("IP_Banned").equalsIgnoreCase("true")) {
                                lore.add(utils.chat("&7Ip Banned: &aTrue"));
                            } else {
                                lore.add(utils.chat("&7Ip Banned: &cFalse"));
                            }
                            lore.add(utils.chat("&7Ban ID:&a #" + bans.get(this.index)));
                            lore.add(utils.chat("&aLeft click delete"));
                            lore.add(utils.chat("&aRight click to tp"));
                            meta.setLore(lore);
                            meta.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "closed-id"), PersistentDataType.INTEGER, bans.get(this.index));
                            meta.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "closed"), PersistentDataType.STRING, "closed");
                            p_head.setItemMeta(meta);
                            this.inventory.addItem(p_head);
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
                            ItemStack p_head = utils.getPlayerHead(plugin.bans.getConfig().getString("bans." + bans.get(index) + ".name"));
                            ItemMeta meta = p_head.getItemMeta();
                            ArrayList<String> lore = new ArrayList<>();
                            meta.setDisplayName(this.plugin.bans.getConfig().get("bans." + bans.get(this.index) + ".name").toString());
                            lore.add(utils.chat("&7Banned by: &a" + this.plugin.bans.getConfig().getString("bans." + bans.get(this.index) + ".banned_by")));
                            lore.add(utils.chat("&7Reason: &b" + this.plugin.bans.getConfig().getString("bans." + bans.get(this.index) + ".reason")));
                            lore.add(utils.chat("&7Created date: &a" + this.plugin.bans.getConfig().getString("bans." + bans.get(this.index) + ".date")));
                            lore.add(utils.chat("&7Expiry date: &c" + this.plugin.bans.getConfig().getString("bans." + bans.get(this.index) + ".expdate")));
                            lore.add(utils.chat("&7Status: &cClosed"));
                            if (StaffCoreAPI.isStillBanned(bans.get(index))) {
                                lore.add(utils.chat("&7Status: &aOpen"));
                                if (Days > 365L) {
                                    lore.add(utils.chat("&7Time left: &4PERMANENT"));
                                } else {
                                    lore.add(utils.chat("&7Time left: &c" + Days + "d " + Hours + "h " + Minutes + "m " + Seconds + "s"));
                                }
                            } else {
                                lore.add(utils.chat("&7Status: &c" + plugin.bans.getConfig().getString("bans." + bans.get(index) + ".status")));
                            }
                            if (plugin.bans.getConfig().getBoolean("bans." + bans.get(index) + ".IP-Banned")) {
                                lore.add(utils.chat("&7Ip Banned: &aTrue"));
                            } else {
                                lore.add(utils.chat("&7Ip Banned: &cFalse"));
                            }
                            lore.add(utils.chat("&7Ban ID:&a #" + bans.get(this.index)));
                            lore.add(utils.chat("&aLeft click delete"));
                            lore.add(utils.chat("&aRight click to tp"));
                            meta.setLore(lore);
                            meta.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "closed-id"), PersistentDataType.INTEGER, bans.get(this.index));
                            meta.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "closed"), PersistentDataType.STRING, "closed");
                            p_head.setItemMeta(meta);
                            this.inventory.addItem(p_head);
                        }
                    }
                } catch (ParseException ignored) {
                }
            }
    }
}
