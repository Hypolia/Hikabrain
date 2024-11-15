package fr.zuhowks.hikabrain;

import fr.zuhowks.hikabrain.commands.CommandAdmin;
import fr.zuhowks.hikabrain.listeners.PlayerListener;
import fr.zuhowks.hikabrain.map.HikabrainMap;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


public final class Hikabrain extends JavaPlugin {

    public static final String prefixMessage = ChatColor.YELLOW + "" + ChatColor.BOLD +  "[" + ChatColor.AQUA + " HIKABRAIN " + ChatColor.YELLOW + "" + ChatColor.BOLD + "]" + ChatColor.RESET + " ";
    private static Hikabrain INSTANCE;
    private FileConfiguration config;
    private final Map<UUID, ItemStack[]> inventoryRegistry = new HashMap<>(); //For setup mod
    private boolean partyIsSetup = false;
    private boolean partyIsStarted = false;

    private HikabrainMap map;

    @Override
    public void onEnable() {
        INSTANCE = this;
        saveDefaultConfig();

        this.config = this.getConfig();

        this.partyIsSetup = this.config.getBoolean("party.setup");

        this.map = new HikabrainMap();
        this.map.loadMapData();


        this.getCommand("ah").setExecutor(new CommandAdmin());
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);



    }

    @Override
    public void onDisable() {
        this.map.saveMapData();
    }

    public Map<UUID, ItemStack[]> getInventoryRegistry() {
        return inventoryRegistry;
    }

    public boolean isInSetupMod(Player p) {
        Map<UUID, ItemStack[]> inventoryRegistry = this.getInventoryRegistry();
        for (UUID uuid : inventoryRegistry.keySet()) {
            if (uuid.equals(p.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    @NotNull
    public FileConfiguration getHikaConfig() {
        return config;
    }

    public boolean isPartyIsSetup() {
        return partyIsSetup;
    }

    public boolean isPartyIsStarted() {
        return partyIsStarted;
    }

    public static Hikabrain getINSTANCE() {
        return INSTANCE;
    }
}