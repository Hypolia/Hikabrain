package fr.zuhowks.hikabrain.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public enum SetupModItems {
    SETUP_BLUE_SPAWN(Material.BLUE_WOOL, "§6§lSET §9§lBLUE SPAWN", "ah setspawn blue",  ChatColor.GRAY + "/ah setspawn blue -> To set the blue team's spawn point.", ChatColor.GRAY + "(Put the block)"),
    SETUP_BLUE_BED(Material.BLUE_BED, "§6§lSETUP §9§lBLUE BED", "ah setupbed blue",  ChatColor.GRAY + "/ah setbed blue -> To setup the blue team's bed location.", ChatColor.GRAY + "(Put the bed)"),
    SETUP_RED_SPAWN(Material.RED_WOOL, "§6§lSET §4§lRED SPAWN", "ah setupspawn red",  ChatColor.GRAY + "/ah setspawn red -> To set the red team's spawn point.", ChatColor.GRAY + "(Put the block)"),
    SETUP_RED_BED(Material.RED_BED, "§6§lSETUP §4§lRED BED", "ah setupbed red",  ChatColor.GRAY + "/ah setbed red -> To setup the red team's bed location.", ChatColor.GRAY + "(Put the bed)"),
    ;

    private final ItemStack itemStack;
    private final String command;

    private final String displayName;
    SetupModItems(Material material, String displayName, String command, String... lore) {
        this.itemStack = new ItemStack(material, 1);
        this.command = command;
        this.displayName = displayName;
        //Set a basic item meta
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(Arrays.asList(lore));
        this.itemStack.setItemMeta(meta);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getCommand() {
        return command;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static SetupModItems isInSetupModItems(ItemStack item) {
        for (SetupModItems setupModItems : SetupModItems.values()) {
            if (setupModItems.getItemStack().getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) {
                return setupModItems;
            }
        }
        return null;
    }
}