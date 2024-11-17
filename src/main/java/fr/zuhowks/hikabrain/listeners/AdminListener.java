package fr.zuhowks.hikabrain.listeners;

import fr.zuhowks.hikabrain.game.GameStatus;
import fr.zuhowks.hikabrain.utils.SetupModItems;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

import static fr.zuhowks.hikabrain.Hikabrain.INSTANCE;
import static fr.zuhowks.hikabrain.Hikabrain.prefixMessage;


public class AdminListener implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        Material type = event.getBlock().getType();

        if (INSTANCE.isInSetupMod(p)) {
            event.setCancelled(true);
            if (type == Material.BLUE_BED || type == Material.RED_BED) {
                Location location = event.getBlock().getLocation();

                if (location.equals(INSTANCE.getMap().getBlueBed()) || location.equals((Objects.requireNonNull(getOtherBedPartLocation(INSTANCE.getMap().getBlueBed().getBlock()))).getLocation())) {
                    removeBed(location);
                    INSTANCE.getHikaConfig().set("map.blue-bed", null);
                    INSTANCE.getMap().setBlueBed(null);
                    event.getPlayer().sendMessage(prefixMessage + ChatColor.GREEN + "Bed has been clear !");
                } else if (location.equals(INSTANCE.getMap().getRedBed()) || location.equals((Objects.requireNonNull(getOtherBedPartLocation(INSTANCE.getMap().getRedBed().getBlock()))).getLocation())) {
                    removeBed(location);
                    INSTANCE.getHikaConfig().set("map.red-bed", null);
                    INSTANCE.getMap().setRedBed(null);
                    event.getPlayer().sendMessage(prefixMessage + ChatColor.GREEN + "Bed has been clear !");
                }
            }
        } else if (type == Material.BLUE_BED || type == Material.RED_BED) {

            Location location = event.getBlock().getLocation();
            if (location.equals(INSTANCE.getMap().getBlueBed()) || location.equals(INSTANCE.getMap().getRedBed()) || location.equals((Objects.requireNonNull(getOtherBedPartLocation(INSTANCE.getMap().getBlueBed().getBlock()))).getLocation()) || location.equals((Objects.requireNonNull(getOtherBedPartLocation(INSTANCE.getMap().getRedBed().getBlock()))).getLocation())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(prefixMessage + ChatColor.RED + "You can't break a setup bed.");
            }
        }

    }

    @EventHandler
    public void onBuild(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (INSTANCE.isInSetupMod(p)) {

            ItemStack itemStack = event.getItemInHand();
            SetupModItems setupModItem = SetupModItems.isInSetupModItems(itemStack);
            Location blockLoc = event.getBlock().getLocation();
            if (setupModItem == SetupModItems.SETUP_BLUE_BED) {
                Location oldLoc = (Location) INSTANCE.getHikaConfig().get("map.blue-bed");
                removeBed(oldLoc);

                INSTANCE.getHikaConfig().set("map.blue-bed", blockLoc);
                p.sendMessage(prefixMessage + ChatColor.GREEN + "Blue team's bed has been set !");

                INSTANCE.saveConfig();
                INSTANCE.getMap().setBlueBed(blockLoc);

            } else if (setupModItem == SetupModItems.SETUP_RED_BED) {
                Location oldLoc = (Location) INSTANCE.getHikaConfig().get("map.red-bed");
                removeBed(oldLoc);

                INSTANCE.getHikaConfig().set("map.red-bed", blockLoc);
                p.sendMessage(prefixMessage + ChatColor.GREEN + "Red team's bed has been set !");

                INSTANCE.saveConfig();
                INSTANCE.getMap().setRedBed(blockLoc);

            } else {

                if (setupModItem == SetupModItems.SETUP_BLUE_SPAWN) {
                    INSTANCE.getMap().setBlueSpawnData(p, blockLoc);

                } else if (setupModItem == SetupModItems.SETUP_RED_SPAWN) {
                    INSTANCE.getMap().setRedSpawnData(p, blockLoc);
                }

                event.setCancelled(true);
            }

        }
    }

    private void removeBed(Location oldLoc) {
        if (oldLoc != null) {
            Block old = oldLoc.getBlock();
            Block secondPart = this.getOtherBedPartLocation(old);

            if (secondPart != null) {

                old.setType(Material.AIR, false);
                secondPart.setType(Material.AIR, false);

                old.getState().update();
                secondPart.getState().update();
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

    @Nullable
    public Block getOtherBedPartLocation(Block bedBlock) {
        // Vérifie que le bloc est bien un lit
        if (!(bedBlock.getType() == Material.BLUE_BED || bedBlock.getType() == Material.RED_BED)) return null;

        // Récupère les données du bloc sous forme de Bed
        Bed bedData = (Bed) bedBlock.getBlockData();

        // Vérifie si c'est la tête ou le pied du lit
        Bed.Part part = bedData.getPart();
        BlockFace facing = bedData.getFacing();

        // Calculer la position de l'autre partie du lit
        Block otherPart;
        if (part == Bed.Part.HEAD) {
            // Si c'est la tête, on va vers l'arrière pour obtenir le pied
            otherPart = bedBlock.getRelative(facing.getOppositeFace());
        } else {
            // Si c'est le pied, on va vers l'avant pour obtenir la tête
            otherPart = bedBlock.getRelative(facing);
        }

        return otherPart;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        ItemStack itemStack = event.getItem();
        Block block = event.getClickedBlock();
        GameStatus gameStatus = INSTANCE.getManager().getStatus();
        if (INSTANCE.isInSetupMod(p)) {
            if (block != null) {
                if (block.getType() == Material.RED_BED || block.getType() == Material.BLUE_BED) {
                    event.setCancelled(false);
                    return;
                }
            }
            if (itemStack != null) {
                SetupModItems setupModItem = SetupModItems.isInSetupModItems(itemStack);
                if (setupModItem != null) {
                    if (setupModItem.getCommand() != null && !setupModItem.getCommand().isEmpty()) {
                        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                            p.chat("/" + setupModItem.getCommand());
                            event.setCancelled(true);
                        }
                    } else {
                        Location loc;
                        loc = Objects.requireNonNullElseGet(block, () -> p.getLocation().getBlock()).getLocation();

                        if (setupModItem == SetupModItems.SET_MAP_POS) {
                            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                                INSTANCE.getMap().setPos1(loc);
                                INSTANCE.getHikaConfig().set("map.pos1", loc);
                                p.sendMessage(prefixMessage + ChatColor.GREEN + "Position 1 as been set !");
                                INSTANCE.saveConfig();
                            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                INSTANCE.getMap().setPos2(loc);
                                INSTANCE.getHikaConfig().set("map.pos2", loc);
                                p.sendMessage(prefixMessage + ChatColor.GREEN + "Position 2 as been set !");
                                INSTANCE.saveConfig();
                            }
                        }

                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
