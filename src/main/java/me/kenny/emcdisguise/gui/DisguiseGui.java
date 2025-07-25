package me.kenny.emcdisguise.gui;

import me.kenny.emcdisguise.EMCDisguise;
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
        Inventory inventory = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', emcDisguise.getLocaleDisguiseGuiTitle()));

        String color1 = emcDisguise.getConfigDisguiseGuiPaneColor1();
        String color2 = emcDisguise.getConfigDisguiseGuiPaneColor2();

        ItemStack color1Pane = getPaneColor(color1);
        ItemMeta color1PaneMeta = color1Pane.getItemMeta();
        color1PaneMeta.setDisplayName(emcDisguise.getLocaleDisguiseGuiPaneColor1DisplayName());
        color1PaneMeta.setLore(emcDisguise.getLocaleDisguiseGuiPaneColor1Lore());
        color1Pane.setItemMeta(color1PaneMeta);

        ItemStack color2Pane = getPaneColor(color2);
        ItemMeta color2PaneMeta = color1Pane.getItemMeta();
        color2PaneMeta.setDisplayName(emcDisguise.getLocaleDisguiseGuiPaneColor2DisplayName());
        color2PaneMeta.setLore(emcDisguise.getLocaleDisguiseGuiPaneColor2Lore());
        color2Pane.setItemMeta(color2PaneMeta);

        for (int i = 0; i < 9; i++) {
            if (i != 4)
                inventory.setItem(i, color1Pane);
        }

        for (int i = 9; i < 18; i++) {
            inventory.setItem(i, color2Pane);
        }

        for (int i = 36; i < 45; i++) {
            inventory.setItem(i, color2Pane);
        }

        for (int i = 45; i < 54; i++) {
            if (i != 45 && i != 49 && i != 53)
                inventory.setItem(i, color1Pane);
        }

        ItemStack information = new ItemStack(Material.PAPER);
        ItemMeta informationItemMeta = information.getItemMeta();
        informationItemMeta.setLore(emcDisguise.getLocaleDisguiseInformationLore());
        informationItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', emcDisguise.getLocaleDisguiseInformationDisplayName()));
        information.setItemMeta(informationItemMeta);
        inventory.setItem(4, information);

        ItemStack toggleNameTag = new ItemStack(Material.NAME_TAG);
        ItemMeta toggleNameTagItemMeta = toggleNameTag.getItemMeta();
        toggleNameTagItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', emcDisguise.getLocaleDisguiseGuiToggleNameTagButton()));
        toggleNameTagItemMeta.setLore(emcDisguise.getLocaleDisguiseGuiToggleNameTagButtonLore());
        toggleNameTag.setItemMeta(toggleNameTagItemMeta);
        inventory.setItem(53, toggleNameTag);

        ItemStack undisguise = new ItemStack(Material.BARRIER);
        ItemMeta undisguiseMeta = undisguise.getItemMeta();
        undisguiseMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', emcDisguise.getLocaleDisguiseGuiUndisguiseButton()));
        undisguiseMeta.setLore(emcDisguise.getLocaleDisguiseUndisguiseButtonLore());
        undisguise.setItemMeta(undisguiseMeta);
        inventory.setItem(49, undisguise);

        ItemStack perspective = new ItemStack(Material.SADDLE);
        ItemMeta perspectiveMeta = perspective.getItemMeta();
        perspectiveMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', emcDisguise.getLocaleDisguiseGuiPerspectiveButton()));
        perspectiveMeta.setLore(emcDisguise.getLocaleDisguiseGuiPerspectiveButtonLore());
        perspective.setItemMeta(perspectiveMeta);
        inventory.setItem(45, perspective);

        for (String entity : emcDisguise.getConfigDisguises()) {
            inventory.addItem(getHeadItemStack(entity));
        }

        return inventory;
    }

    public ItemStack getPaneColor(String color) {
        ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE);
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
        return pane;
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
                switch (clicked.getType()) {
                    case SKULL_ITEM:
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
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', emcDisguise.getLocaleDisguiseGuiNoPermissionOnExit()));
                        }
                        player.closeInventory();
                        break;
                    case BARRIER:
                        if (emcDisguise.isDisguised(player)) {
                            emcDisguise.removeDisguise(player, true);
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', emcDisguise.getLocaleUndisguiseCommandNotWearingDisguise()));
                        }
                        player.closeInventory();
                        break;
                    case NAME_TAG:
                        emcDisguise.toggleNameTag(player);
                        player.closeInventory();
                        break;
                    case SADDLE:
                        emcDisguise.togglePerspective(player);
                        player.closeInventory();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
