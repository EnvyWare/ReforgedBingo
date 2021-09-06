package com.envyful.reforged.bingo.forge.config;

public class BingoQueries {

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `reforged_bingo_cards`(" +
            "id             INT             UNSIGNED        NOT NULL        AUTO_INCREMENT, " +
            "uuid           VARCHAR(64)     NOT NULL, " +
            "card           BLOB            NOT NULL, " +
            "timeStarted    BIGINT          UNSIGNED        NOT NULL, " +
            "UNIQUE(uuid), " +
            "PRIMARY KEY(id)" +
            ");";

    public static final String LOAD_PLAYER_BINGO_CARD = "SELECT card, timeStarted FROM `reforged_bingo_cards` WHERE uuid = ?;";

    public static final String UPDATE_PLAYER_BINGO_CARD = "INSERT INTO `reforged_bingo_cards`(uuid, card, timeStarted) VALUES (?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE card = VALUES(`card`), timeStarted = VALUES(`timeStarted`);";

}
