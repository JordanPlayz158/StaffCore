package cl.bebt.staffcore.listeners;

import cl.bebt.staffcore.msgchannel.SendMsg;
import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.utils.CountdownManager;
import cl.bebt.staffcore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class OnChat implements Listener {


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChatEvent(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (!e.getMessage().isEmpty()) {
            e.setMessage(Utils.chat(e.getMessage()));
        }
        if (Utils.getBoolean("discord.type.debug.enabled_debugs.commands")) {
            ArrayList<String> dc = new ArrayList<>();
            dc.add("**Player:** " + p.getName() + ": " + e.getMessage());
            Utils.sendDiscordDebugMsg(p, "⚠ Chat ⚠", dc);
        }
        if (!Utils.isOlderVersion()) {
            PersistentDataContainer persistent = p.getPersistentDataContainer();
            if (persistent.has(new NamespacedKey(StaffCorePlugin.plugin, "frozen"), PersistentDataType.STRING)) {
                if (Utils.getBoolean("freeze.cancel_chat_while_frozen")) {
                    Utils.tell(p, Utils.getString("freeze.talk_while_frozen", "lg", "staff"));
                    e.setCancelled(true);
                }
            }
            if (Utils.getBoolean("staff.staffchat.enable_use_custom")) {
                if (e.getMessage().substring(0, 1).equals(Utils.getString("staff.staffchat.custom_character"))) {
                    if (p.hasPermission("staff.sc")) {
                        e.setCancelled(true);
                        String msg = e.getMessage().substring(1);
                        for (Player people : Bukkit.getOnlinePlayers()) {
                            if (people.getPersistentDataContainer().has(new NamespacedKey(StaffCorePlugin.plugin, "staff"), PersistentDataType.STRING) || people.hasPermission("staff.sc")) {
                                String message = Utils.getString("staff_chat.prefix", "lg", null);
                                message = message.replace("%sender%", p.getName());
                                message = message.replace("%msg%", msg);
                                Utils.tell(people, message);
                            }
                        }
                        SendMsg.sendStaffChatMSG(p.getName(), msg, Utils.getString("bungeecord.server"));
                        return;
                    }
                }
            }
            if (persistent.has(new NamespacedKey(StaffCorePlugin.plugin, "staffchat"), PersistentDataType.STRING)) {
                e.setCancelled(true);
                String msg = e.getMessage();
                for (Player people : Bukkit.getOnlinePlayers()) {
                    if (people.getPersistentDataContainer().has(new NamespacedKey(StaffCorePlugin.plugin, "staff"), PersistentDataType.STRING) || people.hasPermission("staff.sc")) {
                        String message = Utils.getString("staff_chat.prefix", "lg", null);
                        message = message.replace("%sender%", p.getName());
                        message = message.replace("%msg%", msg);
                        Utils.tell(people, message);
                    }
                }
                SendMsg.sendStaffChatMSG(p.getName(), msg, Utils.getString("bungeecord.server"));
            }
            if (!CountdownManager.checkMuteCountdown(p)) {
                e.setCancelled(true);
                long remaining = CountdownManager.getMuteCountDown(p);
                long days = TimeUnit.SECONDS.toDays(remaining);
                long secondsCount = remaining - TimeUnit.DAYS.toSeconds(days);
                long hours = TimeUnit.SECONDS.toHours(secondsCount);
                secondsCount -= TimeUnit.HOURS.toSeconds(hours);
                long minutes = TimeUnit.SECONDS.toMinutes(secondsCount);
                secondsCount -= TimeUnit.MINUTES.toSeconds(minutes);
                Utils.PlaySound(p, "muted_try_to_chat");
                Utils.tell(p, Utils.getString("toggle_chat.muted_try_to_chat", "lg", "sv"));
                Utils.tell(p, Utils.getString("toggle_chat.time_remaining", "lg", "sv")
                        .replace("%days%", String.valueOf(days))
                        .replace("%hours%", String.valueOf(hours))
                        .replace("%minutes%", String.valueOf(minutes))
                        .replace("%seconds%", String.valueOf(secondsCount)));

            } else {
                if (persistent.has(new NamespacedKey(StaffCorePlugin.plugin, "muted"), PersistentDataType.STRING)) {
                    e.setCancelled(true);
                    Utils.tell(p, Utils.getString("toggle_chat.muted_try_to_chat", "lg", "sv"));
                }
            }
        }
        if (StaffCorePlugin.toggledStaffChat.contains(p.getName())) {
            e.setCancelled(true);
            String msg = e.getMessage();
            for (Player people : Bukkit.getOnlinePlayers()) {
                if (people.hasPermission("staff.sc")) {
                    Utils.tell(people, Utils.getString("staff_chat.prefix", "lg", null)
                            .replace("%msg%", msg)
                            .replace("%sender%", p.getName()));
                }
            }
            SendMsg.sendStaffChatMSG(p.getName(), msg, Utils.getString("bungeecord.server"));
        } else if (StaffCorePlugin.plugin.chatMuted) {
            if (!p.hasPermission("staffcore.togglechat.bypass")) {
                e.setCancelled(true);
                Utils.PlaySound(p, "muted_try_to_chat");
                Utils.tell(p, Utils.getString("toggle_chat.chat_muted", "lg", "sv"));
            }
        }

    }
}
