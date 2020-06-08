package me.kenny.emcdisguise;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UndisguiseCommand implements CommandExecutor {
    private EMCDisguise emcDisguise;

    public UndisguiseCommand(EMCDisguise emcDisguise) {
        this.emcDisguise = emcDisguise;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (emcDisguise.isDisguised(player)) {
                emcDisguise.removeDisguise(player, true);
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', emcDisguise.getLocaleUndisguiseCommandNotWearingDisguise()));
            }
        }
        return true;
    }
}
