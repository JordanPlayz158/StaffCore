package cl.bebt.staffcore.commands.staff;

import cl.bebt.staffcore.msgchannel.SendMsg;
import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpOp implements CommandExecutor {

    public HelpOp(StaffCorePlugin plugin) {
        plugin.getCommand("helpop").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("staffcore.helpop")) {
                if (args.length >= 1) {
                    Player player = (Player) sender;
                    StringBuilder reason = new StringBuilder();
                    for (String arg : args) {
                        reason.append(arg).append(" ");
                    }
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.hasPermission("staffcore.staff")) {
                            if (p != sender) {
                                Utils.tell(p, Utils.getString("helpop.tostaff", "lg", null).replace("%player%", player.getName()) + reason);
                                Utils.PlaySound(p, "helpop");
                            }
                        }
                    }
                    Utils.tell(sender, Utils.getString("helpop.tousers", "lg", null));
                    SendMsg.helpOp(sender.getName(), String.valueOf(reason), Utils.getString("bungeecord.server"));
                } else {
                    Utils.tell(sender, Utils.getString("wrong_usage", "lg", "sv").replace("%command%", "helpop <reason>"));
                }
            } else {
                Utils.tell(sender, Utils.getString("no_permission", "lg", "sv"));
            }
        } else {
            Utils.tell(sender, Utils.getString("only_players", "lg", "sv"));
        }
        return false;
    }
}
