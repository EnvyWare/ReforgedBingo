package com.envyful.reforged.bingo.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.SubCommands;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import com.envyful.reforged.bingo.forge.player.BingoAttribute;
import com.envyful.reforged.bingo.forge.ui.BingoCardUI;
import net.minecraft.server.level.ServerPlayer;

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
    public void run(@Sender ServerPlayer player, String[] args) {
        EnvyPlayer<ServerPlayer> sender = ReforgedBingo.getInstance().getPlayerManager().getPlayer(player);

        if (sender == null) {
            return;
        }

        BingoCardUI.open(sender);
        BingoAttribute attribute = sender.getAttribute(BingoAttribute.class);

        sender.message(UtilChatColour.colour(
                ReforgedBingo.getInstance().getLocale().getRemainingTimeMessage()
                        .replace("%hours%", attribute.getTimeRemaining() + "")));
    }
}
