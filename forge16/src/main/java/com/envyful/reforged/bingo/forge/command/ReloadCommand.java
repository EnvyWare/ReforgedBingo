package com.envyful.reforged.bingo.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.Util;

@Command(
        value = "reload"
)
@Permissible("reforged.bingo.command.reload")
public class ReloadCommand {

    @CommandProcessor
    public void onCommand(@Sender ICommandSource sender, String[] args) {
        ReforgedBingo.getInstance().reloadConfig();
        sender.sendMessage(UtilChatColour.colour(
                ReforgedBingo.getInstance().getLocale().getReloadMessage()
        ), Util.NIL_UUID);
    }
}
