package com.envyful.reforged.bingo.forge.listener;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.listener.LazyListener;
import com.envyful.api.forge.server.UtilForgeServer;
import com.envyful.api.math.UtilRandom;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
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
        List<String> commands = Lists.newArrayList();
        List<String> messages = Lists.newArrayList();

        commands.add(UtilRandom.getRandomElement(this.mod.getConfig().getSlotCompleteRewards()));
        messages.add(this.mod.getLocale().getSlotCompleteMessage());

        if (event.isLineComplete()) {
            commands.add(UtilRandom.getRandomElement(this.mod.getConfig().getLineCompleteRewards()));
            messages.add(this.mod.getLocale().getLineCompleteMessage());
        }

        if (event.isCardComplete()) {
            commands.add(UtilRandom.getRandomElement(this.mod.getConfig().getCardCompleteRewards()));
            messages.add(this.mod.getLocale().getCardCompleteMessage());
        }

        if (event.isColumnComplete()) {
            commands.add(UtilRandom.getRandomElement(this.mod.getConfig().getColumnCompleteRewards()));
            messages.add(this.mod.getLocale().getColumnCompleteMessage());
        }

        for (String command : commands) {
            UtilForgeServer.executeCommand(command.replace("%player%", event.getPlayer().getParent().getName().getString()));
        }

        for (String message : messages) {
            event.getPlayer().message(UtilChatColour.translateColourCodes('&', message));
        }
    }
}
