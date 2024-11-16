package fr.zuhowks.hikabrain.listeners;

import fr.zuhowks.hikabrain.Hikabrain;
import fr.zuhowks.hikabrain.game.GameStatus;
import fr.zuhowks.hikabrain.game.Team;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;
import java.util.UUID;

public class GameListener implements Listener {
    private Hikabrain INSTANCE = Hikabrain.getINSTANCE();

    private HashMap<UUID, UUID> entitySource = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        //INSTANCE.getManager().join(event.getPlayer());
        INSTANCE.getManager().getBlue().getMembers().removeIf(member -> member.getUniqueId().equals(event.getPlayer().getUniqueId()));
        INSTANCE.getManager().getBlue().getMembers().add(event.getPlayer());
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
            if (INSTANCE.getManager().getStatus() == GameStatus.IN_GAME) {
                Material type = event.getBlock().getType();
                event.setCancelled(type != Material.SANDSTONE);
            }
        }
    }

    @EventHandler
    public void allowedBlockBuild(BlockPlaceEvent event) {
        if (INSTANCE.getManager().getStatus() != GameStatus.GAME_NOT_SETUP) {
            if (INSTANCE.getManager().getStatus() == GameStatus.IN_GAME) {
                Block block = event.getBlock();
                Material type = block.getType();
                int y = block.getY();

                int middleY = (INSTANCE.getMap().getBlueBed().getBlockY() + INSTANCE.getMap().getRedBed().getBlockY())/2;
                event.setCancelled(type != Material.SANDSTONE || !(middleY - 5 <= y && y <= middleY + 4));
            }
        }
    }

    @EventHandler
    public void scored(PlayerMoveEvent event) {
        if (INSTANCE.getManager().getStatus() != GameStatus.GAME_NOT_SETUP) {
            if (INSTANCE.getManager().getStatus() == GameStatus.IN_GAME) {
                Player p = event.getPlayer();
                Team team = INSTANCE.getManager().getPlayerTeam(p);

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

                    System.out.println(bedLoc.distance(loc));

                    if (bedLoc.distance(loc) < 0.91) {
                        //TODO: Send to all players Title point
                        INSTANCE.getManager().setStuffAndTeleport();
                    }
                }


            }

        }
    }

    @EventHandler
    public void setEntitySource(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player attacked && event.getDamageSource().getCausingEntity() instanceof Player damaged) {
            this.entitySource.put(attacked.getUniqueId(), damaged.getUniqueId());
        }
    }

    @EventHandler
    public void playerFallen(PlayerMoveEvent event) {

        if (INSTANCE.getManager().getStatus() != GameStatus.GAME_NOT_SETUP) {
            if (INSTANCE.getManager().getStatus() == GameStatus.IN_GAME) {
                int middleY = (INSTANCE.getMap().getBlueBed().getBlockY() + INSTANCE.getMap().getRedBed().getBlockY())/2;
                double pY = event.getPlayer().getY();
                if (pY < middleY - 10) {
                    UUID damager = entitySource.get(event.getPlayer().getUniqueId());
                    if (this.entitySource.get(event.getPlayer().getUniqueId()) != null) {
                        event.getPlayer().damage(10000, INSTANCE.getServer().getPlayer(damager));
                    } else {
                        event.getPlayer().damage(10000);
                    }
                }
            }
        }
    }

    @EventHandler
    public void playerDeath(PlayerRespawnEvent event) {
        if (INSTANCE.getManager().getStatus() != GameStatus.GAME_NOT_SETUP) {
            if (INSTANCE.getManager().getStatus() == GameStatus.IN_GAME) {
                INSTANCE.getManager().respawnPlayer(event.getPlayer());
            }
        }
    }

}
