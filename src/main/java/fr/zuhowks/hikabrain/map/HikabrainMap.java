package fr.zuhowks.hikabrain.map;

import fr.zuhowks.hikabrain.Hikabrain;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class HikabrainMap {

    private final Hikabrain INSTANCE = Hikabrain.getINSTANCE();

    private Location redBed;
    private Location blueBed;
    private Location redSpawn;
    private Location blueSpawn;

    private List<BlockState> blockToReset;

    public HikabrainMap() {
        this.blockToReset = new ArrayList<>();
    }

    public Hikabrain getINSTANCE() {
        return INSTANCE;
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

    public List<BlockState> getBlockToReset() {
        return this.blockToReset;
    }

    public void setRedBed(Location redBed) {
        this.redBed = redBed;
    }

    public void setBlueBed(Location blueBed) {
        this.blueBed = blueBed;
    }

    public void setRedSpawn(Location redSpawn) {
        this.redSpawn = redSpawn;
    }

    public void setBlueSpawn(Location blueSpawn) {
        this.blueSpawn = blueSpawn;
    }

    public void loadMapData() {

        FileConfiguration config = INSTANCE.getHikaConfig();
        this.setRedBed((Location) config.get("map.red-bed"));
        this.setRedSpawn((Location) config.get("map.red-spawn"));
        this.setBlueBed((Location) config.get("map.blue-bed"));
        this.setBlueSpawn((Location) config.get("map.blue-spawn"));

    }

    public void saveMapData() {
        FileConfiguration config = INSTANCE.getHikaConfig();
        config.set("map.red-bed", this.getRedBed());
        config.set("map.red-spawn", this.getRedSpawn());
        config.set("map.blue-bed", this.getBlueBed());
        config.set("map.blue-spawn", this.getBlueSpawn());
    }



    public void resetMap() {


    }

}
