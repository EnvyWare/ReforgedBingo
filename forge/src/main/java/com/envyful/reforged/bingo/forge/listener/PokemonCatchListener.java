package com.envyful.reforged.bingo.forge.listener;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import com.envyful.reforged.bingo.forge.player.BingoAttribute;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PokemonCatchListener {

    private final ReforgedBingo mod;

    public PokemonCatchListener(ReforgedBingo mod) {
        this.mod = mod;

        Pixelmon.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onBingoSlotComplete(CaptureEvent.SuccessfulCapture event) {
        EnvyPlayer<EntityPlayerMP> player = this.mod.getPlayerManager().getPlayer(event.player);

        if (player == null) {
            return;
        }

        BingoAttribute attribute = player.getAttribute(ReforgedBingo.class);

        if (attribute == null) {
            return;
        }

        attribute.catchPokemon(event.getPokemon().getSpecies());
    }

    @SubscribeEvent
    public void onRaidDenCapture(CaptureEvent.SuccessfulRaidCapture event) {
        EnvyPlayer<EntityPlayerMP> player = this.mod.getPlayerManager().getPlayer(event.player);

        if (player == null) {
            return;
        }

        BingoAttribute attribute = player.getAttribute(ReforgedBingo.class);

        if (attribute == null) {
            return;
        }

        attribute.catchPokemon(event.getRaidPokemon().getSpecies());
    }
}
