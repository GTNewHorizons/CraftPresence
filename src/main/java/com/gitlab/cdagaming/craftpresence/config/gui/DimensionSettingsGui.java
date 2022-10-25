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

package com.gitlab.cdagaming.craftpresence.config.gui;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAssetUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ScrollableListControl.RenderType;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.DynamicEditorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.SelectorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import net.minecraft.client.gui.GuiScreen;

@SuppressWarnings("DuplicatedCode")
public class DimensionSettingsGui extends ExtendedScreen {
    private ExtendedButtonControl proceedButton, dimensionMessagesButton;
    private ExtendedTextControl defaultMessage;

    DimensionSettingsGui(GuiScreen parentScreen) {
        super(parentScreen);
    }

    @Override
    public void initializeUi() {
        final Pair<String, String> defaultData = CraftPresence.CONFIG.dimensionMessages.get("default");
        final String defaultDimensionMessage = defaultData != null ? defaultData.getFirst() : "";

        defaultMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        (getScreenWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(1),
                        180, 20
                )
        );
        defaultMessage.setControlMessage(defaultDimensionMessage);

        dimensionMessagesButton = addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, CraftPresence.GUIS.getButtonY(2),
                        180, 20,
                        "gui.config.name.dimension_messages.dimension_messages",
                        () -> CraftPresence.GUIS.openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.dimension"), CraftPresence.DIMENSIONS.DIMENSION_NAMES,
                                        null, null,
                                        true, true, RenderType.None,
                                        (attributeName, currentValue) -> {
                                            // Event to Occur when proceeding with adjusted data
                                            final Pair<String, String> defaultDimensionData = CraftPresence.CONFIG.dimensionMessages.get("default");
                                            final Pair<String, String> currentDimensionData = CraftPresence.CONFIG.dimensionMessages.get(attributeName);
                                            final String defaultMessage = defaultDimensionData != null ? defaultDimensionData.getFirst() : "";
                                            final String currentMessage = currentDimensionData != null ? currentDimensionData.getFirst() : "";

                                            CraftPresence.CONFIG.hasChanged = true;
                                            final Pair<String, String> newData = new Pair<>();
                                            if (StringUtils.isNullOrEmpty(currentMessage) || currentMessage.equals(defaultMessage)) {
                                                newData.setFirst(defaultMessage);
                                            }
                                            newData.setSecond(currentValue);
                                            CraftPresence.CONFIG.dimensionMessages.put(attributeName, newData);
                                        },
                                        (currentValue, parentScreen) -> {
                                            // Event to occur when Setting Dynamic/Specific Data
                                            CraftPresence.GUIS.openScreen(
                                                    new DynamicEditorGui(
                                                            parentScreen, currentValue,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing new data
                                                                final Pair<String, String> defaultDimensionData = CraftPresence.CONFIG.dimensionMessages.get("default");
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = defaultDimensionData != null ? defaultDimensionData.getFirst() : "";
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                final Pair<String, String> defaultDimensionData = CraftPresence.CONFIG.dimensionMessages.get("default");
                                                                final Pair<String, String> currentDimensionData = CraftPresence.CONFIG.dimensionMessages.get(attributeName);
                                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.dimension.edit_specific_dimension", attributeName);
                                                                screenInstance.originalPrimaryMessage = defaultDimensionData != null ? defaultDimensionData.getFirst() : "";
                                                                screenInstance.primaryMessage = currentDimensionData != null ? currentDimensionData.getFirst() : screenInstance.originalPrimaryMessage;
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                final Pair<String, String> currentDimensionData = CraftPresence.CONFIG.dimensionMessages.get(attributeName);
                                                                currentDimensionData.setFirst(inputText);
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CraftPresence.CONFIG.dimensionMessages.put(attributeName, currentDimensionData);
                                                                if (!CraftPresence.DIMENSIONS.DIMENSION_NAMES.contains(attributeName)) {
                                                                    CraftPresence.DIMENSIONS.DIMENSION_NAMES.add(attributeName);
                                                                }
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when removing set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CraftPresence.CONFIG.dimensionMessages.remove(attributeName);
                                                                CraftPresence.DIMENSIONS.DIMENSION_NAMES.remove(attributeName);
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when adding an attachment icon to set data
                                                                final Pair<String, String> defaultDimensionData = CraftPresence.CONFIG.dimensionMessages.get("default");
                                                                final Pair<String, String> currentDimensionData = CraftPresence.CONFIG.dimensionMessages.get(attributeName);
                                                                final String defaultIcon = defaultDimensionData != null ? defaultDimensionData.getSecond() : CraftPresence.CONFIG.defaultDimensionIcon;
                                                                final String specificIcon = currentDimensionData != null ? currentDimensionData.getSecond() : defaultIcon;
                                                                CraftPresence.GUIS.openScreen(
                                                                        new SelectorGui(
                                                                                screenInstance,
                                                                                ModUtils.TRANSLATOR.translate("gui.config.title.selector.icon"), DiscordAssetUtils.ASSET_LIST.keySet(),
                                                                                specificIcon, attributeName,
                                                                                true, false, RenderType.DiscordAsset,
                                                                                (innerAttributeName, innerCurrentValue) -> {
                                                                                    // Inner-Event to occur when proceeding with adjusted data
                                                                                    final Pair<String, String> defaultInnerDimensionData = CraftPresence.CONFIG.dimensionMessages.get("default");
                                                                                    final Pair<String, String> currentInnerDimensionData = CraftPresence.CONFIG.dimensionMessages.get(innerAttributeName);
                                                                                    final String defaultMessage = defaultInnerDimensionData != null ? defaultInnerDimensionData.getFirst() : "";
                                                                                    final String currentMessage = currentInnerDimensionData != null ? currentInnerDimensionData.getFirst() : "";

                                                                                    CraftPresence.CONFIG.hasChanged = true;
                                                                                    final Pair<String, String> newData = new Pair<>();
                                                                                    if (StringUtils.isNullOrEmpty(currentMessage) || currentMessage.equals(defaultMessage)) {
                                                                                        newData.setFirst(defaultMessage);
                                                                                    }
                                                                                    newData.setSecond(innerCurrentValue);
                                                                                    CraftPresence.CONFIG.dimensionMessages.put(innerAttributeName, newData);
                                                                                }, null
                                                                        )
                                                                );
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Message Label
                                                                CraftPresence.GUIS.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                ModUtils.TRANSLATOR.translate("gui.config.comment.dimension_messages.dimension_messages",
                                                                                        CraftPresence.DIMENSIONS.generateArgumentMessage())
                                                                        ), screenInstance, true
                                                                );
                                                            }
                                                    )
                                            );
                                        }
                                )
                        ),
                        () -> {
                            if (!dimensionMessagesButton.isControlEnabled()) {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.message.hover.access",
                                                        ModUtils.TRANSLATOR.translate("gui.config.name.general.detect_dimension_data"))
                                        ), this, true
                                );
                            } else {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.comment.dimension_messages.dimension_messages",
                                                        CraftPresence.DIMENSIONS.generateArgumentMessage())
                                        ), this, true
                                );
                            }
                        }
                )
        );
        // Adding Default Icon Button
        addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, CraftPresence.GUIS.getButtonY(3),
                        180, 20,
                        "gui.config.name.dimension_messages.dimension_icon",
                        () -> CraftPresence.GUIS.openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.icon"), DiscordAssetUtils.ASSET_LIST.keySet(),
                                        CraftPresence.CONFIG.defaultDimensionIcon, null,
                                        true, false, RenderType.DiscordAsset,
                                        (attributeName, currentValue) -> {
                                            CraftPresence.CONFIG.hasChanged = true;
                                            CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                            CraftPresence.CONFIG.defaultDimensionIcon = currentValue;
                                        }, null
                                )
                        ),
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.dimension_messages.dimension_icon")
                                ), this, true
                        )
                )
        );
        proceedButton = addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, (getScreenHeight() - 30),
                        180, 20,
                        "gui.config.message.button.back",
                        () -> {
                            if (!defaultMessage.getControlMessage().equals(defaultDimensionMessage)) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                final Pair<String, String> defaultDimensionData = CraftPresence.CONFIG.dimensionMessages.getOrDefault("default", new Pair<>());
                                defaultDimensionData.setFirst(defaultMessage.getControlMessage());
                                CraftPresence.CONFIG.dimensionMessages.put("default", defaultDimensionData);
                            }
                            CraftPresence.GUIS.openScreen(parentScreen);
                        },
                        () -> {
                            if (!proceedButton.isControlEnabled()) {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.message.hover.empty.default")
                                        ), this, true
                                );
                            }
                        }
                )
        );

        super.initializeUi();
    }

    @Override
    public void preRender() {
        final String mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title");
        final String subTitle = ModUtils.TRANSLATOR.translate("gui.config.title.dimension_messages");
        final String defaultMessageText = ModUtils.TRANSLATOR.translate("gui.config.message.default.dimension");

        renderString(mainTitle, (getScreenWidth() / 2f) - (getStringWidth(mainTitle) / 2f), 10, 0xFFFFFF);
        renderString(subTitle, (getScreenWidth() / 2f) - (getStringWidth(subTitle) / 2f), 20, 0xFFFFFF);
        renderString(defaultMessageText, (getScreenWidth() / 2f) - 140, CraftPresence.GUIS.getButtonY(1, 5), 0xFFFFFF);

        proceedButton.setControlEnabled(!StringUtils.isNullOrEmpty(defaultMessage.getControlMessage()));
        dimensionMessagesButton.setControlEnabled(CraftPresence.DIMENSIONS.enabled);
    }

    @Override
    public void postRender() {
        final String defaultMessageText = ModUtils.TRANSLATOR.translate("gui.config.message.default.dimension");
        // Hovering over Default Dimension Message Label
        if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 140, CraftPresence.GUIS.getButtonY(1, 5), getStringWidth(defaultMessageText), getFontHeight())) {
            CraftPresence.GUIS.drawMultiLineString(
                    StringUtils.splitTextByNewLine(
                            ModUtils.TRANSLATOR.translate("gui.config.comment.dimension_messages.dimension_messages",
                                    CraftPresence.DIMENSIONS.generateArgumentMessage())
                    ), this, true
            );
        }
    }
}
