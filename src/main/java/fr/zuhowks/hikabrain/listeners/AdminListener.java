package fr.zuhowks.hikabrain.listeners;

import fr.zuhowks.hikabrain.Hikabrain;
import fr.zuhowks.hikabrain.utils.SetupModItems;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import static fr.zuhowks.hikabrain.Hikabrain.prefixMessage;


public class AdminListener implements Listener {

    private Hikabrain INSTANCE = Hikabrain.getINSTANCE();

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        event.setCancelled(INSTANCE.isInSetupMod(p));
    }

    @EventHandler
    public void onBuild(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (INSTANCE.isInSetupMod(p)) {

            ItemStack itemStack = event.getItemInHand();
            SetupModItems setupModItem = SetupModItems.isInSetupModItems(itemStack);
            if (setupModItem == SetupModItems.SETUP_BLUE_BED) {
                Location old = (Location) INSTANCE.getHikaConfig().get("map.blue-bed");
                if (old != null) {
                    old.getBlock().setType(Material.AIR);
                }

                INSTANCE.getHikaConfig().set("map.blue-bed", event.getBlock().getLocation());
                p.sendMessage(prefixMessage + ChatColor.GREEN + " Blue team's bed has been set !");
                INSTANCE.saveConfig();
            } else if (setupModItem == SetupModItems.SETUP_RED_BED) {
                Location old = (Location) INSTANCE.getHikaConfig().get("map.red-bed");
                if (old != null) {
                    old.getBlock().setType(Material.AIR);
                }

                INSTANCE.getHikaConfig().set("map.red-bed", event.getBlock().getLocation());
                p.sendMessage(prefixMessage + ChatColor.GREEN + " Red team's bed has been set !");
                INSTANCE.saveConfig();
            } else {

                if (setupModItem == SetupModItems.SETUP_BLUE_SPAWN) {
                    INSTANCE.getHikaConfig().set("map.blue-spawn", event.getBlock().getLocation());
                    p.sendMessage(prefixMessage + ChatColor.GREEN + " Blue team's spawn point has been set !");
                    INSTANCE.saveConfig();
                } else if (setupModItem == SetupModItems.SETUP_RED_SPAWN) {
                    INSTANCE.getHikaConfig().set("map.red-spawn", event.getBlock().getLocation());
                    p.sendMessage(prefixMessage + ChatColor.GREEN + " Red team's spawn point has been set !");
                    INSTANCE.saveConfig();
                }

                event.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onPickUp(EntityPickupItemEvent event) {
        Entity e = event.getEntity();
        if (e instanceof Player) {
            Player p = (Player) e;
            if (INSTANCE.isInSetupMod(p)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player p = event.getPlayer();
        event.setCancelled(INSTANCE.isInSetupMod(p));
    }
}
