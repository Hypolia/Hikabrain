package fr.zuhowks.hikabrain.game.player;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class SpawnProtectionRunnable extends BukkitRunnable {

    private final Player player;

    public SpawnProtectionRunnable(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        player.setInvulnerable(false);
    }

    public void startProtection(JavaPlugin plugin) {
        player.setInvulnerable(true);

        this.runTaskLater(plugin, 20L);
    }
}
