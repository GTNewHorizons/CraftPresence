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

package com.gitlab.cdagaming.craftpresence.integrations.curse;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.integrations.curse.impl.CurseInstance;
import com.gitlab.cdagaming.craftpresence.integrations.curse.impl.Manifest;
import com.gitlab.cdagaming.craftpresence.utils.FileUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Set of Utilities used to Parse Curse Manifest Information
 * <p>Applies to: Twitch, Curse, and GDLauncher
 *
 * @author CDAGaming
 */
public class CurseUtils {
    /**
     * The Curse Pack Instance Name
     */
    public static String INSTANCE_NAME;

    /**
     * Attempts to retrieve and load Manifest/Instance Information, if any
     */
    public static void loadManifest() {
        ModUtils.LOG.info(ModUtils.TRANSLATOR.translate("craftpresence.logger.info.manifest.init"));

        Manifest manifest = null;
        CurseInstance instance = null;

        try {
            // Attempt to Gain Curse Pack Info from the manifest.json file
            // This will typically work on released/exported/imported packs
            // But will fail with Custom/User-Created Packs
            // Note: This additionally works in the same way for GDLauncher packs of the same nature
            manifest = FileUtils.getJSONFromFile(new File("manifest.json"), Manifest.class);
        } catch (Exception ex) {
            try {
                // If it fails to get the information from the manifest.json
                // Attempt to read Pack info from the minecraftinstance.json file
                // As Most if not all types of Curse Packs contain this file
                // Though it is considered a fallback due to how much it's parsing
                ModUtils.LOG.info(ModUtils.TRANSLATOR.translate("craftpresence.logger.info.manifest.fallback"));
                if (ex.getClass() != FileNotFoundException.class || ModUtils.IS_VERBOSE) {
                    ex.printStackTrace();
                }

                instance = FileUtils.getJSONFromFile(new File("minecraftinstance.json"), CurseInstance.class);
            } catch (Exception ex2) {
                ModUtils.LOG.error(ModUtils.TRANSLATOR.translate("craftpresence.logger.error.file.manifest"));

                if (ex2.getClass() != FileNotFoundException.class || ModUtils.IS_VERBOSE) {
                    ex2.printStackTrace();
                }
            }
        } finally {
            if (manifest != null || instance != null) {
                INSTANCE_NAME = manifest != null ? manifest.name : instance.name;

                if (!StringUtils.isNullOrEmpty(INSTANCE_NAME)) {
                    CraftPresence.packFound = true;
                    ModUtils.LOG.info(
                            ModUtils.TRANSLATOR.translate("craftpresence.logger.info.manifest.loaded", INSTANCE_NAME));
                }
            }
        }
    }
}
