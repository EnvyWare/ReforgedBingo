package com.envyful.reforged.bingo.forge;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.database.Database;
import com.envyful.api.database.impl.SimpleHikariDatabase;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.concurrency.ForgeTaskBuilder;
import com.envyful.api.forge.gui.factory.ForgeGuiFactory;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.player.SaveMode;
import com.envyful.api.player.save.impl.JsonSaveManager;
import com.envyful.reforged.bingo.forge.command.BingoCardCommand;
import com.envyful.reforged.bingo.forge.config.BingoConfig;
import com.envyful.reforged.bingo.forge.config.BingoLocaleConfig;
import com.envyful.reforged.bingo.forge.config.BingoQueries;
import com.envyful.reforged.bingo.forge.listener.BingoCardCompleteListener;
import com.envyful.reforged.bingo.forge.listener.PokemonCatchListener;
import com.envyful.reforged.bingo.forge.player.BingoAttribute;
import com.envyful.reforged.bingo.forge.task.CardResetTask;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

@Mod("reforgedbingo")
public class ReforgedBingo {

    private static ReforgedBingo instance;

    private ForgePlayerManager playerManager = new ForgePlayerManager();
    private ForgeCommandFactory commandFactory = new ForgeCommandFactory();

    private BingoConfig config;
    private BingoLocaleConfig locale;
    private Database database;
    private Logger logger = LogManager.getLogger("reforgedbingo");

    public ReforgedBingo() {
        UtilLogger.setLogger(this.logger);
        instance = this;
        MinecraftForge.EVENT_BUS.register(this);
        var l = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(null);
    }

    @SubscribeEvent
    public void onInit(ServerAboutToStartEvent event) {
        GuiFactory.setPlatformFactory(new ForgeGuiFactory());

        this.reloadConfig();

        if (this.config.getSaveMode() == SaveMode.JSON) {
            this.playerManager.setSaveManager(new JsonSaveManager<>(this.playerManager));
        }

        this.playerManager.registerAttribute(this, BingoAttribute.class);

        new BingoCardCompleteListener(this);
        new PokemonCatchListener(this);

        if (this.config.getSaveMode() == SaveMode.MYSQL) {
            UtilConcurrency.runAsync(() -> {
                this.database = new SimpleHikariDatabase(this.config.getDatabase());

                try (Connection connection = this.database.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(BingoQueries.CREATE_TABLE)) {
                    preparedStatement.executeUpdate();

                    try (PreparedStatement alterStatement = connection.prepareStatement(BingoQueries.ALTER_TABLE)) {
                        alterStatement.executeUpdate();
                    } catch (SQLException ignored) {}
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }

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

    @SubscribeEvent
    public void onServerStart(RegisterCommandsEvent event) {
        this.commandFactory.registerCommand(event.getDispatcher(), new BingoCardCommand());
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

    public boolean isBlacklisted(Species pokemon) {
        if (pokemon.isLegendary() || pokemon.isUltraBeast()) {
            return true;
        }

        for (Species blacklistedSpawn : this.getConfig().getBlacklistedSpawns()) {
            if (Objects.equals(blacklistedSpawn, pokemon)) {
                return true;
            }
        }

        return false;
    }

    public Logger getLogger() {
        return this.logger;
    }
}
