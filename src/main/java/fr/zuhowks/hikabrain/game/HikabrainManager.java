package fr.zuhowks.hikabrain.game;

import fr.zuhowks.hikabrain.game.items.HikaItem;
import fr.zuhowks.hikabrain.game.player.SpawnProtectionRunnable;
import fr.zuhowks.hikabrain.map.HikabrainMap;
import io.papermc.paper.entity.LookAnchor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.UUID;

import static fr.zuhowks.hikabrain.Hikabrain.INSTANCE;
import static fr.zuhowks.hikabrain.Hikabrain.prefixMessage;

public class HikabrainManager extends BukkitRunnable {

    private Team blue;
    private Team red;
    private int maxPerTeam;

    private GameStatus status;
    private boolean freezePlayers = true;

    public HikabrainManager(int maxPerTeam) {
        this.blue = Team.BLUE;
        this.red = Team.RED;
        this.maxPerTeam = maxPerTeam;

        if (INSTANCE.isPartyIsSetup()) {
            this.status = GameStatus.WAITING_PLAYER;
            INSTANCE.getMap().saveMap();
            System.out.println(this.status);
        } else {
            this.status = GameStatus.GAME_NOT_SETUP;
        }

    }

    public Team getBlue() {
        return blue;
    }

    public Team getRed() {
        return red;
    }

    public int getMaxPerTeam() {
        return maxPerTeam;
    }

    public GameStatus getStatus() {
        return status;
    }

    public boolean isNotFreezePlayers() {
        return !freezePlayers;
    }

