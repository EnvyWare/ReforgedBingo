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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class BingoCardUI {

    public static void open(EnvyPlayer<EntityPlayerMP> player) {
        Pane pane = GuiFactory.paneBuilder()
                .topLeftY(0)
                .topLeftX(0)
                .height(6)
                .width(9)
                .build();
        BingoAttribute attribute = player.getAttribute(ReforgedBingo.class);

        pane.fill(GuiFactory.displayableBuilder(ItemStack.class)
                .itemStack(new ItemBuilder()
                        .type(Item.getByNameOrId("minecraft:stained_glass_pane"))
                        .damage(14)
                        .name(" ").build()).build());

        pane.set(4, 0, GuiFactory.displayableBuilder(ItemStack.class)
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

        GuiFactory.guiBuilder()
                .addPane(pane)
                .height(6)
                .title("Bingo Card")
                .setPlayerManager(ReforgedBingo.getInstance().getPlayerManager())
                .setCloseConsumer(envyPlayer -> {})
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
