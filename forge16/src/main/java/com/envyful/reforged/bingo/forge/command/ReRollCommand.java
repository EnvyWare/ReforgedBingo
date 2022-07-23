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
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;

@Command(
        value = "reroll",
        description = "Rerolls a player's card"
)
@Permissible("reforged.bingo.command.reroll")
@Child
public class ReRollCommand {

    @CommandProcessor
    public void onCommand(@Sender ICommandSource sender,
                          @Completable(PlayerTabCompleter.class) @Argument ServerPlayerEntity target) {
        EnvyPlayer<ServerPlayerEntity> targetPlayer = ReforgedBingo.getInstance().getPlayerManager().getPlayer(target);

        if (targetPlayer == null) {
            return;
        }

        BingoAttribute attribute = targetPlayer.getAttribute(ReforgedBingo.class);

        if (attribute == null) {
            return;
        }

        attribute.generateNewCard();
        sender.sendMessage(UtilChatColour.colour(
                ReforgedBingo.getInstance().getLocale().getRerollCardMessage()
                        .replace("%player%", target.getName().getString())
        ), Util.NIL_UUID);
    }
}
