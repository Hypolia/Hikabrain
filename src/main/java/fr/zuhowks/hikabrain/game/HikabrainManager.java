package fr.zuhowks.hikabrain.game;

import fr.zuhowks.hikabrain.Hikabrain;
import fr.zuhowks.hikabrain.game.items.HikaItem;
import fr.zuhowks.hikabrain.map.HikabrainMap;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;

public class HikabrainManager extends BukkitRunnable {
    private Hikabrain INSTANCE = Hikabrain.getINSTANCE();

    private Team blue;
    private Team red;
    private int maxPerTeam;

    private GameStatus status;

    public HikabrainManager(int maxPerTeam) {
        this.blue = Team.BLUE;
        this.red = Team.RED;
        this.maxPerTeam = maxPerTeam;

        if (INSTANCE.isPartyIsSetup()) {
            this.status = GameStatus.WAITING_PLAYER;
        }

        this.status = GameStatus.GAME_NOT_SETUP;
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

    public void setMaxPerTeam(int maxPerTeam) {
        this.maxPerTeam = maxPerTeam;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public void join(Player player) {
        HikabrainMap map = INSTANCE.getMap();
        Location blueLoc = map.getBlueSpawn();
        Location redLoc = map.getRedSpawn();

        if (this.status == GameStatus.WAITING_PLAYER) {
            if (this.blue.getMembers().size() < this.maxPerTeam) {
                player.setGameMode(GameMode.SURVIVAL);
                this.blue.getMembers().add(player);
                player.teleport(blueLoc);

            } else if (this.red.getMembers().size() < this.maxPerTeam) {
                player.setGameMode(GameMode.SURVIVAL);
                this.red.getMembers().add(player);
                player.teleport(redLoc);

            }

            if (this.red.getMembers().size() + this.blue.getMembers().size() == this.maxPerTeam * 2) {
                this.status = GameStatus.WAITING_FULL;
            }

        }  else if (this.status == GameStatus.GAME_NOT_SETUP) {
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(new Location(blueLoc.getWorld(), (blueLoc.getX()+redLoc.getX())/2, (blueLoc.getY()+redLoc.getY())/2, (blueLoc.getZ()+redLoc.getZ())/2));
        }
    }

    public void start() {
        this.status = GameStatus.IN_GAME;

        setStuffAndTeleport();
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

        this.blue.getMembers().forEach(player -> {
            player.teleport(map.getBlueSpawn());
            PlayerInventory inv = player.getInventory();
            setCommonInventory(inv);

            inv.setHelmet(blueHelmet);
            inv.setChestplate(blueChestplate);
            inv.setLeggings(blueLeggings);
            inv.setBoots(blueBoots);

        });
        this.red.getMembers().forEach(player -> {
            player.teleport(map.getRedSpawn());
            PlayerInventory inv = player.getInventory();
            setCommonInventory(inv);

            inv.setHelmet(redHelmet);
            inv.setChestplate(redChestplate);
            inv.setLeggings(redLeggings);
            inv.setBoots(redBoots);
        });
    }

    public void respawnPlayer(Player player) {
        Team team = this.getPlayerTeam(player);
        if (team != null) {
            PlayerInventory inv = player.getInventory();
            HikabrainMap map = INSTANCE.getMap();
            if (team == Team.BLUE) {
                player.teleport(map.getBlueSpawn());
                ItemStack blueHelmet = this.getBlueLeatherItem(HikaItem.HELMET.getItemStack());
                ItemStack blueChestplate = this.getBlueLeatherItem(HikaItem.CHESTPLATE.getItemStack());
                ItemStack blueLeggings = this.getBlueLeatherItem(HikaItem.LEGGINGS.getItemStack());
                ItemStack blueBoots = this.getBlueLeatherItem(HikaItem.BOOTS.getItemStack());

                inv.setHelmet(blueHelmet);
                inv.setChestplate(blueChestplate);
                inv.setLeggings(blueLeggings);
                inv.setBoots(blueBoots);
            } else {
                player.teleport(map.getRedSpawn());
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
    }

    @Nullable
    public Team getPlayerTeam(Player player) {
        for (Team team : Team.values()) {
            for (Player _player : team.getMembers()) {
                if (_player.getUniqueId().equals(player.getUniqueId())) {
                    return team;
                }
            }
        }

        return null;
    }

    @Override
    public void run() {
        this.cancel();
    }
}
