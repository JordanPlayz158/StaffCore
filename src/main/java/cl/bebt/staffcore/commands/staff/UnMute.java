package cl.bebt.staffcore.commands.staff;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.utils.CountdownManager;
import cl.bebt.staffcore.utils.ToggleChat;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class UnMute implements TabExecutor {
    private final StaffCorePlugin plugin;

    public UnMute(StaffCorePlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("unmute").setExecutor(this);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> muted = new ArrayList<>();
        if (!Utils.isOlderVersion()) {
            if (args.length == 1) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getPersistentDataContainer().has(new NamespacedKey(plugin, "muted"), PersistentDataType.STRING)) {
                        muted.add(p.getName());
                    } else if (!CountdownManager.checkMuteCountdown(p)) {
                        muted.add(p.getName());
                    }
                }
                return muted;
            }
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!Utils.isOlderVersion()) {
            if (!(sender instanceof Player)) {
                if (args.length == 0) {
                    if (!plugin.chatMuted) {
                        Utils.tell(sender, Utils.getString("toggle_chat.the_chat_is_not_muted", "lg", "staff"));
                    } else {
                        Bukkit.broadcastMessage(Utils.chat(Utils.getString("toggle_chat.un_mute_by_console", "lg", "staff")));
                        ToggleChat.Mute(false);
                    }
                } else if (args.length == 1) {
                    if (Bukkit.getPlayer(args[0]) instanceof Player) {
                        Player muted = Bukkit.getPlayer(args[0]);
                        ToggleChat.unMute(sender, muted);
                    } else {
                        Utils.tell(sender, Utils.getString("p_dont_exist", "lg", "sv"));
                    }
                } else {
                    Utils.tell(sender, Utils.getString("wrong_usage", "lg", "staff").replace("%command%", "unmute <player>"));
                }
            } else {
                Player p = (Player) sender;
                if (p.hasPermission("staffcore.unmute")) {
                    if (args.length == 0) {
                        if (!plugin.chatMuted) {
                            Utils.tell(p, Utils.getString("toggle_chat.chat_not_muted", "lg", "staff"));
                        } else {
                            Bukkit.broadcastMessage(Utils.chat(Utils.getString("toggle_chat.un_mute_by_player", "lg", "staff").replace("%player%", p.getName())));
                            ToggleChat.Mute(false);
                        }
                    } else if (args.length == 1) {
                        if (Bukkit.getPlayer(args[0]) instanceof Player) {
                            Player muted = Bukkit.getPlayer(args[0]);
                            ToggleChat.unMute(sender, muted);
                        } else {
                            Utils.tell(sender, Utils.getString("p_dont_exist", "lg", "sv"));
                        }
                    } else {
                        Utils.tell(sender, Utils.getString("wrong_usage", "lg", "staff").replace("%command%", "unmute <player>"));
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
