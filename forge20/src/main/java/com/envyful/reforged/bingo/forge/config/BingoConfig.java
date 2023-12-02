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

    @Comment("""
            The setting to tell the mod how to save the player data.
            The options are:
            - JSON
            - MYSQL
            """)
    private SaveMode saveMode = SaveMode.JSON;

    @Comment("""
            The MySQL database details.
            This will only be used if the save mode is set to MYSQL
            
            NOTE: DO NOT SHARE THESE WITH ANYONE YOU DO NOT TRUST
            """)
    private SQLDatabaseDetails database = SQLDatabaseDetails.DEFAULT;

    private ConfigInterface configInterface = ConfigInterface.defaultInterface("Bingo");

    private int maximumEvolution = 1;
    private long cardDurationSeconds = 86400;
    private boolean staticResetTimeEnabled = false;
    private String staticResetTime;
    private transient ExecutionTime executionTime;

    private int height = 4;
    private int width = 7;

    private List<String> blacklistedSpawns = Lists.newArrayList();
    private transient List<Species> blacklistedSpawnsCache = null;

    private ConfigRewardPool<ConfigReward> slotCompleteRewards = ConfigRewardPool.builder(new ConfigReward(Lists.newArrayList("guaranteed reward"), Lists.newArrayList("Hey")))
            .minRolls(1).maxRolls(1)
            .rewards(
            new ConfigRandomWeightedSet<>(new ConfigRandomWeightedSet.WeightedObject<>(10, new ConfigReward(Lists.newArrayList("reward"), Lists.newArrayList("Hey")))))
            .build();

    private ConfigRewardPool<ConfigReward> lineCompleteRewards = ConfigRewardPool.builder(new ConfigReward(Lists.newArrayList("guaranteed reward"), Lists.newArrayList("Hey")))
            .minRolls(1).maxRolls(1)
            .rewards(
                    new ConfigRandomWeightedSet<>(new ConfigRandomWeightedSet.WeightedObject<>(10, new ConfigReward(Lists.newArrayList("reward"), Lists.newArrayList("Hey")))))
            .build();

    private ConfigRewardPool<ConfigReward> cardCompleteRewards = ConfigRewardPool.builder(new ConfigReward(Lists.newArrayList("guaranteed reward"), Lists.newArrayList("Hey")))
            .minRolls(1).maxRolls(1)
            .rewards(
                    new ConfigRandomWeightedSet<>(new ConfigRandomWeightedSet.WeightedObject<>(10, new ConfigReward(Lists.newArrayList("reward"), Lists.newArrayList("Hey")))))
            .build();

    private ConfigRewardPool<ConfigReward> columnCompleteRewards = ConfigRewardPool.builder(new ConfigReward(Lists.newArrayList("guaranteed reward"), Lists.newArrayList("Hey")))
            .minRolls(1).maxRolls(1)
            .rewards(
                    new ConfigRandomWeightedSet<>(new ConfigRandomWeightedSet.WeightedObject<>(10, new ConfigReward(Lists.newArrayList("reward"), Lists.newArrayList("Hey")))))
            .build();

    private List<String> cardSlotCommands = Lists.newArrayList("pwiki %pokemon%");

    private ConfigItem completeItem = ConfigItem.builder().type("minecraft:lime_stained_glass_pane").amount(1).name("&a&lCOMPLETE").build();

    private ExtendedConfigItem helpItem = new ExtendedConfigItem(
            "minecraft:book", 1, (byte) 0, "&eInfo", Lists.newArrayList(
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
    ), 4, 0, Maps.newHashMap());

    private List<Integer> cardPositions = Lists.newArrayList(
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    );

    private Map<String, BoardFilter> filters = ImmutableMap.of(
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
