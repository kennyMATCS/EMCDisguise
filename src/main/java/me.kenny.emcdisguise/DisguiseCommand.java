package me.kenny.emcdisguise;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DisguiseCommand implements CommandExecutor {
    private EMCDisguise emcDisguise;

    public DisguiseCommand(EMCDisguise emcDisguise) {
        this.emcDisguise = emcDisguise;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 1) {
                List<String> disguises = emcDisguise.getConfigDisguises();
                for (String disguise : disguises) {
                    if (disguise.toLowerCase().equals(args[0].toLowerCase())) {
                        if (!player.hasPermission("disguise." + disguise.toLowerCase())) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', emcDisguise.getLocaleNoDisguisePermission()));
                        } else {
                            String enumEntity = disguise.toUpperCase();
                            if (!emcDisguise.isDisguised(player)) {
                                emcDisguise.addDisguise(player, DisguiseType.valueOf(enumEntity));
                            } else {
                                if (!enumEntity.equals(emcDisguise.getPlayerDisguiseType(player).toString())) {
                                    emcDisguise.removeDisguise(player, true);
                                    emcDisguise.addDisguise(player, DisguiseType.valueOf(enumEntity));
                                } else {
                                    emcDisguise.removeDisguise(player, true);
                                }
                            }
                        }
                        return true;
                    }
                }
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', emcDisguise.getLocaleDisguiseCommandInvalidDisguise()));
            } else {
                player.openInventory(emcDisguise.getDisguiseGui().getGui(player));
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You must be a player to execute this command!");
        }
        return true;
    }
}
