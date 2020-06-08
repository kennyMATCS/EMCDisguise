package me.kenny.emcdisguise;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DisguiseCommand implements CommandExecutor {
    private EMCDisguise emcDisguise;

    public DisguiseCommand(EMCDisguise emcDisguise) {
        this.emcDisguise = emcDisguise;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            player.openInventory(emcDisguise.getDisguiseGui().getGui(player));
        } else {
            sender.sendMessage(ChatColor.RED + "You must be a player to execute this command!");
        }
        return true;
    }
}
