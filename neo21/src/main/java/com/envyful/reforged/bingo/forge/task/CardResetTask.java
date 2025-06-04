package com.envyful.reforged.bingo.forge.task;

import com.envyful.reforged.bingo.forge.ReforgedBingo;
import com.envyful.reforged.bingo.forge.player.BingoAttribute;

public class CardResetTask implements Runnable {

    @Override
    public void run() {
        for (var player : ReforgedBingo.getPlayerManager().getOnlinePlayers()) {
            if (!player.hasAttribute(BingoAttribute.class)) {
                continue;
            }

            var bingoAttribute = player.getAttributeNow(BingoAttribute.class);

            if (!bingoAttribute.checkCardExpiry()) {
                continue;
            }

            bingoAttribute.generateNewCard();
        }
    }
}
