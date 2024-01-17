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
        for (var player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            var envyPlayer = this.mod.getPlayerManager().getPlayer(player);

            if (envyPlayer == null) {
                continue;
            }

            var bingoAttribute = envyPlayer.getAttributeNow(BingoAttribute.class);

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
