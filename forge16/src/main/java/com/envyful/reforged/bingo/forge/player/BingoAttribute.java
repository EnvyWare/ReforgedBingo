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
import com.envyful.api.player.save.attribute.DataDirectory;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import com.envyful.reforged.bingo.forge.config.BingoQueries;
import com.envyful.reforged.bingo.forge.event.BingoSlotCompleteEvent;
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@DataDirectory("config/players/ReforgedBingo/")
public class BingoAttribute extends AbstractForgeAttribute<ReforgedBingo> {

    private long started;
    private CardSlot[][] bingoCard;
    private int completed = 0;

    public BingoAttribute(ReforgedBingo manager, EnvyPlayer<?> parent) {
        super(manager, (ForgeEnvyPlayer) parent);
    }

    public BingoAttribute(UUID uuid) {
        super(uuid);
    }

    public int getCompleted() {
        return this.completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
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
            this.completed = resultSet.getInt("completedCards");
        } catch (SQLException e) {
            ReforgedBingo.getInstance().getLogger().catching(e);
        }
    }

    @Override
    public void save() {
        try (Connection connection = this.manager.getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(BingoQueries.UPDATE_PLAYER_BINGO_CARD)) {

            preparedStatement.setString(1, this.parent.getUuid().toString());
            preparedStatement.setString(2, UtilGson.GSON.toJson(this.bingoCard));
            preparedStatement.setLong(3, this.started);
            preparedStatement.setInt(4, this.completed);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkCardExpiry() {
        return (System.currentTimeMillis() - started) > TimeUnit.SECONDS.toMillis(ReforgedBingo.getInstance().getConfig().getCardDurationSeconds());
    }

    public void generateNewCard() {
        this.bingoCard = new CardSlot[this.manager.getConfig().getHeight()][this.manager.getConfig().getWidth()];

        for (int y = 0; y < this.manager.getConfig().getHeight(); y++) {
            CardSlot[] currentLine = this.bingoCard[y];

            for (int x = 0; x < this.manager.getConfig().getWidth(); x++) {
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
            for (int i = 0; i < cardSlots.length; i++) {
                CardSlot cardSlot = cardSlots[i];
                if (cardSlot.getSpecies() == species && !cardSlot.isComplete()) {
                    cardSlot.setComplete(true);

                    boolean rowComplete = this.checkRowCompletion(cardSlots);
                    boolean cardComplete = this.checkCardCompletion();
                    boolean columnComplete = this.checkColumnCompletion(i);
                    BingoSlotCompleteEvent completeEvent = new BingoSlotCompleteEvent(this.parent,
                            this, rowComplete, cardComplete, columnComplete);
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

    private boolean checkColumnCompletion(int column) {
        for (CardSlot[] cardSlots : this.bingoCard) {
            if (!cardSlots[column].isComplete()) {
                return false;
            }
        }

        return true;
    }

    public long getTimeRemaining() {
        return (TimeUnit.SECONDS.toHours(ReforgedBingo.getInstance().getConfig().getCardDurationSeconds()) - TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - this.started));
    }

    public void display(Pane pane) {
        Displayable complete = GuiFactory.displayableBuilder(UtilConfigItem.fromConfigItem(ReforgedBingo.getInstance().getConfig().getCompleteItem())).build();

        for (int y = 0; y < this.manager.getConfig().getHeight(); y++) {
            for (int x = 0; x < this.manager.getConfig().getWidth(); x++) {
                if (this.bingoCard[y][x].isComplete()) {
                    pane.set(1 + x, 1 + y, complete);
                    continue;
                }

                CardSlot cardSlot = this.bingoCard[y][x];

                List<ITextComponent> lore = Lists.newArrayList();

                for (String s : ReforgedBingo.getInstance().getLocale().getCardSlotLore()) {
                    lore.add(UtilChatColour.colour(s));
                }

                pane.set(this.manager.getConfig().getStartingPosX() + x, this.manager.getConfig().getStartingPosY() + y,
                        GuiFactory.displayableBuilder(ItemStack.class)
                                .itemStack(new ItemBuilder(UtilSprite.getPixelmonSprite(cardSlot.getSpecies()))
                                        .addLore(lore.toArray(new ITextComponent[0]))
                                        .name(cardSlot.getSpecies().getLocalizedName())
                                        .build())
                                .asyncClick(false)
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
