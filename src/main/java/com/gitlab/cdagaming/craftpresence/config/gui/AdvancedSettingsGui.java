package com.gitlab.cdagaming.craftpresence.config.gui;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.CheckBoxControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AdvancedSettingsGui extends GuiScreen {
    private final GuiScreen parentScreen, currentScreen;
    private ExtendedButtonControl proceedButton, guiMessagesButton, itemMessagesButton, entityTargetMessagesButton, entityRidingMessagesButton, characterEditorButton;
    private CheckBoxControl enableCommandsButton, enablePerGUIButton,
            enablePerItemButton, enablePerEntityButton, renderTooltipsButton, formatWordsButton, debugModeButton;
    private GuiTextField splitCharacter;

    AdvancedSettingsGui(GuiScreen parentScreen) {
        mc = CraftPresence.instance;
        currentScreen = this;
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        splitCharacter = new GuiTextField(110, mc.fontRenderer, (width / 2) + 3, CraftPresence.GUIS.getButtonY(1), 180, 20);
        splitCharacter.setText(CraftPresence.CONFIG.splitCharacter);
        splitCharacter.setMaxStringLength(1);

        int calc1 = (width / 2) - 160;
        int calc2 = (width / 2) + 3;

        guiMessagesButton = new ExtendedButtonControl(120, calc1, CraftPresence.GUIS.getButtonY(2), 160, 20, ModUtils.TRANSLATOR.translate("gui.config.name.advanced.guimessages"));
        itemMessagesButton = new ExtendedButtonControl(130, calc2, CraftPresence.GUIS.getButtonY(2), 160, 20, ModUtils.TRANSLATOR.translate("gui.config.name.advanced.itemmessages"));
        entityTargetMessagesButton = new ExtendedButtonControl(140, calc1, CraftPresence.GUIS.getButtonY(3), 160, 20, ModUtils.TRANSLATOR.translate("gui.config.name.advanced.entitytargetmessages"));
        entityRidingMessagesButton = new ExtendedButtonControl(150, calc2, CraftPresence.GUIS.getButtonY(3), 160, 20, ModUtils.TRANSLATOR.translate("gui.config.name.advanced.entityridingmessages"));

        characterEditorButton = new ExtendedButtonControl(160, (width / 2) - 90, CraftPresence.GUIS.getButtonY(4), 180, 20, ModUtils.TRANSLATOR.translate("gui.config.buttonMessage.chareditor"));

        enableCommandsButton = new CheckBoxControl(200, calc1, CraftPresence.GUIS.getButtonY(5), ModUtils.TRANSLATOR.translate("gui.config.name.advanced.enablecommands"), CraftPresence.CONFIG.enableCommands);
        enablePerGUIButton = new CheckBoxControl(300, calc2, CraftPresence.GUIS.getButtonY(5), ModUtils.TRANSLATOR.translate("gui.config.name.advanced.enablepergui"), CraftPresence.CONFIG.enablePERGUI);
        enablePerItemButton = new CheckBoxControl(400, calc1, CraftPresence.GUIS.getButtonY(6) - 10, ModUtils.TRANSLATOR.translate("gui.config.name.advanced.enableperitem"), CraftPresence.CONFIG.enablePERItem);
        enablePerEntityButton = new CheckBoxControl(500, calc2, CraftPresence.GUIS.getButtonY(6) - 10, ModUtils.TRANSLATOR.translate("gui.config.name.advanced.enableperentity"), CraftPresence.CONFIG.enablePEREntity);
        renderTooltipsButton = new CheckBoxControl(600, calc1, CraftPresence.GUIS.getButtonY(7) - 20, ModUtils.TRANSLATOR.translate("gui.config.name.advanced.rendertooltips"), CraftPresence.CONFIG.renderTooltips);
        formatWordsButton = new CheckBoxControl(700, calc2, CraftPresence.GUIS.getButtonY(7) - 20, ModUtils.TRANSLATOR.translate("gui.config.name.advanced.formatwords"), CraftPresence.CONFIG.formatWords);
        debugModeButton = new CheckBoxControl(800, calc1, CraftPresence.GUIS.getButtonY(8) - 30, ModUtils.TRANSLATOR.translate("gui.config.name.advanced.debugmode"), CraftPresence.CONFIG.debugMode);
        proceedButton = new ExtendedButtonControl(900, (width / 2) - 90, (height - 30), 180, 20, ModUtils.TRANSLATOR.translate("gui.config.buttonMessage.back"));

        buttonList.add(guiMessagesButton);
        buttonList.add(itemMessagesButton);
        buttonList.add(entityTargetMessagesButton);
        buttonList.add(entityRidingMessagesButton);
        buttonList.add(characterEditorButton);
        buttonList.add(enableCommandsButton);
        buttonList.add(enablePerGUIButton);
        buttonList.add(enablePerItemButton);
        buttonList.add(enablePerEntityButton);
        buttonList.add(renderTooltipsButton);
        buttonList.add(formatWordsButton);
        buttonList.add(debugModeButton);
        buttonList.add(proceedButton);

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        CraftPresence.GUIS.drawBackground(width, height);

        final String mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title");
        final String subTitle = ModUtils.TRANSLATOR.translate("gui.config.title.advanced");
        final String splitCharacterText = ModUtils.TRANSLATOR.translate("gui.config.name.advanced.splitcharacter");

        drawString(mc.fontRenderer, mainTitle, (width / 2) - (StringUtils.getStringWidth(mainTitle) / 2), 10, 0xFFFFFF);
        drawString(mc.fontRenderer, subTitle, (width / 2) - (StringUtils.getStringWidth(subTitle) / 2), 20, 0xFFFFFF);
        drawString(mc.fontRenderer, splitCharacterText, (width / 2) - 130, CraftPresence.GUIS.getButtonY(1) + 5, 0xFFFFFF);
        splitCharacter.drawTextBox();

        proceedButton.enabled = !StringUtils.isNullOrEmpty(splitCharacter.getText()) && splitCharacter.getText().length() == 1 && !splitCharacter.getText().matches(".*[a-z].*") && !splitCharacter.getText().matches(".*[A-Z].*") && !splitCharacter.getText().matches(".*[0-9].*");
        guiMessagesButton.enabled = CraftPresence.GUIS.enabled;
        itemMessagesButton.enabled = CraftPresence.TILE_ENTITIES.enabled;
        entityTargetMessagesButton.enabled = CraftPresence.ENTITIES.enabled;
        entityRidingMessagesButton.enabled = CraftPresence.ENTITIES.enabled;

        super.drawScreen(mouseX, mouseY, partialTicks);

        // Hovering over Split Character Message Label
        if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, (width / 2f) - 130, CraftPresence.GUIS.getButtonY(1) + 5, StringUtils.getStringWidth(splitCharacterText), mc.fontRenderer.FONT_HEIGHT)) {
            CraftPresence.GUIS.drawMultiLineString(StringUtils.splitTextByNewLine(ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.splitcharacter")), mouseX, mouseY, width, height, -1, mc.fontRenderer, true);
        }
        if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, enableCommandsButton)) {
            CraftPresence.GUIS.drawMultiLineString(StringUtils.splitTextByNewLine(ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.enablecommands")), mouseX, mouseY, width, height, -1, mc.fontRenderer, true);
        }
        if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, enablePerGUIButton)) {
            CraftPresence.GUIS.drawMultiLineString(StringUtils.splitTextByNewLine(ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.enablepergui")), mouseX, mouseY, width, height, -1, mc.fontRenderer, true);
        }
        if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, enablePerItemButton)) {
            CraftPresence.GUIS.drawMultiLineString(StringUtils.splitTextByNewLine(ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.enableperitem")), mouseX, mouseY, width, height, -1, mc.fontRenderer, true);
        }
        if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, enablePerEntityButton)) {
            // TODO
        }
        if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, renderTooltipsButton)) {
            CraftPresence.GUIS.drawMultiLineString(StringUtils.splitTextByNewLine(ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.rendertooltips")), mouseX, mouseY, width, height, -1, mc.fontRenderer, true);
        }
        if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, formatWordsButton)) {
            CraftPresence.GUIS.drawMultiLineString(StringUtils.splitTextByNewLine(ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.formatwords")), mouseX, mouseY, width, height, -1, mc.fontRenderer, true);
        }
        if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, debugModeButton)) {
            CraftPresence.GUIS.drawMultiLineString(StringUtils.splitTextByNewLine(ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.debugmode")), mouseX, mouseY, width, height, -1, mc.fontRenderer, true);
        }
        if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, guiMessagesButton)) {
            if (!guiMessagesButton.enabled) {
                CraftPresence.GUIS.drawMultiLineString(StringUtils.splitTextByNewLine(ModUtils.TRANSLATOR.translate("gui.config.hoverMessage.access", ModUtils.TRANSLATOR.translate("gui.config.name.advanced.guimessages"))), mouseX, mouseY, width, height, -1, mc.fontRenderer, true);
            } else {
                CraftPresence.GUIS.drawMultiLineString(StringUtils.splitTextByNewLine(ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.guimessages")), mouseX, mouseY, width, height, -1, mc.fontRenderer, true);
            }
        }
        if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, itemMessagesButton)) {
            if (!itemMessagesButton.enabled) {
                CraftPresence.GUIS.drawMultiLineString(StringUtils.splitTextByNewLine(ModUtils.TRANSLATOR.translate("gui.config.hoverMessage.access", ModUtils.TRANSLATOR.translate("gui.config.name.advanced.itemmessages"))), mouseX, mouseY, width, height, -1, mc.fontRenderer, true);
            } else {
                CraftPresence.GUIS.drawMultiLineString(StringUtils.splitTextByNewLine(ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.itemmessages")), mouseX, mouseY, width, height, -1, mc.fontRenderer, true);
            }
        }
        if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, entityTargetMessagesButton)) {
            // TODO
        }
        if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, entityRidingMessagesButton)) {
            // TODO
        }
        if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, proceedButton) && !proceedButton.enabled) {
            CraftPresence.GUIS.drawMultiLineString(StringUtils.splitTextByNewLine(ModUtils.TRANSLATOR.translate("gui.config.hoverMessage.defaultempty")), mouseX, mouseY, width, height, -1, mc.fontRenderer, true);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == proceedButton.id) {
            if (!splitCharacter.getText().equals(CraftPresence.CONFIG.splitCharacter)) {
                CraftPresence.CONFIG.hasChanged = true;
                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                CraftPresence.CONFIG.queuedSplitCharacter = splitCharacter.getText();
            }
            if (enableCommandsButton.isChecked() != CraftPresence.CONFIG.enableCommands) {
                CraftPresence.CONFIG.hasChanged = true;
                CraftPresence.CONFIG.enableCommands = enableCommandsButton.isChecked();
            }
            if (enablePerGUIButton.isChecked() != CraftPresence.CONFIG.enablePERGUI) {
                CraftPresence.CONFIG.hasChanged = true;
                if (CraftPresence.GUIS.GUI_NAMES.isEmpty()) {
                    CraftPresence.GUIS.getGUIs();
                    CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                }
                CraftPresence.CONFIG.enablePERGUI = enablePerGUIButton.isChecked();
            }
            if (enablePerItemButton.isChecked() != CraftPresence.CONFIG.enablePERItem) {
                CraftPresence.CONFIG.hasChanged = true;
                if (CraftPresence.TILE_ENTITIES.TILE_ENTITY_NAMES.isEmpty()) {
                    CraftPresence.TILE_ENTITIES.getEntities();
                    CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                }
                CraftPresence.CONFIG.enablePERItem = enablePerItemButton.isChecked();
            }
            if (enablePerEntityButton.isChecked() != CraftPresence.CONFIG.enablePEREntity) {
                CraftPresence.CONFIG.hasChanged = true;
                if (CraftPresence.ENTITIES.ENTITY_NAMES.isEmpty()) {
                    CraftPresence.ENTITIES.getEntities();
                    CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                }
                CraftPresence.CONFIG.enablePEREntity = enablePerEntityButton.isChecked();
            }
            if (renderTooltipsButton.isChecked() != CraftPresence.CONFIG.renderTooltips) {
                CraftPresence.CONFIG.hasChanged = true;
                if (renderTooltipsButton.isChecked() && (Arrays.equals(StringUtils.MC_CHAR_WIDTH, new int[256]) || Arrays.equals(StringUtils.MC_GLYPH_WIDTH, new byte[65536]))) {
                    ModUtils.loadCharData(true, "UTF-8");
                }
                CraftPresence.CONFIG.renderTooltips = renderTooltipsButton.isChecked();
            }
            if (formatWordsButton.isChecked() != CraftPresence.CONFIG.formatWords) {
                CraftPresence.CONFIG.hasChanged = true;
                CraftPresence.CONFIG.formatWords = formatWordsButton.isChecked();
            }
            if (debugModeButton.isChecked() != CraftPresence.CONFIG.debugMode) {
                CraftPresence.CONFIG.hasChanged = true;
                CraftPresence.CONFIG.debugMode = debugModeButton.isChecked();
            }
            CraftPresence.GUIS.openScreen(parentScreen);
        } else if (button.id == guiMessagesButton.id) {
            CraftPresence.GUIS.openScreen(new SelectorGui(currentScreen, CraftPresence.CONFIG.NAME_guiMessages, ModUtils.TRANSLATOR.translate("gui.config.title.selector.gui"), CraftPresence.GUIS.GUI_NAMES, null, null, true));
        } else if (button.id == itemMessagesButton.id) {
            CraftPresence.GUIS.openScreen(new SelectorGui(currentScreen, CraftPresence.CONFIG.NAME_itemMessages, ModUtils.TRANSLATOR.translate("gui.config.title.selector.item"), CraftPresence.TILE_ENTITIES.TILE_ENTITY_NAMES, null, null, true));
        } else if (button.id == entityTargetMessagesButton.id) {
            CraftPresence.GUIS.openScreen(new MessageGui(currentScreen, Lists.newArrayList("Unimplemented Function, Come back later!"))); // TODO
        } else if (button.id == entityRidingMessagesButton.id) {
            CraftPresence.GUIS.openScreen(new MessageGui(currentScreen, Lists.newArrayList("Unimplemented Function, Come back later!"))); // TODO
        } else if (button.id == characterEditorButton.id) {
            CraftPresence.GUIS.openScreen(new CharacterEditorGui(currentScreen));
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            CraftPresence.GUIS.openScreen(parentScreen);
        }
        splitCharacter.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        splitCharacter.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        splitCharacter.updateCursorCounter();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
}
