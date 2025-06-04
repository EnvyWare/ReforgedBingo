package com.envyful.reforged.bingo.forge;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.config.database.DatabaseDetailsConfig;
import com.envyful.api.config.database.DatabaseDetailsRegistry;
import com.envyful.api.config.type.SQLDatabaseDetails;
import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.database.Database;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.neoforge.chat.ComponentTextFormatter;
import com.envyful.api.neoforge.command.ForgeCommandFactory;
import com.envyful.api.neoforge.command.parser.ForgeAnnotationCommandParser;
import com.envyful.api.neoforge.gui.factory.ForgeGuiFactory;
import com.envyful.api.neoforge.platform.ForgePlatformHandler;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.neoforge.player.ForgePlayerManager;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.player.Attribute;
import com.envyful.api.sqlite.config.SQLiteDatabaseDetailsConfig;
import com.envyful.reforged.bingo.forge.command.BingoCardCommand;
import com.envyful.reforged.bingo.forge.config.BingoConfig;
import com.envyful.reforged.bingo.forge.config.BingoLocaleConfig;
import com.envyful.reforged.bingo.forge.listener.BingoCardCompleteListener;
import com.envyful.reforged.bingo.forge.listener.PokemonCatchListener;
import com.envyful.reforged.bingo.forge.player.BingoAttribute;
import com.envyful.reforged.bingo.forge.player.SQLBingoAttributeAdapter;
import com.envyful.reforged.bingo.forge.player.SQLiteBingoAttributeAdapter;
import com.envyful.reforged.bingo.forge.task.CardResetTask;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Objects;

@Mod("reforgedbingo")
public class ReforgedBingo {

    private static final Logger LOGGER = LogManager.getLogger("reforgedbingo");

    private static ReforgedBingo instance;

    private ForgePlayerManager playerManager = new ForgePlayerManager();
    private ForgeCommandFactory commandFactory = new ForgeCommandFactory(ForgeAnnotationCommandParser::new, playerManager);

    private BingoConfig config;
    private BingoLocaleConfig locale;
    private Database database;

    public ReforgedBingo() {
        SQLiteDatabaseDetailsConfig.register();
        UtilLogger.setLogger(LOGGER);

        GuiFactory.setPlatformFactory(new ForgeGuiFactory());
        PlatformProxy.setHandler(ForgePlatformHandler.getInstance());
        PlatformProxy.setPlayerManager(this.playerManager);
        PlatformProxy.setTextFormatter(ComponentTextFormatter.getInstance());

        NeoForge.EVENT_BUS.register(this);
        instance = this;
    }

    @SubscribeEvent
    public void onInit(ServerAboutToStartEvent event) {
        this.reloadConfig();

        var saveMode = DatabaseDetailsRegistry.getRegistry().getKey((Class<DatabaseDetailsConfig>) this.getConfig().getDatabaseDetails().getClass());

        if (saveMode == null) {
            getLogger().error("Failed to find save mode for Bingo config. Please check your config and try again");
            return;
        }

        this.playerManager.registerAttribute(Attribute.builder(BingoAttribute.class, ForgeEnvyPlayer.class)
                .constructor(BingoAttribute::new)
                .registerAdapter(SQLDatabaseDetails.ID, new SQLBingoAttributeAdapter())
                .registerAdapter(SQLiteDatabaseDetailsConfig.ID, new SQLiteBingoAttributeAdapter())
        );
        this.playerManager.setGlobalSaveMode(DatabaseDetailsRegistry.getRegistry().getKey((Class<DatabaseDetailsConfig>) this.getConfig().getDatabaseDetails().getClass()));


        this.database = this.config.getDatabaseDetails().createDatabase();
        this.playerManager.getAdapter(BingoAttribute.class).initialize();

        new BingoCardCompleteListener();
        Pixelmon.EVENT_BUS.register(new PokemonCatchListener());

        UtilConcurrency.runRepeatingTask(new CardResetTask(), 25L, 25L);
    }

    public void reloadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(BingoConfig.class);
            this.locale = YamlConfigFactory.getInstance(BingoLocaleConfig.class);
        } catch (IOException e) {
            getLogger().error("Failed to load config", e);
        }
    }

    @SubscribeEvent
    public void onServerStart(RegisterCommandsEvent event) {
        this.commandFactory.registerCommand(event.getDispatcher(), this.commandFactory.parseCommand(new BingoCardCommand()));
    }

    public static ReforgedBingo getInstance() {
        return instance;
    }

    public static ForgePlayerManager getPlayerManager() {
        return instance.playerManager;
    }

    public static BingoConfig getConfig() {
        return instance.config;
    }

    public static BingoLocaleConfig getLocale() {
        return instance.locale;
    }

    public static Database getDatabase() {
        return instance.database;
    }

    public boolean isBlacklisted(Species pokemon) {
        if (pokemon.isLegendary() || pokemon.isUltraBeast()) {
            return true;
        }

        for (Species blacklistedSpawn : config.getBlacklistedSpawns()) {
            if (Objects.equals(blacklistedSpawn, pokemon)) {
                return true;
            }
        }

        return false;
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
