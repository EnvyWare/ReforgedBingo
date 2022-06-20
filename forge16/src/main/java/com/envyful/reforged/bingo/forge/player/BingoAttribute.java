package com.envyful.reforged.bingo.forge.player;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.forge.player.attribute.AbstractForgeAttribute;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.json.UtilGson;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import com.envyful.reforged.bingo.forge.config.BingoQueries;
import com.envyful.reforged.bingo.forge.event.BingoSlotCompleteEvent;
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BingoAttribute extends AbstractForgeAttribute<ReforgedBingo> {

    private long started;
    private CardSlot[][] bingoCard;

    public BingoAttribute(ReforgedBingo manager, EnvyPlayer<?> parent) {
        super(manager, (ForgeEnvyPlayer) parent);
    }

    public BingoAttribute(UUID uuid) {
        super(uuid);
    }

    @Override
    public void load() {
        try (Connection connection = this.manager.getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(BingoQueries.LOAD_PLAYER_BINGO_CARD)) {
            preparedStatement.setString(1, this.parent.getUuid().toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                this.generateNewCard();
                return;
            }

            this.started = resultSet.getLong("timeStarted");
            this.bingoCard = UtilGson.GSON.fromJson(resultSet.getString("card"), CardSlot[][].class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save() {
        try (Connection connection = this.manager.getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(BingoQueries.UPDATE_PLAYER_BINGO_CARD)) {

            preparedStatement.setString(1, this.parent.getUuid().toString());
            preparedStatement.setString(2, UtilGson.GSON.toJson(this.bingoCard));
            preparedStatement.setLong(3, this.started);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkCardExpiry() {
        return (System.currentTimeMillis() - started) > TimeUnit.SECONDS.toMillis(ReforgedBingo.getInstance().getConfig().getCardDurationSeconds());
    }

    public void generateNewCard() {
        this.bingoCard = new CardSlot[4][7];

        for (int y = 0; y < 4; y++) {
            CardSlot[] currentLine = this.bingoCard[y];

            for (int x = 0; x < 7; x++) {
                Species species = PixelmonSpecies.getRandomSpecies();

                while (!this.canPickPokemon(species)) {
                    species = PixelmonSpecies.getRandomSpecies();
                }

                currentLine[x] = new CardSlot(species, false);
            }
        }

        this.started = System.currentTimeMillis();

        this.parent.message(UtilChatColour.translateColourCodes('&', this.manager.getLocale().getCardReset()));
    }

    private boolean canPickPokemon(Species value) {
        if (value.getDefaultForm().getPreEvolutions().size() >= this.manager.getConfig().getMaximumEvolution()) {
            return false;
        }

        return !this.manager.isBlacklisted(value);
    }

    public void catchPokemon(Species species) {
        for (CardSlot[] cardSlots : this.bingoCard) {
            for (CardSlot cardSlot : cardSlots) {
                if (cardSlot.getSpecies() == species && !cardSlot.isComplete()) {
                    cardSlot.setComplete(true);

                    boolean rowComplete = this.checkRowCompletion(cardSlots);
                    boolean cardComplete = this.checkCardCompletion();
                    BingoSlotCompleteEvent completeEvent = new BingoSlotCompleteEvent(this.parent,
                            this, rowComplete, cardComplete);
                    MinecraftForge.EVENT_BUS.post(completeEvent);
                    return;
                }
            }
        }
    }

    private boolean checkRowCompletion(CardSlot[] complete) {
        for (CardSlot cardSlot : complete) {
            if (!cardSlot.isComplete()) {
                return false;
            }
        }

        return true;
    }

    private boolean checkCardCompletion() {
        for (CardSlot[] cardSlots : this.bingoCard) {
            for (CardSlot cardSlot : cardSlots) {
                if (!cardSlot.isComplete()) {
                    return false;
                }
            }
        }

        return true;
    }

    public long getTimeRemaining() {
        return (TimeUnit.SECONDS.toHours(ReforgedBingo.getInstance().getConfig().getCardDurationSeconds()) - TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - this.started));
    }

    public void display(Pane pane) {
        Displayable complete = GuiFactory.displayableBuilder(ItemStack.class)
                .itemStack(UtilConfigItem.fromConfigItem(ReforgedBingo.getInstance().getConfig().getCompleteItem())).build();

        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 7; x++) {
                if (this.bingoCard[y][x].isComplete()) {
                    pane.set(1 + x, 1 + y, complete);
                    continue;
                }

                CardSlot cardSlot = this.bingoCard[y][x];

                List<String> lore = Lists.newArrayList();

                for (String s : ReforgedBingo.getInstance().getLocale().getCardSlotLore()) {
                    lore.add(UtilChatColour.translateColourCodes('&', s));
                }

                pane.set(1 + x, 1 + y,
                        GuiFactory.displayableBuilder(ItemStack.class)
                                .itemStack(new ItemBuilder(UtilSprite.getPixelmonSprite(cardSlot.getSpecies()))
                                        .addLore(lore.toArray(new String[0])).build())
                                .clickHandler((envyPlayer, clickType) -> {
                                    for (String cardSlotCommand : ReforgedBingo.getInstance().getConfig().getCardSlotCommands()) {
                                        envyPlayer.executeCommand(cardSlotCommand.replace(
                                                "%pokemon%",
                                                cardSlot.getSpecies().getLocalizedName()
                                        ));
                                    }
                                })
                                .build());
            }
        }
    }
}
