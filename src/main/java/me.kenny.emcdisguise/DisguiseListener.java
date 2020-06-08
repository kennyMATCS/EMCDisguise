package me.kenny.emcdisguise;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

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
                emcDisguise.removeDisguise(player);
            }
        }
    }
}
