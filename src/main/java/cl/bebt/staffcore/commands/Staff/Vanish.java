package cl.bebt.staffcore.commands.Staff;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.utils.SetVanish;
import cl.bebt.staffcore.utils.utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;


public class Vanish implements CommandExecutor {

    private final StaffCorePlugin plugin;

    public Vanish(StaffCorePlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("vanish").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!utils.isOlderVersion()) {
            if (!(sender instanceof Player)) {
                if (args.length == 1) {
                    if (Bukkit.getPlayer(args[0]) instanceof Player) {
                        Player p = Bukkit.getPlayer(args[0]);
                        PersistentDataContainer PlayerData = p.getPersistentDataContainer();
                        if (PlayerData.has(new NamespacedKey(plugin, "vanished"), PersistentDataType.STRING)) {
                            SetVanish.setVanish(p, false);
                            utils.tell(sender, utils.getString("vanish.disabled_to", "lg", "staff").replace("%player%", p.getName()));
                            utils.tell(p, utils.getString("vanish.disabled", "lg", "staff"));
                        } else {
                            SetVanish.setVanish(p, true);
                            utils.tell(sender, utils.getString("vanish.enabled_to", "lg", "staff").replace("%player%", p.getName()));
                            utils.tell(p, utils.getString("vanish.enabled", "lg", "staff"));
                        }
                    }
                } else {
                    utils.tell(sender, utils.getString("wrong_usage", "lg", "staff").replace("%command%", "vanish | vanish <player>"));
                }
            } else {
                if (sender.hasPermission("staffcore.vanish")) {
                    if (args.length == 0) {
                        Player p = (Player) sender;
                        PersistentDataContainer PlayerData = p.getPersistentDataContainer();
                        if (PlayerData.has(new NamespacedKey(plugin, "vanished"), PersistentDataType.STRING)) {
                            SetVanish.setVanish(p, false);
                            utils.tell(p, utils.getString("vanish.disabled", "lg", "staff"));
                        } else if (!(PlayerData.has(new NamespacedKey(plugin, "vanished"), PersistentDataType.STRING))) {
                            SetVanish.setVanish(p, true);
                            utils.tell(p, utils.getString("vanish.enabled", "lg", "staff"));
                        }
                    } else if (args.length == 1) {
                        if (Bukkit.getPlayer(args[0]) instanceof Player) {
                            Player p = Bukkit.getPlayer(args[0]);
                            if (!p.hasPermission("staffcore.vanish")) {
                                if (p.getPersistentDataContainer().has(new NamespacedKey(plugin, "vanished"), PersistentDataType.STRING)) {
                                    SetVanish.setVanish(p, false);
                                    utils.tell(sender, utils.getString("vanish.disabled_to", "lg", "staff").replace("%player%", p.getName()));
                                    utils.tell(p, utils.getString("vanish.disabled", "lg", "staff"));
                                } else if (!p.getPersistentDataContainer().has(new NamespacedKey(plugin, "vanished"), PersistentDataType.STRING)) {
                                    SetVanish.setVanish(p, true);
                                    utils.tell(sender, utils.getString("vanish.enabled_to", "lg", "staff").replace("%player%", p.getName()));
                                    utils.tell(p, utils.getString("vanish.enabled", "lg", "staff"));
                                }
                            } else {
                                utils.tell(sender, utils.getString("no_permission", "lg", "staff"));
                            }
                        } else {
                            utils.tell(sender, utils.getString("p_dont_exist", "lg", "sv"));
                        }
                    } else {
                        utils.tell(sender, utils.getString("wrong_usage", "lg", "staff").replace("%command%", "vanish | vanish <player>"));
                    }
                } else {
                    utils.tell(sender, utils.getString("no_permission", "lg", "staff"));
                }
            }
        } else {
            utils.tell(sender, utils.getString("not_for_older_versions", "lg", "sv"));
        }
        return true;
    }
}


