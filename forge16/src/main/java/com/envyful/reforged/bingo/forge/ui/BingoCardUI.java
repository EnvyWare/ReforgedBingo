package com.envyful.reforged.bingo.forge.ui;

import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import com.envyful.reforged.bingo.forge.player.BingoAttribute;

public class BingoCardUI {

    public static void open(ForgeEnvyPlayer player) {
        var config = ReforgedBingo.getConfig();
        var pane = GuiFactory.createPane(config.getConfigInterface());
        var attribute = player.getAttributeNow(BingoAttribute.class);

        UtilConfigItem.builder().extendedConfigItem(player, pane, config.getHelpItem());

        attribute.display(pane);

        GuiFactory.singlePaneGui(config.getConfigInterface(), pane).open(player);
    }
}
