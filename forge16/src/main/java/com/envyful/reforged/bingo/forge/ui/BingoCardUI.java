package com.envyful.reforged.bingo.forge.ui;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.config.UtilConfigInterface;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.text.parse.SimplePlaceholder;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import com.envyful.reforged.bingo.forge.config.BingoConfig;
import com.envyful.reforged.bingo.forge.player.BingoAttribute;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.stream.Collectors;

public class BingoCardUI {

    public static void open(EnvyPlayer<ServerPlayerEntity> player) {
        BingoConfig config = ReforgedBingo.getInstance().getConfig();

        Pane pane = GuiFactory.paneBuilder()
                .topLeftY(0)
                .topLeftX(0)
                .height(config.getConfigInterface().getHeight())
                .width(9)
                .build();

        BingoAttribute attribute = player.getAttributeNow(BingoAttribute.class);

        UtilConfigInterface.fillBackground(pane, config.getConfigInterface());

        UtilConfigItem.builder().extendedConfigItem((ForgeEnvyPlayer) player, pane, config.getHelpItem(), (SimplePlaceholder) s -> s);
        attribute.display(pane);

        GuiFactory.guiBuilder()
                .addPane(pane)
                .height(6)
                .title(UtilChatColour.colour(config.getConfigInterface().getTitle()))
                .setPlayerManager(ReforgedBingo.getInstance().getPlayerManager())
                .build().open(player);
    }
}
