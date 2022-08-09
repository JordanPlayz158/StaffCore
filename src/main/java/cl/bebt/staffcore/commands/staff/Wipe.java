package cl.bebt.staffcore.commands.staff;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.utils.Utils;
import cl.bebt.staffcore.utils.wipePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Wipe implements TabExecutor {

    private final StaffCorePlugin plugin;

    public Wipe(StaffCorePlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("wipe").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("staffcore.wipe")) {
                if (args.length == 1) {
                    if (Utils.isRegistered(args[0])) {
                        new wipePlayer(sender, args[0]);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                            try {
                                Utils.tell(Bukkit.getPlayer(args[0]), Utils.getString("wipe.account_wiped", "lg", "staff"));
                                String ban_msg = "\n";
                                for (String msg : Utils.getStringList("wipe.wipe_kick_msg", "alerts")) {
                                    msg = msg.replace("%wiper%", p.getName());
                                    msg = msg.replace("%wiped%", args[0]);
                                    ban_msg = ban_msg + msg + "\n";
                                }
                                Bukkit.getPlayer(args[0]).kickPlayer(Utils.chat(ban_msg));
                            } catch (NullPointerException ignored) {
                            }
                        }, 6L);
                    } else {
                        Utils.tell(sender, Utils.getString("never_seen", "lg", "staff").replace("%player%", args[0]));
                    }
                } else {
                    Utils.tell(sender, Utils.getString("wrong_usage", "lg", "staff").replace("%command%", "wipe <player>"));
                }
            } else {
                Utils.tell(sender, Utils.getString("no_permission", "lg", "staff"));
            }
        } else {
            if (args.length == 1) {
                if (Utils.isRegistered(args[0])) {
                    new wipePlayer(sender, args[0]);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        try {
                            Utils.tell(Bukkit.getPlayer(args[0]), Utils.getString("wipe.account_wiped", "lg", "staff"));
                            String ban_msg = "\n";
                            for (String msg : Utils.getStringList("wipe.wipe_kick_msg", "alerts")) {
                                msg = msg.replace("%wiper%", "CONSOLE");
                                msg = msg.replace("%wiped%", args[0]);
                                ban_msg = ban_msg + msg + "\n";
                            }
                            Bukkit.getPlayer(args[0]).kickPlayer(Utils.chat(ban_msg));
                        } catch (NullPointerException ignored) {
                        }
                    }, 6L);
                } else {
                    Utils.tell(sender, Utils.getString("never_seen", "lg", "staff").replace("%player%", args[0]));
                }
            } else {
                Utils.tell(sender, Utils.getString("wrong_usage", "lg", "staff").replace("%command%", "wipe <player>"));
            }
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
