package fr.zuhowks.hikabrain;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;



public final class Hikabrain extends JavaPlugin {

    public static final String prefixMessage = ChatColor.YELLOW + "" + ChatColor.BOLD +  "[" + ChatColor.AQUA + " HIKABRAIN " + ChatColor.YELLOW + "" + ChatColor.BOLD + "]" + ChatColor.RESET + " ";
    private static Hikabrain INSTANCE;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        INSTANCE = this;
        this.config = this.getConfig();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}