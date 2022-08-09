package cl.bebt.staffcore.commands.time;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Night implements CommandExecutor {

    private final StaffCorePlugin plugin;

    public Night(StaffCorePlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("night").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                for (World world : Bukkit.getServer().getWorlds()) {
                    world.setTime(14000);
                    world.setThundering(false);
                    world.setStorm(false);
                }
                Bukkit.broadcastMessage(Utils.chat(Utils.getString("time.night.set_by_console", "lg", "sv")));
                return true;
            }
            Player p = (Player) sender;
            if (p.hasPermission("staffcore.night")) {
                for (World world : Bukkit.getServer().getWorlds()) {
                    world.setTime(14000);
                    world.setThundering(false);
                    world.setStorm(false);
                }
                Bukkit.broadcastMessage(Utils.chat(Utils.getString("time.night.set_by_console", "lg", "sv").replace("%player%", p.getName())));
            } else {
                Utils.tell(sender, Utils.getString("no_permission", "lg", "staff"));
            }
        } else {
            Utils.tell(sender, Utils.getString("wrong_usage", "lg", "staff").replace("%command%", "night"));
        }
        return true;
    }
}
