package com.envyful.reforged.bingo.forge.event;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.bingo.forge.player.BingoAttribute;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Event;

public class BingoSlotCompleteEvent extends Event {

    private final EnvyPlayer<EntityPlayerMP> player;
    private final BingoAttribute attribute;

    private boolean lineComplete;
    private boolean cardComplete;

    public BingoSlotCompleteEvent(EnvyPlayer<EntityPlayerMP> player, BingoAttribute attribute) {
        this(player, attribute, false, false);
    }

    public BingoSlotCompleteEvent(EnvyPlayer<EntityPlayerMP> player, BingoAttribute attribute,
                                  boolean lineComplete, boolean cardComplete) {
        this.player = player;
        this.attribute = attribute;
        this.lineComplete = lineComplete;
        this.cardComplete = cardComplete;
    }

    public EnvyPlayer<EntityPlayerMP> getPlayer() {
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
}