    public void setMaxPerTeam(int maxPerTeam) {
        this.maxPerTeam = maxPerTeam;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public void setFreezePlayers(boolean freezePlayers) {
        this.freezePlayers = freezePlayers;
    }

    public void join(Player player) {
        HikabrainMap map = INSTANCE.getMap();
        Location blueLoc = map.getBlueSpawn();
        Location redLoc = map.getRedSpawn();

        player.getInventory().clear();
        player.updateInventory();

        if (this.status == GameStatus.WAITING_PLAYER) {
            if (this.getPlayerTeam(player.getUniqueId()) != null) return;
            if (this.blue.getMembers().size() < this.maxPerTeam) {

                player.setGameMode(GameMode.SURVIVAL);
                this.blue.getMembers().add(player.getUniqueId());

                teleportPlayer(player, blueLoc, redLoc);


            } else if (this.red.getMembers().size() < this.maxPerTeam) {

                player.setGameMode(GameMode.SURVIVAL);
                this.red.getMembers().add(player.getUniqueId());

                teleportPlayer(player, redLoc, blueLoc);
            }

            if (this.red.getMembers().size() + this.blue.getMembers().size() >= this.maxPerTeam * 2) {
                this.status = GameStatus.WAITING_FULL;
                this.start();
            }

        }  else if (this.status != GameStatus.GAME_NOT_SETUP) {
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(new Location(blueLoc.getWorld(), (blueLoc.getX()+redLoc.getX())/2, (blueLoc.getY()+redLoc.getY())/2, (blueLoc.getZ()+redLoc.getZ())/2));
        }
    }

    public void start() {
        for (Team team : Team.values()) {
            team.setPoint(0);
        }

        this.status = GameStatus.IN_GAME;
        this.setStuffAndTeleport();
        this.beginRound();
    }

    public void setStuffAndTeleport() {
        HikabrainMap map = INSTANCE.getMap();

        ItemStack blueHelmet = this.getBlueLeatherItem(HikaItem.HELMET.getItemStack());
        ItemStack blueChestplate = this.getBlueLeatherItem(HikaItem.CHESTPLATE.getItemStack());
        ItemStack blueLeggings = this.getBlueLeatherItem(HikaItem.LEGGINGS.getItemStack());
        ItemStack blueBoots = this.getBlueLeatherItem(HikaItem.BOOTS.getItemStack());

        ItemStack redHelmet = this.getRedLeatherItem(HikaItem.HELMET.getItemStack());
        ItemStack redChestplate = this.getRedLeatherItem(HikaItem.CHESTPLATE.getItemStack());
        ItemStack redLeggings = this.getRedLeatherItem(HikaItem.LEGGINGS.getItemStack());
        ItemStack redBoots = this.getRedLeatherItem(HikaItem.BOOTS.getItemStack());

        this.blue.getMembers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                teleportPlayer(player, map.getBlueSpawn(), map.getRedSpawn());

                PlayerInventory inv = player.getInventory();
                setCommonInventory(inv);

                inv.setHelmet(blueHelmet);
                inv.setChestplate(blueChestplate);
                inv.setLeggings(blueLeggings);
                inv.setBoots(blueBoots);

                setMaxHealthTo(player);

            }


        });
        this.red.getMembers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                teleportPlayer(player, map.getRedSpawn(), map.getBlueSpawn());

                PlayerInventory inv = player.getInventory();
                setCommonInventory(inv);

                inv.setHelmet(redHelmet);
                inv.setChestplate(redChestplate);
                inv.setLeggings(redLeggings);
                inv.setBoots(redBoots);

                setMaxHealthTo(player);
            }
        });
    }

    public static void teleportPlayer(Player player, Location loc, Location look) {
        player.teleport(loc);
        player.lookAt(look, LookAnchor.EYES);
        player.lookAt(look, LookAnchor.FEET);
    }

    public void respawnPlayer(Player player) {
        Team team = this.getPlayerTeam(player.getUniqueId());
        setMaxHealthTo(player);
        if (team != null) {
            PlayerInventory inv = player.getInventory();
            HikabrainMap map = INSTANCE.getMap();
            if (team == Team.BLUE) {
                teleportPlayer(player, map.getBlueSpawn(), map.getRedSpawn());

                ItemStack blueHelmet = this.getBlueLeatherItem(HikaItem.HELMET.getItemStack());
                ItemStack blueChestplate = this.getBlueLeatherItem(HikaItem.CHESTPLATE.getItemStack());
                ItemStack blueLeggings = this.getBlueLeatherItem(HikaItem.LEGGINGS.getItemStack());
                ItemStack blueBoots = this.getBlueLeatherItem(HikaItem.BOOTS.getItemStack());

                inv.setHelmet(blueHelmet);
                inv.setChestplate(blueChestplate);
                inv.setLeggings(blueLeggings);
                inv.setBoots(blueBoots);
            } else {
                teleportPlayer(player, map.getRedSpawn(), map.getBlueSpawn());

                ItemStack redHelmet = this.getRedLeatherItem(HikaItem.HELMET.getItemStack());
                ItemStack redChestplate = this.getRedLeatherItem(HikaItem.CHESTPLATE.getItemStack());
                ItemStack redLeggings = this.getRedLeatherItem(HikaItem.LEGGINGS.getItemStack());
                ItemStack redBoots = this.getRedLeatherItem(HikaItem.BOOTS.getItemStack());


                inv.setHelmet(redHelmet);
                inv.setChestplate(redChestplate);
                inv.setLeggings(redLeggings);
                inv.setBoots(redBoots);
            }

            setCommonInventory(inv);
        }

        new SpawnProtectionRunnable(player).startProtection(INSTANCE);
    }

    private static void setMaxHealthTo(Player player) {
        player.setHealth(player.getHealthScale());
        player.sendHealthUpdate();
    }

    public ItemStack getBlueLeatherItem(ItemStack item) {
        ItemStack clone = item.clone();
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(Color.BLUE);
        clone.setItemMeta(meta);

        return clone;
    }

    public ItemStack getRedLeatherItem(ItemStack item) {
        ItemStack clone = item.clone();
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(Color.RED);
        clone.setItemMeta(meta);

        return clone;
    }

    public void setCommonInventory(PlayerInventory inv) {
        inv.setItem(0, HikaItem.SWORD.getItemStack());
        inv.setItem(1, HikaItem.PICKAXE.getItemStack());
        inv.setItem(2, HikaItem.GOLDEN_APPLE.getItemStack());
        inv.setItem(3, HikaItem.SANDSTONE.getItemStack());
        inv.setItem(4, HikaItem.SANDSTONE.getItemStack());
        inv.setItem(5, HikaItem.SANDSTONE.getItemStack());
        inv.setItem(6, HikaItem.SANDSTONE.getItemStack());
        inv.setItem(7, HikaItem.SANDSTONE.getItemStack());
        inv.setItem(8, HikaItem.SANDSTONE.getItemStack());
        inv.setItemInOffHand(HikaItem.SANDSTONE.getItemStack());
    }

    @Nullable
    public Team getPlayerTeam(UUID uuid) {
        for (Team team : Team.values()) {
            for (UUID _uuid : team.getMembers()) {
                if (_uuid.equals(uuid)) {
                    return team;

                }
            }
        }

        return null;
    }

    @Override
    public void run() {
        //TODO: IMPLEMENT SCOREBOARD
        this.cancel();
    }

    public void teamScored(Player player, Team team) {
        team.setPoint(team.getPoint() + 1);
        INSTANCE.getMap().resetMap();

        if (team.getPoint() >= 5) {
            this.end(team);
        } else {
            for (Player _p : Bukkit.getOnlinePlayers()) {
                _p.sendMessage(prefixMessage + team.getColor() + player.getName() + ChatColor.GOLD + " a marqué +1 point !");
            }
            this.beginRound();
        }


    }

    public void end(Team team) {
        this.status = GameStatus.FINISH;
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.getInventory().clear();
            p.updateInventory();
            p.setGameMode(GameMode.SPECTATOR);
            p.sendMessage(prefixMessage + ChatColor.GOLD + "Victoire de l'équipe " + team.getColor() + team.getName() + ChatColor.GOLD + " !");
        }
    }

    public void beginRound() {
        this.freezePlayers = true;
        this.setStuffAndTeleport();

        INSTANCE.getMap().setBlueBlockSpawn();
        INSTANCE.getMap().setRedBlockSpawn();

        announceRound();
    }

    private void announceRound() {
        new BukkitRunnable() {

            int countdown = 5;

            @Override
            public void run() {

                if (countdown <= 0) {
                    INSTANCE.getManager().setFreezePlayers(false);
                    INSTANCE.getMap().removeBlueBlockSpawn();
                    INSTANCE.getMap().removeRedBlockSpawn();
                    for (Player _p : Bukkit.getOnlinePlayers()) {
                        _p.clearTitle();
                        (new SpawnProtectionRunnable(_p)).startProtection(INSTANCE);
                    }

                    this.cancel();
                } else {
                    for (Player _p : Bukkit.getOnlinePlayers()) {
                        _p.sendTitlePart(TitlePart.TITLE, Component.text(ChatColor.GOLD + String.valueOf(countdown)));
                    }
                    countdown--;
                }

            }

        }.runTaskTimer(INSTANCE, 0L, 20L);
    }
}
