package com.envyful.reforged.bingo.forge.event;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.bingo.forge.player.BingoAttribute;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.Event;

public class BingoSlotCompleteEvent extends Event {

    private final EnvyPlayer<ServerPlayerEntity> player;
    private final BingoAttribute attribute;

    private boolean lineComplete;
    private boolean cardComplete;
    private boolean columnComplete;

    public BingoSlotCompleteEvent(EnvyPlayer<ServerPlayerEntity> player, BingoAttribute attribute) {
        this(player, attribute, false, false, false);
    }

    public BingoSlotCompleteEvent(EnvyPlayer<ServerPlayerEntity> player, BingoAttribute attribute,
                                  boolean lineComplete, boolean cardComplete, boolean columnComplete) {
        this.player = player;
        this.attribute = attribute;
        this.lineComplete = lineComplete;
        this.cardComplete = cardComplete;
        this.columnComplete = columnComplete;
    }

    public EnvyPlayer<ServerPlayerEntity> getPlayer() {
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
