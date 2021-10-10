package com.envyful.reforged.bingo.forge.ui;

import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import com.envyful.reforged.bingo.forge.config.BingoConfig;
import com.envyful.reforged.bingo.forge.player.BingoAttribute;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class BingoCardUI {

    public static void open(EnvyPlayer<EntityPlayerMP> player) {
        BingoConfig config = ReforgedBingo.getInstance().getConfig();

        Pane pane = GuiFactory.paneBuilder()
                .topLeftY(0)
                .topLeftX(0)
                .height(config.getConfigInterface().getHeight())
                .width(9)
                .build();

        BingoAttribute attribute = player.getAttribute(ReforgedBingo.class);

        for (ConfigItem fillerItem : config.getConfigInterface().getFillerItems()) {
            pane.add(GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(fillerItem))
                             .build());
        }

        if (config.getHelpItem().isEnabled()) {
            pane.set(config.getHelpItem().getXPos(), config.getHelpItem().getYPos(),
                     GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(config.getHelpItem()))
                             .build()
            );
        }

        attribute.display(pane);

        GuiFactory.guiBuilder()
                .addPane(pane)
                .height(6)
                .title(UtilChatColour.translateColourCodes('&', config.getConfigInterface().getTitle()))
                .setPlayerManager(ReforgedBingo.getInstance().getPlayerManager())
                .setCloseConsumer(envyPlayer -> {})
                .build().open(player);
    }
}
