package cl.bebt.staffcore.menu.menu.inventory;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.menu.InventoryMenu;
import cl.bebt.staffcore.menu.PlayerMenuUtility;
import cl.bebt.staffcore.utils.Items;
import cl.bebt.staffcore.utils.OpenEnderSee;
import cl.bebt.staffcore.utils.TpPlayers;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;

public class EnderSee extends InventoryMenu {

    private final Player target;

    public EnderSee(PlayerMenuUtility playerMenuUtility, Player target) {
        super(playerMenuUtility);
        this.target = target;
    }

    @Override
    public String getMenuName() {
        return Utils.chat(Utils.getString("inventory.ender_see.name", "menu", null).replace("%player%", target.getName()));
    }

    @Override
    public int getSlots() {
        return 45;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        ItemStack cursor = e.getCursor();
        int slot = e.getSlot();
        if (e.getClick() == ClickType.DOUBLE_CLICK) {
            e.setCancelled(true);
            return;
        }

        if (e.getClick() == ClickType.MIDDLE) {
            return;
        }

        if (e.getClick() == ClickType.DROP || e.getClick() == ClickType.CONTROL_DROP) {
            e.setCancelled(true);
            return;
        }

        if (e.getClickedInventory() == p.getInventory()) {
            return;
        }
        if (e.getSlot() == 27 || e.getSlot() == 28 || e.getSlot() == 29) {
            e.setCancelled(true);
            return;
        } else if (e.getSlot() >= 36 && e.getSlot() <= 44) {
            slot = slot - 36;
        } else if (e.getSlot() == 30) {
            slot = target.getInventory().getHeldItemSlot();
        } else if (e.getSlot() == 32) {
            slot = 39;
        } else if (e.getSlot() == 33) {
            slot = 38;
        } else if (e.getSlot() == 34) {
            slot = 37;
        } else if (e.getSlot() == 35) {
            slot = 36;
        }

        try {
            if (item.equals(Material.AIR)) {
                // player put item to inventory
                //Bukkit.getScheduler( ).scheduleSyncDelayedTask( main.plugin , ( ) ->OpenInvSee.updateTargetInventory( p, target ),6L);
                OpenEnderSee.setItem(p, target, slot);
            } else if (!item.equals(Material.AIR) && cursor.getType().equals(Material.AIR)) {
                // player take item from inventory
                //Bukkit.getScheduler( ).scheduleSyncDelayedTask( main.plugin , ( ) -> OpenInvSee.updateTargetInventory( p, target ) , 5L );
                OpenEnderSee.removeItem(target, slot);
            } else if (!item.equals(Material.AIR) && !cursor.getType().equals(Material.AIR)) {
                // player swap item in inventory
                OpenEnderSee.removeItem(target, slot);
                OpenEnderSee.setItem(p, target, slot);
                //OpenInvSee.removeItem( p , target , slot , item );
                //OpenInvSee.setItem( p , target , slot );
            }
            if (item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(StaffCorePlugin.plugin, "head"), PersistentDataType.STRING)) {
                e.setCancelled(true);
                String target = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(StaffCorePlugin.plugin, "name"), PersistentDataType.STRING);
                TpPlayers.tpToPlayer(p, target);
            }

        } catch (NullPointerException ignored) {
            //OpenInvSee.updateTargetInventory( p, target );
            OpenEnderSee.setItem(p, target, slot);
        }

    }

    @Override
    public void handleMenu(InventoryDragEvent e) {
        Player p = (Player) e.getWhoClicked();
        ArrayList<ItemStack> items = new ArrayList<>(e.getNewItems().values());
        ArrayList<Integer> slots = new ArrayList<>(e.getRawSlots());
        for (int i = 0; i < e.getRawSlots().size(); i++) {
            int slot = slots.get(i);
            if (slot == 27 || slot == 28 || slot == 29) {
                e.setCancelled(true);
                return;
            } else if (slot >= 36 && slot <= 44) {
                slot = slot - 36;
            } else if (slot == 30) {
                slot = target.getInventory().getHeldItemSlot();
            } else if (slot == 32) {
                slot = 39;
            } else if (slot == 33) {
                slot = 38;
            } else if (slot == 34) {
                slot = 37;
            } else if (slot == 35) {
                slot = 36;
            }
            OpenEnderSee.setItem(p, target, slot, items.get(i));
        }

    }

    @Override
    public void handleMenu(InventoryCloseEvent e) {
        StaffCorePlugin.enderSee.remove(e.getPlayer(), target);
        Utils.PlaySound((Player) e.getPlayer(), "endersee_close");
    }

    @Override
    public void setMenuItemsPlayer(Player p) {
        HashMap<Integer, ItemStack> hotbar = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            try {
                hotbar.put(i, target.getInventory().getItem(i));
            } catch (NullPointerException ignored) {
            }
        }
        HashMap<Integer, ItemStack> inv = new HashMap<>();
        for (int c = 0; c <= 35; c++) {
            try {
                inv.put(c, target.getEnderChest().getItem(c));
            } catch (NullPointerException ignored) {
            }
        }
        for (int a = 0; a < hotbar.size(); a++) {
            inventory.setItem(a + 36, hotbar.get(a));
        }
        for (int b = 0; b < inv.size(); b++) {
            inventory.setItem(b, inv.get(b));
        }
        inventory.setItem(27, Items.greenPanel());
        inventory.setItem(28, Items.potions(target));
        inventory.setItem(29, Items.food(target));
        if (target.getInventory().getItemInMainHand() != null) {
            inventory.setItem(30, target.getInventory().getItemInMainHand());
        }
        inventory.setItem(31, Items.head(target));
        if (target.getInventory().getHelmet() != null) {
            inventory.setItem(32, target.getInventory().getHelmet());
        }
        if (target.getInventory().getChestplate() != null) {
            inventory.setItem(33, target.getInventory().getChestplate());
        }
        if (target.getInventory().getLeggings() != null) {
            inventory.setItem(34, target.getInventory().getLeggings());
        }
        if (target.getInventory().getBoots() != null) {
            inventory.setItem(35, target.getInventory().getBoots());
        }
        Utils.PlaySound(p, "endersee");
    }

}
