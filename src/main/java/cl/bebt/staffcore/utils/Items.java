package cl.bebt.staffcore.utils;

import cl.bebt.staffcore.api.StaffCoreAPI;
import cl.bebt.staffcore.StaffCorePlugin;
import cl.bebt.staffcore.sql.queries.ServerQuery;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class Items {

    public static ItemStack head(Player target) {
        ArrayList<String> lore = new ArrayList<>();
        ItemStack head = Utils.getPlayerHead(target.getName());
        ItemMeta head_meta = head.getItemMeta();
        head_meta.setDisplayName(Utils.chat("&a" + target.getName() + "'s &7Stats:"));
        lore.add(Utils.chat("&a► &7Gamemode: &b" + target.getGameMode()));
        lore.add(Utils.chat("&a► &7Location: &d" + (int) target.getLocation().getX() + " &3" + (int) target.getLocation().getY() + " &d" + (int) target.getLocation().getZ()));
        lore.add(Utils.chat("&a► &a" + target.getName() + "'s &7ping: &a" + Utils.getPing(target)));
        if (target.hasPermission("staffcore.staff")) {
            lore.add(Utils.chat("&5STAFF MEMBER"));
            if (target.getPersistentDataContainer().has(new NamespacedKey(StaffCorePlugin.plugin, "staff"), PersistentDataType.STRING)) {
                lore.add(Utils.chat("&a► &7Staff Mode &aOn"));
            }
            if (!target.getPersistentDataContainer().has(new NamespacedKey(StaffCorePlugin.plugin, "staff"), PersistentDataType.STRING)) {
                lore.add(Utils.chat("&a► &7Staff Mode &cOff"));
            }
            if (target.getPersistentDataContainer().has(new NamespacedKey(StaffCorePlugin.plugin, "vanished"), PersistentDataType.STRING)) {
                lore.add(Utils.chat("&a► &7Vanished &aOn"));
            }
            if (!target.getPersistentDataContainer().has(new NamespacedKey(StaffCorePlugin.plugin, "vanished"), PersistentDataType.STRING)) {
                lore.add(Utils.chat("&a► &7Vanished &cOff"));
            }
            if (StaffCoreAPI.getTrollStatus(target.getName())) {
                lore.add(Utils.chat("&a► &7Troll Mode: &aON"));
            }
            if (!StaffCoreAPI.getTrollStatus(target.getName())) {
                lore.add(Utils.chat("&a► &7Troll Mode: &cOFF"));
            }
        }
        head_meta.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "head"), PersistentDataType.STRING, "head");
        head_meta.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "name"), PersistentDataType.STRING, target.getName());
        head_meta.setLore(lore);
        head.setItemMeta(head_meta);
        return head;
    }

    public static ItemStack food(Player target) {
        ArrayList<String> lore = new ArrayList<>();
        ItemStack food = new ItemStack(Material.COOKED_BEEF);
        ItemMeta food_meta = food.getItemMeta();
        food_meta.setDisplayName(Utils.chat("&a" + target.getName() + "'s &7Health Stats:"));
        lore.add(Utils.chat("&a► &aHealth level: &c" + (int) target.getHealth() + "&l❤"));
        lore.add(Utils.chat("&a► &aFood level: &6" + target.getFoodLevel()));
        food_meta.setLore(lore);
        food_meta.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "food"), PersistentDataType.STRING, "food");
        food.setItemMeta(food_meta);
        return food;
    }

    public static ItemStack potions(Player target) {
        ArrayList<PotionEffect> potions = new ArrayList<>(target.getActivePotionEffects());
        if (!potions.isEmpty()) {
            ArrayList<String> lore = new ArrayList<>();
            for (PotionEffect a : potions) {
                lore.add(Utils.chat("&a► &7" + a.getType().getName() + " - " + getTime((long) a.getDuration())));
            }
            ItemStack item = new ItemStack(Material.BREWING_STAND);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(Utils.chat("&aPotion Effects:"));
            itemMeta.setLore(lore);
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "poti"), PersistentDataType.STRING, "poti");
            item.setItemMeta(itemMeta);
            return item;
        } else {
            ItemStack item = new ItemStack(Material.BREWING_STAND);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(Utils.chat("&aPotion Effects:"));
            itemMeta.setLore(Collections.singletonList(Utils.chat("&a► &cNo potion effects")));
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "poti"), PersistentDataType.STRING, "poti");
            item.setItemMeta(itemMeta);
            return item;
        }
    }

    public static ItemStack EmptyItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(Utils.chat("&cEMPTY WARN"));
        item.setItemMeta(itemMeta);
        return item;

    }

    public static ItemStack greenPanel() {
        ItemStack panel = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta panel_meta = panel.getItemMeta();
        panel_meta.setDisplayName(" ");
        panel_meta.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "panel"), PersistentDataType.STRING, "panel");
        panel.setItemMeta(panel_meta);
        return panel;
    }

    private static String getTime(Long timeLeft) {
        Long left = (timeLeft / 20) * 1000;
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        Date date = new Date(left);
        return format.format(date);
    }

    public static ItemStack ServerStatus() {
        ArrayList<String> lore = new ArrayList<>();
        ItemStack server = new ItemStack(Material.COMPASS);
        ItemMeta metaServer = server.getItemMeta();
        metaServer.setDisplayName(Utils.chat("&5SERVER STATUS:"));
        metaServer.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "server"), PersistentDataType.STRING, "server");
        double tps = Math.round(Utils.getTPS() * 100.0D) / 100.0D;
        int mb = 1024 * 1024;
        Runtime instance = Runtime.getRuntime();
        if (tps > 20) {
            tps = 20D;
        }
        lore.add(Utils.chat("&a► &7Tps: &a" + tps));
        lore.add(Utils.chat("&a► &7Online players: &a" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers()));
        lore.add(Utils.chat("&a► &7Ram in use: &a" + (instance.totalMemory() - instance.freeMemory()) / mb) + "/" + instance.maxMemory() / mb);
        lore.add(Utils.chat("&5PUNISHMENTS STATUS:"));
        if (Utils.mysqlEnabled()) {
            HashMap<String, Integer> serverStatus = ServerQuery.getServerStatus();
            lore.add(Utils.chat("&a► &7Current Bans: &a" + serverStatus.get("CurrentBans")));
            lore.add(Utils.chat("&a► &7Current Reports: &a" + serverStatus.get("CurrentReports")));
            lore.add(Utils.chat("&a► &7Current Warns: &a" + serverStatus.get("CurrentWarns")));

        } else {
            lore.add(Utils.chat("&a► &7Current Bans: &a" + Utils.count("bans")));
            lore.add(Utils.chat("&a► &7Current Report: &a" + Utils.count("reports")));
            lore.add(Utils.chat("&a► &7Current Warns: &a" + Utils.count("warns")));
        }
        metaServer.setLore(lore);
        server.setItemMeta(metaServer);
        return server;
    }

    public static ItemStack PlayerStats(Player p) {
        ArrayList<String> lore = new ArrayList<>();
        ItemStack playerHead = Utils.getPlayerHead(p.getName());
        ItemMeta metaPlayerHead = playerHead.getItemMeta();
        metaPlayerHead.setDisplayName(Utils.chat("&5" + p.getName().toUpperCase() + "'S STATS:"));
        metaPlayerHead.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "player"), PersistentDataType.STRING, "players");
        if (p.getPersistentDataContainer().has(new NamespacedKey(StaffCorePlugin.plugin, "staff"), PersistentDataType.STRING)) {
            lore.add(Utils.chat("&a► &7Staff Mode &aOn"));
        }
        if (!p.getPersistentDataContainer().has(new NamespacedKey(StaffCorePlugin.plugin, "staff"), PersistentDataType.STRING)) {
            lore.add(Utils.chat("&a► &7Staff Mode &cOff"));
        }
        if (p.getPersistentDataContainer().has(new NamespacedKey(StaffCorePlugin.plugin, "staffchat"), PersistentDataType.STRING)) {
            lore.add(Utils.chat("&a► &7Staff Chat &aOn"));
        }
        if (!p.getPersistentDataContainer().has(new NamespacedKey(StaffCorePlugin.plugin, "staffchat"), PersistentDataType.STRING)) {
            lore.add(Utils.chat("&a► &7Staff Chat &cOff"));
        }
        if (p.getPersistentDataContainer().has(new NamespacedKey(StaffCorePlugin.plugin, "vanished"), PersistentDataType.STRING)) {
            lore.add(Utils.chat("&a► &7Vanished &aOn"));
        }
        if (!p.getPersistentDataContainer().has(new NamespacedKey(StaffCorePlugin.plugin, "vanished"), PersistentDataType.STRING)) {
            lore.add(Utils.chat("&a► &7Vanished &cOff"));
        }
        if (StaffCoreAPI.getTrollStatus(p.getName())) {
            lore.add(Utils.chat("&a► &7Troll Mode: &aON"));
        }
        if (!StaffCoreAPI.getTrollStatus(p.getName())) {
            lore.add(Utils.chat("&a► &7Troll Mode: &cOFF"));
        }
        lore.add(Utils.chat("&a► &7Gamemode: &b" + p.getGameMode()));
        lore.add(Utils.chat("&a► &7Ping: &a" + Utils.getPing(p)));
        lore.add(Utils.chat("&a► &7Location: &d" + (int) p.getLocation().getX() + " &3" + (int) p.getLocation().getY() + " &d" + (int) p.getLocation().getZ()));
        metaPlayerHead.setLore(lore);
        playerHead.setItemMeta(metaPlayerHead);
        return playerHead;
    }

    public static ItemStack FakeJoinOrLeave(boolean enabled) {
        ArrayList<String> lore = new ArrayList<>();
        ItemStack item;
        if (enabled) {
            item = new ItemStack(Material.LIME_DYE);
        } else {
            item = new ItemStack(Material.GRAY_DYE);
        }
        ItemMeta itemMeta = item.getItemMeta();
        for (String key : Utils.getStringList("fake_join_leave_msg.lore", "item")) {
            lore.add(Utils.chat(key));
        }
        if (enabled) {
            itemMeta.setDisplayName(Utils.chat("&8Fake Join/Leave msg &aOn "));
            itemMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            lore.add(Utils.chat("&7Click to: &cDisable"));
        } else {
            itemMeta.setDisplayName(Utils.chat("&8Fake Join/Leave msg &cOff"));
            lore.add(Utils.chat("&7Click to: &aEnable"));
        }
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "FakeJoinOrLeave"), PersistentDataType.STRING, "FakeJoinOrLeave");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack ComingSoon() {
        ArrayList<String> lore = new ArrayList<>();
        ItemStack item = new ItemStack(Material.getMaterial(Utils.getString("menu_items.coming_soon.material", "item", null)));
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(Utils.chat(Utils.getString("menu_items.coming_soon.name", "item", null)));
        for (String key : Utils.getStringList("menu_items.coming_soon.lore", "item")) {
            lore.add(Utils.chat(key));
        }
        itemMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "ComingSoon"), PersistentDataType.STRING, "ComingSoon");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack WebServerStatus() {
        ArrayList<String> lore = new ArrayList<>();
        ItemStack item = new ItemStack(Material.getMaterial(Utils.getString("menu_items.coming_soon.material", "item", null)));
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(Utils.chat(Utils.getString("menu_items.coming_soon.name", "item", null)));
        for (String key : Utils.getStringList("menu_items.coming_soon.lore", "item")) {
            lore.add(Utils.chat(key));
        }
        itemMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(StaffCorePlugin.plugin, "ComingSoon"), PersistentDataType.STRING, "ComingSoon");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
}
