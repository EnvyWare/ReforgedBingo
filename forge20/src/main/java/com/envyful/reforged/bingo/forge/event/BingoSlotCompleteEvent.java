package com.envyful.reforged.bingo.forge.event;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.reforged.bingo.forge.player.BingoAttribute;
import net.minecraftforge.eventbus.api.Event;

public class BingoSlotCompleteEvent extends Event {

    private final ForgeEnvyPlayer player;
    private final BingoAttribute attribute;

    private boolean lineComplete;
    private boolean cardComplete;
    private boolean columnComplete;

    public BingoSlotCompleteEvent(ForgeEnvyPlayer player, BingoAttribute attribute) {
        this(player, attribute, false, false, false);
    }

    public BingoSlotCompleteEvent(ForgeEnvyPlayer player, BingoAttribute attribute,
                                  boolean lineComplete, boolean cardComplete, boolean columnComplete) {
        this.player = player;
        this.attribute = attribute;
        this.lineComplete = lineComplete;
        this.cardComplete = cardComplete;
        this.columnComplete = columnComplete;
    }

    public ForgeEnvyPlayer getPlayer() {
        return this.player;
    }

    public BingoAttribute getAttribute() {
        return this.attribute;
    }

    public boolean isLineComplete() {
        return this.lineComplete;
    }

    public void setLineComplete(boolean lineComplete) {
        this.lineComplete = lineComplete;
    }

    public boolean isCardComplete() {
        return this.cardComplete;
    }

    public void setCardComplete(boolean cardComplete) {
        this.cardComplete = cardComplete;
    }

    public boolean isColumnComplete() {
        return this.columnComplete;
    }

    public void setColumnComplete(boolean columnComplete) {
        this.columnComplete = columnComplete;
    }
}
