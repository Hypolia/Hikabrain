package fr.zuhowks.hikabrain.game.items;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.Map;

public enum HikaItem {

    SWORD(Material.IRON_SWORD, 1, "Sword", "Utilité: Taper"),
    PICKAXE(Material.IRON_PICKAXE, 1, "Axe", "Utilité: Casser"),
    GOLDEN_APPLE(Material.GOLDEN_APPLE, 64, "Caca", "Utilité: Prout"),
    SANDSTONE(Material.SANDSTONE, 64, "Sword", "Utilité: Poser"),
    HELMET(Material.LEATHER_HELMET, 1, "Pipi", "Utilité: T'es bête ou quoi là ?"),
    CHESTPLATE(Material.LEATHER_CHESTPLATE, 1, "Pipi", "Utilité: T'es bête ou quoi là ?"),
    LEGGINGS(Material.LEATHER_LEGGINGS, 1, "Pipi", "Utilité: T'es bête ou quoi là ?"),
    BOOTS(Material.LEATHER_BOOTS, 1, "Pipi", "Utilité: T'es bête ou quoi là ?"),
    ;

    private ItemStack itemStack;

    HikaItem(Material material, int number, String name, String desc) {
        this.itemStack = new ItemStack(material, number);

        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Collections.singletonList(desc));
        itemStack.setItemMeta(meta);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void applyEnchantments(Map<Enchantment, Integer> enchantments) {
        ItemMeta meta = itemStack.getItemMeta();
        for (java.util.Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            meta.addEnchant(entry.getKey(), entry.getValue(), true);
        }
        itemStack.setItemMeta(meta);
    }
}
