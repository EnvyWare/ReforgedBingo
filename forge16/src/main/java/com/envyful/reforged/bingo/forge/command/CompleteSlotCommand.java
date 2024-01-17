package com.envyful.reforged.bingo.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.Argument;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Completable;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.command.completion.player.PlayerTabCompleter;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import com.envyful.reforged.bingo.forge.player.BingoAttribute;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;

@Command(
        value = "completeslot"
)
@Permissible("reforged.bingo.command.complete.slot")
public class CompleteSlotCommand {

    @CommandProcessor
    public void onCommand(@Sender ICommandSource sender,
                          @Completable(PlayerTabCompleter.class) @Argument ServerPlayerEntity target,
                          @Argument int slotX,
                          @Argument int slotY) {
        EnvyPlayer<ServerPlayerEntity> targetPlayer = ReforgedBingo.getInstance().getPlayerManager().getPlayer(target);

        if (targetPlayer == null) {
            return;
        }

        BingoAttribute attribute = targetPlayer.getAttributeNow(BingoAttribute.class);

        if (attribute == null) {
            return;
        }

        if (!attribute.completeSlot(slotY, slotX)) {
            sender.sendMessage(UtilChatColour.colour("&c&l(!) &cInvalid slot numbers (start form 0)"), Util.NIL_UUID);
            return;
        }

        sender.sendMessage(UtilChatColour.colour("&c&l(!) &cCompleted slot at " + slotY + ", " + slotX + " for " + target.getName()), Util.NIL_UUID);
    }
}
