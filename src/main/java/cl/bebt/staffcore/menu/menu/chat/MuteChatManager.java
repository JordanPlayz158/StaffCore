package cl.bebt.staffcore.menu.menu.chat;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.menu.MenuC;
import cl.bebt.staffcore.menu.PlayerMenuUtility;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class MuteChatManager extends MenuC {
    private final StaffCorePlugin plugin;

    public MuteChatManager(PlayerMenuUtility playerMenuUtility, StaffCorePlugin plugin) {
        super(playerMenuUtility);
        this.plugin = plugin;
    }

    @Override
    public String getMenuName() {
        return Utils.chat(Utils.getString("chat.mute_chat_manager.name", "menu", null));
    }

    @Override
    public int getSlots() {
        return 45;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "Head"), PersistentDataType.STRING)) {
            p.closeInventory();
            new MutePlayer(StaffCorePlugin.getPlayerMenuUtility(p), plugin).open();
            p.updateInventory();
            e.setCancelled(true);
        } else if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "panel"), PersistentDataType.STRING)) {
            e.setCancelled(true);
        } else if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "TChatOn"), PersistentDataType.STRING)) {
            p.closeInventory();
            plugin.chatMuted = true;
            new MuteChatManager(StaffCorePlugin.getPlayerMenuUtility(p), plugin).open(p);
            Bukkit.broadcastMessage(Utils.chat(Utils.getString("toggle_chat.global_mute_by_player", "lg", "sv").replace("%player%", p.getName())));
            e.setCancelled(true);
        } else if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "TChatOff"), PersistentDataType.STRING)) {
            p.closeInventory();
            plugin.chatMuted = false;
            new MuteChatManager(StaffCorePlugin.getPlayerMenuUtility(p), plugin).open(p);
            Bukkit.broadcastMessage(Utils.chat(Utils.getString("toggle_chat.global_un_mute_by_player", "lg", "sv").replace("%player%", p.getName())));
            e.setCancelled(true);
        } else if (e.getCurrentItem().equals(close())) {
            p.closeInventory();
            if (e.getClick().isLeftClick()) {
                new ChatManager(StaffCorePlugin.getPlayerMenuUtility(p), StaffCorePlugin.plugin).open(p);
            }
        }
    }

    @Override
    public void setMenuItemsPlayer(Player p) {
        ArrayList<String> lore = new ArrayList<>();

        ItemStack TChatOn = new ItemStack(Material.ENDER_EYE, 1);
        ItemStack TChatOff = new ItemStack(Material.ENDER_EYE, 1);
        ItemStack Head = Utils.getPlayerHead(p.getName());

        ItemMeta metaTChatOn = TChatOn.getItemMeta();
        ItemMeta metaTChatOff = TChatOff.getItemMeta();
        ItemMeta metaHead = Head.getItemMeta();

        metaTChatOn.setDisplayName(Utils.chat("&8Current Chat: &aNormal"));
        metaTChatOff.setDisplayName(Utils.chat("&8Current Chat: &cMuted"));
        metaHead.setDisplayName(Utils.chat("&cMute Player Chat"));

        lore.add(Utils.chat("&7Click to &cMUTE &7the Chat."));
        metaTChatOn.setLore(lore);
        lore.clear();
        lore.add(Utils.chat("&7Click to &aUnMute &7the Chat."));
        metaTChatOff.setLore(lore);
        lore.clear();
        lore.add(Utils.chat("&7Click to &cMute &7a specific player chat."));
        metaHead.setLore(lore);

        metaTChatOff.addEnchant(Enchantment.MENDING, 1, false);
        metaTChatOff.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        metaTChatOn.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "TChatOn"), PersistentDataType.STRING, "TChatOn");
        metaTChatOff.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "TChatOff"), PersistentDataType.STRING, "TChatOff");
        metaHead.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "Head"), PersistentDataType.STRING, "Head");

        TChatOn.setItemMeta(metaTChatOn);
        TChatOff.setItemMeta(metaTChatOff);
        Head.setItemMeta(metaHead);

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
        if (!plugin.chatMuted) {
            inventory.setItem(20, TChatOn);
        } else if (plugin.chatMuted) {
            inventory.setItem(20, TChatOff);
        }
        inventory.setItem(21, super.redPanel());
        inventory.setItem(22, close());
        inventory.setItem(23, super.redPanel());
        inventory.setItem(24, Head);
        inventory.setItem(25, super.redPanel());
    }
}
