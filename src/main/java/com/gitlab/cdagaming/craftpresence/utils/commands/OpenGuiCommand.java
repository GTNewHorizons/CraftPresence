package com.gitlab.cdagaming.craftpresence.utils.commands;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.gui.MainGui;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class OpenGuiCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return ModUtils.NAME.toLowerCase();
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + this.getCommandName();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        sender.addChatMessage(
                new ChatComponentText("hi " + CraftPresence.GUIS.isFocused + " " + CraftPresence.GUIS.configGUIOpened));
        FMLCommonHandler.instance().bus().register(this);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        FMLCommonHandler.instance().bus().unregister(this);
        if (!CraftPresence.GUIS.isFocused && !CraftPresence.GUIS.configGUIOpened) {
            CraftPresence.instance.displayGuiScreen(new MainGui(CraftPresence.instance.currentScreen));
        }
    }
}
