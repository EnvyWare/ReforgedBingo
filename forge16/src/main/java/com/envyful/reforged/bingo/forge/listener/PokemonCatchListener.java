package com.envyful.reforged.bingo.forge.listener;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import com.envyful.reforged.bingo.forge.player.BingoAttribute;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PokemonCatchListener {

    private final ReforgedBingo mod;

    public PokemonCatchListener(ReforgedBingo mod) {
        this.mod = mod;

        Pixelmon.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onBingoSlotComplete(CaptureEvent.SuccessfulCapture event) {
        EnvyPlayer<ServerPlayerEntity> player = this.mod.getPlayerManager().getPlayer(event.getPlayer());

        if (player == null) {
            return;
        }

        BingoAttribute attribute = player.getAttribute(BingoAttribute.class);

        if (attribute == null) {
            return;
        }

        attribute.catchPokemon(event.getPokemon().getSpecies());
    }

    @SubscribeEvent
    public void onRaidDenCapture(CaptureEvent.SuccessfulRaidCapture event) {
        EnvyPlayer<ServerPlayerEntity> player = this.mod.getPlayerManager().getPlayer(event.getPlayer());

        if (player == null) {
            return;
        }

        BingoAttribute attribute = player.getAttribute(BingoAttribute.class);

        if (attribute == null) {
            return;
        }

        attribute.catchPokemon(event.getRaidPokemon().getSpecies());
    }
}
