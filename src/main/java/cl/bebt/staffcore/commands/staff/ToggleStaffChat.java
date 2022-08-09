package cl.bebt.staffcore.commands.staff;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ToggleStaffChat implements CommandExecutor, Listener {

    private final StaffCorePlugin plugin;

    public ToggleStaffChat(StaffCorePlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("togglestaffchat").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!Utils.isOlderVersion()) {
            if (!(sender instanceof Player)) {
                if (args.length == 1) {
                    if (Bukkit.getPlayer(args[0]) instanceof Player) {
                        Player p = Bukkit.getPlayer(args[0]);
                        PersistentDataContainer PlayerData = p.getPersistentDataContainer();
                        if (PlayerData.has(new NamespacedKey(plugin, "staffchat"), PersistentDataType.STRING)) {
                            Utils.tell(sender, Utils.getString("staff_chat.disabled_to", "lg", "staff").replace("%player%", p.getName()));
                            Utils.tell(p, Utils.getString("staff_chat.disabled", "lg", "staff"));
                            PlayerData.remove(new NamespacedKey(plugin, "staffchat"));
                        } else {
                            Utils.tell(sender, Utils.getString("staff_chat.enabled_to", "lg", "staff").replace("%player%", p.getName()));
                            Utils.tell(p, Utils.getString("staff_chat.enabled", "lg", "staff"));
                            PlayerData.set(new NamespacedKey(plugin, "staffchat"), PersistentDataType.STRING, "staffchat");
                        }
                    } else {
                        Utils.tell(sender, Utils.getString("p_dont_exist", "lg", "sv"));
                    }
                } else {
                    Utils.tell(sender, Utils.getString("wrong_usage", "lg", "staff").replace("%command%", "tsc | tsc <player>"));
                }

            } else {
                if (args.length == 0) {
                    Player p = (Player) sender;
                    PersistentDataContainer PlayerData = p.getPersistentDataContainer();
                    if (p.hasPermission("staffcore.tsc")) {
                        if (PlayerData.has(new NamespacedKey(plugin, "staffchat"), PersistentDataType.STRING)) {
                            Utils.tell(p, Utils.getString("staff_chat.disabled", "lg", "staff"));
                            PlayerData.remove(new NamespacedKey(plugin, "staffchat"));
                        } else {
                            Utils.tell(p, Utils.getString("staff_chat.enabled", "lg", "staff"));
                            PlayerData.set(new NamespacedKey(plugin, "staffchat"), PersistentDataType.STRING, "staffchat");
                        }
                    } else {
                        Utils.tell(sender, Utils.getString("no_permission", "lg", "staff"));
                    }
                } else if (args.length == 1) {
                    if (Bukkit.getPlayer(args[0]) instanceof Player) {
                        if (sender.hasPermission("staffcore.tsc")) {
                            Player p = Bukkit.getPlayer(args[0]);
                            PersistentDataContainer PlayerData = p.getPersistentDataContainer();
                            if (PlayerData.has(new NamespacedKey(plugin, "staffchat"), PersistentDataType.STRING)) {
                                Utils.tell(sender, Utils.getString("staff_chat.disabled_to", "lg", "staff").replace("%player%", p.getName()));
                                Utils.tell(p, Utils.getString("staff_chat.disabled", "lg", "staff"));
                                PlayerData.remove(new NamespacedKey(plugin, "staffchat"));
                            } else {
                                Utils.tell(sender, Utils.getString("staff_chat.enabled_to", "lg", "staff").replace("%player%", p.getName()));
                                Utils.tell(p, Utils.getString("staff_chat.enabled", "lg", "staff"));
                                PlayerData.set(new NamespacedKey(plugin, "staffchat"), PersistentDataType.STRING, "staffchat");
                            }
                        } else {
                            Utils.tell(sender, Utils.getString("no_permission", "lg", "sv"));
                        }
                    } else {
                        Utils.tell(sender, Utils.getString("p_dont_exist", "lg", "sv"));
                    }
                } else {
                    Utils.tell(sender, Utils.getString("wrong_usage", "lg", "staff").replace("%command%", "tsc | tsc <player>"));
                }
            }
        } else {
            Utils.tell(sender, Utils.getString("not_for_older_versions", "lg", "sv"));
        }
        return true;
    }

}
