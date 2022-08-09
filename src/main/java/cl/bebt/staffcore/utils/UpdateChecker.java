package cl.bebt.staffcore.utils;

import cl.bebt.staffcore.StaffCorePlugin;
import org.bukkit.Bukkit;

import java.util.function.Consumer;

public class UpdateChecker {
    private final StaffCorePlugin plugin;

    public UpdateChecker(StaffCorePlugin plugin) {
        this.plugin = plugin;
    }

    public void getLatestVersion(Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String version = Http.getLatestVersion("https://staffcore.glitch.me/api/version", "latest");
            plugin.latestVersion = version;
            consumer.accept(version);
        });
    }
}
