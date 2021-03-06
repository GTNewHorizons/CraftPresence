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
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.utils.FileUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.world.biome.BiomeGenBase;

/**
 * Biome Utilities used to Parse Biome Data and handle related RPC Events
 *
 * @author CDAGaming
 */
public class BiomeUtils {
    /**
     * A List of the detected Biome Type's
     */
    private final List<BiomeGenBase> BIOME_TYPES = Lists.newArrayList();
    /**
     * Whether this module is active and currently in use
     */
    public boolean isInUse = false;
    /**
     * Whether this module is allowed to start and enabled
     */
    public boolean enabled = false;
    /**
     * A List of the detected Biome Names
     */
    public List<String> BIOME_NAMES = Lists.newArrayList();
    /**
     * The Name of the Current Biome the Player is in
     */
    private String CURRENT_BIOME_NAME;

    /**
     * Clears FULL Data from this Module
     */
    private void emptyData() {
        BIOME_NAMES.clear();
        BIOME_TYPES.clear();
        clearClientData();
    }

    /**
     * Clears Runtime Client Data from this Module (PARTIAL Clear)
     */
    public void clearClientData() {
        CURRENT_BIOME_NAME = null;

        isInUse = false;
        CraftPresence.CLIENT.initArgument("&BIOME&");
    }

    /**
     * Module Event to Occur on each tick within the Application
     */
    public void onTick() {
        enabled = !CraftPresence.CONFIG.hasChanged ? CraftPresence.CONFIG.detectBiomeData : enabled;
        final boolean needsUpdate = enabled && (BIOME_NAMES.isEmpty() || BIOME_TYPES.isEmpty());

        if (needsUpdate) {
            getBiomes();
        }

        if (enabled) {
            if (CraftPresence.player != null) {
                isInUse = true;
                updateBiomeData();
            } else if (isInUse) {
                clearClientData();
            }
        } else {
            emptyData();
        }
    }

    /**
     * Synchronizes Data related to this module, if needed
     */
    private void updateBiomeData() {
        final BiomeGenBase newBiome = CraftPresence.player.worldObj.getBiomeGenForCoords(
                (int) CraftPresence.player.posX, (int) CraftPresence.player.posZ);
        final String newBiomeName = newBiome.biomeName;

        if (!newBiomeName.equals(CURRENT_BIOME_NAME)) {
            CURRENT_BIOME_NAME = newBiomeName;

            if (!BIOME_NAMES.contains(newBiomeName)) {
                BIOME_NAMES.add(newBiomeName);
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
        List<Pair<String, String>> biomeArgs = Lists.newArrayList();

        biomeArgs.add(new Pair<>("&BIOME&", CURRENT_BIOME_NAME));

        // Add All Generalized Arguments, if any
        if (!CraftPresence.CLIENT.generalArgs.isEmpty()) {
            biomeArgs.addAll(CraftPresence.CLIENT.generalArgs);
        }

        final String defaultBiomeMessage = StringUtils.getConfigPart(
                CraftPresence.CONFIG.biomeMessages, "default", 0, 1, CraftPresence.CONFIG.splitCharacter, null);
        final String currentBiomeMessage = StringUtils.getConfigPart(
                CraftPresence.CONFIG.biomeMessages,
                CURRENT_BIOME_NAME,
                0,
                1,
                CraftPresence.CONFIG.splitCharacter,
                defaultBiomeMessage);
        final String currentBiomeIcon = StringUtils.getConfigPart(
                CraftPresence.CONFIG.biomeMessages,
                CURRENT_BIOME_NAME,
                0,
                2,
                CraftPresence.CONFIG.splitCharacter,
                CURRENT_BIOME_NAME);
        final String formattedIconKey = StringUtils.formatAsIcon(currentBiomeIcon.replace(" ", "_"));

        final String CURRENT_BIOME_ICON = formattedIconKey.replace("&icon&", CraftPresence.CONFIG.defaultBiomeIcon);
        final String CURRENT_BIOME_MESSAGE = StringUtils.sequentialReplaceAnyCase(currentBiomeMessage, biomeArgs);

        CraftPresence.CLIENT.syncArgument("&BIOME&", CURRENT_BIOME_MESSAGE, false);
        CraftPresence.CLIENT.syncArgument(
                "&BIOME&",
                CraftPresence.CLIENT.imageOf(CURRENT_BIOME_ICON, CraftPresence.CONFIG.defaultBiomeIcon, true),
                true);
    }

    /**
     * Retrieves a List of detected Biome Types
     *
     * @return The detected Biome Types found
     */
    private List<BiomeGenBase> getBiomeTypes() {
        List<BiomeGenBase> biomeTypes = Lists.newArrayList();

        if (BiomeGenBase.getBiomeGenArray() != null) {
            for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
                if (biome != null && !biomeTypes.contains(biome)) {
                    biomeTypes.add(biome);
                }
            }
        }

        if (biomeTypes.isEmpty()) {
            // Fallback: Use Manual Class Lookup
            for (Class<?> classObj : FileUtils.getClassNamesMatchingSuperType(
                    BiomeGenBase.class, true, "net.minecraft", "com.gitlab.cdagaming.craftpresence")) {
                if (classObj != null) {
                    try {
                        BiomeGenBase biomeObj = (BiomeGenBase) classObj.newInstance();
                        if (biomeObj != null && !biomeTypes.contains(biomeObj)) {
                            biomeTypes.add(biomeObj);
                        }
                    } catch (Exception | Error ex) {
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
        for (BiomeGenBase biome : getBiomeTypes()) {
            if (biome != null) {
                if (!BIOME_NAMES.contains(biome.biomeName)) {
                    BIOME_NAMES.add(biome.biomeName);
                }
                if (!BIOME_TYPES.contains(biome)) {
                    BIOME_TYPES.add(biome);
                }
            }
        }

        for (String biomeMessage : CraftPresence.CONFIG.biomeMessages) {
            if (!StringUtils.isNullOrEmpty(biomeMessage)) {
                final String[] part = biomeMessage.split(CraftPresence.CONFIG.splitCharacter);
                if (!StringUtils.isNullOrEmpty(part[0]) && !BIOME_NAMES.contains(part[0])) {
                    BIOME_NAMES.add(part[0]);
                }
            }
        }
    }
}
