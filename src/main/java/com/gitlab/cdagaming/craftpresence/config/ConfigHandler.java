package com.gitlab.cdagaming.craftpresence.config;

import com.gitlab.cdagaming.craftpresence.Constants;
import com.gitlab.cdagaming.craftpresence.handler.StringHandler;
import net.minecraft.client.resources.I18n;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ConfigHandler {
    // CONFIG NAMES
    // GENERAL
    public String NAME_detectCurseManifest,
            NAME_detectMultiMCManifest,
            NAME_detectTechnicPack,
            NAME_showTime,
            NAME_showCurrentBiome,
            NAME_showCurrentDimension,
            NAME_showGameState,
            NAME_clientID,
            NAME_defaultIcon;
    // BIOME MESSAGES
    public String NAME_biomeMessages;
    // DIMENSION MESSAGES
    public String NAME_defaultDimensionIcon,
            NAME_dimensionMessages;
    // SERVER MESSAGES
    public String NAME_defaultServerIcon,
            NAME_defaultServerName,
            NAME_defaultServerMOTD,
            NAME_serverMessages;
    // STATUS MESSAGES
    public String NAME_mainmenuMSG,
            NAME_singleplayerMSG,
            NAME_loadingMSG;
    // ADVANCED
    public String NAME_enableCommands,
            NAME_enablePERGUI,
            NAME_enablePERItem,
            NAME_splitCharacter,
            NAME_guiMessages,
            NAME_itemMessages;
    // Config Variables
    // GENERAL
    public boolean detectCurseManifest = true,
            detectMultiMCManifest = true,
            detectTechnicPack = true,
            showTime = true,
            showCurrentBiome = false,
            showCurrentDimension = true,
            showGameState = true;
    public String clientID = "450485984333660181";
    public String defaultIcon = "grass";
    // BIOME MESSAGES
    public String[] biomeMessages = new String[]{"default;Playing in &biome&"};
    // DIMENSION MESSAGES
    public String defaultDimensionIcon = "unknown";
    public String[] dimensionMessages = new String[]{"default;In The &dimension&"};
    // SERVER MESSAGES
    public String defaultServerIcon = "default",
            defaultServerName = I18n.format("selectServer.defaultName"),
            defaultServerMOTD = I18n.format("craftpresence.defaults.servermessages.servermotd");
    public String[] serverMessages = new String[]{"default;Playing on &motd&"};
    // STATUS MESSAGES
    public String mainmenuMSG = I18n.format("craftpresence.defaults.state.mainmenu"),
            singleplayerMSG = I18n.format("craftpresence.defaults.state.singleplayer"),
            loadingMSG = I18n.format("craftpresence.defaults.state.loading");
    // ADVANCED
    public boolean enableCommands = true,
            enablePERGUI = false,
            enablePERItem = false;
    public String splitCharacter = ";";
    public String[] guiMessages = new String[]{"default;In &gui&"},
            itemMessages = new String[]{"default;Holding &main&"};
    // CLASS-SPECIFIC - PUBLIC
    public boolean hasChanged = false;
    public boolean hasClientPropertiesChanged = false;
    public boolean rebootOnWorldLoad = false;
    // CLASS-SPECIFIC - PRIVATE
    private String fileName;
    private boolean verified = false;

    public ConfigHandler(String fileName) {
        this.fileName = fileName;
    }

    private void setupNames() {
        // GENERAL
        NAME_detectCurseManifest = I18n.format("gui.config.name.general.detectcursemanifest").replaceAll(" ", "_");
        NAME_detectMultiMCManifest = I18n.format("gui.config.name.general.detectmultimcmanifest").replaceAll(" ", "_");
        NAME_detectTechnicPack = I18n.format("gui.config.name.general.detecttechnicpack").replaceAll(" ", "_");
        NAME_showTime = I18n.format("gui.config.name.general.showtime").replaceAll(" ", "_");
        NAME_showCurrentBiome = I18n.format("gui.config.name.general.showbiome").replaceAll(" ", "_");
        NAME_showCurrentDimension = I18n.format("gui.config.name.general.showdimension").replaceAll(" ", "_");
        NAME_showGameState = I18n.format("gui.config.name.general.showstate").replaceAll(" ", "_");
        NAME_clientID = I18n.format("gui.config.name.general.clientid").replaceAll(" ", "_");
        NAME_defaultIcon = I18n.format("gui.config.name.general.defaulticon").replaceAll(" ", "_");
        // BIOME MESSAGES
        NAME_biomeMessages = I18n.format("gui.config.name.biomemessages.biomemessages").replaceAll(" ", "_");
        // DIMENSION MESSAGES
        NAME_defaultDimensionIcon = I18n.format("gui.config.name.dimensionmessages.dimensionicon").replaceAll(" ", "_");
        NAME_dimensionMessages = I18n.format("gui.config.name.dimensionmessages.dimensionmessages").replaceAll(" ", "_");
        // SERVER MESSAGES
        NAME_defaultServerIcon = I18n.format("gui.config.name.servermessages.servericon").replaceAll(" ", "_");
        NAME_defaultServerName = I18n.format("gui.config.name.servermessages.servername").replaceAll(" ", "_");
        NAME_defaultServerMOTD = I18n.format("gui.config.name.servermessages.servermotd").replaceAll(" ", "_");
        NAME_serverMessages = I18n.format("gui.config.name.servermessages.servermessages").replaceAll(" ", "_");
        // STATUS MESSAGES
        NAME_mainmenuMSG = I18n.format("gui.config.name.statusmessages.mainmenumsg").replaceAll(" ", "_");
        NAME_singleplayerMSG = I18n.format("gui.config.name.statusmessages.singleplayermsg").replaceAll(" ", "_");
        NAME_loadingMSG = I18n.format("gui.config.name.statusmessages.loadingmsg").replaceAll(" ", "_");
        // ADVANCED
        NAME_enableCommands = I18n.format("gui.config.name.advanced.enablecommands").replaceAll(" ", "_");
        NAME_enablePERGUI = I18n.format("gui.config.name.advanced.enablepergui").replaceAll(" ", "_");
        NAME_enablePERItem = I18n.format("gui.config.name.advanced.enableperitem").replaceAll(" ", "_");
        NAME_splitCharacter = I18n.format("gui.config.name.advanced.splitcharacter").replaceAll(" ", "_");
        NAME_guiMessages = I18n.format("gui.config.name.advanced.guimessages").replaceAll(" ", "_");
        NAME_itemMessages = I18n.format("gui.config.name.advanced.itemmessages").replaceAll(" ", "_");
    }

    public void initialize() {
        try {
            File configFile = new File(fileName);
            File parentDir = configFile.getParentFile();
            boolean isCFGNEW = false;

            if ((!parentDir.exists() && parentDir.mkdirs()) || (!configFile.exists() && configFile.createNewFile())) {
                isCFGNEW = true;
            }

            setupNames();

            if (isCFGNEW) {
                updateConfig();
            }
            read();
        } catch (Exception ex) {
            Constants.LOG.error(I18n.format("craftpresence.logger.error.config.save"));
            ex.printStackTrace();
        }
    }

    public void read() {
        Properties properties = new Properties();

        try {
            Reader configReader = new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8);
            properties.load(configReader);
            configReader.close();
        } catch (Exception ex) {
            Constants.LOG.error(I18n.format("craftpresence.logger.error.config.save"));
            ex.printStackTrace();
        }

        try {
            // GENERAL
            detectCurseManifest = Boolean.parseBoolean(properties.getProperty(NAME_detectCurseManifest));
            detectMultiMCManifest = Boolean.parseBoolean(properties.getProperty(NAME_detectMultiMCManifest));
            detectTechnicPack = Boolean.parseBoolean(properties.getProperty(NAME_detectTechnicPack));
            showTime = Boolean.parseBoolean(properties.getProperty(NAME_showTime));
            showCurrentBiome = Boolean.parseBoolean(properties.getProperty(NAME_showCurrentBiome));
            showCurrentDimension = Boolean.parseBoolean(properties.getProperty(NAME_showCurrentDimension));
            showGameState = Boolean.parseBoolean(properties.getProperty(NAME_showGameState));
            clientID = properties.getProperty(NAME_clientID);
            defaultIcon = properties.getProperty(NAME_defaultIcon);
            // BIOME MESSAGES
            biomeMessages = properties.getProperty(NAME_biomeMessages).replaceAll("\\[", "").replaceAll("]", "").split(", ");
            // DIMENSION MESSAGES
            defaultDimensionIcon = properties.getProperty(NAME_defaultDimensionIcon);
            dimensionMessages = properties.getProperty(NAME_dimensionMessages).replaceAll("\\[", "").replaceAll("]", "").split(", ");
            // SERVER MESSAGES
            defaultServerIcon = properties.getProperty(NAME_defaultServerIcon);
            defaultServerName = properties.getProperty(NAME_defaultServerName);
            defaultServerMOTD = properties.getProperty(NAME_defaultServerMOTD);
            serverMessages = properties.getProperty(NAME_serverMessages).replaceAll("\\[", "").replaceAll("]", "").split(", ");
            // STATUS MESSAGES
            mainmenuMSG = properties.getProperty(NAME_mainmenuMSG);
            singleplayerMSG = properties.getProperty(NAME_singleplayerMSG);
            loadingMSG = properties.getProperty(NAME_loadingMSG);
            // ADVANCED
            enableCommands = Boolean.parseBoolean(properties.getProperty(NAME_enableCommands));
            enablePERGUI = Boolean.parseBoolean(properties.getProperty(NAME_enablePERGUI));
            enablePERItem = Boolean.parseBoolean(properties.getProperty(NAME_enablePERItem));
            splitCharacter = properties.getProperty(NAME_splitCharacter);
            guiMessages = properties.getProperty(NAME_guiMessages).replaceAll("\\[", "").replaceAll("]", "").split(", ");
            itemMessages = properties.getProperty(NAME_itemMessages).replaceAll("\\[", "").replaceAll("]", "").split(", ");
        } catch (NullPointerException ex) {
            verifyConfig(properties);
        } finally {
            if (!verified) {
                verifyConfig(properties);
            } else {
                Constants.LOG.info(I18n.format("craftpresence.logger.info.config.save"));
            }
        }
    }

    public void updateConfig() {
        Properties properties = new Properties();

        // GENERAL
        properties.setProperty(NAME_detectCurseManifest, detectCurseManifest ? "true" : "false");
        properties.setProperty(NAME_detectMultiMCManifest, detectMultiMCManifest ? "true" : "false");
        properties.setProperty(NAME_detectTechnicPack, detectTechnicPack ? "true" : "false");
        properties.setProperty(NAME_showTime, showTime ? "true" : "false");
        properties.setProperty(NAME_showCurrentBiome, showCurrentBiome ? "true" : "false");
        properties.setProperty(NAME_showCurrentDimension, showCurrentDimension ? "true" : "false");
        properties.setProperty(NAME_showGameState, showGameState ? "true" : "false");
        properties.setProperty(NAME_clientID, !StringHandler.isNullOrEmpty(clientID) ? clientID : "450485984333660181");
        properties.setProperty(NAME_defaultIcon, !StringHandler.isNullOrEmpty(defaultIcon) ? defaultIcon : "grass");
        // BIOME MESSAGES
        properties.setProperty(NAME_biomeMessages, !StringHandler.isNullOrEmpty(Arrays.toString(biomeMessages)) ? Arrays.toString(biomeMessages) : Arrays.toString(new String[]{"default" + splitCharacter + "Playing in &biome&"}));
        // DIMENSION MESSAGES
        properties.setProperty(NAME_defaultDimensionIcon, !StringHandler.isNullOrEmpty(defaultDimensionIcon) ? defaultDimensionIcon : "unknown");
        properties.setProperty(NAME_dimensionMessages, !StringHandler.isNullOrEmpty(Arrays.toString(dimensionMessages)) ? Arrays.toString(dimensionMessages) : Arrays.toString(new String[]{"default" + splitCharacter + "In The &dimension&"}));
        // SERVER MESSAGES
        properties.setProperty(NAME_defaultServerIcon, !StringHandler.isNullOrEmpty(defaultServerIcon) ? defaultServerIcon : "default");
        properties.setProperty(NAME_defaultServerName, !StringHandler.isNullOrEmpty(defaultServerName) ? defaultServerName : I18n.format("selectServer.defaultName"));
        properties.setProperty(NAME_defaultServerMOTD, !StringHandler.isNullOrEmpty(defaultServerMOTD) ? defaultServerMOTD : I18n.format("craftpresence.defaults.servermessages.servermotd"));
        properties.setProperty(NAME_serverMessages, !StringHandler.isNullOrEmpty(Arrays.toString(serverMessages)) ? Arrays.toString(serverMessages) : Arrays.toString(new String[]{"default" + splitCharacter + "Playing on &motd&"}));
        // STATUS MESSAGES
        properties.setProperty(NAME_mainmenuMSG, !StringHandler.isNullOrEmpty(mainmenuMSG) ? mainmenuMSG : I18n.format("craftpresence.defaults.state.mainmenu"));
        properties.setProperty(NAME_singleplayerMSG, !StringHandler.isNullOrEmpty(singleplayerMSG) ? singleplayerMSG : I18n.format("craftpresence.defaults.state.singleplayer"));
        properties.setProperty(NAME_loadingMSG, !StringHandler.isNullOrEmpty(loadingMSG) ? loadingMSG : I18n.format("craftpresence.defaults.state.loading"));
        // ADVANCED
        properties.setProperty(NAME_enableCommands, enableCommands ? "true" : "false");
        properties.setProperty(NAME_enablePERGUI, enablePERGUI ? "true" : "false");
        properties.setProperty(NAME_enablePERItem, enablePERItem ? "true" : "false");
        properties.setProperty(NAME_splitCharacter, !StringHandler.isNullOrEmpty(splitCharacter) ? splitCharacter : ";");
        properties.setProperty(NAME_guiMessages, !StringHandler.isNullOrEmpty(Arrays.toString(guiMessages)) ? Arrays.toString(guiMessages) : Arrays.toString(new String[]{"default" + splitCharacter + "In &gui&"}));
        properties.setProperty(NAME_itemMessages, !StringHandler.isNullOrEmpty(Arrays.toString(itemMessages)) ? Arrays.toString(itemMessages) : Arrays.toString(new String[]{"default" + splitCharacter + "Holding &main&"}));

        // Check for Conflicts before Saving
        if (showCurrentBiome && showGameState) {
            Constants.LOG.warn(I18n.format("craftpresence.logger.warning.config.conflict.biomestate"));
            showCurrentBiome = false;
            properties.setProperty(NAME_showCurrentBiome, "false");
        }
        if (enablePERGUI && showGameState) {
            Constants.LOG.warn(I18n.format("craftpresence.logger.warning.config.conflict.pergui"));
            enablePERGUI = false;
            properties.setProperty(NAME_enablePERGUI, "false");
        }

        save(properties);
    }

    private void verifyConfig(final Properties properties) {
        List<String> validProperties = new ArrayList<>();
        List<String> removedProperties = new ArrayList<>();
        boolean needsFullUpdate = false;

        for (Field field : getClass().getFields()) {
            if (field.getName().contains("NAME_")) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(this);
                    validProperties.add(value.toString());
                    if (!properties.stringPropertyNames().contains(value.toString()) && validProperties.contains(value.toString())) {
                        Constants.LOG.error(I18n.format("craftpresence.logger.error.config.emptyprop", value.toString()));
                        needsFullUpdate = true;
                    }
                } catch (Exception ignored) {
                }
            }
        }

        for (String property : properties.stringPropertyNames()) {
            if (!validProperties.contains(property)) {
                Constants.LOG.error(I18n.format("craftpresence.logger.error.config.invalidprop", property));
                removedProperties.add(property);
                properties.remove(property);
                save(properties);
            }
            if (!removedProperties.contains(property)) {
                if (StringHandler.isNullOrEmpty(properties.getProperty(property))) {
                    Constants.LOG.error(I18n.format("craftpresence.logger.error.config.emptyprop", property));
                    needsFullUpdate = true;
                } else {
                    if (property.equals(NAME_clientID) && (properties.getProperty(property).length() < 18 || properties.getProperty(property).matches(".*[a-z].*"))) {
                        Constants.LOG.error(I18n.format("craftpresence.logger.error.config.invalidprop", property));
                        properties.setProperty(property, "450485984333660181");
                        save(properties);
                    }
                }
            }
        }

        if (needsFullUpdate) {
            updateConfig();
            verified = true;
            read();
        } else {
            verified = true;
        }
    }

    private void save(final Properties properties) {
        try {
            Writer configWriter = new OutputStreamWriter(new FileOutputStream(new File(fileName)), StandardCharsets.UTF_8);
            properties.store(configWriter, null);
            configWriter.close();
        } catch (Exception ex) {
            Constants.LOG.error(I18n.format("craftpresence.logger.error.config.save"));
            ex.printStackTrace();
        }
    }
}
