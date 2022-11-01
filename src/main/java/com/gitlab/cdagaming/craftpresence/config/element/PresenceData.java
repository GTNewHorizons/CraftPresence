package com.gitlab.cdagaming.craftpresence.config.element;

import com.gitlab.cdagaming.craftpresence.config.Module;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;

import java.io.Serializable;
import java.util.List;

public class PresenceData extends Module implements Serializable {
    private static final long serialVersionUID = -7560029890988753870L;
    private static PresenceData DEFAULT;

    public String details;
    public String gameState;
    public String largeImageKey;
    public String largeImageText;
    public String smallImageKey;
    public String smallImageText;
    public List<Button> buttons;

    public static PresenceData getDefaults() {
        if (DEFAULT == null) {
            DEFAULT = new PresenceData();
        }
        return DEFAULT;
    }

    public PresenceData setDetails(String details) {
        this.details = details;
        return this;
    }

    public PresenceData setGameState(String gameState) {
        this.gameState = gameState;
        return this;
    }

    public PresenceData setLargeImage(String imageKey, String imageText) {
        this.largeImageKey = imageKey;
        this.largeImageText = imageText;
        return this;
    }

    public PresenceData setSmallImage(String imageKey, String imageText) {
        this.smallImageKey = imageKey;
        this.smallImageText = imageText;
        return this;
    }

    public PresenceData addButton(Button button) {
        this.buttons.add(button);
        return this;
    }

    public PresenceData removeButton(Button button) {
        this.buttons.remove(button);
        return this;
    }

    @Override
    public Object getProperty(final String name) {
        return StringUtils.lookupObject(PresenceData.class, this, name);
    }

    @Override
    public void setProperty(final String name, final Object value) {
        StringUtils.updateField(PresenceData.class, this, new Tuple<>(name, value, null));
    }

    @Override
    public void resetProperty(final String name) {
        setProperty(name, getDefaults().getProperty(name));
    }
}
