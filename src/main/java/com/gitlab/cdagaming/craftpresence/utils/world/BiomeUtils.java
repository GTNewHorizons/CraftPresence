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

package com.gitlab.cdagaming.craftpresence.utils.world;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.impl.discord.ArgumentType;
import com.gitlab.cdagaming.craftpresence.utils.FileUtils;
import com.gitlab.cdagaming.craftpresence.utils.MappingUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.world.biome.Biome;

import java.util.List;
import java.util.Map;

/**
 * Biome Utilities used to Parse Biome Data and handle related RPC Events
 *
 * @author CDAGaming
 */
public class BiomeUtils {
    /**
     * A List of the detected Biome Type's
     */
    private final List<Biome> BIOME_TYPES = Lists.newArrayList();
    /**
     * The argument format to follow for Rich Presence Data
     */
    private final String argumentFormat = "&BIOME&";
    /**
     * The sub-argument format to follow for Rich Presence Data
     */
    private final String subArgumentFormat = "&BIOME:";
    /**
     * A Mapping of the Arguments attached to the &BIOME& RPC Message placeholder
     */
    private final List<Pair<String, String>> biomeArgs = Lists.newArrayList();
    /**
     * A Mapping of the Arguments attached to the &ICON& RPC Message placeholder
     */
    private final List<Pair<String, String>> iconArgs = Lists.newArrayList();
    /**
     * Whether this module is active and currently in use
     */
    public boolean isInUse = false;
    /**
     * Whether this module is allowed to start and enabled
     */
    public boolean enabled = false;
    /**
     * Whether this module has performed an initial retrieval of items
     */
    public boolean hasScanned = false;
    /**
     * A List of the detected Biome Names
     */
    public List<String> BIOME_NAMES = Lists.newArrayList();
    /**
     * The Name of the Current Biome the Player is in
     */
    private String CURRENT_BIOME_NAME;
    /**
     * The alternative name for the Current Biome the Player is in, if any
     */
    private String CURRENT_BIOME_IDENTIFIER;

    /**
     * Clears FULL Data from this Module
     */
    private void emptyData() {
        hasScanned = false;
        BIOME_NAMES.clear();
        BIOME_TYPES.clear();
        clearClientData();
    }

    /**
     * Clears Runtime Client Data from this Module (PARTIAL Clear)
     */
    public void clearClientData() {
        CURRENT_BIOME_NAME = null;
        CURRENT_BIOME_IDENTIFIER = null;
        biomeArgs.clear();
        iconArgs.clear();

        isInUse = false;
        CraftPresence.CLIENT.removeArgumentsMatching(subArgumentFormat);
        CraftPresence.CLIENT.initArgument(argumentFormat);
        CraftPresence.CLIENT.clearOverride(argumentFormat);
    }

    /**
     * Module Event to Occur on each tick within the Application
     */
    public void onTick() {
        enabled = !CraftPresence.CONFIG.hasChanged ? CraftPresence.CONFIG.generalSettings.detectBiomeData : enabled;
        final boolean needsUpdate = enabled && !hasScanned;

        if (needsUpdate) {
            new Thread(this::getBiomes, "CraftPresence-Biome-Lookup").start();
            hasScanned = true;
        }

        if (enabled) {
            if (CraftPresence.player != null) {
                isInUse = true;
                updateBiomeData();
            } else if (isInUse) {
                clearClientData();
            }
        } else if (isInUse) {
            emptyData();
        }
    }

    /**
     * Synchronizes Data related to this module, if needed
     */
    private void updateBiomeData() {
        final Biome newBiome = CraftPresence.player.world.getBiome(CraftPresence.player.getPosition());
        final String newBiomeName = StringUtils.formatIdentifier(newBiome.getBiomeName(), false, !CraftPresence.CONFIG.advancedSettings.formatWords);

        final String newBiome_primaryIdentifier = StringUtils.formatIdentifier(newBiome.getBiomeName(), true, !CraftPresence.CONFIG.advancedSettings.formatWords);
        final String newBiome_alternativeIdentifier = StringUtils.formatIdentifier(MappingUtils.getClassName(newBiome), true, !CraftPresence.CONFIG.advancedSettings.formatWords);
        final String newBiome_Identifier = !StringUtils.isNullOrEmpty(newBiome_primaryIdentifier) ? newBiome_primaryIdentifier : newBiome_alternativeIdentifier;

        if (!newBiomeName.equals(CURRENT_BIOME_NAME) || !newBiome_Identifier.equals(CURRENT_BIOME_IDENTIFIER)) {
            CURRENT_BIOME_NAME = !StringUtils.isNullOrEmpty(newBiomeName) ? newBiomeName : newBiome_Identifier;
            CURRENT_BIOME_IDENTIFIER = newBiome_Identifier;

            if (!BIOME_NAMES.contains(newBiome_Identifier)) {
                BIOME_NAMES.add(newBiome_Identifier);
            }
            if (!BIOME_TYPES.contains(newBiome)) {
                BIOME_TYPES.add(newBiome);
            }

            updateBiomePresence();
        }
    }

