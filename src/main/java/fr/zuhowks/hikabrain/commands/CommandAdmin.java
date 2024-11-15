 package fr.zuhowks.hikabrain.commands;

import fr.zuhowks.hikabrain.Hikabrain;
import fr.zuhowks.hikabrain.utils.SetupModItems;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;
import java.util.UUID;

import static fr.zuhowks.hikabrain.Hikabrain.prefixMessage;

public class CommandAdmin implements CommandExecutor {

    final Hikabrain INSTANCE = Hikabrain.getINSTANCE();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (sender instanceof Player) {
            Player p = (Player) sender;
            int argsLen = args.length;
            if (p.hasPermission("hikabrain.admin")) {
                if (INSTANCE.isInSetupMod(p)) {
                    if (argsLen == 1) {
                        if (args[0].equalsIgnoreCase("setupmod")) {
                            PlayerInventory inv = p.getInventory();
                            UUID uuid = p.getUniqueId();
                            Map<UUID, ItemStack[]> inventoryMap = INSTANCE.getInventoryRegistry();
                            inv.setContents(inventoryMap.get(uuid));
                            p.updateInventory();
                            inventoryMap.remove(uuid);
                            p.sendMessage(prefixMessage + ChatColor.GREEN + "Setup mod disable !");

                        }
                    } else if (argsLen == 2) {
                        Location loc = p.getLocation();
                        if (args[0].equalsIgnoreCase("setspawn")) {
                            if (args[1].equalsIgnoreCase("blue")) {
                                INSTANCE.getHikaConfig().set("map.blue-spawn", loc);
                                p.sendMessage(prefixMessage + ChatColor.GREEN + " Blue team's spawn point has been set !");
                                INSTANCE.saveConfig();
                            } else if (args[1].equalsIgnoreCase("red")) {
                                INSTANCE.getHikaConfig().set("map.red-spawn", loc);
                                p.sendMessage(prefixMessage + ChatColor.GREEN + " Red team's spawn point has been set !");
                                INSTANCE.saveConfig();
                            } else {
                                p.sendMessage(prefixMessage + ChatColor.RED + " try command /ah help.");
                            }

                        } else if (args[0].equalsIgnoreCase("setbed")) {
                            if (args[1].equalsIgnoreCase("blue")) {
                                INSTANCE.getHikaConfig().set("map.blue-bed", loc);
                                p.sendMessage(prefixMessage + ChatColor.GREEN + " Blue team's bed location has been set !");
                                INSTANCE.saveConfig();
                                Block block = p.getLocation().getBlock();
                                block.setType(Material.BLUE_BED);
                            } else if (args[1].equalsIgnoreCase("red")) {
                                INSTANCE.getHikaConfig().set("map.red-bed", loc);
                                p.sendMessage(prefixMessage + ChatColor.GREEN + " Red team's bed location has been set !");
                                INSTANCE.saveConfig();
                                Block block = p.getLocation().getBlock();
                                block.setType(Material.RED_BED);
                            } else {
                                p.sendMessage(prefixMessage + ChatColor.RED + " try command /ah help.");
                            }
                        } else {
                            p.sendMessage(prefixMessage + ChatColor.RED + " try command /ah help.");
                        }

                    } else {
                        p.sendMessage(prefixMessage + ChatColor.RED + " try command /ah help.");                    }
                } else {
                    if (argsLen == 1) {
                        if (args[0].equalsIgnoreCase("setupmod")) {
                            INSTANCE.getInventoryRegistry().put(p.getUniqueId(), p.getInventory().getContents());
                            setupMod(p.getInventory());
                            p.updateInventory();

                            p.sendMessage(prefixMessage + ChatColor.GREEN + "Setup mod activate ! Use items in your inventory to setup the party without command.");
                        } else if (args[0].equalsIgnoreCase("enable")) {

                        }
                    } else {
                        p.sendMessage(prefixMessage + ChatColor.RED + " try command /ah help.");
                    }
                }

                return true;
            }
        }
        return false;
    }

    private void setupMod(PlayerInventory inv) {
        inv.clear();
        inv.setItem(0, SetupModItems.SETUP_BLUE_SPAWN.getItemStack());
        inv.setItem(1, SetupModItems.SETUP_BLUE_BED.getItemStack());
        inv.setItem(2, SetupModItems.SETUP_RED_SPAWN.getItemStack());
        inv.setItem(3, SetupModItems.SETUP_RED_BED.getItemStack());

    }
}
