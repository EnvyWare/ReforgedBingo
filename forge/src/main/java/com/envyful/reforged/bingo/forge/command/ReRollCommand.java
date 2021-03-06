package com.envyful.reforged.bingo.forge.command;

import akka.event.AddressTerminatedTopic;
import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.TabCompletions;
import com.envyful.api.command.annotate.executor.Argument;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Completable;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.command.completion.player.PlayerTabCompleter;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import com.envyful.reforged.bingo.forge.player.BingoAttribute;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;

@Command(
        value = "reroll",
        description = "Rerolls a player's card"
)
@Permissible("reforged.bingo.command.reroll")
@Child
public class ReRollCommand {

    @CommandProcessor
    public void onCommand(@Sender ICommandSender sender,
                          @Completable(PlayerTabCompleter.class) @Argument EntityPlayerMP target) {
        EnvyPlayer<EntityPlayerMP> targetPlayer = ReforgedBingo.getInstance().getPlayerManager().getPlayer(target);

        if (targetPlayer == null) {
            return;
        }

        BingoAttribute attribute = targetPlayer.getAttribute(ReforgedBingo.class);

        if (attribute == null) {
            return;
        }

        attribute.generateNewCard();
        sender.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes(
                '&',
                ReforgedBingo.getInstance().getLocale().getRerollCardMessage()
                        .replace("%player%", target.getName())
        )));
    }

}
