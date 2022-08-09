package cl.bebt.staffcore.commands.staff;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.menu.menu.reports.ReportMenu;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Report implements TabExecutor {

    public Report(StaffCorePlugin plugin) {
        plugin.getCommand("report").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!Utils.isOlderVersion()) {
            if (sender instanceof Player) {
                if (sender.hasPermission("staffcore.report")) {
                    if (args.length == 1) {
                        Player p = (Player) sender;
                        new ReportMenu(StaffCorePlugin.getPlayerMenuUtility(p), StaffCorePlugin.plugin, args[0]).open();
                    } else {
                        Utils.tell(sender, Utils.getString("wrong_usage", "lg", "staff").replace("%command%", "report <player>"));
                    }
                } else {
                    Utils.tell(sender, Utils.getString("no_permission", "lg", "sv"));
                }
            } else {
                Utils.tell(sender, Utils.getString("only_players", "lg", "sv"));
            }
        } else {
            Utils.tell(sender, Utils.getString("not_for_older_versions", "lg", "sv"));
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> version = new ArrayList<>();
        if (args.length == 1) {
            ArrayList<String> Players = Utils.getUsers();
            if (!Players.isEmpty()) {
                Players.remove(sender.getName());
                version.addAll(Players);
            }
        }
        return version;
    }
}
