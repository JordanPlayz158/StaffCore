package cl.bebt.staffcore.commands.staff;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.utils.TpPlayers;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpAll implements CommandExecutor {

    public TpAll(StaffCorePlugin plugin) {
        plugin.getCommand("tpall").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 1) {
                TpPlayers.tpAll(sender, args[0]);
            } else {
                Utils.tell(sender, Utils.getString("no_permission", "lg", "tpall <player>"));
            }
        } else {
            Player p = (Player) sender;
            if (p.hasPermission("staffcore.tp.all")) {
                if (args.length == 0) {
                    TpPlayers.tpAll(p, p.getName());
                    return true;
                } else if (args.length == 1) {
                    TpPlayers.tpAll(p, args[0]);
                } else {
                    Utils.tell(sender, Utils.getString("no_permission", "lg", "tpall | tpall <player>"));
                }
            } else {
                Utils.tell(sender, Utils.getString("no_permission", "lg", "staff"));
            }
        }
        return true;
    }
}
