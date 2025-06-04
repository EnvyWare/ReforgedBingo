package com.envyful.reforged.bingo.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.Argument;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Completable;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.neoforge.command.completion.player.PlayerTabCompleter;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.platform.Messageable;
import com.envyful.reforged.bingo.forge.player.BingoAttribute;

@Command(
        value = "completeslot"
)
@Permissible("reforged.bingo.command.complete.slot")
public class CompleteSlotCommand {

    @CommandProcessor
    public void onCommand(@Sender Messageable<?> sender,
                          @Completable(PlayerTabCompleter.class) @Argument ForgeEnvyPlayer target,
                          @Argument int slotX,
                          @Argument int slotY) {
        var attribute = target.getAttributeNow(BingoAttribute.class);

        if (attribute == null) {
            return;
        }

        if (!attribute.completeSlot(slotY, slotX)) {
            sender.message("&c&l(!) &cInvalid slot numbers (start form 0)");
            return;
        }

        sender.message("&c&l(!) &cCompleted slot at " + slotY + ", " + slotX + " for " + target.getName());
    }
}
