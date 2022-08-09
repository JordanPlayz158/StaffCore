package cl.bebt.staffcore.commands.staff;

import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.sql.queries.VanishQuery;
import cl.bebt.staffcore.utils.SetFly;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class GmSurvival implements CommandExecutor {

    private final StaffCorePlugin plugin;

    public GmSurvival(StaffCorePlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("gms").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 1) {
                Player p = Bukkit.getPlayer(args[0]);
                if (p instanceof Player) {
                    if (!(p.getGameMode() == GameMode.SURVIVAL)) {
                        p.setGameMode(GameMode.SURVIVAL);
                        p.setInvulnerable(false);
                        if (plugin.getConfig().getBoolean("mysql")) {
                            if (VanishQuery.isVanished(p.getName()).equalsIgnoreCase("true")) {
                                p.setAllowFlight(true);
                                p.setFlying(true);
                            }
                        } else {
                            try {
                                PersistentDataContainer PlayerData = p.getPersistentDataContainer();
                                if (PlayerData.has(new NamespacedKey(plugin, "vanished"), PersistentDataType.STRING)) {
                                    p.setAllowFlight(true);
                                    p.setFlying(true);
                                }
                            } catch (NoSuchMethodError ignored) {
                            }
                        }
                        Utils.tell(sender, Utils.getString("survival.enabled_to", "lg", "sv").replace("%player%", p.getName()));
                    } else {
                        Utils.tell(sender, Utils.getString("survival.already", "lg", "sv").replace("%player%", p.getName()));
                    }
                    return false;
                }
                Utils.tell(sender, Utils.getString("p_dont_exist", "lg", "sv"));
            } else {
                Utils.tell(sender, Utils.getString("wrong_usage", "lg", "sv").replace("%command%", "gms | gms <player>"));
            }
            return false;
        }
        if (args.length == 0) {
            Player p = (Player) sender;
            if (p.hasPermission("staffcore.gms")) {
                if (!(p.getGameMode() == GameMode.SURVIVAL)) {
                    p.setGameMode(GameMode.SURVIVAL);
                    p.setInvulnerable(false);
                    if (plugin.getConfig().getBoolean("mysql")) {
                        if (VanishQuery.isVanished(p.getName()).equalsIgnoreCase("true")) {
                            p.setAllowFlight(true);
                            p.setFlying(true);
                        }
                    } else {
                        try {
                            PersistentDataContainer PlayerData = p.getPersistentDataContainer();
                            if (PlayerData.has(new NamespacedKey(plugin, "flying"), PersistentDataType.STRING)) {
                                new SetFly(p, true);
                            }
                            if (PlayerData.has(new NamespacedKey(plugin, "vanished"), PersistentDataType.STRING)) {
                                p.setAllowFlight(true);
                                p.setFlying(true);
                            }
                        } catch (NoSuchMethodError ignored) {
                        }
                    }
                    Utils.tell(sender, Utils.getString("survival.enabled", "lg", "sv"));
                } else {
                    Utils.tell(sender, Utils.getString("survival.already", "lg", "sv").replace("%player%", p.getName()));
                }
            } else {
                Utils.tell(sender, Utils.getString("no_permission", "lg", "sv"));
            }
        } else if (args.length == 1) {
            if (Bukkit.getPlayer(args[0]) instanceof Player) {
                Player p = Bukkit.getPlayer(args[0]);
                if (sender.hasPermission("staffcore.gms")) {
                    if (!(p.getGameMode() == GameMode.SURVIVAL)) {
                        p.setGameMode(GameMode.SURVIVAL);
                        p.setInvulnerable(false);
                        if (plugin.getConfig().getBoolean("mysql")) {
                            if (VanishQuery.isVanished(p.getName()).equalsIgnoreCase("true")) {
                                p.setAllowFlight(true);
                                p.setFlying(true);
                            }
                        } else {
                            try {
                                PersistentDataContainer PlayerData = p.getPersistentDataContainer();
                                if (PlayerData.has(new NamespacedKey(plugin, "flying"), PersistentDataType.STRING)) {
                                    new SetFly(p, true);
                                }
                                if (PlayerData.has(new NamespacedKey(plugin, "vanished"), PersistentDataType.STRING)) {
                                    p.setAllowFlight(true);
                                    p.setFlying(true);
                                }
                            } catch (NoSuchMethodError ignored) {
                            }
                        }
                        if (!(sender == p)) {
                            Utils.tell(sender, Utils.getString("survival.enabled_to", "lg", "sv").replace("%player%", p.getName()));
                        }
                        Utils.tell(sender, Utils.getString("survival.enabled", "lg", "sv").replace("%player%", p.getName()));
                    } else {
                        Utils.tell(sender, Utils.getString("survival.already", "lg", "sv").replace("%player%", p.getName()));
                    }
                } else {
                    Utils.tell(sender, Utils.getString("no_permission", "lg", "sv"));
                }
                return false;
            }
            Utils.tell(sender, Utils.getString("p_dont_exist", "lg", "sv"));
        } else {
            Utils.tell(sender, Utils.getString("wrong_usage", "lg", "sv").replace("%command%", "gms | gms <player>"));
        }
        return true;
    }
}