package com.envyful.reforged.bingo.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.type.*;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.envyful.api.forge.config.ConfigReward;
import com.envyful.api.forge.config.ConfigRewardPool;
import com.envyful.api.player.SaveMode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigPath("config/ReforgedBingo/config.yml")
@ConfigSerializable
public class BingoConfig extends AbstractYamlConfig {

    private SaveMode saveMode = SaveMode.JSON;
    private SQLDatabaseDetails database = new SQLDatabaseDetails("Bingo", "0.0.0.0", 3306,
            "admin", "password", "reforged");

    private ConfigInterface configInterface = new ConfigInterface("Bingo", 6, "BLOCK", Maps.newHashMap(ImmutableMap.of(
            "one", ConfigItem.builder().type("minecraft:black_stained_glass_pane").amount(1).name(" ").build())));

    private int maximumEvolution = 1;
    private long cardDurationSeconds = 86400;

    private int height = 4;
    private int width = 7;
    private int startingPosX = 1;
    private int startingPosY = 1;

    private List<String> blacklistedSpawns = Lists.newArrayList();
    private transient List<Species> blacklistedSpawnsCache = null;

    private ConfigRewardPool slotCompleteRewards = ConfigRewardPool.builder()
            .minRolls(1).maxRolls(1)
            .guranteedReward(new ConfigReward(Lists.newArrayList("guaranteed reward"), Lists.newArrayList("Hey")))
            .rewards(
            new ConfigRandomWeightedSet<>(new ConfigRandomWeightedSet.WeightedObject<>(10, new ConfigReward(Lists.newArrayList("reward"), Lists.newArrayList("Hey")))))
            .build();

    private ConfigRewardPool lineCompleteRewards = ConfigRewardPool.builder()
            .minRolls(1).maxRolls(1)
            .guranteedReward(new ConfigReward(Lists.newArrayList("guaranteed reward"), Lists.newArrayList("Hey")))
            .rewards(
                    new ConfigRandomWeightedSet<>(new ConfigRandomWeightedSet.WeightedObject<>(10, new ConfigReward(Lists.newArrayList("reward"), Lists.newArrayList("Hey")))))
            .build();

    private ConfigRewardPool cardCompleteRewards = ConfigRewardPool.builder()
            .minRolls(1).maxRolls(1)
            .guranteedReward(new ConfigReward(Lists.newArrayList("guaranteed reward"), Lists.newArrayList("Hey")))
            .rewards(
                    new ConfigRandomWeightedSet<>(new ConfigRandomWeightedSet.WeightedObject<>(10, new ConfigReward(Lists.newArrayList("reward"), Lists.newArrayList("Hey")))))
            .build();

    private ConfigRewardPool columnCompleteRewards = ConfigRewardPool.builder()
            .minRolls(1).maxRolls(1)
            .guranteedReward(new ConfigReward(Lists.newArrayList("guaranteed reward"), Lists.newArrayList("Hey")))
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
            11, 12, 13, 14, 15, 16, 17,
            20, 21, 22, 23, 24, 25, 26,
            29, 30, 31, 32, 33, 34, 35,
            38, 39, 40, 41, 42, 43, 44
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

    public int getStartingPosY() {
        return this.startingPosY;
    }

    public int getStartingPosX() {
        return this.startingPosX;
    }

    public List<Integer> getCardPositions() {
        return this.cardPositions;
    }

}
