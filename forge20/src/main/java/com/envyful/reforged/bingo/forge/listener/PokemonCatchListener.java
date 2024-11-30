package com.envyful.reforged.bingo.forge.listener;

import com.envyful.reforged.bingo.forge.ReforgedBingo;
import com.envyful.reforged.bingo.forge.player.BingoAttribute;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PokemonCatchListener {

    @SubscribeEvent
    public void onBingoSlotComplete(CaptureEvent.SuccessfulCapture event) {
        var player = ReforgedBingo.getPlayerManager().getPlayer(event.getPlayer());

        if (player == null) {
            return;
        }

        var attribute = player.getAttributeNow(BingoAttribute.class);

        if (attribute == null) {
            return;
        }

        attribute.catchPokemon(event.getPokemon().getSpecies());
    }

    @SubscribeEvent
    public void onRaidDenCapture(CaptureEvent.SuccessfulRaidCapture event) {
        var player = ReforgedBingo.getPlayerManager().getPlayer(event.getPlayer());

        if (player == null) {
            return;
        }

        var attribute = player.getAttributeNow(BingoAttribute.class);

        if (attribute == null) {
            return;
        }

        attribute.catchPokemon(event.getRaidPokemon().getSpecies());
    }
}
