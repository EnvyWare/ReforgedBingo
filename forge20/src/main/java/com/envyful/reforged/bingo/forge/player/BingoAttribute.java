package com.envyful.reforged.bingo.forge.player;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.api.forge.player.attribute.AbstractForgeAttribute;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.json.UtilGson;
import com.envyful.api.player.save.attribute.DataDirectory;
import com.envyful.api.reforged.pixelmon.UtilPokemonInfo;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import com.envyful.reforged.bingo.forge.config.BingoQueries;
import com.envyful.reforged.bingo.forge.event.BingoSlotCompleteEvent;
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@DataDirectory("config/players/ReforgedBingo/")
public class BingoAttribute extends AbstractForgeAttribute<ReforgedBingo> {

    private long started;
    private CardSlot[][] bingoCard;
    private int completed = 0;

    public BingoAttribute(ReforgedBingo manager, ForgePlayerManager playerManager) {
        super(manager, playerManager);
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
        if (this.manager.getConfig().isStaticResetTimeEnabled()) {
            return System.currentTimeMillis() > this.started;
        }

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

        if (this.manager.getConfig().isStaticResetTimeEnabled()) {
            this.started = this.manager.getConfig().getStaticResetTime().nextExecution(ZonedDateTime.now()).get().toInstant().toEpochMilli();
        }

        this.parent.message(UtilChatColour.colour(this.manager.getLocale().getCardReset()));
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
                    BingoSlotCompleteEvent completeEvent = new BingoSlotCompleteEvent((ForgeEnvyPlayer) this.parent,
                            this, rowComplete, cardComplete, columnComplete);
                    MinecraftForge.EVENT_BUS.post(completeEvent);
                    return;
                }
            }
        }
    }

    public boolean completeSlot(int slotY, int slotX) {
        if (slotY >= this.bingoCard.length) {
            return false;
        }

        CardSlot[] cardSlots = this.bingoCard[slotY];

        if (slotX >= cardSlots.length) {
            return false;
        }

        this.bingoCard[slotY][slotX].setComplete(true);
        boolean rowComplete = this.checkRowCompletion(cardSlots);
        boolean cardComplete = this.checkCardCompletion();
        boolean columnComplete = this.checkColumnCompletion(slotX);
        BingoSlotCompleteEvent completeEvent = new BingoSlotCompleteEvent((ForgeEnvyPlayer) this.parent,
                this, rowComplete, cardComplete, columnComplete);
        MinecraftForge.EVENT_BUS.post(completeEvent);
        return true;
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
        if (this.manager.getConfig().isStaticResetTimeEnabled()) {
            return (TimeUnit.MILLISECONDS.toHours(this.started - System.currentTimeMillis()));
        }

        return (TimeUnit.SECONDS.toHours(ReforgedBingo.getInstance().getConfig().getCardDurationSeconds()) - TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - this.started));
    }

    public void display(Pane pane) {
        Displayable complete = GuiFactory.displayableBuilder(UtilConfigItem.fromConfigItem(ReforgedBingo.getInstance().getConfig().getCompleteItem())).build();

        int slot = 0;

        for (int y = 0; y < this.manager.getConfig().getHeight(); y++) {
            for (int x = 0; x < this.manager.getConfig().getWidth(); x++) {
                int position = this.manager.getConfig().getCardPositions().get(slot);
                ++slot;

                if (this.bingoCard[y][x].isComplete()) {
                    pane.set(position % 9, position / 9, complete);
                    continue;
                }

                CardSlot cardSlot = this.bingoCard[y][x];

                List<Component> lore = Lists.newArrayList();

                for (String s : ReforgedBingo.getInstance().getLocale().getCardSlotLore()) {
                    lore.add(UtilChatColour.colour(s
                            .replace("%biomes%", String.join(ReforgedBingo.getInstance().getLocale().getBiomeInfoDelimiter(), UtilPokemonInfo.getSpawnBiomes(cardSlot.getSpecies().getDefaultForm())))
                            .replace("%catch_rate%", String.join(ReforgedBingo.getInstance().getLocale().getCatchRateDelimiter(), UtilPokemonInfo.getCatchRate(cardSlot.getSpecies().getDefaultForm())))
                            .replace("%spawn_times%", String.join(
                            ReforgedBingo.getInstance().getLocale().getSpawnTimesDelimiter(),
                            UtilPokemonInfo.getSpawnTimes(cardSlot.getSpecies().getDefaultForm())))));
                }

                pane.set(position % 9, position / 9,
                        GuiFactory.displayableBuilder(ItemStack.class)
                                .itemStack(new ItemBuilder(UtilSprite.getPixelmonSprite(cardSlot.getSpecies()))
                                        .addLore(lore.toArray(new Component[0]))
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
