package com.envyful.reforged.bingo.forge.player;

import com.pixelmonmod.pixelmon.enums.EnumSpecies;

public class CardSlot {

    private final EnumSpecies species;
    private boolean complete;

    public CardSlot(EnumSpecies species, boolean complete) {
        this.species = species;
        this.complete = complete;
    }

    public EnumSpecies getSpecies() {
        return this.species;
    }

    public boolean isComplete() {
        return this.complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
