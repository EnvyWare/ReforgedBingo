package com.envyful.reforged.bingo.forge.config;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.type.*;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.envyful.api.forge.config.ConfigReward;
import com.envyful.api.forge.config.ConfigRewardPool;
import com.envyful.api.player.SaveMode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.List;
import java.util.Map;

@ConfigPath("config/ReforgedBingo/config.yml")
@ConfigSerializable
public class BingoConfig extends AbstractYamlConfig {

    @Comment("The type of saving to use for the player data (MYSQL, JSON)")
    private SaveMode saveMode = SaveMode.JSON;

    @Comment("The database details for data if using MYSQL")
    private SQLDatabaseDetails database = new SQLDatabaseDetails("Bingo", "0.0.0.0", 3306,
            "admin", "password", "reforged");

    @Comment("The gui settings for the bingo card")
    private ConfigInterface configInterface = ConfigInterface.builder()
            .title("Bingo")
            .height(6)
            .fillType(ConfigInterface.FillType.BLOCK)
            .fillerItem(ConfigItem.builder().type("minecraft:black_stained_glass_pane").amount(1).name(" ").build())
            .build();

    @Comment("The maximum number of pre-evolutions a Pokemon can have for it to be allowed on the bingo card (0 = baby pokemon like Charmander, 1 = middle evolutions like Charmeleon, 2 = final evolutions like Charizard)")
    private int maximumEvolution = 1;

    @Comment("The duration of the bingo card in seconds for the player to complete (86400 = 24 hours)")
    private long cardDurationSeconds = 86400;

    @Comment("Whether to reset the bingo card at a specific time each day (true = enabled, false = disabled)")
    private boolean staticResetTimeEnabled = false;

    @Comment("The time to reset the bingo card each day (in cron format, e.g. 0 0 0 * * * = midnight each day)")
    private String staticResetTime;
    private transient ExecutionTime executionTime;

    @Comment("The dimensions of the bingo card (height = rows, width = columns)")
    private int height = 4;

    @Comment("The dimensions of the bingo card (height = rows, width = columns)")
    private int width = 7;

    @Comment("A list of Pokemon species that are not allowed to spawn on the bingo card (e.g. Charmander)")
    private List<String> blacklistedSpawns = Lists.newArrayList(
            "hoopa"
    );
    private transient List<Species> blacklistedSpawnsCache = null;

    @Comment("The rewards for completing a slot on the bingo card")
    private ConfigRewardPool<ConfigReward> slotCompleteRewards = ConfigRewardPool.builder(ConfigReward.builder().commands("guaranteed reward").messages("Hey").build())
            .minRolls(1).maxRolls(1)
            .rewards(ConfigRandomWeightedSet.builder(ConfigReward.builder().commands("reward").messages("Hey").build(), 10)
                    .build())
            .build();

    @Comment("The rewards for completing a line on the bingo card")
    private ConfigRewardPool<ConfigReward> lineCompleteRewards = ConfigRewardPool.builder(ConfigReward.builder().commands("guaranteed reward").messages("Hey").build())
            .minRolls(1).maxRolls(1)
            .rewards(ConfigRandomWeightedSet.builder(ConfigReward.builder().commands("reward").messages("Hey").build(), 10)
                    .build())
            .build();

    @Comment("The rewards for completing the entire bingo card")
    private ConfigRewardPool<ConfigReward> cardCompleteRewards = ConfigRewardPool.builder(ConfigReward.builder().commands("guaranteed reward").messages("Hey").build())
            .minRolls(1).maxRolls(1)
            .rewards(ConfigRandomWeightedSet.builder(ConfigReward.builder().commands("reward").messages("Hey").build(), 10)
                    .build())
            .build();

    @Comment("The rewards for completing a column on the bingo card")
    private ConfigRewardPool<ConfigReward> columnCompleteRewards = ConfigRewardPool.builder(ConfigReward.builder().commands("guaranteed reward").messages("Hey").build())
            .minRolls(1).maxRolls(1)
            .rewards(ConfigRandomWeightedSet.builder(ConfigReward.builder().commands("reward").messages("Hey").build(), 10)
                    .build())
            .build();

    @Comment("The commands to run when a player clicks on a slot on the bingo card")
    private List<String> cardSlotCommands = Lists.newArrayList("pwiki %pokemon%");

