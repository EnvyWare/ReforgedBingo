package com.envyful.reforged.bingo.forge.ui;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import com.envyful.reforged.bingo.forge.player.BingoAttribute;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.List;

public class BingoCardUI {

    public static void open(EnvyPlayer<EntityPlayerMP> player) {
        Pane pane = GuiFactory.paneBuilder()
                .width(9)
                .height(6)
                .topLeftY(0)
                .topLeftX(0)
                .build();
        BingoAttribute attribute = player.getAttribute(ReforgedBingo.class);

        pane.set(0, 4, GuiFactory.displayableBuilder(ItemStack.class)
                .itemStack(new ItemBuilder()
                        .type(Items.BOOK)
                        .name(UtilChatColour.translateColourCodes('&', "&eInfo"))
                        .lore(getLore())
                        .build())
                .clickHandler((envyPlayer, clickType) -> {
                    envyPlayer.message(getLore());
                })
                .build());

        attribute.display(pane);

        GuiFactory.guiBuilder().addPane(pane).title("Bingo Card")
                .build().open(player);
    }

    private static List<String> getLore() {
        List<String> lore = Lists.newArrayList();

        for (String s : ReforgedBingo.getInstance().getLocale().getHelpInfo()) {
            lore.add(UtilChatColour.translateColourCodes('&', s));
        }

        return lore;
    }
}
