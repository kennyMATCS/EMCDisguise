package me.kenny.emcdisguise;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DisguiseGui implements Listener {
    private EMCDisguise emcDisguise;

    private Inventory gui;

    public DisguiseGui(EMCDisguise emcDisguise) {
        this.emcDisguise = emcDisguise;

        gui = createDisguiseGui();
    }

    private Inventory createDisguiseGui() {
        Inventory inventory = Bukkit.createInventory(null, 36, ChatColor.translateAlternateColorCodes('&', emcDisguise.getLocaleDisguiseGuiTitle()));
        ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE);

        String color = emcDisguise.getConfigDisguiseGuiPaneColor();
        try {
            pane.setDurability((short) DyeColor.valueOf(color.toUpperCase()).getWoolData());
        } catch (IllegalArgumentException e) {
            emcDisguise.getServer().getConsoleSender().sendMessage("[" + emcDisguise.getName() + "] " + ChatColor.YELLOW + color + ChatColor.RED + " is not a valid glass pane color. Defaulting to GRAY.");
            String validColors = ChatColor.RED + "Valid colors: ";
            for (int i = 0; i < DyeColor.values().length; i++) {
                DyeColor dyeColor = DyeColor.values()[i];
                if (i == DyeColor.values().length - 1)
                    validColors = validColors + dyeColor + ".";
                else
                    validColors = validColors + dyeColor + ", ";
            }
            emcDisguise.getServer().getConsoleSender().sendMessage("[" + emcDisguise.getName() + "] " + validColors);
            pane.setDurability((short) DyeColor.GRAY.getWoolData());
        }

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, pane);
        }

        for (int i = 18; i < 27; i++) {
            inventory.setItem(i, pane);
        }

        for (int i = 27; i < 36; i++) {
            if (i != 30 && i != 32)
                inventory.setItem(i, pane);
        }

        ItemStack toggleNameTag = new ItemStack(Material.PAPER);
        ItemMeta toggleNameTagItemMeta = toggleNameTag.getItemMeta();
        toggleNameTagItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', emcDisguise.getLocaleDisguiseGuiToggleNameTagButton()));
        toggleNameTag.setItemMeta(toggleNameTagItemMeta);
        inventory.setItem(32, toggleNameTag);

        ItemStack exit = new ItemStack(Material.BARRIER);
        ItemMeta exitItemMeta = exit.getItemMeta();
        exitItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', emcDisguise.getLocaleDisguiseGuiExitButton()));
        exit.setItemMeta(exitItemMeta);
        inventory.setItem(30, exit);

        for (String entity : emcDisguise.getConfigDisguises()) {
            inventory.addItem(getHeadItemStack(entity));
        }
        return inventory;
    }

    public Inventory getGui(Player player) {
        Inventory clone = Bukkit.createInventory(null, gui.getSize(), gui.getTitle());
        clone.setContents(gui.getContents());

        for (ItemStack itemStack : clone.getContents()) {
            if (itemStack != null && itemStack.getType() == Material.SKULL_ITEM) {
                String entity = ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).replace(" Disguise", "");
                entity = entity.toLowerCase().replace(" ", "_");
                if (!player.hasPermission("disguise." + entity)) {
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    String message = emcDisguise.getLocaleNoDisguisePermission();
                    itemMeta.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', message)));
                    itemStack.setItemMeta(itemMeta);
                }

                if (emcDisguise.isDisguised(player) && emcDisguise.getPlayerDisguiseType(player).toString().equals(entity.toUpperCase())) {
                    itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    itemStack.setItemMeta(itemMeta);
                }
            }
        }
        return clone;
    }

    public ItemStack getHeadItemStack(String entity) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        String localeDisguiseDisplayName = emcDisguise.getLocaleDisguiseDisplayName().replace("%entity%", entity).replace("_", " ");
        String localeClickToUseDisguise = emcDisguise.getLocaleClickToUseDisguise().replace("%entity%", entity).replace("_", " ");
        skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', localeDisguiseDisplayName));
        skullMeta.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', localeClickToUseDisguise)));
        skullMeta.setOwner(getOwner(entity));
        head.setItemMeta(skullMeta);
        return head;
    }

    public String getOwner(String entity) {
        String owner;
        switch (entity.toLowerCase()) {
            case "silverfish":
                owner = "BoxicToxic";
                break;
            case "iron_golem":
                owner = "Kennardinbound";
                break;
            default:
                owner = "MHF_" + entity;
                break;
        }
        return owner;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', emcDisguise.getLocaleDisguiseGuiTitle()))) {
            event.setCancelled(true);

            ItemStack clicked = event.getCurrentItem();
            Player player = (Player) event.getWhoClicked();
            if (clicked != null) {
                if (clicked.getType() == Material.SKULL_ITEM) {
                    String entity = ChatColor.stripColor(clicked.getItemMeta().getDisplayName()).replace(" Disguise", "");
                    String enumEntity = entity.toUpperCase().replace(" ", "_");
                    if (player.hasPermission("disguise." + enumEntity.toLowerCase())) {
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
                        player.closeInventory();
                    }
                } else if (clicked.getType() == Material.BARRIER) {
                    player.closeInventory();
                } else if (clicked.getType() == Material.PAPER) {
                    emcDisguise.toggleNameTag(player);
                    player.closeInventory();
                }
            }
        }
    }
}
