package cl.bebt.staffcore.commands.staff;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.utils.WarnPlayer;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Warn implements TabExecutor {

    private final StaffCorePlugin plugin;

    public Warn(StaffCorePlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("warn").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!Utils.isOlderVersion()) {
            if (!(sender instanceof Player)) {
                Utils.tell(sender, "&cThis command can only be executed by players.");
            } else {
                if (args.length == 1) {
                    Player p = (Player) sender;
                    if (p.hasPermission("staffcore.warn")) {
                        if (Utils.isRegistered(args[0])) {
                            new WarnPlayer(p, args[0], plugin);
                        } else {
                            Utils.tell(sender, Utils.getString("never_seen", "lg", "staff").replace("%player%", args[0]));
                        }
                    } else {
                        Utils.tell(sender, Utils.getString("no_permission", "lg", "staff"));
                    }

                } else {
                    Utils.tell(sender, Utils.getString("wrong_usage", "lg", "staff").replace("%command%", "warn <player>"));
                }
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
            } else {
                Utils.tell(sender, Utils.getString("no_players_saved", "lg", "staff"));
            }
        }
        return version;
    }
}
