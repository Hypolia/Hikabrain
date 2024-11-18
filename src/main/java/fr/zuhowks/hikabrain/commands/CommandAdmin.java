 package fr.zuhowks.hikabrain.commands;

 import fr.zuhowks.hikabrain.game.GameStatus;
 import fr.zuhowks.hikabrain.game.Team;
 import fr.zuhowks.hikabrain.utils.SetupModItems;
 import org.bukkit.ChatColor;
 import org.bukkit.Location;
 import org.bukkit.Material;
 import org.bukkit.block.Block;
 import org.bukkit.block.BlockFace;
 import org.bukkit.block.data.type.Bed;
 import org.bukkit.command.Command;
 import org.bukkit.command.CommandExecutor;
 import org.bukkit.command.CommandSender;
 import org.bukkit.entity.Player;
 import org.bukkit.inventory.ItemStack;
 import org.bukkit.inventory.PlayerInventory;

 import javax.annotation.Nullable;
 import java.util.Map;
 import java.util.UUID;

 import static fr.zuhowks.hikabrain.Hikabrain.INSTANCE;
 import static fr.zuhowks.hikabrain.Hikabrain.prefixMessage;

public class CommandAdmin implements CommandExecutor {


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

                        } else if (args[0].equalsIgnoreCase("setpos")) {
                            p.getInventory().setItem(8, SetupModItems.SET_MAP_POS.getItemStack());
                            p.sendMessage(prefixMessage + ChatColor.GREEN + "Set position !");

                        }
                    } else if (argsLen == 2) {
                        Location loc = p.getLocation();
                        if (args[0].equalsIgnoreCase("setspawn")) {
                            if (args[1].equalsIgnoreCase("blue")) {
                                INSTANCE.getMap().setBlueSpawnData(p, loc);

                            } else if (args[1].equalsIgnoreCase("red")) {
                                INSTANCE.getMap().setRedSpawnData(p, loc);

                            } else {
                                p.sendMessage(prefixMessage + ChatColor.RED + "try command /ah help.");
                            }

                        } else if (args[0].equalsIgnoreCase("setbed")) {
                            if (args[1].equalsIgnoreCase("blue")) {
                                Block bed = findAvailableBed(loc, "BLUE");
                                if (bed != null) {
                                    INSTANCE.getHikaConfig().set("map.blue-bed", getFootOfBed(bed).getLocation());
                                    p.sendMessage(prefixMessage + ChatColor.GREEN + "Blue team's bed location has been set !");

                                    INSTANCE.saveConfig();
                                    INSTANCE.getMap().setBlueBed(loc);

                                } else {
                                    p.sendMessage(prefixMessage + ChatColor.RED + "Any blue bed has not been find around you. Please execute this command close to it.");
                                }

                            } else if (args[1].equalsIgnoreCase("red")) {
                                Block bed = findAvailableBed(loc, "RED");
                                if (bed != null) {
                                    INSTANCE.getHikaConfig().set("map.red-bed", getFootOfBed(bed).getLocation());
                                    p.sendMessage(prefixMessage + ChatColor.GREEN + "Red team's bed location has been set !");
                                    INSTANCE.saveConfig();

                                    INSTANCE.getMap().setRedBed(loc);
                                } else {
                                    p.sendMessage(prefixMessage + ChatColor.RED + "Any red bed has not been find around you. Please execute this command close to it.");
                                }

                            } else {
                                p.sendMessage(prefixMessage + ChatColor.RED + "try command /ah help.");
                            }
                        } else {
                            p.sendMessage(prefixMessage + ChatColor.RED + "try command /ah help.");
                        }

                    } else {
                        p.sendMessage(prefixMessage + ChatColor.RED + "try command /ah help.");                    }
                } else {
                    if (argsLen == 1) {

                        if (args[0].equalsIgnoreCase("reload")) {
                            INSTANCE.reloadConfig();
                        } else if (args[0].equalsIgnoreCase("setupmod")) {
                            INSTANCE.getInventoryRegistry().put(p.getUniqueId(), p.getInventory().getContents());
                            setupMod(p.getInventory());
                            p.updateInventory();

                            p.sendMessage(prefixMessage + ChatColor.GREEN + "Setup mod activate ! Use items in your inventory to setup the party without command.");
                        } else if (args[0].equalsIgnoreCase("enable")) {
                            if (INSTANCE.getMap().isReady()) {
                                INSTANCE.getHikaConfig().set("party.setup", true);
                                INSTANCE.setPartyIsSetup(true);

                                Team.BLUE.getMembers().clear();
                                Team.RED.getMembers().clear();

                                INSTANCE.getMap().setBlueBlockSpawn();
                                INSTANCE.getMap().setRedBlockSpawn();

                                p.sendMessage(prefixMessage + ChatColor.GREEN + "Enable party setup !");
                                INSTANCE.saveConfig();

                                INSTANCE.getManager().setStatus(GameStatus.WAITING_PLAYER);
                                INSTANCE.getMap().saveMap();
                            } else {
                                p.sendMessage(prefixMessage + ChatColor.RED + "Can't enable the party !");
                            }

                        } else if (args[0].equalsIgnoreCase("disable")) {
                            INSTANCE.getHikaConfig().set("party.setup", false);
                            INSTANCE.setPartyIsSetup(false);
                            p.sendMessage(prefixMessage + ChatColor.GREEN + "Disable party setup !");
                            INSTANCE.saveConfig();

                            Team.BLUE.getMembers().clear();
                            Team.RED.getMembers().clear();

                            INSTANCE.getMap().removeBlueBlockSpawn();
                            INSTANCE.getMap().removeRedBlockSpawn();

                            INSTANCE.getManager().setStatus(GameStatus.GAME_NOT_SETUP);
                        } else if (args[0].equalsIgnoreCase("start")) {
                            INSTANCE.getManager().start();
                            p.sendMessage(prefixMessage + ChatColor.GREEN + "The party has been started !");
                        } else if (args[0].equalsIgnoreCase("status")) {
                            p.sendMessage(prefixMessage + ChatColor.YELLOW + "Party status: " + (INSTANCE.isPartyIsSetup() ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"));
                        }
                    } else {
                        p.sendMessage(prefixMessage + ChatColor.RED + "try command /ah help.");
                    }
                }

                return true;
            }
        }
        return false;
    }

    private static void setupMod(PlayerInventory inv) {
        inv.clear();
        inv.setItem(0, SetupModItems.SETUP_BLUE_SPAWN.getItemStack());
        inv.setItem(1, SetupModItems.SETUP_BLUE_BED.getItemStack());
        inv.setItem(2, SetupModItems.SETUP_RED_SPAWN.getItemStack());
        inv.setItem(3, SetupModItems.SETUP_RED_BED.getItemStack());
        inv.setItem(8, SetupModItems.SET_MAP_POS.getItemStack());

    }

    private static Block findAvailableBed(Location loc, String bedColor) {
        for (int x = loc.getBlockX() - 1; x <= loc.getBlockX() + 1; x++) {
            for (int y = loc.getBlockY() - 1; y <= loc.getBlockY() + 1; y++) {
                for (int z = loc.getBlockZ() - 1; z <= loc.getBlockZ() + 1; z++) {
                    Block block = loc.getWorld().getBlockAt(x, y, z);
                    if (block.getType() == Material.valueOf(bedColor+"_BED")) {
                        return block;
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    private static Block getFootOfBed(Block bedBlock) {
        // Vérifie que le bloc est bien un lit de la couleur souhaitée
        if (!(bedBlock.getType() == Material.BLUE_BED || bedBlock.getType() == Material.RED_BED)) return null;

        // Récupère les données du bloc sous forme de Bed
        Bed bedData = (Bed) bedBlock.getBlockData();
        BlockFace facing = bedData.getFacing();

        // Vérifie si c'est la tête ou le pied du lit
        if (bedData.getPart() == Bed.Part.HEAD) {
            // Si c'est la tête, on va vers l'arrière pour obtenir le pied
            return bedBlock.getRelative(facing.getOppositeFace());
        } else {
            // Si c'est déjà le pied, on retourne directement ce bloc
            return bedBlock;
        }
    }
}
