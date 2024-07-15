package com.envyful.reforged.bingo.forge.task;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import com.envyful.reforged.bingo.forge.player.BingoAttribute;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;

public class CardResetTask implements Runnable {

    private final ReforgedBingo mod;

    public CardResetTask(ReforgedBingo mod) {
        this.mod = mod;
    }

    @Override
    public void run() {
        for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            EnvyPlayer<ServerPlayer> envyPlayer = this.mod.getPlayerManager().getPlayer(player);

            if (envyPlayer == null) {
                continue;
            }

            BingoAttribute bingoAttribute = envyPlayer.getAttributeNow(BingoAttribute.class);

            if (bingoAttribute == null) {
                continue;
            }

            if (!bingoAttribute.checkCardExpiry()) {
                continue;
            }

            bingoAttribute.generateNewCard();
        }
    }
}
