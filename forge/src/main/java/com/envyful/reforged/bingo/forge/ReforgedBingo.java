package com.envyful.reforged.bingo.forge;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.database.Database;
import com.envyful.api.database.impl.SimpleHikariDatabase;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.concurrency.ForgeTaskBuilder;
import com.envyful.api.forge.gui.factory.ForgeGuiFactory;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.reforged.bingo.forge.command.BingoCardCommand;
import com.envyful.reforged.bingo.forge.config.BingoConfig;
import com.envyful.reforged.bingo.forge.config.BingoLocaleConfig;
import com.envyful.reforged.bingo.forge.config.BingoQueries;
import com.envyful.reforged.bingo.forge.listener.BingoCardCompleteListener;
import com.envyful.reforged.bingo.forge.listener.PokemonCatchListener;
import com.envyful.reforged.bingo.forge.player.BingoAttribute;
import com.envyful.reforged.bingo.forge.task.CardResetTask;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

@Mod(
        modid = "reforgedbingo",
        name = "Reforged Bingo Forge",
        version = ReforgedBingo.VERSION,
        acceptableRemoteVersions = "*",
        updateJSON = "https://ogn.pixelmonmod.com/update/sm-rb/update.json"
)
public class ReforgedBingo {

    protected static final String VERSION = "0.9.1";

    private static ReforgedBingo instance;

    private ForgePlayerManager playerManager = new ForgePlayerManager();
    private ForgeCommandFactory commandFactory = new ForgeCommandFactory();

    private BingoConfig config;
    private BingoLocaleConfig locale;
    private Database database;

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        GuiFactory.setPlatformFactory(new ForgeGuiFactory());
        instance = this;

        this.reloadConfig();
        this.playerManager.registerAttribute(this, BingoAttribute.class);

        new BingoCardCompleteListener(this);
        new PokemonCatchListener(this);

        UtilConcurrency.runAsync(() -> {
            this.database = new SimpleHikariDatabase(this.config.getDatabase());

            try (Connection connection = this.database.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(BingoQueries.CREATE_TABLE)) {
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        new ForgeTaskBuilder()
                .async(true)
                .delay(10L)
                .interval(10L)
                .task(new CardResetTask(this))
                .start();
    }

    public void reloadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(BingoConfig.class);
            this.locale = YamlConfigFactory.getInstance(BingoLocaleConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        this.commandFactory.registerCommand(event.getServer(), new BingoCardCommand());
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

    public Database getDatabase() {
        return this.database;
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
