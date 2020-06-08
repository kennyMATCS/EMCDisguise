package me.kenny.emcdisguise;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArmorStandConstructor implements Listener {
    private EMCDisguise emcDisguise;
    private Map<Player, ArmorStand> armorStands = new HashMap<>();

    public ArmorStandConstructor(EMCDisguise emcDisguise) {
        this.emcDisguise = emcDisguise;
    }

    public void update(Player player) {
        boolean canNameTagToggle = emcDisguise.isNameTagToggled(player);
        if (canNameTagToggle) {
            if (!armorStands.containsKey(player) && emcDisguise.isDisguised(player)) {
                addArmorstand(player);
            }
        } else {
            if (armorStands.containsKey(player) && emcDisguise.isDisguised(player)) {
                removeArmorStand(player);
            }
        }
    }

    public void addArmorstand(Player player) {
        boolean canNameTagToggle = emcDisguise.isNameTagToggled(player);
        if (canNameTagToggle) {
            ArmorStand armorStand = (ArmorStand) player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
            armorStand.setVisible(false);
            armorStand.setCustomName(player.getName());
            armorStand.setCustomNameVisible(true);
            armorStand.setGravity(false);
            armorStand.setSmall(true);
            player.setPassenger(armorStand);
            armorStands.put(player, armorStand);
        }
    }

    public void removeArmorStand(Player player) {
        if (armorStands.containsKey(player)) {
            armorStands.get(player).remove();
            armorStands.remove(player);
        }
    }

    public void removeAll() {
        for (Player player : armorStands.keySet()) {
            removeArmorStand(player);
        }
    }
}
