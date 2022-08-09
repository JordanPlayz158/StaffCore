package cl.bebt.staffcore.commands.staff;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.menu.menu.reports.ReportManager;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportList implements CommandExecutor {

    public ReportList(StaffCorePlugin plugin) {
        plugin.getCommand("reportlist").setExecutor(this);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!Utils.isOlderVersion()) {
            if (sender instanceof Player) {
                if (sender.hasPermission("staffcore.reportlist")) {
                    Player p = (Player) sender;
                    new ReportManager(StaffCorePlugin.getPlayerMenuUtility(p), StaffCorePlugin.plugin).open();
                } else {
                    Utils.tell(sender, Utils.getString("no_permission", "lg", "staff"));
                }
            } else {
                Utils.tell(sender, Utils.getString("only_players", "lg", "sv"));
            }
        } else {
            Utils.tell(sender, Utils.getString("not_for_older_versions", "lg", "sv"));
        }
        return false;
    }

}
