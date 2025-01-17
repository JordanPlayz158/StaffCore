package cl.bebt.staffcore.menu.menu.chat;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.menu.PaginatedMenu;
import cl.bebt.staffcore.menu.PlayerMenuUtility;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class MutePlayer extends PaginatedMenu {
    private final StaffCorePlugin plugin;

    public MutePlayer(PlayerMenuUtility playerMenuUtility, StaffCorePlugin plugin) {
        super(playerMenuUtility);
        this.plugin = plugin;
    }

    public String getMenuName() {
        return Utils.chat(Utils.getString("chat.mute_players_chat.name", "menu", null));
    }

    public int getSlots() {
        return 54;
    }

    public void handleMenu(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        ArrayList<Player> players = new ArrayList<>(Bukkit.getServer().getOnlinePlayers());
        if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(this.plugin, "mute"), PersistentDataType.STRING)) {
            p.closeInventory();
            Player jugador = p.getServer().getPlayer(e.getCurrentItem().getItemMeta().getDisplayName());
            p.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "muted_player"), PersistentDataType.STRING, jugador.getName());
            (new Amount(StaffCorePlugin.getPlayerMenuUtility(p), this.plugin, p)).open();
        } else if (e.getCurrentItem().equals(close())) {
            p.closeInventory();
        } else if (e.getCurrentItem().equals(back())) {
            if (page == 0) {
                Utils.tell(p, Utils.getString("menu.already_in_first_page", "lg", "sv"));
            } else {
                page--;
                p.closeInventory();
                open();
            }
        } else if (e.getCurrentItem().equals(next())) {
            if (index + 1 <= players.size()) {
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
        ArrayList<Player> players = new ArrayList<>(Bukkit.getServer().getOnlinePlayers());
        if (players != null && !players.isEmpty())
            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                this.index = getMaxItemsPerPage() * this.page + i;
                if (this.index >= players.size())
                    break;
                if (players.get(this.index) != null) {
                    ItemStack p_head = Utils.getPlayerHead(players.get(this.index).getName());
                    ItemMeta meta = p_head.getItemMeta();
                    ArrayList<String> lore = new ArrayList<>();
                    meta.setDisplayName(players.get(this.index).getName());
                    lore.add(Utils.chat(Utils.chat("&cMute &r") + players.get(this.index).getDisplayName()));
                    meta.setLore(lore);
                    meta.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "mute"), PersistentDataType.STRING, "mute");
                    p_head.setItemMeta(meta);
                    this.inventory.addItem(p_head);
                }
            }
    }
}
