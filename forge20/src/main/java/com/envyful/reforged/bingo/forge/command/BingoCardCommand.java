package com.envyful.reforged.bingo.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.SubCommands;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import com.envyful.reforged.bingo.forge.player.BingoAttribute;
import com.envyful.reforged.bingo.forge.ui.BingoCardUI;

@Command(
        value = {
                "bingocard",
                "bingo",
                "bcard",
                "bingoc"
        }
)
@SubCommands({
        ReloadCommand.class,
        ReRollCommand.class,
        CompleteSlotCommand.class
})
public class BingoCardCommand {

    @CommandProcessor
    public void run(@Sender ForgeEnvyPlayer sender, String[] args) {
        BingoCardUI.open(sender);

        var attribute = sender.getAttributeNow(BingoAttribute.class);

        sender.message(ReforgedBingo.getLocale().getRemainingTimeMessage().replace("%hours%", attribute.getTimeRemaining() + ""));
    }
}
