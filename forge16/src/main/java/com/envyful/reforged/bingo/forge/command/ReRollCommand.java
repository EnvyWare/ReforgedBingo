package com.envyful.reforged.bingo.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.Argument;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Completable;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.forge.command.completion.player.PlayerTabCompleter;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.platform.Messageable;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import com.envyful.reforged.bingo.forge.player.BingoAttribute;

@Command(
        value = "reroll"
)
@Permissible("reforged.bingo.command.reroll")
public class ReRollCommand {

    @CommandProcessor
    public void onCommand(@Sender Messageable<?> sender,
                          @Completable(PlayerTabCompleter.class) @Argument ForgeEnvyPlayer target) {
        var attribute = target.getAttributeNow(BingoAttribute.class);

        if (attribute == null) {
            return;
        }

        attribute.generateNewCard();
        sender.message(ReforgedBingo.getLocale().getRerollCardMessage()
                .replace("%player%", target.getName()));
    }
}
