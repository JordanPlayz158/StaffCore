package cl.bebt.staffcore.commands;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Suicide implements CommandExecutor {

    private final StaffCorePlugin plugin;

    public Suicide(StaffCorePlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("suicide").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("staffcore.suicide")) {
                p.setHealth(0);
            } else {
                Utils.tell(sender, Utils.getString("no_permission", "lg", "sv"));
            }
        } else {
            Utils.tell(sender, Utils.getString("only_players", "lg", "sv"));
        }

        return true;
    }
}