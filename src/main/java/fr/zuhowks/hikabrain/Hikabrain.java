package fr.zuhowks.hikabrain;

import fr.zuhowks.hikabrain.commands.CommandAdmin;
import fr.zuhowks.hikabrain.game.HikabrainManager;
import fr.zuhowks.hikabrain.game.items.HikaItem;
import fr.zuhowks.hikabrain.listeners.AdminListener;
import fr.zuhowks.hikabrain.listeners.GameListener;
import fr.zuhowks.hikabrain.map.HikabrainMap;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public final class Hikabrain extends JavaPlugin {

    public static final String prefixMessage = ChatColor.YELLOW + "" + ChatColor.BOLD +  "[" + ChatColor.AQUA + " HIKABRAIN " + ChatColor.YELLOW + "" + ChatColor.BOLD + "]" + ChatColor.RESET + " ";
    private static Hikabrain INSTANCE;
    private FileConfiguration config;
    private final Map<UUID, ItemStack[]> inventoryRegistry = new HashMap<>(); //For setup mod
    private boolean partyIsSetup = false;
    private HikabrainManager manager;

    private HikabrainMap map;

    @Override
    public void onEnable() {
        INSTANCE = this;
        saveDefaultConfig();

        this.config = this.getConfig();

        this.partyIsSetup = this.config.getBoolean("party.setup");

        this.map = new HikabrainMap();
        this.map.loadMapData();

        this.manager = new HikabrainManager(this.config.getInt("party.max-per-team"));

        this.applyEnchantmentsOnHikaItems();
        this.setGameRules();


        this.getCommand("ah").setExecutor(new CommandAdmin());
        getServer().getPluginManager().registerEvents(new AdminListener(), this);
        getServer().getPluginManager().registerEvents(new GameListener(), this);
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

    public HikabrainMap getMap() {
        return map;
    }

    public HikabrainManager getManager() {
        return manager;
    }

    public static Hikabrain getINSTANCE() {
        return INSTANCE;
    }

    public void applyEnchantmentsOnHikaItems() {
        Map<Enchantment, Integer> basicEnchantments = new HashMap<>();
        basicEnchantments.put(Enchantment.UNBREAKING, 999);
        HikaItem.SWORD.applyEnchantments(basicEnchantments);

        basicEnchantments.put(Enchantment.EFFICIENCY, 2);
        HikaItem.PICKAXE.applyEnchantments(basicEnchantments);

        Map<Enchantment, Integer> armorEnchantments = new HashMap<>();
        armorEnchantments.put(Enchantment.UNBREAKING, 999);
        armorEnchantments.put(Enchantment.PROTECTION, 3);
        HikaItem.HELMET.applyEnchantments(armorEnchantments);
        HikaItem.CHESTPLATE.applyEnchantments(armorEnchantments);
        HikaItem.LEGGINGS.applyEnchantments(armorEnchantments);
        HikaItem.BOOTS.applyEnchantments(armorEnchantments);
    }

    public void setGameRules() {
        getServer().getWorlds().forEach(world -> {
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN,true);
            world.setGameRule(GameRule.DO_MOB_SPAWNING,false);
            world.setGameRule(GameRule.DO_MOB_LOOT,false);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE,false);
            world.setGameRule(GameRule.KEEP_INVENTORY,true);
        });
    }
}