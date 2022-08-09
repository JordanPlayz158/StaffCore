package cl.bebt.staffcore.commands.staff;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.menu.PlayerMenuUtility;
import cl.bebt.staffcore.menu.menu.warnmanager.WarnManager;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Warnings implements TabExecutor {
    private final StaffCorePlugin plugin;

    public Warnings(StaffCorePlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("warningns").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!Utils.isOlderVersion()) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (sender.hasPermission("staffcore.warnings")) {
                    if (args.length == 0) {
                        new WarnManager(new PlayerMenuUtility(p), plugin).open();
                    } else if (args.length == 1) {
                        try {
                            new cl.bebt.staffcore.menu.menu.warnmanager.Warnings(new PlayerMenuUtility(p), plugin, p, args[0]).open();
                        } catch (NullPointerException ignored) {
                            Utils.tell(sender, Utils.getString("offline", "lg", "staff").replace("%player%", args[0]));
                        }
                    } else {
                        Utils.tell(sender, Utils.getString("wrong_usage", "lg", "staff").replace("%command%", "warnings <player>"));
                    }
                } else {
                    Utils.tell(sender, Utils.getString("no_permission", "lg", "staff"));
                }
            } else {
                Utils.tell(sender, Utils.getString("only_players", "lg", "staff"));
            }
        } else {
            Utils.tell(sender, Utils.getString("not_for_older_versions", "lg", "staff"));
        }
        return true;
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