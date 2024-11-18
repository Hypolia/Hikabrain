package fr.zuhowks.hikabrain.map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.zuhowks.hikabrain.Hikabrain.INSTANCE;
import static fr.zuhowks.hikabrain.Hikabrain.prefixMessage;

public class HikabrainMap {


    private Location blueBed;
    private Location redBed;

    private Location blueSpawn;
    private Location redSpawn;

    private Location pos1;
    private Location pos2;

    private HashMap<Material, List<Location>> registeredBlocks;



    public HikabrainMap() {
        this.blueBed = null;
        this.redBed = null;
        this.blueSpawn = null;
        this.redSpawn = null;
        this.pos1 = null;
        this.pos2 = null;

        this.registeredBlocks = new HashMap<>();
    }

    public Location getRedBed() {
        return redBed;
    }

    public Location getBlueBed() {
        return blueBed;
    }

    public Location getRedSpawn() {
        return redSpawn;
    }

    public Location getBlueSpawn() {
        return blueSpawn;
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public void setRedBed(Location redBed) {
        this.redBed = redBed;
    }

    public void setBlueBed(Location blueBed) {
        this.blueBed = blueBed;
    }

    public void setRedSpawn(Location newLoc) {
        newLoc.set(newLoc.getBlockX() + 0.5, newLoc.getBlockY() + 1, newLoc.getBlockZ() + 0.5);
        this.redSpawn = newLoc;
    }

    public void setBlueSpawn(Location newLoc) {
        newLoc.set(newLoc.getBlockX() + 0.5, newLoc.getBlockY() + 1, newLoc.getBlockZ() + 0.5);
        this.blueSpawn = newLoc;
    }

    public void setPos1(Location pos1) {
        this.pos1 = pos1;
    }

    public void setPos2(Location pos2) {
        this.pos2 = pos2;
    }

    public boolean isReady() {
        return this.blueBed != null && this.redBed != null && this.blueSpawn != null && this.redSpawn != null && this.pos1 != null && this.pos2 != null;
    }

    public void loadMapData() {

        FileConfiguration config = INSTANCE.getHikaConfig();
        this.redBed = (Location) config.get("map.red-bed");
        this.blueBed = (Location) config.get("map.blue-bed");
        this.redSpawn = (Location) config.get("map.red-spawn");
        this.blueSpawn = (Location) config.get("map.blue-spawn");
        this.pos1 = (Location) config.get("map.pos1");
        this.pos2 = (Location) config.get("map.pos2");

    }

    public void saveMapData() {
        FileConfiguration config = INSTANCE.getHikaConfig();
        config.set("map.red-bed", this.getRedBed());
        config.set("map.red-spawn", this.getRedSpawn());
        config.set("map.blue-bed", this.getBlueBed());
        config.set("map.blue-spawn", this.getBlueSpawn());
        config.set("map.pos1", this.getPos1());
        config.set("map.pos2", this.getPos2());

        INSTANCE.saveConfig();
    }

    public void setBlueSpawnData(Player p, Location blockLoc) {
        this.setBlueSpawn(blockLoc);

        if (this.getRedSpawn() != null) {
            updateSpawnDirection();
            INSTANCE.getHikaConfig().set("map.red-spawn", this.getRedSpawn());
        }

        INSTANCE.getHikaConfig().set("map.blue-spawn", this.getBlueSpawn());
        p.sendMessage(prefixMessage + ChatColor.GREEN + "Blue team's spawn point has been set !");

        INSTANCE.saveConfig();
    }

    private void updateSpawnDirection() {
        Location blueLoc = this.getBlueSpawn().clone().add(0, 1.5, 0);
        Location redLoc = this.getRedSpawn().clone().add(0, 1.5, 0);

        this.getRedSpawn().setDirection(new Vector(blueLoc.getX(), blueLoc.getY(), blueLoc.getZ()));
        this.getBlueSpawn().setDirection(new Vector(redLoc.getX(), redLoc.getY(), redLoc.getZ()));
    }

    public void setRedSpawnData(Player p, Location blockLoc) {
        this.setRedSpawn(blockLoc);

        if (this.getBlueSpawn() != null) {
            updateSpawnDirection();
            INSTANCE.getHikaConfig().set("map.blue-spawn", this.getBlueSpawn());
        }

        INSTANCE.getHikaConfig().set("map.red-spawn", this.getRedSpawn());
        p.sendMessage(prefixMessage + ChatColor.GREEN + "Red team's spawn point has been set !");
        INSTANCE.saveConfig();
    }

    public void saveMap() {
        World world = this.getPos2().getWorld();

        int xMax = Math.max(this.getPos1().getBlockX(), this.getPos2().getBlockX());
        int yMax = Math.max(this.getPos1().getBlockY(), this.getPos2().getBlockY());
        int zMax = Math.max(this.getPos1().getBlockZ(), this.getPos2().getBlockZ());

        int xMin = Math.min(this.getPos1().getBlockX(), this.getPos2().getBlockX());
        int yMin = Math.min(this.getPos1().getBlockY(), this.getPos2().getBlockY());
        int zMin = Math.min(this.getPos1().getBlockZ(), this.getPos2().getBlockZ());

        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int z = zMin; z <= zMax; z++) {
                    Block block = world.getBlockAt(x, y, z);

                    this.registeredBlocks.computeIfAbsent(block.getType(), k -> new ArrayList<>());

                    this.registeredBlocks.get(block.getType()).add(block.getLocation());
                }
            }
        }
    }

    public void resetMap() {
        for (Map.Entry<Material, List<Location>> entry : this.registeredBlocks.entrySet()) {
            Material material = entry.getKey();
            List<Location> list = entry.getValue();
            for (Location location : list) {
                Block block = location.getBlock();
                block.setType(material);
                block.getState().update();
            }
        }
    }

    private @NotNull Block getBlueBlockSpawn() {
        return this.getBlueSpawn().clone().add(0, -1, 0).getBlock();
    }

    public void setBlueBlockSpawn() {
        Block block = getBlueBlockSpawn();
        block.setType(Material.BLUE_WOOL);
        block.getState().update();
    }

    public void removeBlueBlockSpawn() {
        Block block = getBlueBlockSpawn();
        block.setType(Material.AIR);
        block.getState().update();
    }

    private @NotNull Block getRedBlockSpawn() {
        return this.getRedSpawn().clone().add(0, -1, 0).getBlock();
    }

    public void setRedBlockSpawn() {
        Block block = getRedBlockSpawn();
        block.setType(Material.RED_WOOL);
        block.getState().update();
    }

    public void removeRedBlockSpawn() {
        Block block = getRedBlockSpawn();
        block.setType(Material.AIR);
        block.getState().update();
    }



}
