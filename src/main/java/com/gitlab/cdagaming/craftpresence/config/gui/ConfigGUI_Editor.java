package com.gitlab.cdagaming.craftpresence.config.gui;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.handler.StringHandler;
import com.gitlab.cdagaming.craftpresence.handler.discord.assets.DiscordAssetHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class ConfigGUI_Editor extends GuiScreen {
    private final GuiScreen parentscreen;
    private GuiButton proceedButton, specificIconButton;
    private GuiTextField specificMessage;
    private String selecteditem, specificMSG, defaultMSG, title;

    ConfigGUI_Editor(GuiScreen parentScreen, String selectedItem) {
        mc = Minecraft.getMinecraft();
        parentscreen = parentScreen;
        selecteditem = selectedItem;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        ScaledResolution sr = new ScaledResolution(mc);

        if (parentscreen instanceof ConfigGUI_BiomeSettings) {
            title = I18n.format("gui.config.title.biome.editspecificbiome", selecteditem);
            defaultMSG = StringHandler.getConfigPart(CraftPresence.CONFIG.biomeMessages, "default", 0, 1, CraftPresence.CONFIG.splitCharacter, null);
            specificMSG = StringHandler.getConfigPart(CraftPresence.CONFIG.biomeMessages, selecteditem, 0, 1, CraftPresence.CONFIG.splitCharacter, defaultMSG);
        }
        if (parentscreen instanceof ConfigGUI_DimensionSettings) {
            title = I18n.format("gui.config.title.dimension.editspecificdimension", selecteditem);
            defaultMSG = StringHandler.getConfigPart(CraftPresence.CONFIG.dimensionMessages, "default", 0, 1, CraftPresence.CONFIG.splitCharacter, null);
            specificMSG = StringHandler.getConfigPart(CraftPresence.CONFIG.dimensionMessages, selecteditem, 0, 1, CraftPresence.CONFIG.splitCharacter, defaultMSG);
        }
        if (parentscreen instanceof ConfigGUI_ServerSettings) {
            title = I18n.format("gui.config.title.server.editspecificserver", selecteditem);
            defaultMSG = StringHandler.getConfigPart(CraftPresence.CONFIG.serverMessages, "default", 0, 1, CraftPresence.CONFIG.splitCharacter, null);
            specificMSG = StringHandler.getConfigPart(CraftPresence.CONFIG.serverMessages, selecteditem, 0, 1, CraftPresence.CONFIG.splitCharacter, defaultMSG);
        }

        specificMessage = new GuiTextField(110, fontRenderer, (sr.getScaledWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(1), 180, 20);
        specificMessage.setText(specificMSG);

        if (parentscreen instanceof ConfigGUI_DimensionSettings || parentscreen instanceof ConfigGUI_ServerSettings) {
            specificIconButton = new GuiButton(100, (sr.getScaledWidth() / 2) - 90, CraftPresence.GUIS.getButtonY(2), 180, 20, "Change Icon");
            buttonList.add(specificIconButton);
        }

        proceedButton = new GuiButton(900, (sr.getScaledWidth() / 2) - 90, (sr.getScaledHeight() - 30), 180, 20, "Back");

        buttonList.add(proceedButton);
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        drawDefaultBackground();
        drawString(fontRenderer, title, (sr.getScaledWidth() / 2) - (fontRenderer.getStringWidth(title) / 2), 20, 0xFFFFFF);
        drawString(fontRenderer, "Message:", (sr.getScaledWidth() / 2) - 130, CraftPresence.GUIS.getButtonY(1) + 5, 0xFFFFFF);
        specificMessage.drawTextBox();

        if (!specificMessage.getText().equals(specificMSG)) {
            proceedButton.displayString = "Continue";
        } else {
            proceedButton.displayString = "Back";
        }

        proceedButton.enabled = !selecteditem.equals("default") && !StringHandler.isNullOrEmpty(specificMessage.getText());

        super.drawScreen(mouseX, mouseY, partialTicks);

        if (proceedButton.isMouseOver() && !proceedButton.enabled) {
            drawHoveringText(CraftPresence.GUIS.formatText(I18n.format("gui.config.hoverMessage.defaultempty").split("\n")), mouseX, mouseY);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == proceedButton.id) {
            if (!specificMessage.getText().equals(specificMSG)) {
                CraftPresence.CONFIG.hasChanged = true;
                CraftPresence.CONFIG.rebootOnWorldLoad = true;
                if (parentscreen instanceof ConfigGUI_BiomeSettings) {
                    CraftPresence.CONFIG.biomeMessages = StringHandler.setConfigPart(CraftPresence.CONFIG.biomeMessages, selecteditem, 0, 1, CraftPresence.CONFIG.splitCharacter, specificMessage.getText());
                }
                if (parentscreen instanceof ConfigGUI_DimensionSettings) {
                    CraftPresence.CONFIG.dimensionMessages = StringHandler.setConfigPart(CraftPresence.CONFIG.dimensionMessages, selecteditem, 0, 1, CraftPresence.CONFIG.splitCharacter, specificMessage.getText());
                }
                if (parentscreen instanceof ConfigGUI_ServerSettings) {
                    CraftPresence.CONFIG.serverMessages = StringHandler.setConfigPart(CraftPresence.CONFIG.serverMessages, selecteditem, 0, 1, CraftPresence.CONFIG.splitCharacter, specificMessage.getText());
                }
            }
            if (StringHandler.isNullOrEmpty(specificMessage.getText()) || specificMessage.getText().equalsIgnoreCase(defaultMSG)) {
                CraftPresence.CONFIG.hasChanged = true;
                CraftPresence.CONFIG.rebootOnWorldLoad = true;
                if (parentscreen instanceof ConfigGUI_BiomeSettings) {
                    CraftPresence.CONFIG.biomeMessages = StringHandler.removeFromArray(CraftPresence.CONFIG.biomeMessages, selecteditem, 0, CraftPresence.CONFIG.splitCharacter);
                }
                if (parentscreen instanceof ConfigGUI_DimensionSettings) {
                    CraftPresence.CONFIG.dimensionMessages = StringHandler.removeFromArray(CraftPresence.CONFIG.dimensionMessages, selecteditem, 0, CraftPresence.CONFIG.splitCharacter);
                }
                if (parentscreen instanceof ConfigGUI_ServerSettings) {
                    CraftPresence.CONFIG.serverMessages = StringHandler.removeFromArray(CraftPresence.CONFIG.serverMessages, selecteditem, 0, CraftPresence.CONFIG.splitCharacter);
                }
            }
            mc.displayGuiScreen(parentscreen);
        } else if (parentscreen instanceof ConfigGUI_DimensionSettings) {
            if (button.id == specificIconButton.id) {
                mc.displayGuiScreen(new ConfigGUI_Selector(this, CraftPresence.CONFIG.NAME_dimensionMessages, "CraftPresence - Select an Icon", DiscordAssetHandler.ICON_LIST, selecteditem));
            }
        } else if (parentscreen instanceof ConfigGUI_ServerSettings) {
            if (button.id == specificIconButton.id) {
                mc.displayGuiScreen(new ConfigGUI_Selector(this, CraftPresence.CONFIG.NAME_serverMessages, "CraftPresence - Select an Icon", DiscordAssetHandler.ICON_LIST, selecteditem));
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        specificMessage.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        specificMessage.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        specificMessage.updateCursorCounter();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
}
