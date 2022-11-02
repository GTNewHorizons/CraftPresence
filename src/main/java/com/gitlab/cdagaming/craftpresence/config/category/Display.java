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

package com.gitlab.cdagaming.craftpresence.config.category;

import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.Module;
import com.gitlab.cdagaming.craftpresence.config.element.Button;
import com.gitlab.cdagaming.craftpresence.config.element.PresenceData;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Display extends Module implements Serializable {
    private static final long serialVersionUID = -3302764075156017733L;
    private static Display DEFAULT;
    public PresenceData presenceData = new PresenceData()
            .setGameState("&SERVER& &PACK&")
            .setDetails("&MAINMENU&|&DIMENSION&")
            .setLargeImage("&MAINMENU&|&DIMENSION&", "&MAINMENU&|&DIMENSION&")
            .setSmallImage("&SERVER&|&PACK&", "&SERVER& &PACK&");
    public Map<String, Button> buttonMessages = new HashMap<String, Button>() {
        private static final long serialVersionUID = -1738414795267027009L;

        {
            put("default", new Button(
                    ModUtils.TRANSLATOR.translate("craftpresence.defaults.display.button.label"),
                    ModUtils.TRANSLATOR.translate("craftpresence.defaults.display.button.url")
            ));
        }
    };
    public Map<String, String> dynamicIcons = new HashMap<String, String>() {
        private static final long serialVersionUID = 4900744874595923346L;

        {
            put("default", ModUtils.TRANSLATOR.translate("craftpresence.defaults.display.image.url"));
        }
    };

    @Override
    public Display getDefaults() {
        if (DEFAULT == null) {
            DEFAULT = new Display();
        }
        return DEFAULT;
    }

    @Override
    public Object getProperty(final String name) {
        return StringUtils.lookupObject(Display.class, this, name);
    }

    @Override
    public void setProperty(final String name, final Object value) {
        StringUtils.updateField(Display.class, this, new Tuple<>(name, value, null));
    }
}
