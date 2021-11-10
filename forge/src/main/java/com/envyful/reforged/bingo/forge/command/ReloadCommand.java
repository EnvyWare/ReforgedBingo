package com.envyful.reforged.bingo.forge.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;

@Command(
        value = "reload",
        description = "Reloads the configs"
)
@Permissible("reforged.bingo.command.reload")
@Child
public class ReloadCommand {

    @CommandProcessor
    public void onCommand(@Sender ICommandSender sender, String[] args) {
        ReforgedBingo.getInstance().reloadConfig();
        sender.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes(
                '&',
                ReforgedBingo.getInstance().getLocale().getReloadMessage()
        )));
    }

}