    @Comment("The item to display when the player has completed a slot on the bingo card")
    private ConfigItem completeItem = ConfigItem.builder().type("minecraft:lime_stained_glass_pane").amount(1).name("&a&lCOMPLETE")
            .lore("%pokemon%").build();

    @Comment("An item to display in the GUI that will explain the bingo game to the player")
    private ExtendedConfigItem helpItem = ExtendedConfigItem.builder()
            .type("minecraft:book")
            .name("&eInfo")
            .lore(
                    "",
                    "&eWhat is bingo?",
                    "&7Each day you will get a new set of 27 pokemon",
                    "&7on your bingo card that you must find and catch",
                    "&7in order to receive the bingo card's rewards.",
                    "",
                    "&eWhat are the rewards?",
                    "&7For each pokemon you complete on the bingo card",
                    "&7you will receive a random reward. Then for each",
                    "&7line of the bingo card you complete you'll get",
                    "&7another random reward. Finally, if you complete",
                    "&7the entire bingo card you will receive yet another",
                    "&7random, but better, reward.",
                    "",
                    "&eWhat if I have two of the same pokemon?",
                    "&7You simply just have to find and catch that pokemon twice."
            )
            .amount(1)
            .positions(4, 0)
            .build();

    @Comment("The positions on the bingo card where the pokemon will be displayed (0-53)")
    private List<Integer> cardPositions = Lists.newArrayList(
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    );

    @Comment("A spec based filter that limits the number of Pokemon that match the filter on the bingo card. For example, 'leg' set to 1 would only allow at most one legendary Pokemon on the card. You can set it to 0 to prevent any Pokemon matching the spec from appearing on the card.")
    private Map<String, BoardFilter> filters = Map.of(
            "one", new BoardFilter("leg", 1)
    );

    public BingoConfig() {
        super();
    }

    public SaveMode getSaveMode() {
        return this.saveMode;
    }

    public List<String> getCardSlotCommands() {
        return this.cardSlotCommands;
    }

    public ConfigInterface getConfigInterface() {
        return this.configInterface;
    }

    public SQLDatabaseDetails getDatabase() {
        return this.database;
    }

    public int getMaximumEvolution() {
        return this.maximumEvolution;
    }

    public List<Species> getBlacklistedSpawns() {
        if (this.blacklistedSpawnsCache == null) {
            this.blacklistedSpawnsCache = Lists.newArrayList();

            for (String blacklistedSpawn : this.blacklistedSpawns) {
                Species species = PixelmonSpecies.fromNameOrDex(blacklistedSpawn).orElse(null);

                if (species != null) {
                    this.blacklistedSpawnsCache.add(species);
                }
            }
        }

        return this.blacklistedSpawnsCache;
    }

    public ConfigRewardPool getSlotCompleteReward() {
        return this.slotCompleteRewards;
    }

    public ConfigRewardPool getLineCompleteRewards() {
        return this.lineCompleteRewards;
    }

    public ConfigRewardPool getCardCompleteRewards() {
        return this.cardCompleteRewards;
    }

    public ConfigRewardPool getColumnCompleteRewards() {
        return this.columnCompleteRewards;
    }

    public ExtendedConfigItem getHelpItem() {
        return this.helpItem;
    }

    public ConfigItem getCompleteItem() {
        return this.completeItem;
    }

    public long getCardDurationSeconds() {
        return this.cardDurationSeconds;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public List<Integer> getCardPositions() {
        return this.cardPositions;
    }

    public boolean isStaticResetTimeEnabled() {
        return this.staticResetTimeEnabled;
    }

    public ExecutionTime getStaticResetTime() {
        if (this.executionTime == null) {
            CronParser parser =  new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ));
            Cron parse = parser.parse(this.staticResetTime);
            this.executionTime = ExecutionTime.forCron(parse);
        }

        return this.executionTime;
    }

    public List<BoardFilter> getFilters() {
        return Lists.newArrayList(this.filters.values());
    }

    @ConfigSerializable
    public static class BoardFilter {

        private String spec;
        private transient PokemonSpecification cachedSpec;
        private int limit;

        public BoardFilter() {
        }

        public BoardFilter(String spec, int limit) {
            this.spec = spec;
            this.limit = limit;
        }

        public PokemonSpecification getSpec() {
            if (this.cachedSpec == null) {
                this.cachedSpec = PokemonSpecificationProxy.create(this.spec).get();
            }

            return this.cachedSpec;
        }

        public int getLimit() {
            return this.limit;
        }
    }
}
