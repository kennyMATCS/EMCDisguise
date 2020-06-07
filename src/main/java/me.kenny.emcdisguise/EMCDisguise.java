package me.kenny.emcdisguise;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EMCDisguise extends JavaPlugin {
    private FileConfiguration localeFileConfiguration;

    private String localeNoDisguisePermission;
    private String localeClickToUseDisguise;
    private String localeRemoveDisguise;
    private String localeNowDisguised;
    private String localeDisguiseDisplayName;
    private String localeDisguiseGuiTitle;

    private int configDisguiseGuiSize;
    private String configDisguiseGuiPaneColor;
    private List<String> configDisguises;

    private DisguiseGui disguiseGui;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        createLocaleConfig();

        localeNoDisguisePermission = localeFileConfiguration.getString("no-disguise-permission");
        localeClickToUseDisguise = localeFileConfiguration.getString("click-to-use-disguise");
        localeRemoveDisguise = localeFileConfiguration.getString("remove-disguise");
        localeNowDisguised = localeFileConfiguration.getString("now-disguised");
        localeDisguiseDisplayName = localeFileConfiguration.getString("disguise-display-name");
        localeDisguiseGuiTitle = localeFileConfiguration.getString("disguise-gui-title");

        configDisguiseGuiSize = getConfig().getInt("disguise-gui-size");
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

        getServer().getPluginManager().registerEvents(disguiseGui, this);

        getCommand("disguise").setExecutor(new DisguiseCommand(this));
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

    public String getConfigDisguiseGuiPaneColor() {
        return configDisguiseGuiPaneColor;
    }

    public Integer getConfigDisguisesGuiSize() {
        return configDisguiseGuiSize;
    }

    public List<String> getConfigDisguises() {
        return configDisguises;
    }

    public DisguiseGui getDisguiseGui() {
        return disguiseGui;
    }
}
