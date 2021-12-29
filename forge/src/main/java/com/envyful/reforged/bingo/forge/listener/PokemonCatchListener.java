package com.envyful.reforged.bingo.forge.listener;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import com.envyful.reforged.bingo.forge.player.BingoAttribute;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.enums.ReceiveType;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.EggHatchEvent;
import com.pixelmonmod.pixelmon.api.events.PixelmonReceivedEvent;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PokemonCatchListener {

    private final ReforgedBingo mod;

    public PokemonCatchListener(ReforgedBingo mod) {
        this.mod = mod;

        Pixelmon.EVENT_BUS.register(this);
    }

    // We want to wait for other addons to do their thing and not register if they cancel the event.
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBingoSlotComplete(CaptureEvent.SuccessfulCapture event) {
        if (this.mod.getConfig().acceptCatching() && !event.isCanceled())
            this.attemptBingoScratch(event.player, event.getPokemon().getSpecies());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRaidDenCapture(CaptureEvent.SuccessfulRaidCapture event) {
        if (this.mod.getConfig().acceptRaidCatching() && !event.isCanceled())
            this.attemptBingoScratch(event.player, event.getPokemon().getSpecies());
    }

    @SubscribeEvent
    public void onFossilRevive(PixelmonReceivedEvent event) {
        if (event.receiveType == ReceiveType.Fossil && this.mod.getConfig().acceptFossilReviving())
            this.attemptBingoScratch(event.player, event.pokemon.getSpecies());
    }

    @SubscribeEvent
    public void onHatch(EggHatchEvent event) {
        if (this.mod.getConfig().acceptEggHatch()) {
            final EntityPlayerMP player = event.pokemon.getOwnerPlayer();
            // This shouldn't happen but you never know
            if (player == null)
                return;
            this.attemptBingoScratch(player, event.pokemon.getSpecies());
        }
    }

    private void attemptBingoScratch(EntityPlayerMP player, EnumSpecies species) {
        final EnvyPlayer<EntityPlayerMP> envyPlayer = this.mod.getPlayerManager().getPlayer(player);
        if (envyPlayer == null)
            return;
        final BingoAttribute attribute = envyPlayer.getAttribute(ReforgedBingo.class);
        if (attribute != null)
            attribute.catchPokemon(species);
    }

}
