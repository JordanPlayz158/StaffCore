package cl.bebt.staffcore.commands.staff;

import cl.bebt.staffcore.api.StaffCoreAPI;
import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class TrollMode implements CommandExecutor {


    public TrollMode(StaffCorePlugin plugin) {
        plugin.getCommand("troll").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Utils.isOlderVersion()) {
            if (sender instanceof Player) {
                if (args.length == 0) {
                    Player p = (Player) sender;
                    if (p.hasPermission("staffcore.troll")) {
                        StaffCoreAPI.setTrollMode(p, !StaffCoreAPI.getTrollStatus(p.getName()));
                        if (StaffCoreAPI.getTrollStatus(p.getName())) {
                            Utils.tell(p, Utils.getString("troll.enabled", "lg", "staff"));
                        } else {
                            Utils.tell(p, Utils.getString("troll.disabled", "lg", "staff"));
                        }
                    } else {
                        Utils.tell(p, Utils.getString("no_permission", "lg", "staff"));
                    }
                } else if (args.length == 1) {
                    if (Bukkit.getPlayer(args[0]) != null) {
                        if (sender.hasPermission("staffcore.troll")) {
                            Player target = Bukkit.getPlayer(args[0]);
                            StaffCoreAPI.setTrollMode(target, !StaffCoreAPI.getTrollStatus(args[0]));
                            if (StaffCoreAPI.getTrollStatus(args[0])) {
                                if (target != sender) {
                                    Utils.tell(target, Utils.getString("troll.enabled", "lg", "staff"));
                                }
                                Utils.tell(sender, Utils.getString("troll.enabled_to", "lg", "staff").replace("%player%", args[0]));
                            } else {
                                if (target != sender) {
                                    Utils.tell(target, Utils.getString("troll.disabled", "lg", "staff"));
                                }
                                Utils.tell(sender, Utils.getString("troll.disabled_to", "lg", "staff").replace("%player%", args[0]));
                            }
                        } else {
                            Utils.tell(sender, Utils.getString("no_permission", "lg", "staff"));
                        }
                    }
                } else {
                    Utils.tell(sender, Utils.getString("wrong_usage", "lg", "staff").replace("%command%", "troll | troll <player>"));
                }
            } else {
                if (args.length == 1) {
                    if (Bukkit.getPlayer(args[0]) != null) {
                        Player target = Bukkit.getPlayer(args[0]);
                        StaffCoreAPI.setTrollMode(target, !StaffCoreAPI.getTrollStatus(args[0]));
                        if (StaffCoreAPI.getTrollStatus(args[0])) {
                            if (target != sender) {
                                Utils.tell(target, Utils.getString("troll.enabled", "lg", "staff"));
                            }
                            Utils.tell(sender, Utils.getString("troll.enabled_to", "lg", "staff").replace("%player%", args[0]));
                        } else {
                            if (target != sender) {
                                Utils.tell(target, Utils.getString("troll.disabled", "lg", "staff"));
                            }
                            Utils.tell(sender, Utils.getString("troll.disabled_to", "lg", "staff").replace("%player%", args[0]));
                        }
                    }
                } else {
                    Utils.tell(sender, Utils.getString("wrong_usage", "lg", "staff").replace("%command%", "troll | troll <player>"));
                }
            }
        } else {
            Utils.tell(sender, Utils.getString("not_for_older_versions", "lg", "sv"));
        }
        return false;
    }
}
