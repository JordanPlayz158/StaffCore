package cl.bebt.staffcore.menu.menu.inventory;

import cl.bebt.staffcore.menu.Menu;
import cl.bebt.staffcore.menu.PlayerMenuUtility;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class OpenChest extends Menu {
    private final HashMap<Integer, ItemStack> chest_slots;
    private final int size;

    public OpenChest(PlayerMenuUtility playerMenuUtility, HashMap<Integer, ItemStack> chest_slots, int size) {
        super(playerMenuUtility);
        this.chest_slots = chest_slots;
        this.size = size;
    }

    @Override
    public String getMenuName() {
        return Utils.chat(Utils.getString("inventory.open_chest.name", "menu", null));
    }

    @Override
    public int getSlots() {
        return size;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {

    }

    @Override
    public void setMenuItems() {
        for (int c = 0; c < size; c++) {
            inventory.setItem(c, chest_slots.get(c));
        }
    }
}