    /**
     * Updates RPC Data related to this Module
     */
    public void updateBiomePresence() {
        // Form Biome Argument List
        biomeArgs.clear();
        iconArgs.clear();

        final ModuleData defaultData = CraftPresence.CONFIG.biomeSettings.biomeData.get("default");
        final ModuleData currentData = CraftPresence.CONFIG.biomeSettings.biomeData.get(CURRENT_BIOME_IDENTIFIER);

        final String defaultMessage = Config.isValidProperty(defaultData, "textOverride") ? defaultData.getTextOverride() : "";
        final String currentMessage = Config.isValidProperty(currentData, "textOverride") ? currentData.getTextOverride() : defaultMessage;
        final String currentIcon = Config.isValidProperty(currentData, "iconOverride") ? currentData.getIconOverride() : CURRENT_BIOME_IDENTIFIER;
        final String formattedIcon = StringUtils.formatAsIcon(currentIcon.replace(" ", "_"));

        biomeArgs.add(new Pair<>("&BIOME&", CURRENT_BIOME_NAME));

        iconArgs.add(new Pair<>("&ICON&", CraftPresence.CONFIG.biomeSettings.fallbackBiomeIcon));

        // Add applicable args as sub-placeholders
        for (Pair<String, String> argumentData : biomeArgs) {
            CraftPresence.CLIENT.syncArgument(subArgumentFormat + argumentData.getFirst().substring(1), argumentData.getSecond(), ArgumentType.Text);
        }
        for (Pair<String, String> argumentData : iconArgs) {
            CraftPresence.CLIENT.syncArgument(subArgumentFormat + argumentData.getFirst().substring(1), argumentData.getSecond(), ArgumentType.Image);
        }

        // Add All Generalized Arguments, if any
        if (!CraftPresence.CLIENT.generalArgs.isEmpty()) {
            StringUtils.addEntriesNotPresent(biomeArgs, CraftPresence.CLIENT.generalArgs);
        }

        final String CURRENT_BIOME_ICON = StringUtils.sequentialReplaceAnyCase(formattedIcon, iconArgs);
        final String CURRENT_BIOME_MESSAGE = StringUtils.sequentialReplaceAnyCase(currentMessage, biomeArgs);

        CraftPresence.CLIENT.syncOverride(argumentFormat, currentData != null ? currentData : defaultData);
        CraftPresence.CLIENT.syncArgument(argumentFormat, CURRENT_BIOME_MESSAGE, ArgumentType.Text);
        CraftPresence.CLIENT.syncArgument(argumentFormat, CraftPresence.CLIENT.imageOf(argumentFormat, true, CURRENT_BIOME_ICON, CraftPresence.CONFIG.biomeSettings.fallbackBiomeIcon), ArgumentType.Image);
    }

    /**
     * Retrieves a List of detected Biome Types
     *
     * @return The detected Biome Types found
     */
    private List<Biome> getBiomeTypes() {
        List<Biome> biomeTypes = Lists.newArrayList();

        if (Biome.REGISTRY != null) {
            for (Biome biome : Biome.REGISTRY) {
                if (biome != null && !biomeTypes.contains(biome)) {
                    biomeTypes.add(biome);
                }
            }
        }

        if (biomeTypes.isEmpty()) {
            // Fallback: Use Manual Class Lookup
            for (Class<?> classObj : FileUtils.getClassNamesMatchingSuperType(Biome.class, CraftPresence.CONFIG.advancedSettings.includeExtraGuiClasses)) {
                if (classObj != null) {
                    try {
                        Biome biomeObj = (Biome) classObj.getDeclaredConstructor().newInstance();
                        if (!biomeTypes.contains(biomeObj)) {
                            biomeTypes.add(biomeObj);
                        }
                    } catch (Throwable ex) {
                        if (ModUtils.IS_VERBOSE) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }

        return biomeTypes;
    }

    /**
     * Updates and Initializes Module Data, based on found Information
     */
    public void getBiomes() {
        for (Biome biome : getBiomeTypes()) {
            if (biome != null) {
                String biomeName = !StringUtils.isNullOrEmpty(biome.getBiomeName()) ? biome.getBiomeName() : MappingUtils.getClassName(biome);
                String name = StringUtils.formatIdentifier(biomeName, true, !CraftPresence.CONFIG.advancedSettings.formatWords);
                if (!BIOME_NAMES.contains(name)) {
                    BIOME_NAMES.add(name);
                }
                if (!BIOME_TYPES.contains(biome)) {
                    BIOME_TYPES.add(biome);
                }
            }
        }

        for (String biomeEntry : CraftPresence.CONFIG.biomeSettings.biomeData.keySet()) {
            if (!StringUtils.isNullOrEmpty(biomeEntry)) {
                String name = StringUtils.formatIdentifier(biomeEntry, true, !CraftPresence.CONFIG.advancedSettings.formatWords);
                if (!BIOME_NAMES.contains(name)) {
                    BIOME_NAMES.add(name);
                }
            }
        }
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param types The argument types to interpret
     * @return the parsable string
     */
    public String generateArgumentMessage(ArgumentType... types) {
        types = (types != null && types.length > 0 ? types : ArgumentType.values());
        final Map<ArgumentType, List<String>> argumentData = Maps.newHashMap();
        List<String> queuedEntries;
        for (ArgumentType type : types) {
            queuedEntries = Lists.newArrayList();
            if (type == ArgumentType.Image) {
                queuedEntries.add(subArgumentFormat + "ICON&");
            } else if (type == ArgumentType.Text) {
                queuedEntries.add(subArgumentFormat + "BIOME&");
            }
            argumentData.put(type, queuedEntries);
        }
        return CraftPresence.CLIENT.generateArgumentMessage(argumentFormat, subArgumentFormat, argumentData);
    }
}
