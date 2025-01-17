package cl.bebt.staffcore.commands.staff;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Heal implements CommandExecutor {

    private final StaffCorePlugin plugin;

    public Heal(StaffCorePlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("heal").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 1) {
                if (Bukkit.getPlayer(args[0]) instanceof Player) {
                    Player p = Bukkit.getPlayer(args[0]);
                    p.setFoodLevel(20);
                    p.setHealth(20);
                    p.setSaturation(5f);
                    Utils.tell(sender, Utils.getString("heal.heal_to", "lg", "sv").replace("%player%", p.getName()));
                    Utils.tell(p, Utils.getString("heal.heal", "lg", "sv"));
                }
            } else {
                Utils.tell(sender, Utils.getString("wrong_usage", "lg", "staff").replace("%command%", "heal <player>"));
            }
        } else {
            Player p = (Player) sender;
            if (p.hasPermission("staffcore.heal")) {
                if (args.length == 0) {
                    p.setFoodLevel(20);
                    p.setHealth(20);
                    p.setSaturation(5f);
                    Utils.tell(p, Utils.getString("heal.heal", "lg", "sv"));
                } else if (args.length == 1) {
                    if (Bukkit.getPlayer(args[0]) instanceof Player) {
                        Player target = Bukkit.getPlayer(args[0]);
                        if (target == p) {
                            p.setFoodLevel(20);
                            p.setHealth(20);
                            p.setSaturation(5f);
                            Utils.tell(p, Utils.getString("heal.heal", "lg", "sv"));
                        } else {
                            target.setFoodLevel(20);
                            target.setHealth(20);
                            target.setSaturation(5f);
                            Utils.tell(p, Utils.getString("heal.heal_to", "lg", "sv").replace("%player%", p.getName()));
                            Utils.tell(target, Utils.getString("heal.heal", "lg", "sv"));
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
        return true;
    }

}