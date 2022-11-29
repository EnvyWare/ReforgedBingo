package com.envyful.reforged.bingo.forge.listener;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.listener.LazyListener;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import com.envyful.reforged.bingo.forge.config.BingoConfig;
import com.envyful.reforged.bingo.forge.event.BingoSlotCompleteEvent;
import com.google.common.collect.Lists;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class BingoCardCompleteListener extends LazyListener {

    private final ReforgedBingo mod;

    public BingoCardCompleteListener(ReforgedBingo mod) {
        super();

        this.mod = mod;
    }

    @SubscribeEvent
    public void onBingoSlotComplete(BingoSlotCompleteEvent event) {
        List<BingoConfig.BingoReward> commands = Lists.newArrayList();
        List<String> messages = Lists.newArrayList();

        event.getAttribute().setCompleted(event.getAttribute().getCompleted() + 1);

        commands.add(this.mod.getConfig().getSlotCompleteReward());
        messages.add(this.mod.getLocale().getSlotCompleteMessage());

        if (event.isLineComplete()) {
            commands.add(this.mod.getConfig().getLineCompleteRewards());
            messages.add(this.mod.getLocale().getLineCompleteMessage());
        }

        if (event.isCardComplete()) {
            commands.add(this.mod.getConfig().getCardCompleteRewards());
            messages.add(this.mod.getLocale().getCardCompleteMessage());
        }

        if (event.isColumnComplete()) {
            commands.add(this.mod.getConfig().getColumnCompleteRewards());
            messages.add(this.mod.getLocale().getColumnCompleteMessage());
        }

        for (BingoConfig.BingoReward command : commands) {
            if (command == null) {
                continue;
            }

            command.executeCommands(event.getPlayer().getParent());
        }

        for (String message : messages) {
            if (message.isEmpty()) {
                continue;
            }

            event.getPlayer().message(UtilChatColour.translateColourCodes('&', message));
        }
    }
}
