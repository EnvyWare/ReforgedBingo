package com.envyful.reforged.bingo.forge.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.Argument;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Completable;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.command.completion.player.PlayerTabCompleter;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import com.envyful.reforged.bingo.forge.player.BingoAttribute;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.level.ServerPlayer;

@Command(
        value = "reroll",
        description = "Rerolls a player's card"
)
@Permissible("reforged.bingo.command.reroll")
@Child
public class ReRollCommand {

    @CommandProcessor
    public void onCommand(@Sender CommandSource sender,
                          @Completable(PlayerTabCompleter.class) @Argument ServerPlayer target) {
        EnvyPlayer<ServerPlayer> targetPlayer = ReforgedBingo.getInstance().getPlayerManager().getPlayer(target);

        if (targetPlayer == null) {
            return;
        }

        BingoAttribute attribute = targetPlayer.getAttribute(ReforgedBingo.class);

        if (attribute == null) {
            return;
        }

        attribute.generateNewCard();
        sender.sendSystemMessage(UtilChatColour.colour(
                ReforgedBingo.getInstance().getLocale().getRerollCardMessage()
                        .replace("%player%", target.getName().getString())
        ));
    }
}
