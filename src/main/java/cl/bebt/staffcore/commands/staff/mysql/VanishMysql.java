package cl.bebt.staffcore.commands.staff.mysql;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.sql.queries.VanishQuery;
import cl.bebt.staffcore.utils.SetVanish;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class VanishMysql implements CommandExecutor {


    private final StaffCorePlugin plugin;

    public VanishMysql(StaffCorePlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("vanish").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!Utils.isOlderVersion()) {
            if (!(sender instanceof Player)) {
                if (args.length == 1) {
                    if (Bukkit.getPlayer(args[0]) instanceof Player) {
                        Player p = Bukkit.getPlayer(args[0]);
                        String is = VanishQuery.isVanished(p.getName());
                        if (is.equals("true")) {
                            SetVanish.setVanish(p, false);
                            Utils.tell(sender, Utils.getString("vanish.disabled_to", "lg", "staff").replace("%player%", p.getName()));
                            Utils.tell(p, Utils.getString("vanish.disabled", "lg", "staff"));
                        } else if (is.equals("false")) {
                            SetVanish.setVanish(p, true);
                            Utils.tell(sender, Utils.getString("vanish.enabled_to", "lg", "staff").replace("%player%", p.getName()));
                            Utils.tell(p, Utils.getString("vanish.enabled", "lg", "staff"));
                        }
                    } else {
                        Utils.tell(sender, Utils.getString("p_dont_exist", "lg", "sv"));
                    }
                } else {
                    Utils.tell(sender, Utils.getString("wrong_usage", "lg", "staff").replace("%command%", "vanish | vanish <player>"));
                }
            } else {
                if (sender.hasPermission("staffcore.vanish")) {
                    if (args.length == 0) {
                        Player p = (Player) sender;
                        String is = VanishQuery.isVanished(p.getName());
                        if (is.equals("true")) {
                            SetVanish.setVanish(p, false);
                            Utils.tell(p, Utils.getString("vanish.disabled", "lg", "staff"));
                        } else if (is.equals("false")) {
                            SetVanish.setVanish(p, true);
                            Utils.tell(p, Utils.getString("vanish.enabled", "lg", "staff"));
                        }
                    } else if (args.length == 1) {
                        if (Bukkit.getPlayer(args[0]) instanceof Player) {
                            Player p = Bukkit.getPlayer(args[0]);
                            String is = VanishQuery.isVanished(p.getName());
                            if (is.equals("true")) {
                                SetVanish.setVanish(p, false);
                                Utils.tell(sender, Utils.getString("vanish.disabled_to", "lg", "staff").replace("%player%", p.getName()));
                                Utils.tell(p, Utils.getString("vanish.disabled", "lg", "staff"));
                            } else if (is.equals("false")) {
                                SetVanish.setVanish(p, true);
                                Utils.tell(sender, Utils.getString("vanish.enabled_to", "lg", "staff").replace("%player%", p.getName()));
                                Utils.tell(p, Utils.getString("vanish.enabled", "lg", "staff"));
                            }
                        } else {
                            Utils.tell(sender, Utils.getString("p_dont_exist", "lg", "sv"));
                        }
                    } else {
                        Utils.tell(sender, Utils.getString("wrong_usage", "lg", "staff").replace("%command%", "vanish | vanish <player>"));
                    }
                } else {
                    Utils.tell(sender, Utils.getString("no_permission", "lg", "staff"));
                }
            }
        } else {
            Utils.tell(sender, Utils.getString("not_for_older_versions", "lg", "sv"));
        }
        return true;
    }
}

