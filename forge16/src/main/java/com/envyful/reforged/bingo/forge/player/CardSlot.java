package com.envyful.reforged.bingo.forge.player;

import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;

public class CardSlot {

    private final int species;
    private boolean complete;

    public CardSlot(Species species, boolean complete) {
        this.species = species.getDex();
        this.complete = complete;
    }

    public Species getSpecies() {
        return PixelmonSpecies.fromDex(this.species).orElse(null);
    }

    public boolean isComplete() {
        return this.complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
