package com.envyful.reforged.bingo.forge.player;

import com.envyful.api.database.sql.SqlType;
import com.envyful.api.json.UtilGson;
import com.envyful.api.player.attribute.adapter.AttributeAdapter;
import com.envyful.reforged.bingo.forge.ReforgedBingo;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SQLiteBingoAttributeAdapter implements AttributeAdapter<BingoAttribute, UUID> {

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `reforged_bingo_cards`(" +
            "id             INTEGER         NOT NULL, " +
            "uuid           VARCHAR(64)     NOT NULL, " +
            "card           BLOB            NOT NULL, " +
            "timeStarted    BIGINT          NOT NULL, " +
            "completedCards INTEGER         NOT NULL, " +
            "UNIQUE(uuid), " +
            "PRIMARY KEY(id AUTOINCREMENT)" +
            ");";

    public static final String LOAD_PLAYER_BINGO_CARD = "SELECT card, timeStarted, completedCards FROM `reforged_bingo_cards` WHERE uuid = ?;";

    public static final String CREATE_BINGO_CARD = "INSERT OR IGNORE INTO `reforged_bingo_cards`(uuid, card, timeStarted, completedCards) VALUES (?, ?, ?, ?);";

    public static final String UPDATE_BINGO_CARD = "UPDATE `reforged_bingo_cards` SET card = ?, timeStarted = ?, completedCards = ? WHERE uuid = ?;";

    @Override
    public CompletableFuture<Void> save(BingoAttribute bingoAttribute) {
        return CompletableFuture.allOf(
                ReforgedBingo.getDatabase().update(CREATE_BINGO_CARD)
                        .data(SqlType.text(bingoAttribute.getId().toString()),
                                SqlType.text(UtilGson.GSON.toJson(bingoAttribute.bingoCard)),
                                SqlType.bigInt(bingoAttribute.started),
                                SqlType.integer(bingoAttribute.completed))
                        .executeAsync(),
                ReforgedBingo.getDatabase().update(UPDATE_BINGO_CARD)
                        .data(SqlType.text(UtilGson.GSON.toJson(bingoAttribute.bingoCard)),
                                SqlType.bigInt(bingoAttribute.started),
                                SqlType.integer(bingoAttribute.completed),
                                SqlType.text(bingoAttribute.getId().toString()))
                        .executeAsync()
        );
    }

    @Override
    public void load(BingoAttribute bingoAttribute) {
        ReforgedBingo.getDatabase().query(LOAD_PLAYER_BINGO_CARD)
                .data(SqlType.text(bingoAttribute.getId().toString()))
                .converter(resultSet -> {
                    bingoAttribute.started = resultSet.getLong("timeStarted");
                    bingoAttribute.bingoCard = UtilGson.GSON.fromJson(resultSet.getString("card"), CardSlot[][].class);
                    bingoAttribute.completed = resultSet.getInt("completedCards");
                    return null;
                })
                .executeWithConverter();
    }

    @Override
    public CompletableFuture<Void> delete(BingoAttribute bingoAttribute) {
        //TODO:
        return null;
    }

    @Override
    public CompletableFuture<Void> deleteAll() {
        //TODO:
        return null;
    }

    @Override
    public void initialize() {
        ReforgedBingo.getDatabase().update(CREATE_TABLE).executeAsync();
    }
}
