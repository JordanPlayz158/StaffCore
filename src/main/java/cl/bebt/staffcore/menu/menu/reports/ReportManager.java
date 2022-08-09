package cl.bebt.staffcore.menu.menu.reports;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.menu.Menu;
import cl.bebt.staffcore.menu.PlayerMenuUtility;
import cl.bebt.staffcore.menu.menu.staff.ServerManager;
import cl.bebt.staffcore.sql.queries.ReportsQuery;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class ReportManager extends Menu {

    private final StaffCorePlugin plugin;

    public ReportManager(PlayerMenuUtility playerMenuUtility, StaffCorePlugin plugin) {
        super(playerMenuUtility);
        this.plugin = plugin;
    }

    @Override
    public String getMenuName() {
        return Utils.chat(Utils.getString("reports.manager.name", "menu", null));
    }

    @Override
    public int getSlots() {
        return 45;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "openReports"), PersistentDataType.STRING)) {
            p.closeInventory();
            new OpenReportsMenu(StaffCorePlugin.getPlayerMenuUtility(p), plugin).open();
            e.setCancelled(true);
        } else if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "closeReports"), PersistentDataType.STRING)) {
            p.closeInventory();
            new ClosedReportsMenu(StaffCorePlugin.getPlayerMenuUtility(p), plugin).open();
            e.setCancelled(true);
        } else if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "panel"), PersistentDataType.STRING)) {
            e.setCancelled(true);
        } else if (e.getCurrentItem().equals(close())) {
            p.closeInventory();
            if (e.getClick().isLeftClick()) {
                new ServerManager(StaffCorePlugin.getPlayerMenuUtility(p), plugin).open(p);
                e.setCancelled(true);
            }
        }
    }

    private int Closed() {
        int close = 0;
        if (Utils.mysqlEnabled()) {
            return ReportsQuery.getClosedReports().size();
        } else {
            int count = plugin.reports.getConfig().getInt("current") + plugin.reports.getConfig().getInt("count");
            for (int id = 0; id <= count; ) {
                plugin.reports.reloadConfig();
                id++;
                try {
                    if (plugin.reports.getConfig().get("reports." + id + ".status").equals("close")) {
                        close++;
                    }
                } catch (NullPointerException ignored) {
                }
            }
            return close;
        }
    }

    private int Opens() {
        int opens = 0;
        if (Utils.mysqlEnabled()) {
            return ReportsQuery.getOpenReports().size();
        } else {
            int count = plugin.reports.getConfig().getInt("current") + plugin.reports.getConfig().getInt("count");
            for (int id = 0; id <= count + 1; ) {
                plugin.reports.reloadConfig();
                id++;
                try {
                    if (plugin.reports.getConfig().get("reports." + id + ".status").equals("open")) {
                        opens++;
                    }
                } catch (NullPointerException ignored) {
                }
            }
            return opens;
        }
    }

    @Override
    public void setMenuItems() {
        ArrayList<String> lore = new ArrayList<>();
        ItemStack openReports = new ItemStack(Material.FLOWER_BANNER_PATTERN, 1);
        ItemStack closeReports = new ItemStack(Material.NAME_TAG, 1);

        ItemMeta or_meta = openReports.getItemMeta();
        ItemMeta cr_meta = closeReports.getItemMeta();

        or_meta.setDisplayName(Utils.chat("&aOpen Reports"));
        cr_meta.setDisplayName(Utils.chat("&cClosed Reports"));

        lore.add(Utils.chat("&8&lClick to open all the opened Reports"));

        lore.add(Utils.chat("&8&lCurrent Opened: &a" + Opens()));
        or_meta.setLore(lore);
        lore.clear();
        lore.add(Utils.chat("&8&lClick to open all the closed Reports"));

        lore.add(Utils.chat("&8&lCurrent Closed: &a" + Closed()));
        cr_meta.setLore(lore);
        lore.clear();

        or_meta.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "openReports"), PersistentDataType.STRING, "openReports");
        cr_meta.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "closeReports"), PersistentDataType.STRING, "closeReports");

        openReports.setItemMeta(or_meta);
        closeReports.setItemMeta(cr_meta);

        for (int i = 0; i < 10; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, super.bluePanel());
            }
        }
        for (int i = 10; i < 17; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, super.redPanel());
            }
        }
        inventory.setItem(17, super.bluePanel());
        inventory.setItem(18, super.bluePanel());
        inventory.setItem(19, super.redPanel());
        inventory.setItem(25, super.redPanel());
        inventory.setItem(26, super.bluePanel());
        inventory.setItem(27, super.bluePanel());
        for (int i = 28; i < 35; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, super.redPanel());
            }
        }
        for (int i = 35; i < 45; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, super.bluePanel());
            }
        }
        inventory.setItem(20, openReports);
        inventory.setItem(21, super.redPanel());
        inventory.setItem(22, close());
        inventory.setItem(23, super.redPanel());
        inventory.setItem(24, closeReports);
    }

}
