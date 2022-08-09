package cl.bebt.staffcore.commands.staff;

import cl.bebt.staffcore.msgchannel.SendMsg;
import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.menu.PlayerMenuUtility;
import cl.bebt.staffcore.menu.menu.staff.StaffListGui;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffList implements CommandExecutor {

    private final StaffCorePlugin plugin;

    public StaffList(StaffCorePlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("stafflist").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!Utils.isOlderVersion()) {
            if (sender instanceof Player) {
                if (args.length == 0) {
                    Player p = (Player) sender;
                    if (p.hasPermission("staffcore.stafflist")) {
                        if (Utils.getBoolean("bungeecord.enabled")) {
                            StaffCorePlugin.staffMembers.clear();
                            StaffCorePlugin.playersServerMap.clear();
                            StaffCorePlugin.playersServerPingMap.clear();
                            StaffCorePlugin.playersServerGamemodesMap.clear();
                            SendMsg.sendStaffListRequest(p.getName(), Utils.getString("bungeecord.server"));
                        } else {
                            new StaffListGui(new PlayerMenuUtility(p), plugin).open();
                        }
                    } else {
                        Utils.tell(sender, Utils.getString("no_permission", "lg", "staff"));
                    }
                } else {
                    Utils.tell(sender, Utils.getString("wrong_usage", "lg", "staff").replace("%command%", "stafflist"));
                }
            }
        } else {
            Utils.tell(sender, Utils.getString("not_for_older_versions", "lg", "sv"));
        }
        return true;
    }
}
