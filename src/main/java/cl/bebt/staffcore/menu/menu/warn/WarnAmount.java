package cl.bebt.staffcore.menu.menu.warn;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.menu.PaginatedMenu;
import cl.bebt.staffcore.menu.PlayerMenuUtility;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class WarnAmount extends PaginatedMenu {
    private final StaffCorePlugin plugin;
    private final Player player;
    private final String warned;
    private final String reason;

    public WarnAmount(PlayerMenuUtility playerMenuUtility, StaffCorePlugin plugin, Player player, String warned, String reason) {
        super(playerMenuUtility);
        this.plugin = plugin;
        this.player = player;
        this.warned = warned;
        this.reason = reason;
    }

    @Override
    public String getMenuName() {
        return Utils.chat(Utils.getString("warns.amount.name", "menu", null));
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        int amount = 49;
        if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "amount"), PersistentDataType.LONG)) {
            p.closeInventory();
            long yep = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "amount"), PersistentDataType.LONG);
            new WarnQuantity(playerMenuUtility, plugin, warned, reason, yep).open();
        } else if (e.getCurrentItem().equals(close())) {
            p.closeInventory();
            if (e.getClick().isLeftClick()) {
                new WarnTimeChose(playerMenuUtility, StaffCorePlugin.plugin, p, warned, reason).open();
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
            if (index + 1 <= amount) {
                page++;
                p.closeInventory();
                open();
            } else {
                Utils.tell(p, Utils.getString("menu.already_in_last_page", "lg", "sv"));
            }
        }
    }

    @Override
    public void setMenuItems() {
        addMenuBorder();
        int amount = 49;
        player.getPersistentDataContainer().remove(new NamespacedKey(plugin, "seconds"));
        player.getPersistentDataContainer().remove(new NamespacedKey(plugin, "amount"));
        for (int i = 1; i < getMaxItemsPerPage() + 1; i++) {
            index = getMaxItemsPerPage() * page + i;
            if (index >= amount) break;
            //////////////////////////////
            ItemStack clock = new ItemStack(Material.CLOCK, index);
            ItemMeta meta = clock.getItemMeta();
            meta.setDisplayName(Utils.chat("&a") + index);
            long a = index;
            meta.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "amount"), PersistentDataType.LONG, a);
            clock.setItemMeta(meta);
            inventory.addItem(clock);
            /////////////////////////////
        }


    }
}