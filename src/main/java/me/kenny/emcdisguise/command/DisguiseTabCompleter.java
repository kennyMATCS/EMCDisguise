package me.kenny.emcdisguise.command;

import com.google.common.collect.Lists;
import me.kenny.emcdisguise.EMCDisguise;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DisguiseTabCompleter implements TabCompleter {
    private EMCDisguise emcDisguise;
    private List<String> completions = new ArrayList<>();

    public DisguiseTabCompleter(EMCDisguise emcDisguise) {
        this.emcDisguise = emcDisguise;

        for (String disguise : emcDisguise.getConfigDisguises()) {
            completions.add(disguise.toUpperCase());
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 1) {
                List<String> finalCompletions = new ArrayList<>();
                StringUtil.copyPartialMatches(args[0], completions, finalCompletions);
                finalCompletions.addAll(Arrays.asList("perspective", "name"));
                Collections.sort(finalCompletions);
                return finalCompletions;
            }
        }
        return Lists.newArrayList();
    }
}
