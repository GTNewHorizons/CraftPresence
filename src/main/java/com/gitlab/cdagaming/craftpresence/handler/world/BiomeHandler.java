package com.gitlab.cdagaming.craftpresence.handler.world;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.handler.StringHandler;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public class BiomeHandler {
    public boolean enabled = false;
    public List<String> BIOME_NAMES = new ArrayList<>();
    private List<Integer> BIOME_IDS = new ArrayList<>();
    private List<Biome> BIOME_TYPES = new ArrayList<>();
    private String CURRENT_BIOME_NAME, formattedBiomeMSG;
    private Integer CURRENT_BIOME_ID;

    public void emptyData() {
        BIOME_NAMES.clear();
        BIOME_IDS.clear();
        BIOME_TYPES.clear();
    }

    public void onTick() {
        enabled = !CraftPresence.CONFIG.hasChanged ? CraftPresence.CONFIG.showCurrentBiome && !CraftPresence.CONFIG.showGameState : enabled;
        final boolean needsUpdate = enabled && (
                BIOME_NAMES.isEmpty() || BIOME_IDS.isEmpty() || BIOME_TYPES.isEmpty()
        ) || (
                !BIOME_TYPES.isEmpty() && getBiomeTypes() != BIOME_TYPES
        );
        final boolean removeBiomeData = (!enabled || CraftPresence.player == null) && (
                !StringHandler.isNullOrEmpty(CURRENT_BIOME_NAME) ||
                        CURRENT_BIOME_ID != null ||
                        !StringHandler.isNullOrEmpty(formattedBiomeMSG)
        );

        if (enabled) {
            if (needsUpdate) {
                getBiomes();
            }

            if (CraftPresence.player != null) {
                final Biome newBiome = CraftPresence.player.world.getBiome(CraftPresence.player.getPosition());
                final String newBiomeName = newBiome.getBiomeName();
                final Integer newBiomeID = Biome.getIdForBiome(newBiome);
                if (!newBiomeName.equals(CURRENT_BIOME_NAME) || !newBiomeID.equals(CURRENT_BIOME_ID)) {
                    CURRENT_BIOME_NAME = newBiomeName;
                    CURRENT_BIOME_ID = newBiomeID;
                    updateBiomePresence();

                    if (!BIOME_TYPES.contains(newBiome)) {
                        getBiomes();
                    }
                }
            }
        }

        if (removeBiomeData) {
            CraftPresence.CLIENT.GAME_STATE = CraftPresence.CLIENT.GAME_STATE.replace(formattedBiomeMSG, "");
            CraftPresence.CLIENT.updatePresence(CraftPresence.CLIENT.buildRichPresence());

            CURRENT_BIOME_NAME = null;
            CURRENT_BIOME_ID = null;
            formattedBiomeMSG = null;
        }
    }

    private List<Biome> getBiomeTypes() {
        List<Biome> biomeTypes = new ArrayList<>();
        for (Biome biome : Biome.REGISTRY) {
            if (biome != null) {
                biomeTypes.add(biome);
            }
        }

        return biomeTypes;
    }

    private void updateBiomePresence() {
        final String defaultBiomeMSG = StringHandler.getConfigPart(CraftPresence.CONFIG.biomeMessages, "default", 0, 1, CraftPresence.CONFIG.splitCharacter, null);
        final String currentBiomeMSG = StringHandler.getConfigPart(CraftPresence.CONFIG.biomeMessages, CURRENT_BIOME_NAME, 0, 1, CraftPresence.CONFIG.splitCharacter, defaultBiomeMSG);
        formattedBiomeMSG = currentBiomeMSG.replace("&biome&", CURRENT_BIOME_NAME).replace("&id&", CURRENT_BIOME_ID.toString());
        CraftPresence.CLIENT.GAME_STATE = formattedBiomeMSG;
        CraftPresence.CLIENT.updatePresence(CraftPresence.CLIENT.buildRichPresence());
    }

    public void getBiomes() {
        for (Biome biome : getBiomeTypes()) {
            if (biome != null) {
                if (!BIOME_NAMES.contains(biome.getBiomeName())) {
                    BIOME_NAMES.add(biome.getBiomeName());
                }
                if (!BIOME_IDS.contains(Biome.getIdForBiome(biome))) {
                    BIOME_IDS.add(Biome.getIdForBiome(biome));
                }
                if (!BIOME_TYPES.contains(biome)) {
                    BIOME_TYPES.add(biome);
                }
            }
        }

        for (String biomeMessage : CraftPresence.CONFIG.biomeMessages) {
            if (!StringHandler.isNullOrEmpty(biomeMessage)) {
                final String[] part = biomeMessage.split(CraftPresence.CONFIG.splitCharacter);
                if (!StringHandler.isNullOrEmpty(part[0]) && !BIOME_NAMES.contains(part[0])) {
                    BIOME_NAMES.add(part[0]);
                }
            }
        }
    }
}
