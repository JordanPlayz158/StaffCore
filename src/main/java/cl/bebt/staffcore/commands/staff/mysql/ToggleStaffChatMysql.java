package cl.bebt.staffcore.commands.staff.mysql;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.sql.queries.StaffChatQuery;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;

public class ToggleStaffChatMysql implements CommandExecutor, Listener {


    private final StaffCorePlugin plugin;

    public ToggleStaffChatMysql(StaffCorePlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("togglestaffchat").setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 1) {
                if (Bukkit.getPlayer(args[0]) instanceof Player) {
                    Player p = Bukkit.getPlayer(args[0]);
                    String is = StaffChatQuery.isStaffChat(p.getName());
                    if (is.equals("true")) {
                        Utils.tell(sender, Utils.getString("staff_chat.disabled_to", "lg", "staff").replace("%player%", p.getName()));
                        Utils.tell(p, Utils.getString("staff_chat.disabled", "lg", "staff"));
                        StaffChatQuery.disable(p.getName());
                        StaffCorePlugin.toggledStaffChat.remove(p.getName());
                        if (!Utils.isOlderVersion()) {
                            p.getPersistentDataContainer().remove(new NamespacedKey(plugin, "staffchat"));
                        }
                    } else if (is.equals("false")) {
                        Utils.tell(sender, Utils.getString("staff_chat.enabled_to", "lg", "staff").replace("%player%", p.getName()));
                        Utils.tell(p, Utils.getString("staff_chat.enabled", "lg", "staff"));
                        StaffChatQuery.enable(p.getName());
                        StaffCorePlugin.toggledStaffChat.add(p.getName());
                        if (!Utils.isOlderVersion()) {
                            p.getPersistentDataContainer().set(new NamespacedKey(plugin, "staffchat"), PersistentDataType.STRING, "staffchat");
                        }
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
                String is = StaffChatQuery.isStaffChat(p.getName());
                if (p.hasPermission("staffcore.tsc")) {
                    if (is.equals("true")) {
                        Utils.tell(p, Utils.getString("staff_chat.disabled", "lg", "staff"));
                        StaffChatQuery.disable(p.getName());
                        StaffCorePlugin.toggledStaffChat.remove(p.getName());
                        if (!Utils.isOlderVersion()) {
                            p.getPersistentDataContainer().remove(new NamespacedKey(plugin, "staffchat"));
                        }
                    } else if (is.equals("false")) {
                        Utils.tell(p, "&8[&3&lSC&r&8]&r &aOn");
                        StaffChatQuery.enable(p.getName());
                        StaffCorePlugin.toggledStaffChat.add(p.getName());
                        if (!Utils.isOlderVersion()) {
                            p.getPersistentDataContainer().set(new NamespacedKey(plugin, "staffchat"), PersistentDataType.STRING, "staffchat");
                        }
                    }
                } else {
                    Utils.tell(sender, Utils.getString("no_permission", "lg", "staff"));
                }
            } else if (args.length == 1) {
                if (Bukkit.getPlayer(args[0]) instanceof Player) {
                    if (sender.hasPermission("staffcore.tsc")) {
                        Player p = Bukkit.getPlayer(args[0]);
                        String is = StaffChatQuery.isStaffChat(p.getName());
                        if (is.equals("true")) {
                            Utils.tell(sender, Utils.getString("staff_chat.disabled_to", "lg", "staff").replace("%player%", p.getName()));
                            Utils.tell(p, Utils.getString("staff_chat.disabled", "lg", "staff"));
                            StaffChatQuery.disable(p.getName());
                            StaffCorePlugin.toggledStaffChat.remove(p.getName());
                            if (!Utils.isOlderVersion()) {
                                p.getPersistentDataContainer().remove(new NamespacedKey(plugin, "staffchat"));
                            }
                        } else if (is.equals("false")) {
                            Utils.tell(sender, Utils.getString("staff_chat.enabled_to", "lg", "staff").replace("%player%", p.getName()));
                            Utils.tell(p, Utils.getString("staff_chat.enabled", "lg", "staff"));
                            StaffChatQuery.enable(p.getName());
                            StaffCorePlugin.toggledStaffChat.add(p.getName());
                            if (!Utils.isOlderVersion()) {
                                p.getPersistentDataContainer().set(new NamespacedKey(plugin, "staffchat"), PersistentDataType.STRING, "staffchat");
                            }
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
        return true;
    }
}
