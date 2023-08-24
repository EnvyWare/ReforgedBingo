package com.envyful.reforged.bingo.forge.listener;

import com.envyful.api.forge.listener.LazyListener;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import com.envyful.reforged.bingo.forge.event.BingoSlotCompleteEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BingoCardCompleteListener extends LazyListener {

    private final ReforgedBingo mod;

    public BingoCardCompleteListener(ReforgedBingo mod) {
        super();

        this.mod = mod;
    }

    @SubscribeEvent
    public void onBingoSlotComplete(BingoSlotCompleteEvent event) {
        event.getAttribute().setCompleted(event.getAttribute().getCompleted() + 1);

        this.mod.getConfig().getSlotCompleteReward().give(event.getPlayer().getParent());

        if (event.isLineComplete()) {
            this.mod.getConfig().getLineCompleteRewards().give(event.getPlayer().getParent());
        }

        if (event.isCardComplete()) {
            this.mod.getConfig().getCardCompleteRewards().give(event.getPlayer().getParent());
        }

        if (event.isColumnComplete()) {
            this.mod.getConfig().getColumnCompleteRewards().give(event.getPlayer().getParent());
        }
    }
}
