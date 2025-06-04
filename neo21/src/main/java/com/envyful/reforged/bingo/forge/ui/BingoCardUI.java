package com.envyful.reforged.bingo.forge.ui;

import com.envyful.api.neoforge.config.UtilConfigItem;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import com.envyful.reforged.bingo.forge.player.BingoAttribute;

public class BingoCardUI {

    public static void open(ForgeEnvyPlayer player) {
        var config = ReforgedBingo.getConfig();
        var pane = config.getConfigInterface().toPane();
        var attribute = player.getAttributeNow(BingoAttribute.class);

        UtilConfigItem.builder().extendedConfigItem(player, pane, config.getHelpItem());

        attribute.display(pane);

        pane.open(player, config.getConfigInterface());
    }
}
