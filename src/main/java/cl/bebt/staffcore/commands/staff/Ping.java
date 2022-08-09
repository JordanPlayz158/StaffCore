package cl.bebt.staffcore.commands.staff;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.utils.Utils;
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
                    Utils.tell(sender, Utils.getString("ping_other", "lg", "staff").replace("%player%", args[0]).replace("%ping%", String.valueOf(Utils.getPing(p))));
                } else {
                    Utils.tell(sender, Utils.getString("p_dont_exist", "lg", "sv"));
                }
            } else {
                Utils.tell(sender, Utils.getString("wrong_usage", "lg", "staff").replace("%command%", "ping | ping <player>"));
            }
        } else if (sender instanceof Player) {
            if (args.length == 0) {
                Player p = (Player) sender;
                if (p.hasPermission("staffcore.ping")) {
                    Utils.tell(p, Utils.getString("ping", "lg", "staff").replace("%ping%", String.valueOf(Utils.getPing(p))));
                } else {
                    Utils.tell(p, Utils.getString("no_permission", "lg", "staff"));
                }
            } else if (args.length == 1) {
                if (sender.hasPermission("staffcore.ip")) {
                    if (Bukkit.getPlayer(args[0]) instanceof Player) {
                        Player p = Bukkit.getPlayer(args[0]);
                        Utils.tell(sender, Utils.getString("ping_other", "lg", "staff").replace("%player%", args[0]).replace("%ping%", String.valueOf(Utils.getPing(p))));
                    } else {
                        Utils.tell(sender, Utils.getString("p_dont_exist", "lg", "sv"));
                    }
                } else {
                    Utils.tell(sender, Utils.getString("no_permission", "lg", "staff"));
                }
            } else {
                Utils.tell(sender, Utils.getString("wrong_usage", "lg", "staff").replace("%command%", "vanish | vanish <player>"));
            }
        }
        return true;
    }
}