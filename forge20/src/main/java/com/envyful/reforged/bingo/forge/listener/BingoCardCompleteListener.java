package com.envyful.reforged.bingo.forge.listener;

import com.envyful.api.forge.listener.LazyListener;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import com.envyful.reforged.bingo.forge.event.BingoSlotCompleteEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BingoCardCompleteListener extends LazyListener {

    @SubscribeEvent
    public void onBingoSlotComplete(BingoSlotCompleteEvent event) {
        event.getAttribute().setCompleted(event.getAttribute().getCompleted() + 1);

        ReforgedBingo.getConfig().getSlotCompleteReward().give(event.getPlayer().getParent());

        if (event.isLineComplete()) {
            ReforgedBingo.getConfig().getLineCompleteRewards().give(event.getPlayer().getParent());
        }

        if (event.isCardComplete()) {
            ReforgedBingo.getConfig().getCardCompleteRewards().give(event.getPlayer().getParent());
        }

        if (event.isColumnComplete()) {
            ReforgedBingo.getConfig().getColumnCompleteRewards().give(event.getPlayer().getParent());
        }
    }
}
