package me.kenny.emcdisguise.listener;

import me.kenny.emcdisguise.EMCDisguise;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DisguiseListener implements Listener {
    private EMCDisguise emcDisguise;

    public DisguiseListener(EMCDisguise emcDisguise) {
        this.emcDisguise = emcDisguise;
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (emcDisguise.isDisguised(player)) {
                String disguise = emcDisguise.getPlayerDisguiseType(player).toString();
                emcDisguise.removeDisguise(player, false);
                disguise = WordUtils.capitalizeFully(disguise.replace("_", " "));
                String message = emcDisguise.getLocaleDisguiseRemoveBecauseAttacked().replace("%entity%", disguise);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        emcDisguise.removeDisguise(event.getPlayer(), false);
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        emcDisguise.removeDisguise(event.getPlayer(), false);
    }
}
