package cl.bebt.staffcore.utils;

import cl.bebt.staffcore.msgchannel.SendMsg;
import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.sql.queries.FreezeQuery;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class FreezePlayer {

    private static final Plugin plugin = StaffCorePlugin.plugin;

    public static void freeze(Player p, String freezer, Boolean bol) {
        PersistentDataContainer PlayerData = p.getPersistentDataContainer();
        String status = "";
        if (bol) {
            p.setAllowFlight(true);
            p.setInvulnerable(true);
            Utils.PlaySound(p, "freeze");
            Utils.PlayParticle(p, "unfreeze_player");
            PlayerData.set(new NamespacedKey(plugin, "frozen"), PersistentDataType.STRING, "frozen");
            if (Utils.mysqlEnabled()) {
                FreezeQuery.enable(p.getName());
            }
            status = Utils.getString("freeze.freeze", "lg", null);
            try {
                PlayerData.set(new NamespacedKey(plugin, "frozen_helmet"), PersistentDataType.STRING, Serializer.serialize(p.getInventory().getHelmet()));
            } catch (NullPointerException ignored) {
            }
            if (Utils.getBoolean("freeze.set_ice_block")) {
                p.getInventory().setItem(39, new ItemStack(Material.BLUE_ICE));
            }
        } else {
            Utils.PlaySound(p, "un_freeze");
            Utils.PlayParticle(p, "freeze_player");
            if (!(p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR ||
                    PlayerData.has(new NamespacedKey(plugin, "vanished"), PersistentDataType.STRING) ||
                    PlayerData.has(new NamespacedKey(plugin, "flying"), PersistentDataType.STRING) ||
                    PlayerData.has(new NamespacedKey(plugin, "staff"), PersistentDataType.STRING))) {
                p.setAllowFlight(false);
                p.setInvulnerable(false);
            }
            PlayerData.remove(new NamespacedKey(plugin, "frozen"));
            if (Utils.mysqlEnabled()) {
                FreezeQuery.disable(p.getName());
            }
            status = Utils.getString("freeze.unfreeze", "lg", null);

            if (Utils.getBoolean("freeze.set_ice_block")) {
                try {
                    ItemStack helmet = Serializer.deserialize(PlayerData.get(new NamespacedKey(plugin, "frozen_helmet"), PersistentDataType.STRING));
                    p.getInventory().setHelmet(helmet);
                    PlayerData.remove(new NamespacedKey(plugin, "frozen_helmet"));
                } catch (NullPointerException ignored) {
                    p.getInventory().setHelmet(null);
                }
            }
        }
        for (Player people : Bukkit.getOnlinePlayers()) {
            if (Utils.getBoolean("alerts.freeze") || people.hasPermission("staffcore.staff")) {
                for (String key : Utils.getStringList("freeze", "alerts")) {
                    key = key.replace("%frozen%", p.getName());
                    key = key.replace("%freezer%", freezer);
                    key = key.replace("%status%", status);
                    Utils.tell(people, key);
                }
            }
        }
        SendMsg.sendFreezeAlert(freezer, p.getName(), bol, Utils.getServer());
    }

}
