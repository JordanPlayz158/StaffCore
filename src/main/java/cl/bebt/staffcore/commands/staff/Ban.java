package cl.bebt.staffcore.commands.staff;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.menu.menu.ban.gui.BanMenu;
import cl.bebt.staffcore.utils.BanPlayer;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class Ban implements TabExecutor {
    private final StaffCorePlugin plugin;

    public Ban(StaffCorePlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("ban").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!Utils.isOlderVersion()) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (p.hasPermission("staffcore.ban")) {
                    p.getPersistentDataContainer().remove(new NamespacedKey(plugin, "ban-ip"));
                    if (args.length == 0) {
                        Utils.tell(sender, Utils.getString("wrong_usage", "lg", "staff").replace("%command%", "ban <player> <time> <-ip> <reason>"));
                    } else if (args.length == 1) {
                        if (Utils.getUsers().contains(args[0])) {
                            new BanMenu(StaffCorePlugin.getPlayerMenuUtility(p), plugin, p, args[0]).open();
                        } else {
                            Utils.tell(p, Utils.getString("never_seen", "lg", "staff").replace("%player%", args[0]));
                        }
                    } else {
                        if (isNormal(args[1])) {
                            String lastWord = args[1].substring(args[1].length() - 1);
                            int amount = Integer.parseInt(args[1].substring(0, args[1].length() - 1));
                            StringBuilder reason = new StringBuilder();
                            if (args[2].equalsIgnoreCase("-ip")) {
                                for (int i = 3; i < args.length; i++) {
                                    reason.append(args[i]).append(" ");
                                }
                                p.getPersistentDataContainer().set(new NamespacedKey(plugin, "ban-ip"), PersistentDataType.STRING, "ban-ip");
                            } else {
                                for (int i = 2; i < args.length; i++) {
                                    reason.append(args[i]).append(" ");
                                }
                            }
                            BanPlayer.BanCooldown(p, args[0], String.valueOf(reason), amount, lastWord);
                        } else {
                            StringBuilder reason = new StringBuilder();
                            if (args[1].equalsIgnoreCase("-ip")) {
                                for (int i = 2; i < args.length; i++) {
                                    reason.append(args[i]).append(" ");
                                }
                                p.getPersistentDataContainer().set(new NamespacedKey(plugin, "ban-ip"), PersistentDataType.STRING, "ban-ip");
                            } else {
                                for (int i = 1; i < args.length; i++) {
                                    reason.append(args[i]).append(" ");
                                }
                            }
                            BanPlayer.Ban(p, args[0], String.valueOf(reason));
                        }
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

    private Boolean isNormal(String s) {
        String lastWord = s.substring(s.length() - 1);
        String ncadena = s.substring(0, s.length() - 1);
        try {
            int amount = Integer.parseInt(ncadena);
            return lastWord.equalsIgnoreCase("s") ||
                    lastWord.equalsIgnoreCase("m") ||
                    lastWord.equalsIgnoreCase("h") ||
                    lastWord.equalsIgnoreCase("d");
        } catch (NumberFormatException error) {
            return false;
        }
    }
}
