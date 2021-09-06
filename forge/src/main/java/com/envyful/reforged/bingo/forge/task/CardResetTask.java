package com.envyful.reforged.bingo.forge.task;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import com.envyful.reforged.bingo.forge.player.BingoAttribute;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class CardResetTask implements Runnable {

    private final ReforgedBingo mod;

    public CardResetTask(ReforgedBingo mod) {
        this.mod = mod;
    }

    @Override
    public void run() {
        for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
            EnvyPlayer<EntityPlayerMP> envyPlayer = this.mod.getPlayerManager().getPlayer(player);

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
