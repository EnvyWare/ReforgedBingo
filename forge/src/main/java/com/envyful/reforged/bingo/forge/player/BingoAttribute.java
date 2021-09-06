package com.envyful.reforged.bingo.forge.player;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.forge.player.attribute.AbstractForgeAttribute;
import com.envyful.api.json.UtilGson;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import com.envyful.reforged.bingo.forge.config.BingoQueries;
import com.envyful.reforged.bingo.forge.event.BingoSlotCompleteEvent;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.minecraftforge.common.MinecraftForge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class BingoAttribute extends AbstractForgeAttribute<ReforgedBingo> {

    private long started;
    private CardSlot[][] bingoCard;

    public BingoAttribute(ReforgedBingo manager, EnvyPlayer<?> parent) {
        super(manager, (ForgeEnvyPlayer) parent);
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
        return (System.currentTimeMillis() - started) > TimeUnit.DAYS.toMillis(1);
    }

    public void generateNewCard() {
        this.bingoCard = new CardSlot[4][7];

        for (int y = 0; y < 4; y++) {
            CardSlot[] currentLine = this.bingoCard[y];

            for (int x = 0; x < 7; x++) {
                EnumSpecies species = EnumSpecies.randomPoke();

                while (!this.canPickPokemon(species)) {
                    species = EnumSpecies.randomPoke();
                }

                currentLine[x] = new CardSlot(species, false);
            }
        }

        this.started = System.currentTimeMillis();

        this.parent.message(this.manager.getLocale().getCardReset());
    }

    private boolean canPickPokemon(EnumSpecies value) {
        if (value.getBaseStats().specPreEvolutions.length >= this.manager.getConfig().getMaximumEvolution()) {
            return false;
        }

        return !this.manager.isBlacklisted(value);
    }

    public void catchPokemon(EnumSpecies species) {
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
}