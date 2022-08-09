package cl.bebt.staffcore.commands.staff;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChat implements CommandExecutor {

    private final StaffCorePlugin plugin;

    public ClearChat(StaffCorePlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("clearchat").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("staffcore.clearchat")) {
            if (args.length == 0) {
                Utils.ccAll();
                Bukkit.broadcastMessage(Utils.chat(Utils.getString("clear_chat.global", "lg", "sv").replace("%player%", sender.getName())));
            } else if (args.length == 1) {
                if (Bukkit.getPlayer(args[0]) instanceof Player) {
                    Player p = Bukkit.getPlayer(args[0]);
                    if (sender.getName().equals(p.getName())) {
                        Utils.ccPlayer(p);
                        Utils.tell(sender, Utils.getString("clear_chat.own", "lg", "sv"));
                    } else {
                        Utils.ccPlayer(p);
                        Utils.tell(sender, Utils.getString("clear_chat.global", "lg", "sv").replace("%player%", sender.getName()));
                        Utils.tell(p, Utils.getString("clear_chat.player", "lg", "sv"));

                    }
                } else {
                    Utils.tell(sender, Utils.chat(Utils.getString("p_dont_exist", "lg", "sv")));
                }
            } else {
                Utils.tell(sender, Utils.getString("wrong_usage", "lg", "staff").replace("%command%", "cc | cc <player>"));
            }
        } else {
            Utils.tell(sender, Utils.chat(Utils.getString("no_permission", "lg", "staff")));
        }
        return true;
    }


}