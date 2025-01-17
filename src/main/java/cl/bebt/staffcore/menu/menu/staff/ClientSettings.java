package cl.bebt.staffcore.menu.menu.staff;

import cl.bebt.staffcore.api.StaffCoreAPI;
import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.menu.MenuC;
import cl.bebt.staffcore.menu.PlayerMenuUtility;
import cl.bebt.staffcore.menu.menu.chat.ChatSettings;
import cl.bebt.staffcore.menu.menu.chat.MuteChatManager;
import cl.bebt.staffcore.sql.queries.StaffChatQuery;
import cl.bebt.staffcore.utils.Items;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class ClientSettings extends MenuC {

    private final StaffCorePlugin plugin;

    public ClientSettings(PlayerMenuUtility playerMenuUtility, StaffCorePlugin plugin) {
        super(playerMenuUtility);
        this.plugin = plugin;
    }

    public String getMenuName() {
        return Utils.chat(Utils.getString("others.client_settings.name", "menu", null));
    }

    public int getSlots() {
        return 45;
    }

    public void handleMenu(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        PersistentDataContainer PlayerData = p.getPersistentDataContainer();
        if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(this.plugin, "mcmanager"), PersistentDataType.STRING)) {
            p.closeInventory();
            new MuteChatManager(StaffCorePlugin.getPlayerMenuUtility(p), this.plugin).open(p);
            p.updateInventory();
            e.setCancelled(true);
        } else if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(this.plugin, "panel"), PersistentDataType.STRING)) {
            e.setCancelled(true);
        } else if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(this.plugin, "TStaffOn"), PersistentDataType.STRING)) {
            p.closeInventory();
            PlayerData.remove(new NamespacedKey(this.plugin, "staffchat"));
            if (Utils.mysqlEnabled())
                StaffChatQuery.disable(p.getName());
            Utils.tell(p, "&8[&3&lSC&r&8]&r &cOff");
            new ClientSettings(StaffCorePlugin.getPlayerMenuUtility(p), this.plugin).open(p);
            p.updateInventory();
            e.setCancelled(true);
        } else if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(this.plugin, "TStaffOff"), PersistentDataType.STRING)) {
            p.closeInventory();
            PlayerData.set(new NamespacedKey(this.plugin, "staffchat"), PersistentDataType.STRING, "staffchat");
            if (Utils.mysqlEnabled())
                StaffChatQuery.enable(p.getName());
            Utils.tell(p, "&8[&3&lSC&r&8]&r &aOn");
            new ClientSettings(StaffCorePlugin.getPlayerMenuUtility(p), this.plugin).open(p);
            p.updateInventory();
            e.setCancelled(true);
        } else if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(this.plugin, "TrollModeOn"), PersistentDataType.STRING)) {
            p.closeInventory();
            StaffCoreAPI.setTrollMode(p, !StaffCoreAPI.getTrollStatus(p.getName()));
            Utils.tell(p, Utils.getString("troll.disabled", "lg", "staff"));
            new ClientSettings(StaffCorePlugin.getPlayerMenuUtility(p), this.plugin).open(p);
            p.updateInventory();
            e.setCancelled(true);
        } else if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(this.plugin, "TrollModeOff"), PersistentDataType.STRING)) {
            p.closeInventory();
            StaffCoreAPI.setTrollMode(p, !StaffCoreAPI.getTrollStatus(p.getName()));
            Utils.tell(p, Utils.getString("troll.enabled", "lg", "staff"));
            new ClientSettings(StaffCorePlugin.getPlayerMenuUtility(p), this.plugin).open(p);
            p.updateInventory();
            e.setCancelled(true);
        } else if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(this.plugin, "ccmanager"), PersistentDataType.STRING)) {
            p.closeInventory();
            new ChatSettings(StaffCorePlugin.getPlayerMenuUtility(p), this.plugin).open(p);
            e.setCancelled(true);
        } else if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(this.plugin, "FakeJoinOrLeave"), PersistentDataType.STRING)) {
            p.closeInventory();
            if (p.getPersistentDataContainer().has(new NamespacedKey(plugin, "FakeJoinOrLeave"), PersistentDataType.STRING)) {
                p.getPersistentDataContainer().remove(new NamespacedKey(plugin, "FakeJoinOrLeave"));
                Utils.tell(p, Utils.getString("fake_join_leave_msg.disabled", "lg", "staff"));
            } else {
                p.getPersistentDataContainer().set(new NamespacedKey(plugin, "FakeJoinOrLeave"), PersistentDataType.STRING, "FakeJoinOrLeave");
                Utils.tell(p, Utils.getString("fake_join_leave_msg.enabled", "lg", "staff"));
            }
            new ClientSettings(StaffCorePlugin.getPlayerMenuUtility(p), this.plugin).open(p);
        } else if (e.getCurrentItem().equals(close())) {
            p.closeInventory();
            if (e.getClick().isLeftClick()) {
                new ServerManager(StaffCorePlugin.getPlayerMenuUtility(p), this.plugin).open(p);
                e.setCancelled(true);
            }
        }
    }

    public void setMenuItemsPlayer(Player p) {
        ArrayList<String> lore = new ArrayList<>();
        ItemStack TStaffChatOn = new ItemStack(Material.ENDER_EYE, 1);
        ItemStack TStaffChatOff = new ItemStack(Material.ENDER_EYE, 1);
        ItemStack TrollModeOn = new ItemStack(Material.TRIDENT, 1);
        ItemStack TrollModeOff = new ItemStack(Material.TRIDENT, 1);

        ItemMeta metaTStaffOn = TStaffChatOn.getItemMeta();
        ItemMeta metaTStaffOff = TStaffChatOff.getItemMeta();
        ItemMeta metaTrollModeOn = TrollModeOn.getItemMeta();
        ItemMeta metaTrollModeOff = TrollModeOff.getItemMeta();

        metaTStaffOn.setDisplayName(Utils.chat("&8Staff Chat &aOn"));
        metaTStaffOff.setDisplayName(Utils.chat("&8Staff Chat &cOff"));
        metaTrollModeOn.setDisplayName(Utils.chat("&8Troll Mode &aOn"));
        metaTrollModeOff.setDisplayName(Utils.chat("&8Troll Mode &cOff"));

        lore.add(Utils.chat("&7Click to turn &cOFF &7the Staff Chat."));
        metaTStaffOn.setLore(lore);
        lore.clear();
        lore.add(Utils.chat("&7Click to turn &aON &7the Staff Chat."));
        metaTStaffOff.setLore(lore);
        lore.clear();

        lore.add(Utils.chat("&7Click to turn &cOFF &7the Troll Mode."));
        metaTrollModeOn.setLore(lore);
        lore.clear();
        lore.add(Utils.chat("&7Click to turn &aON &7the Troll Mode."));
        metaTrollModeOff.setLore(lore);
        lore.clear();

        metaTStaffOn.addEnchant(Enchantment.MENDING, 1, false);
        metaTStaffOn.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        metaTrollModeOn.addEnchant(Enchantment.MENDING, 1, false);
        metaTrollModeOn.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        metaTStaffOn.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "TStaffOn"), PersistentDataType.STRING, "TStaffOn");
        metaTStaffOff.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "TStaffOff"), PersistentDataType.STRING, "TStaffOff");
        metaTrollModeOn.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "TrollModeOn"), PersistentDataType.STRING, "TrollModeOn");
        metaTrollModeOff.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "TrollModeOff"), PersistentDataType.STRING, "TrollModeOff");

        TStaffChatOn.setItemMeta(metaTStaffOn);
        TStaffChatOff.setItemMeta(metaTStaffOff);
        TrollModeOn.setItemMeta(metaTrollModeOn);
        TrollModeOff.setItemMeta(metaTrollModeOff);

        int i;
        for (i = 0; i < 10; i++) {
            if (this.inventory.getItem(i) == null)
                this.inventory.setItem(i, bluePanel());
        }
        for (i = 10; i < 17; i++) {
            if (this.inventory.getItem(i) == null)
                this.inventory.setItem(i, redPanel());
        }
        this.inventory.setItem(17, bluePanel());
        this.inventory.setItem(18, bluePanel());
        this.inventory.setItem(19, redPanel());
        this.inventory.setItem(25, redPanel());
        this.inventory.setItem(26, bluePanel());
        this.inventory.setItem(27, bluePanel());
        for (i = 28; i < 35; i++) {
            if (this.inventory.getItem(i) == null)
                this.inventory.setItem(i, redPanel());
        }
        for (i = 35; i < 45; i++) {
            if (this.inventory.getItem(i) == null)
                this.inventory.setItem(i, bluePanel());
        }
        if (p.getPersistentDataContainer().has(new NamespacedKey(StaffCorePlugin.plugin, "staffchat"), PersistentDataType.STRING)) {
            this.inventory.setItem(20, TStaffChatOn);
        } else if (!p.getPersistentDataContainer().has(new NamespacedKey(StaffCorePlugin.plugin, "staffchat"), PersistentDataType.STRING)) {
            this.inventory.setItem(20, TStaffChatOff);
        }
        if (StaffCoreAPI.getTrollStatus(p.getName())) {
            this.inventory.setItem(22, TrollModeOn);
        } else {
            this.inventory.setItem(22, TrollModeOff);
        }
        this.inventory.setItem(21, redPanel());
        this.inventory.setItem(23, redPanel());
        if (Utils.getBoolean("alerts.fake_join_leave_msg")) {
            this.inventory.setItem(24, Items.FakeJoinOrLeave(p.getPersistentDataContainer().has(new NamespacedKey(plugin, "FakeJoinOrLeave"), PersistentDataType.STRING)));
        } else {
            this.inventory.setItem(24, Items.ComingSoon());
        }

        this.inventory.setItem(25, redPanel());
        this.inventory.setItem(31, close());
    }
}
