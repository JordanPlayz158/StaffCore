package cl.bebt.staffcore.commands.Staff;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.utils.utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Ping implements CommandExecutor {


    public Ping(StaffCorePlugin plugin) {
        plugin.getCommand("ping").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 1) {
                if (Bukkit.getPlayer(args[0]) instanceof Player) {
                    Player p = Bukkit.getPlayer(args[0]);
                    utils.tell(sender, utils.getString("ping_other", "lg", "staff").replace("%player%", args[0]).replace("%ping%", String.valueOf(utils.getPing(p))));
                } else {
                    utils.tell(sender, utils.getString("p_dont_exist", "lg", "sv"));
                }
            } else {
                utils.tell(sender, utils.getString("wrong_usage", "lg", "staff").replace("%command%", "ping | ping <player>"));
            }
        } else if (sender instanceof Player) {
            if (args.length == 0) {
                Player p = (Player) sender;
                if (p.hasPermission("staffcore.ping")) {
                    utils.tell(p, utils.getString("ping", "lg", "staff").replace("%ping%", String.valueOf(utils.getPing(p))));
                } else {
                    utils.tell(p, utils.getString("no_permission", "lg", "staff"));
                }
            } else if (args.length == 1) {
                if (sender.hasPermission("staffcore.ip")) {
                    if (Bukkit.getPlayer(args[0]) instanceof Player) {
                        Player p = Bukkit.getPlayer(args[0]);
                        utils.tell(sender, utils.getString("ping_other", "lg", "staff").replace("%player%", args[0]).replace("%ping%", String.valueOf(utils.getPing(p))));
                    } else {
                        utils.tell(sender, utils.getString("p_dont_exist", "lg", "sv"));
                    }
                } else {
                    utils.tell(sender, utils.getString("no_permission", "lg", "staff"));
                }
            } else {
                utils.tell(sender, utils.getString("wrong_usage", "lg", "staff").replace("%command%", "vanish | vanish <player>"));
            }
        }
        return true;
    }
}