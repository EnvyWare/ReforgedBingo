package com.envyful.reforged.bingo.forge;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.database.Database;
import com.envyful.api.database.impl.SimpleHikariDatabase;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.reforged.bingo.forge.config.BingoConfig;
import com.envyful.reforged.bingo.forge.config.BingoLocaleConfig;
import com.envyful.reforged.bingo.forge.config.BingoQueries;
import com.envyful.reforged.bingo.forge.player.BingoAttribute;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

@Mod(
        modid = "reforgedbingo",
        name = "Reforged Bingo Forge",
        version = ReforgedBingo.VERSION,
        acceptableRemoteVersions = "*"
)
public class ReforgedBingo {

    protected static final String VERSION = "0.3.0";

    private static ReforgedBingo instance;

    private ForgePlayerManager playerManager = new ForgePlayerManager();
    private ForgeCommandFactory commandFactory = new ForgeCommandFactory();

    private BingoConfig config;
    private BingoLocaleConfig locale;
    private Database database;

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        instance = this;

        this.reloadConfig();
        this.playerManager.registerAttribute(this, BingoAttribute.class);

        UtilConcurrency.runAsync(() -> {
            this.database = new SimpleHikariDatabase(this.config.getDatabase());

            try (Connection connection = this.database.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(BingoQueries.CREATE_TABLE)) {
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void reloadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(BingoConfig.class);
            this.locale = YamlConfigFactory.getInstance(BingoLocaleConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ReforgedBingo getInstance() {
        return instance;
    }

    public ForgePlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public BingoConfig getConfig() {
        return this.config;
    }

    public BingoLocaleConfig getLocale() {
        return this.locale;
    }

    public boolean isBlacklisted(EnumSpecies pokemon) {
        if (pokemon.isLegendary() || pokemon.isUltraBeast()) {
            return true;
        }

        for (EnumSpecies blacklistedSpawn : this.getConfig().getBlacklistedSpawns()) {
            if (Objects.equals(blacklistedSpawn, pokemon)) {
                return true;
            }
        }

        return false;
    }
}
