package cl.bebt.staffcore.commands.staff;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.sql.queries.AltsQuery;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class CheckAlts implements TabExecutor {
    private static StaffCorePlugin plugin;

    public CheckAlts(StaffCorePlugin plugin) {
        CheckAlts.plugin = plugin;
        plugin.getCommand("alts").setExecutor(this);
    }

    public static List<String> ips(String p) {
        if (Utils.mysqlEnabled()) {
            return Utils.makeList(AltsQuery.getAlts(p));
        } else {
            return plugin.alts.getConfig().getStringList("alts." + p);
        }
    }

    public static List<String> alts(String player) {
        List<String> alts = new ArrayList<>();
        List<String> accounts = new ArrayList<>();
        if (Utils.mysqlEnabled()) {
            List<String> ip = Utils.makeList(AltsQuery.getAlts(player));
            List<String> players = AltsQuery.getPlayersNames();
            for (String key : players) {
                if (!player.equals(key)) {
                    alts.add(key);
                }
            }
            for (String alt : alts) {
                List<String> list2 = Utils.makeList(AltsQuery.getAlts(alt));
                if (ip.stream().anyMatch(list2::contains)) {
                    accounts.add(alt);
                }
            }
        } else {
            ConfigurationSection inventorySection = plugin.alts.getConfig().getConfigurationSection("alts");
            List<String> ip = StaffCorePlugin.plugin.alts.getConfig().getStringList("alts." + player);
            assert inventorySection != null;
            for (String key : inventorySection.getKeys(false)) {
                if (!player.equals(key)) {
                    alts.add(key);
                }
            }
            for (String alt : alts) {
                List<String> list2 = StaffCorePlugin.plugin.alts.getConfig().getStringList("alts." + alt);
                if (ip.stream().anyMatch(list2::contains)) {
                    accounts.add(alt);
                }
            }
        }
        return accounts;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("staffcore.alts")) {
            if (args.length == 1) {
                try {
                    List<String> alts = alts(args[0]);
                    if (alts.isEmpty()) {
                        Utils.tell(sender, Utils.getString("alts.no_alts", "lg", "staff").replace("%player%", args[0]));
                    } else {
                        Utils.tell(sender, Utils.getString("alts.alts", "lg", "staff").replace("%player%", args[0]));
                        for (String alt : alts) {
                            List<String> ips = ips(alt);
                            if (!alt.equalsIgnoreCase(args[0])) {
                                Utils.tell(sender, "&7  ► &a" + alt);
                                for (String ip : ips) {
                                    Utils.tell(sender, "&7    ► &a" + ip);
                                }
                            }
                        }
                    }
                } catch (NullPointerException error) {
                    Utils.tell(sender, Utils.getString("never_seen", "lg", "staff").replace("%player%", args[0]));
                }
            } else {
                Utils.tell(sender, Utils.getString("wrong_usage", "lg", "staff").replace("%command%", "alts <player>"));
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
