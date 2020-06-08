package me.kenny.emcdisguise;

import com.google.common.collect.Lists;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EMCDisguise extends JavaPlugin {
    private FileConfiguration localeFileConfiguration;
    private FileConfiguration nameTagToggledFileConfiguration;
    private File nameTagToggledFile;

    private String localeNoDisguisePermission;
    private String localeClickToUseDisguise;
    private String localeRemoveDisguise;
    private String localeNowDisguised;
    private String localeDisguiseDisplayName;
    private String localeDisguiseGuiTitle;
    private String localeDisguiseGuiExitButton;
    private String localeDisguiseGuiToggleNameTagButton;
    private String localeDisguiseToggleNametag;
    private String localeDisguiseCommandInvalidDisguise;
    private String localeUndisguiseCommandNotWearingDisguise;
    private String localeDisguiseRemoveBecauseAttacked;

    private String configDisguiseGuiPaneColor;
    private List<String> configDisguises;

    private DisguiseGui disguiseGui;
    private ArmorStandConstructor armorStandConstructor;

    private Map<Player, DisguiseType> disguised = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        createLocaleConfig();
        createNameTagToggledConfig();

        localeNoDisguisePermission = localeFileConfiguration.getString("no-disguise-permission");
        localeClickToUseDisguise = localeFileConfiguration.getString("click-to-use-disguise");
        localeRemoveDisguise = localeFileConfiguration.getString("remove-disguise");
        localeNowDisguised = localeFileConfiguration.getString("now-disguised");
        localeDisguiseDisplayName = localeFileConfiguration.getString("disguise-display-name");
        localeDisguiseGuiTitle = localeFileConfiguration.getString("disguise-gui-title");
        localeDisguiseGuiExitButton = localeFileConfiguration.getString("disguise-gui-exit-button");
        localeDisguiseGuiToggleNameTagButton = localeFileConfiguration.getString("disguise-gui-toggle-nametag-button");
        localeDisguiseToggleNametag = localeFileConfiguration.getString("disguise-toggle-nametag");
        localeDisguiseCommandInvalidDisguise = localeFileConfiguration.getString("disguise-command-invalid-disguise");
        localeUndisguiseCommandNotWearingDisguise = localeFileConfiguration.getString("undisguise-command-not-wearing-disguise");
        localeDisguiseRemoveBecauseAttacked = localeFileConfiguration.getString("disguise-remove-because-attacked");

        configDisguiseGuiPaneColor = getConfig().getString("disguise-gui-pane-color");
        configDisguises = getConfig().getStringList("disguises");

        List<String> remove = new ArrayList<>();
        for (String entity : configDisguises) {
            try {
                EntityType entityType = EntityType.valueOf(entity.toUpperCase());
            } catch (IllegalArgumentException e) {
                remove.add(entity);
                getServer().getConsoleSender().sendMessage("[" + getName() + "] " + ChatColor.YELLOW + entity + ChatColor.RED + " is not a valid entity and will be removed from the available disguises.");
            }
        }
        configDisguises.removeAll(remove);

        disguiseGui = new DisguiseGui(this);
        armorStandConstructor = new ArmorStandConstructor(this);

        getServer().getPluginManager().registerEvents(disguiseGui, this);
        getServer().getPluginManager().registerEvents(armorStandConstructor, this);
        getServer().getPluginManager().registerEvents(new DisguiseListener(this), this);

        getCommand("disguise").setExecutor(new DisguiseCommand(this));
        getCommand("disguise").setTabCompleter(new DisguiseTabCompleter(this));
        getCommand("undisguise").setExecutor(new UndisguiseCommand(this));
    }

    @Override
    public void onDisable() {
        armorStandConstructor.removeAll();
    }

    private void createLocaleConfig() {
        File localeFile = new File(getDataFolder(), "locale.yml");
        if (!localeFile.exists()) {
            localeFile.getParentFile().mkdirs();
            saveResource("locale.yml", false);
        }

        localeFileConfiguration = new YamlConfiguration();

        try {
            localeFileConfiguration.load(localeFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void createNameTagToggledConfig() {
        nameTagToggledFile = new File(getDataFolder(), "nameTagToggled.yml");
        if (!nameTagToggledFile.exists()) {
            nameTagToggledFile.getParentFile().mkdirs();
            saveResource("nameTagToggled.yml", false);
        }

        nameTagToggledFileConfiguration = new YamlConfiguration();

        try {
            nameTagToggledFileConfiguration.load(nameTagToggledFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public String getLocaleNoDisguisePermission() {
        return localeNoDisguisePermission;
    }

    public String getLocaleClickToUseDisguise() {
        return localeClickToUseDisguise;
    }

    public String getLocaleRemoveDisguise() {
        return localeRemoveDisguise;
    }

    public String getLocaleNowDisguised() {
        return localeNowDisguised;
    }

    public String getLocaleDisguiseDisplayName() {
        return localeDisguiseDisplayName;
    }

    public String getLocaleDisguiseGuiTitle() {
        return localeDisguiseGuiTitle;
    }

    public String getLocaleDisguiseGuiExitButton() {
        return localeDisguiseGuiExitButton;
    }

    public String getLocaleDisguiseGuiToggleNameTagButton() {
        return localeDisguiseGuiToggleNameTagButton;
    }

    public String getLocaleDisguiseCommandInvalidDisguise() {
        return localeDisguiseCommandInvalidDisguise;
    }

    public String getLocaleUndisguiseCommandNotWearingDisguise() {
        return localeUndisguiseCommandNotWearingDisguise;
    }

    public String getLocaleDisguiseRemoveBecauseAttacked() {
        return localeDisguiseRemoveBecauseAttacked;
    }

    public String getConfigDisguiseGuiPaneColor() {
        return configDisguiseGuiPaneColor;
    }

    public String getLocaleDisguiseToggleNametag() {
        return localeDisguiseToggleNametag;
    }

    public List<String> getConfigDisguises() {
        return configDisguises;
    }

    public DisguiseGui getDisguiseGui() {
        return disguiseGui;
    }

    public void toggleNameTag(Player player) {
        boolean isToggled = isNameTagToggled(player);
        String on_off = isToggled ? "off" : "on";
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', getLocaleDisguiseToggleNametag().replace("%on_off%", on_off)));
        if (isToggled)
            removeNameTagToggled(player);
        else
            addNameTagToggled(player);
    }

    public boolean isNameTagToggled(Player player) {
        return nameTagToggledFileConfiguration.getStringList("players").contains(player.getUniqueId().toString());
    }

    public void addNameTagToggled(Player player) {
        String uuid = player.getUniqueId().toString();
        List<String> toggled = nameTagToggledFileConfiguration.getStringList("players");
        if (!toggled.contains(uuid)) {
            toggled.add(uuid);
            nameTagToggledFileConfiguration.set("players", toggled);
            armorStandConstructor.update(player);

            try {
                nameTagToggledFileConfiguration.save(nameTagToggledFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeNameTagToggled(Player player) {
        String uuid = player.getUniqueId().toString();
        List<String> toggled = nameTagToggledFileConfiguration.getStringList("players");
        if (toggled.contains(uuid)) {
            toggled.remove(uuid);
            nameTagToggledFileConfiguration.set("players", toggled);
            armorStandConstructor.update(player);

            try {
                nameTagToggledFileConfiguration.save(nameTagToggledFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addDisguise(Player player, DisguiseType disguiseType) {
        if (!disguised.containsKey(player)) {
            String entity = WordUtils.capitalizeFully(disguiseType.toString().replace("_", " "));
            String localeNowDisguised = ChatColor.translateAlternateColorCodes('&', getLocaleNowDisguised().replace("%entity%", entity));
            player.sendMessage(localeNowDisguised);
            MobDisguise mobDisguise = new MobDisguise(disguiseType);
            mobDisguise.setViewSelfDisguise(false);
            mobDisguise.setEntity(player);
            mobDisguise.startDisguise();

            armorStandConstructor.addArmorstand(player);
            disguised.put(player, disguiseType);
        }
    }

    public void removeDisguise(Player player, boolean message) {
        if (disguised.containsKey(player)) {
            if (DisguiseAPI.isDisguised(player)) {
                String currentDisguise = getPlayerDisguiseType(player).toString().replace("_", " ");
                currentDisguise = WordUtils.capitalizeFully(currentDisguise);
                String localeRemoveDisguise = ChatColor.translateAlternateColorCodes('&', getLocaleRemoveDisguise().replace("%entity%", currentDisguise));
                if (message)
                    player.sendMessage(localeRemoveDisguise);
                DisguiseAPI.undisguiseToAll(player);
            }
            armorStandConstructor.removeArmorStand(player);
            disguised.remove(player);
        }
    }

    public DisguiseType getPlayerDisguiseType(Player player) {
        if (disguised.containsKey(player))
            return disguised.get(player);
        return null;
    }


    public boolean isDisguised(Player player) {
        return disguised.containsKey(player);
    }
}
