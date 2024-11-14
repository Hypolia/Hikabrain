package fr.zuhowks.hikabrain;

import fr.zuhowks.hikabrain.commands.CommandAdmin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public final class Hikabrain extends JavaPlugin {

    public static final String prefixMessage = ChatColor.YELLOW + "" + ChatColor.BOLD +  "[" + ChatColor.AQUA + " HIKABRAIN " + ChatColor.YELLOW + "" + ChatColor.BOLD + "]" + ChatColor.RESET + " ";
    private static Hikabrain INSTANCE;
    private FileConfiguration config;
    private final Map<UUID, ItemStack[]> inventoryRegistry = new HashMap<>(); //For setup mod
    private boolean partyIsSetup = false;

    @Override
    public void onEnable() {
        INSTANCE = this;
        this.config = this.getConfig();
        saveDefaultConfig();

        this.partyIsSetup = config.getBoolean("party.isSetup");

        this.getCommand("ah").setExecutor(new CommandAdmin());


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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
}