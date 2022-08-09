package cl.bebt.staffcore.commands.staff;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.utils.SetStaffItems;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;


public class Staff implements CommandExecutor {
    private final StaffCorePlugin plugin;

    public Staff(StaffCorePlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("staff").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!Utils.isOlderVersion()) {
            if (!(sender instanceof Player)) {
                if (args.length == 1) {
                    if (Bukkit.getPlayer(args[0]) instanceof Player) {
                        Player p = Bukkit.getPlayer(args[0]);
                        PersistentDataContainer PlayerData = p.getPersistentDataContainer();
                        if (PlayerData.has(new NamespacedKey(plugin, "staff"), PersistentDataType.STRING)) {
                            SetStaffItems.Off(p);
                            Utils.tell(sender, Utils.getString("staff.disabled_to", "lg", "staff").replace("%player%", p.getName()));
                        } else if (!(PlayerData.has(new NamespacedKey(plugin, "staff"), PersistentDataType.STRING))) {
                            SetStaffItems.On(p);
                            Utils.tell(sender, Utils.getString("staff.enabled_to", "lg", "staff").replace("%player%", p.getName()));
                        }
                    }
                } else {
                    Utils.tell(sender, Utils.getString("wrong_usage", "lg", "staff").replace("%command%", "staff | staff <player>"));
                }
            } else {
                if (args.length == 0) {
                    Player p = (Player) sender;
                    PersistentDataContainer PlayerData = p.getPersistentDataContainer();
                    if (p.hasPermission("staffcore.staff")) {
                        if (PlayerData.has(new NamespacedKey(plugin, "staff"), PersistentDataType.STRING)) {
                            SetStaffItems.Off(p);
                            Utils.tell(sender, Utils.getString("staff.disabled", "lg", "staff"));
                        } else if (!(PlayerData.has(new NamespacedKey(plugin, "staff"), PersistentDataType.STRING))) {
                            SetStaffItems.On(p);
                            Utils.tell(sender, Utils.getString("staff.enabled", "lg", "staff"));
                        }
                    } else {
                        Utils.tell(sender, Utils.getString("no_permission", "lg", "staff"));
                    }
                } else if (args.length == 1) {
                    if (sender.hasPermission("staffcore.staff")) {
                        if (Bukkit.getPlayer(args[0]) instanceof Player) {
                            Player p = Bukkit.getPlayer(args[0]);
                            PersistentDataContainer PlayerData = p.getPersistentDataContainer();
                            if (PlayerData.has(new NamespacedKey(plugin, "staff"), PersistentDataType.STRING)) {
                                SetStaffItems.Off(p);
                                Utils.tell(sender, Utils.getString("staff.disabled_to", "lg", "staff").replace("%player%", p.getName()));
                                Utils.tell(p, Utils.getString("staff.disabled", "lg", "staff"));
                            } else if (!(PlayerData.has(new NamespacedKey(plugin, "staff"), PersistentDataType.STRING))) {
                                SetStaffItems.On(p);
                                Utils.tell(sender, Utils.getString("staff.enabled_to", "lg", "staff").replace("%player%", p.getName()));
                                Utils.tell(p, Utils.getString("staff.enabled", "lg", "staff"));
                            }
                        } else {
                            Utils.tell(sender, Utils.getString("p_dont_exist", "lg", "sv"));
                        }
                    } else {
                        Utils.tell(sender, Utils.getString("no_permission", "lg", "staff"));
                    }
                } else {
                    Utils.tell(sender, Utils.getString("wrong_usage", "lg", "staff").replace("%command%", "staff | staff <player>"));
                }
            }
        } else {
            Utils.tell(sender, Utils.getString("not_for_older_versions", "lg", "sv"));
        }
        return true;
    }

}