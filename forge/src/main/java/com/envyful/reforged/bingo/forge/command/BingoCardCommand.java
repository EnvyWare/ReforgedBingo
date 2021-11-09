package com.envyful.reforged.bingo.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import com.envyful.reforged.bingo.forge.player.BingoAttribute;
import com.envyful.reforged.bingo.forge.ui.BingoCardUI;
import net.minecraft.entity.player.EntityPlayerMP;

@Command(
        value = "bingocard",
        description = "/bingocard",
        aliases = {
                "bingo",
                "bcard",
                "bingoc"
        }
)
public class BingoCardCommand {

    @CommandProcessor
    public void run(@Sender EntityPlayerMP player, String[] args) {
        EnvyPlayer<EntityPlayerMP> sender = ReforgedBingo.getInstance().getPlayerManager().getPlayer(player);

        if (sender == null) {
            return;
        }

        BingoCardUI.open(sender);
        BingoAttribute attribute = sender.getAttribute(ReforgedBingo.class);

        sender.message(UtilChatColour.translateColourCodes('&',
                ReforgedBingo.getInstance().getLocale().getRemainingTimeMessage()
                        .replace("%hours%", attribute.getTimeRemaining() + "")));
    }
}
