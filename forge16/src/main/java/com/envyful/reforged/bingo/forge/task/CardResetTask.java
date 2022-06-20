package com.envyful.reforged.bingo.forge.task;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import com.envyful.reforged.bingo.forge.player.BingoAttribute;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class CardResetTask implements Runnable {

    private final ReforgedBingo mod;

    public CardResetTask(ReforgedBingo mod) {
        this.mod = mod;
    }

    @Override
    public void run() {
        for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            EnvyPlayer<ServerPlayerEntity> envyPlayer = this.mod.getPlayerManager().getPlayer(player);

            if (envyPlayer == null) {
                continue;
            }

            BingoAttribute bingoAttribute = envyPlayer.getAttribute(ReforgedBingo.class);

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
