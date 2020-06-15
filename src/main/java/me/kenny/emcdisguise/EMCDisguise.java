package me.kenny.emcdisguise;

import me.kenny.emcdisguise.armorstand.ArmorStandConstructor;
import me.kenny.emcdisguise.command.DisguiseCommand;
import me.kenny.emcdisguise.command.DisguiseTabCompleter;
import me.kenny.emcdisguise.command.UndisguiseCommand;
import me.kenny.emcdisguise.gui.DisguiseGui;
import me.kenny.emcdisguise.listener.DisguiseListener;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
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
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EMCDisguise extends JavaPlugin {
    private File localeFile;
    private FileConfiguration localeFileConfiguration;
    private File nameTagToggledFile;
    private FileConfiguration nameTagToggledFileConfiguration;
    private File showSelfFile;
    private FileConfiguration showSelfFileConfiguration;

    private String localeReloadMessage;
    private String localeNoDisguisePermission;
    private String localeClickToUseDisguise;
    private String localeRemoveDisguise;
    private String localeNowDisguised;
    private String localeDisguiseDisplayName;
    private String localeDisguiseGuiTitle;
    private String localeDisguiseGuiNoPermissionOnExit;
    private String localeDisguiseGuiUndisguiseButton;
    private List<String> localeDisguiseGuiUndisguiseButtonLore;
    private String localeDisguiseGuiToggleNameTagButton;
    private List<String> localeDisguiseGuiToggleNameTagButtonLore;
    private String localeDisguiseGuiPerspectiveButton;
    private List<String> localeDisguiseGuiPerspectiveButtonLore;
    private String localeDisguiseTogglePerspective;
    private String localeDisguiseToggleNametag;
    private String localeDisguiseCommandInvalidDisguise;
    private String localeUndisguiseCommandNotWearingDisguise;
    private String localeDisguiseRemoveBecauseAttacked;
    private List<String> localeDisguiseInformationLore;
    private String localeDisguiseInformationDisplayName;
    private String localeDisguiseGuiPaneColor1DisplayName;
    private List<String> localeDisguiseGuiPaneColor1Lore;
    private String localeDisguiseGuiPaneColor2DisplayName;
    private List<String> localeDisguiseGuiPaneColor2Lore;

    private String configDisguiseGuiPaneColor1;
    private String configDisguiseGuiPaneColor2;
    private List<String> configDisguises;

    private DisguiseGui disguiseGui;
    private ArmorStandConstructor armorStandConstructor;

    private Map<Player, DisguiseType> disguised = new HashMap<>();
    private Map<Player, Disguise> disguises = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        createLocaleConfig();
        createNameTagToggledConfig();
        createShowSelfConfig();

        initConfigs();

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

        setupPermissions();
    }

    @Override
    public void onDisable() {
        armorStandConstructor.removeAll();
    }

    public void reloadConfigs() {
        reloadConfig();
        localeFileConfiguration = YamlConfiguration.loadConfiguration(localeFile);
        showSelfFileConfiguration = YamlConfiguration.loadConfiguration(showSelfFile);
        nameTagToggledFileConfiguration = YamlConfiguration.loadConfiguration(nameTagToggledFile);

        initConfigs();
    }

    public void initConfigs() {
        localeReloadMessage = localeFileConfiguration.getString("reload-message");
        localeNoDisguisePermission = localeFileConfiguration.getString("no-disguise-permission");
        localeClickToUseDisguise = localeFileConfiguration.getString("click-to-use-disguise");
        localeRemoveDisguise = localeFileConfiguration.getString("remove-disguise");
        localeNowDisguised = localeFileConfiguration.getString("now-disguised");
        localeDisguiseDisplayName = localeFileConfiguration.getString("disguise-display-name");
        localeDisguiseGuiTitle = localeFileConfiguration.getString("disguise-gui-title");
        localeDisguiseGuiNoPermissionOnExit = localeFileConfiguration.getString("disguise-gui-no-permission-on-exit");
        localeDisguiseGuiUndisguiseButton = localeFileConfiguration.getString("disguise-gui-undisguise-button");
        localeDisguiseGuiUndisguiseButtonLore = getTranslatedLore(localeFileConfiguration.getStringList("disguise-gui-undisguise-button-lore"));
        localeDisguiseGuiToggleNameTagButton = localeFileConfiguration.getString("disguise-gui-toggle-nametag-button");
        localeDisguiseGuiToggleNameTagButtonLore = getTranslatedLore(localeFileConfiguration.getStringList("disguise-gui-toggle-nametag-button-lore"));
        localeDisguiseGuiPerspectiveButton = localeFileConfiguration.getString("disguise-gui-perspective-button");
        localeDisguiseGuiPerspectiveButtonLore = getTranslatedLore(localeFileConfiguration.getStringList("disguise-gui-perspective-button-lore"));
        localeDisguiseTogglePerspective = localeFileConfiguration.getString("disguise-toggle-perspective");
        localeDisguiseToggleNametag = localeFileConfiguration.getString("disguise-toggle-nametag");
        localeDisguiseCommandInvalidDisguise = localeFileConfiguration.getString("disguise-command-invalid-disguise");
        localeUndisguiseCommandNotWearingDisguise = localeFileConfiguration.getString("undisguise-command-not-wearing-disguise");
        localeDisguiseRemoveBecauseAttacked = localeFileConfiguration.getString("disguise-remove-because-attacked");
        localeDisguiseInformationLore = getTranslatedLore(localeFileConfiguration.getStringList("disguise-information-lore"));
        localeDisguiseInformationDisplayName = localeFileConfiguration.getString("disguise-information-display-name");
        localeDisguiseGuiPaneColor1DisplayName = localeFileConfiguration.getString("disguise-gui-pane-color1-displayname");
        localeDisguiseGuiPaneColor1Lore = getTranslatedLore(localeFileConfiguration.getStringList("disguise-gui-pane-color1-lore"));
        localeDisguiseGuiPaneColor2DisplayName = localeFileConfiguration.getString("disguise-gui-pane-color2-displayname");
        localeDisguiseGuiPaneColor2Lore = getTranslatedLore(localeFileConfiguration.getStringList("disguise-gui-pane-color2-lore"));

        configDisguiseGuiPaneColor1 = getConfig().getString("disguise-gui-pane-color1");
        configDisguiseGuiPaneColor2 = getConfig().getString("disguise-gui-pane-color2");
        configDisguises = getConfig().getStringList("disguises");
    }

    public void setupPermissions() {
        Bukkit.getServer().getPluginManager().addPermission(new Permission("disguise.manage", PermissionDefault.OP));
        for (String disguise : getConfigDisguises()) {
            Permission permission = new Permission("disguise" + disguise.toLowerCase(), PermissionDefault.OP);
            Bukkit.getServer().getPluginManager().addPermission(permission);
        }
    }

    public List<String> getTranslatedLore(List<String> lore) {
        List<String> newLore = new ArrayList<>();
        for (String line : lore) {
            newLore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        return newLore;
    }

    private void createLocaleConfig() {
        localeFile = new File(getDataFolder(), "locale.yml");
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

    private void createShowSelfConfig() {
        showSelfFile = new File(getDataFolder(), "showSelf.yml");
        if (!showSelfFile.exists()) {
            showSelfFile.getParentFile().mkdirs();
            saveResource("showSelf.yml", false);
        }

        showSelfFileConfiguration = new YamlConfiguration();

        try {
            showSelfFileConfiguration.load(showSelfFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void togglePerspective(Player player) {
        if (isDisguised(player)) {
            if (!isShowSelf(player)) {
                addShowSelf(player);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getLocaleDisguiseTogglePerspective().replace("%on_off%", "on")));
            } else {
                removeShowSelf(player);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getLocaleDisguiseTogglePerspective().replace("%on_off%", "off")));
            }
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getLocaleUndisguiseCommandNotWearingDisguise()));
        }
    }

    public String getLocaleReloadMessage() {
        return localeReloadMessage;
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

    public String getLocaleDisguiseGuiNoPermissionOnExit() {
        return localeDisguiseGuiNoPermissionOnExit;
    }

    public String getLocaleDisguiseGuiUndisguiseButton() {
        return localeDisguiseGuiUndisguiseButton;
    }

    public List<String> getLocaleDisguiseUndisguiseButtonLore() {
        return localeDisguiseGuiUndisguiseButtonLore;
    }

    public String getLocaleDisguiseGuiToggleNameTagButton() {
        return localeDisguiseGuiToggleNameTagButton;
    }

    public List<String> getLocaleDisguiseGuiToggleNameTagButtonLore() {
        return localeDisguiseGuiToggleNameTagButtonLore;
    }

    public String getLocaleDisguiseGuiPerspectiveButton() {
        return localeDisguiseGuiPerspectiveButton;
    }

    public List<String> getLocaleDisguiseGuiPerspectiveButtonLore() {
        return localeDisguiseGuiPerspectiveButtonLore;
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

    public String getLocaleDisguiseToggleNametag() {
        return localeDisguiseToggleNametag;
    }

    public List<String> getLocaleDisguiseInformationLore() {
        return localeDisguiseInformationLore;
    }

    public String getLocaleDisguiseInformationDisplayName() {
        return localeDisguiseInformationDisplayName;
    }

    public String getLocaleDisguiseGuiPaneColor1DisplayName() {
        return localeDisguiseGuiPaneColor1DisplayName;
    }

    public List<String> getLocaleDisguiseGuiPaneColor1Lore() {
        return localeDisguiseGuiPaneColor1Lore;
    }

    public List<String> getLocaleDisguiseGuiPaneColor2Lore() {
        return localeDisguiseGuiPaneColor2Lore;
    }

    public String getLocaleDisguiseGuiPaneColor2DisplayName() {
        return localeDisguiseGuiPaneColor2DisplayName;
    }

    public String getLocaleDisguiseTogglePerspective() {
        return localeDisguiseTogglePerspective;
    }

    public String getConfigDisguiseGuiPaneColor1() {
        return configDisguiseGuiPaneColor1;
    }

    public String getConfigDisguiseGuiPaneColor2() {
        return configDisguiseGuiPaneColor2;
    }

    public List<String> getConfigDisguises() {
        return configDisguises;
    }

    public DisguiseGui getDisguiseGui() {
        return disguiseGui;
    }

    public void toggleNameTag(Player player) {
        if (!isDisguised(player)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', localeUndisguiseCommandNotWearingDisguise));
            return;
        }

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

    public boolean isShowSelf(Player player) {
        return showSelfFileConfiguration.getStringList("players").contains(player.getUniqueId().toString());
    }

    public void addShowSelf(Player player) {
        String uuid = player.getUniqueId().toString();
        List<String> toggled = showSelfFileConfiguration.getStringList("players");
        if (!toggled.contains(uuid)) {
            toggled.add(uuid);
            showSelfFileConfiguration.set("players", toggled);
            disguises.get(player).setViewSelfDisguise(false);

            try {
                showSelfFileConfiguration.save(showSelfFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeShowSelf(Player player) {
        String uuid = player.getUniqueId().toString();
        List<String> toggled = showSelfFileConfiguration.getStringList("players");
        if (toggled.contains(uuid)) {
            toggled.remove(uuid);
            disguises.get(player).setViewSelfDisguise(true);
            showSelfFileConfiguration.set("players", toggled);

            try {
                showSelfFileConfiguration.save(showSelfFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
            mobDisguise.setHideArmorFromSelf(false);
            mobDisguise.setViewSelfDisguise(false);
            mobDisguise.setEntity(player);
            mobDisguise.startDisguise();

            armorStandConstructor.addArmorstand(player);
            disguised.put(player, disguiseType);
            disguises.put(player, mobDisguise);

            addShowSelf(player);
            addNameTagToggled(player);
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
            disguises.remove(player);
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
