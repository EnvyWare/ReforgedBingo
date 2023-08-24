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
        value = "completeslot",
        description = "Complete's a slot for a player's card"
)
@Permissible("reforged.bingo.command.complete.slot")
@Child
public class CompleteSlotCommand {

    @CommandProcessor
    public void onCommand(@Sender CommandSource sender,
                          @Completable(PlayerTabCompleter.class) @Argument ServerPlayer target,
                          @Argument int slotX,
                          @Argument int slotY) {
        EnvyPlayer<ServerPlayer> targetPlayer = ReforgedBingo.getInstance().getPlayerManager().getPlayer(target);

        if (targetPlayer == null) {
            return;
        }

        BingoAttribute attribute = targetPlayer.getAttribute(ReforgedBingo.class);

        if (attribute == null) {
            return;
        }

        if (!attribute.completeSlot(slotY, slotX)) {
            sender.sendSystemMessage(UtilChatColour.colour("&c&l(!) &cInvalid slot numbers (start form 0)"));
            return;
        }

        sender.sendSystemMessage(UtilChatColour.colour("&c&l(!) &cCompleted slot at " + slotY + ", " + slotX + " for " + target.getName()));
    }
}
