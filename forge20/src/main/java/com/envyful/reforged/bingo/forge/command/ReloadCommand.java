package com.envyful.reforged.bingo.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import net.minecraft.commands.CommandSource;

@Command(
        value = "reload"
)
@Permissible("reforged.bingo.command.reload")
public class ReloadCommand {

    @CommandProcessor
    public void onCommand(@Sender CommandSource sender, String[] args) {
        ReforgedBingo.getInstance().reloadConfig();
        sender.sendSystemMessage(UtilChatColour.colour(
                ReforgedBingo.getInstance().getLocale().getReloadMessage()
        ));
    }
}
