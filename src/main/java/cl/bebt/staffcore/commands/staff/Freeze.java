package cl.bebt.staffcore.commands.staff;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.utils.FreezePlayer;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class Freeze implements CommandExecutor {

    private final StaffCorePlugin plugin;

    public Freeze(StaffCorePlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("freeze").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!Utils.isOlderVersion()) {
            if (!(sender instanceof Player)) {
                if (args.length == 1) {
                    Player p = Bukkit.getPlayer(args[0]);
                    PersistentDataContainer PlayerData = p.getPersistentDataContainer();
                    if (p instanceof Player) {
                        if (PlayerData.has(new NamespacedKey(plugin, "frozen"), PersistentDataType.STRING)) {
                            FreezePlayer.freeze(p, "CONSOLE", false);
                        } else if (!(PlayerData.has(new NamespacedKey(plugin, "frozen"), PersistentDataType.STRING))) {
                            FreezePlayer.freeze(p, "CONSOLE", true);
                        }
                    }
                } else {
                    Utils.tell(sender, Utils.getString("wrong_usage", "lg", "staff").replace("%command%", "freeze <player>"));
                }
            } else if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("staffcore.freeze")) {
                    if (args.length == 1) {
                        if (Bukkit.getPlayer(args[0]) instanceof Player) {
                            Player p = Bukkit.getPlayer(args[0]);
                            PersistentDataContainer PlayerData = p.getPersistentDataContainer();
                            if (p == player) {
                                if (PlayerData.has(new NamespacedKey(plugin, "frozen"), PersistentDataType.STRING)) {
                                    if (p.hasPermission("staffcore.unfreeze.himself")) {
                                        FreezePlayer.freeze(p, player.getName(), false);
                                        Utils.tell(p, Utils.getString("freeze.unfreeze_ur_self", "lg", "staff"));
                                    } else {
                                        Utils.tell(p, Utils.chat(Utils.getString("no_permission", "lg", "staff")));
                                    }
                                } else {
                                    Utils.tell(p, Utils.getString("wrong_usage", "lg", "staff").replace("%command%", "freeze <player>"));
                                }
                            } else if (PlayerData.has(new NamespacedKey(plugin, "frozen"), PersistentDataType.STRING)) {
                                FreezePlayer.freeze(p, player.getName(), false);
                            } else if (!(PlayerData.has(new NamespacedKey(plugin, "frozen"), PersistentDataType.STRING))) {
                                if (p.hasPermission("staffcore.freeze.bypass")) {
                                    Utils.tell(sender, Utils.getString("freeze.freeze_bypass", "lg", "staff"));
                                    return false;
                                } else {
                                    FreezePlayer.freeze(p, player.getName(), true);
                                }
                            }
                        } else if (!(Bukkit.getPlayer(args[0]) instanceof Player)) {
                            Utils.tell(sender, Utils.getString("p_dont_exist", "lg", "sv"));
                        }
                    } else {
                        Utils.tell(sender, Utils.getString("wrong_usage", "lg", "staff").replace("%command%", "freeze <player>"));
                    }
                } else {
                    sender.sendMessage(Utils.getString("no_permission", "lg", "staff"));
                }
            }
        } else {
            Utils.tell(sender, Utils.getString("not_for_older_versions", "lg", "sv"));
        }
        return true;
    }
}
