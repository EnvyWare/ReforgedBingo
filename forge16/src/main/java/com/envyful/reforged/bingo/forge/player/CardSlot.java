package com.envyful.reforged.bingo.forge.player;

import com.pixelmonmod.pixelmon.api.pokemon.species.Species;

public class CardSlot {

    private final Species species;
    private boolean complete;

    public CardSlot(Species species, boolean complete) {
        this.species = species;
        this.complete = complete;
    }

    public Species getSpecies() {
        return this.species;
    }

    public boolean isComplete() {
        return this.complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
