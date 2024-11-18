package fr.zuhowks.hikabrain.listeners;

import fr.zuhowks.hikabrain.game.GameStatus;
import fr.zuhowks.hikabrain.game.HikabrainManager;
import fr.zuhowks.hikabrain.game.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.UUID;

import static fr.zuhowks.hikabrain.Hikabrain.INSTANCE;
import static fr.zuhowks.hikabrain.Hikabrain.prefixMessage;

public class GameListener implements Listener {

    private HashMap<UUID, UUID> entitySource = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        System.out.println(INSTANCE.getManager().getStatus());
        INSTANCE.getManager().join(event.getPlayer());
        AttributeInstance instance = event.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        instance.setBaseValue(17.0D);
    }

    @EventHandler
    public void onHungerDeplete(FoodLevelChangeEvent e) {
        if (INSTANCE.getManager().getStatus() != GameStatus.GAME_NOT_SETUP) {
            e.setCancelled(true);
            e.setFoodLevel(30);
        }
    }

    @EventHandler
    public void cancelBedInteraction(PlayerInteractEvent event) {
        if (INSTANCE.getManager().getStatus() != GameStatus.GAME_NOT_SETUP) {
            Block _block = event.getClickedBlock();
            if (_block != null) {
                Material type = _block.getType();
                event.setCancelled(type == Material.RED_BED || type == Material.BLUE_BED || type == Material.GREEN_BED || type == Material.YELLOW_BED);
            }
        }

    }

    @EventHandler
    public void allowedBlockBreak(BlockBreakEvent event) {
        if (INSTANCE.getManager().getStatus() != GameStatus.GAME_NOT_SETUP) {
            if (INSTANCE.getManager().isNotFreezePlayers()) {
                if (INSTANCE.getManager().getStatus() == GameStatus.IN_GAME) {
                    Material type = event.getBlock().getType();
                    event.setCancelled(type != Material.CUT_SANDSTONE);
                }
            }
        }
    }

    @EventHandler
    public void allowedBlockBuild(BlockPlaceEvent event) {
        if (INSTANCE.getManager().getStatus() != GameStatus.GAME_NOT_SETUP) {
            if (INSTANCE.getManager().isNotFreezePlayers()) {
                if (INSTANCE.getManager().getStatus() == GameStatus.IN_GAME) {
                    Block block = event.getBlock();
                    Material type = block.getType();
                    int y = block.getY();

                    int middleY = (INSTANCE.getMap().getBlueBed().getBlockY() + INSTANCE.getMap().getRedBed().getBlockY())/2;
                    event.setCancelled(type != Material.CUT_SANDSTONE || !(middleY - 6 <= y && y <= middleY + 4));

                    if (!event.isCancelled()) {
                        if (event.getItemInHand().getAmount() <= 1) {
                            event.getItemInHand().setAmount(64);
                        }
                    }
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void setEntitySource(EntityDamageEvent event) {
        if (INSTANCE.getManager().getStatus() != GameStatus.GAME_NOT_SETUP) {
            if (INSTANCE.getManager().getStatus() == GameStatus.IN_GAME) {
                if (event.getEntity() instanceof Player attacked && event.getDamageSource().getCausingEntity() instanceof Player damaged) {
                    this.entitySource.put(attacked.getUniqueId(), damaged.getUniqueId());
                    if (attacked.getHealth() <= 0) {
                        event.setCancelled(true);
                        INSTANCE.getManager().respawnPlayer(attacked);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (INSTANCE.getManager().getStatus() != GameStatus.GAME_NOT_SETUP) {
            if (INSTANCE.getManager().isNotFreezePlayers()) {
                if (INSTANCE.getManager().getStatus() == GameStatus.IN_GAME) {
                    onFallen(event);
                    onScore(event);
                }
            } else {
                Team team = INSTANCE.getManager().getPlayerTeam(event.getPlayer().getUniqueId());
                if (team == Team.BLUE) {
                    HikabrainManager.teleportPlayer(event.getPlayer(), INSTANCE.getMap().getBlueSpawn(), INSTANCE.getMap().getRedSpawn());
                }

            }

        }
    }

    private static void onScore(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        Team team = INSTANCE.getManager().getPlayerTeam(p.getUniqueId());

        if (team != null) {
            Location loc = p.getLocation();
            Location bedLoc;

            if (team == Team.BLUE) {
                bedLoc = INSTANCE.getMap().getRedBed().clone();
            } else {
                bedLoc = INSTANCE.getMap().getBlueBed().clone();
            }

            bedLoc.setX(bedLoc.getX()+0.5);
            bedLoc.setY(bedLoc.getY()+0.5);
            bedLoc.setZ(bedLoc.getZ()+0.5);

            if (bedLoc.distance(loc) < 0.91) {
                INSTANCE.getManager().teamScored(p, team);
            }
        }
    }

    private void onFallen(PlayerMoveEvent event) {
        int middleY = (INSTANCE.getMap().getBlueBed().getBlockY() + INSTANCE.getMap().getRedBed().getBlockY())/2;
        Player p = event.getPlayer();
        double pY = p.getY();
        if (pY < middleY - 10) {
            UUID damager = entitySource.get(p.getUniqueId());
            if (this.entitySource.get(p.getUniqueId()) != null) {
                Player d = Bukkit.getPlayer(damager);
                if (d != null) {
                    INSTANCE.getManager().respawnPlayer(p);
                    Team playerTeam = INSTANCE.getManager().getPlayerTeam(p.getUniqueId());
                    Team damagerTeam = INSTANCE.getManager().getPlayerTeam(d.getUniqueId());
                    if (playerTeam != null && damagerTeam != null) {
                        for (Player _p : Bukkit.getOnlinePlayers()) {
                            _p.sendMessage(prefixMessage + playerTeam.getColor() + p.getName() + ChatColor.GOLD + " a été tué par " + damagerTeam.getColor() + d.getName() + ChatColor.GOLD + ".");
                        }
                    }
                }

            } else {
                INSTANCE.getManager().respawnPlayer(p);
                Team playerTeam = INSTANCE.getManager().getPlayerTeam(p.getUniqueId());
                if (playerTeam != null) {
                    for (Player _p : Bukkit.getOnlinePlayers()) {
                        _p.sendMessage(prefixMessage + playerTeam.getColor() + p.getName() + ChatColor.GOLD + " a glissé. Désolé chef!");
                    }
                }

            }
        }
    }
}
