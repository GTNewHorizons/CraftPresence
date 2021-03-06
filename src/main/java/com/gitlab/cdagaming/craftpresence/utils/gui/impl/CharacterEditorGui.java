/*
 * MIT License
 *
 * Copyright (c) 2018 - 2022 CDAGaming (cstack2011@yahoo.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.gitlab.cdagaming.craftpresence.utils.gui.impl;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;

public class CharacterEditorGui extends ExtendedScreen {
    private ExtendedButtonControl saveButton, syncSingleButton;
    private ExtendedTextControl charInput, charWidth;
    private String lastScannedString;
    private char lastScannedChar;

    private int[] originalCharArray = StringUtils.MC_CHAR_WIDTH.clone();
    private byte[] originalGlyphArray = StringUtils.MC_GLYPH_WIDTH.clone();

    public CharacterEditorGui(GuiScreen parentScreen) {
        super(parentScreen);
    }

    @Override
    public void initializeUi() {
        charInput = addControl(
                new ExtendedTextControl(getFontRenderer(), (width / 2) + 3, CraftPresence.GUIS.getButtonY(1), 180, 20));
        charWidth = addControl(
                new ExtendedTextControl(getFontRenderer(), (width / 2) + 3, CraftPresence.GUIS.getButtonY(2), 180, 20));
        charInput.setMaxStringLength(1);
        charWidth.setMaxStringLength(2);

        final ExtendedButtonControl resetCharsButton = addControl(new ExtendedButtonControl(
                (width / 2) - 90,
                (height - 30),
                180,
                20,
                ModUtils.TRANSLATOR.translate("gui.config.message.button.reset"),
                () -> ModUtils.loadCharData(true, "UTF-8")));
        final ExtendedButtonControl syncAllButton = addControl(new ExtendedButtonControl(
                (width / 2) - 90,
                resetCharsButton.getControlPosY() - 25,
                180,
                20,
                ModUtils.TRANSLATOR.translate("gui.config.message.button.sync.all"),
                () -> {
                    // Sync ALL Values to FontRender Defaults
                    for (int currentCharIndex = 0;
                            currentCharIndex < StringUtils.MC_CHAR_WIDTH.length - 1;
                            currentCharIndex++) {
                        final char characterObj = (char) (currentCharIndex);
                        StringUtils.MC_CHAR_WIDTH[currentCharIndex] =
                                getFontRenderer().getStringWidth(Character.toString(characterObj));
                    }

                    for (int currentGlyphIndex = 0;
                            currentGlyphIndex < StringUtils.MC_GLYPH_WIDTH.length - 1;
                            currentGlyphIndex++) {
                        final char glyphObj = (char) (currentGlyphIndex & 255);
                        StringUtils.MC_GLYPH_WIDTH[currentGlyphIndex] =
                                (byte) getFontRenderer().getStringWidth(Character.toString(glyphObj));
                    }
                }));
        syncSingleButton = addControl(new ExtendedButtonControl(
                (width / 2) - 90,
                syncAllButton.getControlPosY() - 25,
                180,
                20,
                ModUtils.TRANSLATOR.translate("gui.config.message.button.sync.single", charInput.getText()),
                () -> {
                    // Sync Single Value to FontRender Defaults
                    if (lastScannedChar > 0
                            && lastScannedChar < StringUtils.MC_CHAR_WIDTH.length
                            && !ModUtils.TRANSLATOR.isUnicode) {
                        StringUtils.MC_CHAR_WIDTH[lastScannedChar] =
                                getFontRenderer().getStringWidth(Character.toString(lastScannedChar));
                    } else if (StringUtils.MC_GLYPH_WIDTH[lastScannedChar] != 0) {
                        StringUtils.MC_GLYPH_WIDTH[lastScannedChar & 255] =
                                (byte) getFontRenderer().getStringWidth(Character.toString(lastScannedChar));
                    }
                }));

        // Adding Back Button
        addControl(new ExtendedButtonControl(
                5,
                (height - 30),
                100,
                20,
                ModUtils.TRANSLATOR.translate("gui.config.message.button.back"),
                () -> CraftPresence.GUIS.openScreen(parentScreen)));
        saveButton = addControl(new ExtendedButtonControl(
                width - 105,
                (height - 30),
                100,
                20,
                ModUtils.TRANSLATOR.translate("gui.config.message.button.save"),
                () -> {
                    final Pair<Boolean, Integer> charData = StringUtils.getValidInteger(charWidth.getText());

                    if (charData.getFirst()) {
                        // Save Single Value if Char Data is a Valid Number
                        final int characterWidth = charData.getSecond();
                        if (lastScannedChar > 0
                                && lastScannedChar <= StringUtils.MC_CHAR_WIDTH.length
                                && !ModUtils.TRANSLATOR.isUnicode) {
                            StringUtils.MC_CHAR_WIDTH[lastScannedChar] = characterWidth;
                        } else if (StringUtils.MC_GLYPH_WIDTH[lastScannedChar] != 0) {
                            StringUtils.MC_GLYPH_WIDTH[lastScannedChar & 255] = (byte) characterWidth;
                        }
                    }
                }));

        super.initializeUi();
    }

    @Override
    public void preRender() {
        checkValues();

        final String mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.editor.character");
        final String charInputTitle = ModUtils.TRANSLATOR.translate("gui.config.message.editor.char.input");
        final String charWidthTitle = ModUtils.TRANSLATOR.translate("gui.config.message.editor.char.width");
        final List<String> notice =
                StringUtils.splitTextByNewLine(ModUtils.TRANSLATOR.translate("gui.config.message.character.notice"));

        renderNotice(notice, 2, 2);

        renderString(mainTitle, (width / 2f) - (StringUtils.getStringWidth(mainTitle) / 2f), 15, 0xFFFFFF);
        renderString(charInputTitle, (width / 2f) - 130, CraftPresence.GUIS.getButtonY(1, 5), 0xFFFFFF);

        if (StringUtils.isNullOrEmpty(charInput.getText())) {
            charWidth.setText("");
            charWidth.setVisible(false);
            charWidth.setEnabled(charWidth.getVisible());

            syncSingleButton.setControlEnabled(false);
        }

        if (!charInput.getText().equals(lastScannedString)) {
            lastScannedString = charInput.getText();

            if (!StringUtils.isNullOrEmpty(lastScannedString)) {
                lastScannedChar = lastScannedString.charAt(0);
                charWidth.setText(Integer.toString(StringUtils.getStringWidth(lastScannedString)));
                charWidth.setVisible(true);
                charWidth.setEnabled(charWidth.getVisible());

                syncSingleButton.setControlEnabled(true);
            } else {
                lastScannedChar = Character.UNASSIGNED;
            }
        }

        if (charWidth.getVisible()) {
            // Only Draw string for Character Width when it's enabled
            renderString(charWidthTitle, (width / 2f) - 130, CraftPresence.GUIS.getButtonY(2, 10), 0xFFFFFF);
        }

        syncSingleButton.setControlMessage(
                ModUtils.TRANSLATOR.translate("gui.config.message.button.sync.single", charInput.getText()));
        saveButton.setControlEnabled(syncSingleButton.isControlEnabled());
        syncSingleButton.setControlVisible(syncSingleButton.isControlEnabled());
        saveButton.setControlVisible(syncSingleButton.isControlVisible());
    }

    /**
     * Checks and Modifies Character and Glyph Arrays, depending on conditions
     */
    private void checkValues() {
        if (!Arrays.equals(originalCharArray, StringUtils.MC_CHAR_WIDTH)
                || !Arrays.equals(originalGlyphArray, StringUtils.MC_GLYPH_WIDTH)) {
            // Write to Char Data and Re-Set originalCharArray and originalGlyphArray
            ModUtils.writeToCharData("UTF-8");
            originalCharArray = StringUtils.MC_CHAR_WIDTH.clone();
            originalGlyphArray = StringUtils.MC_GLYPH_WIDTH.clone();

            // Clear Data for Next Entry
            lastScannedString = null;
            lastScannedChar = Character.UNASSIGNED;
            charInput.setText("");
            charWidth.setText("");
        }
    }
}
