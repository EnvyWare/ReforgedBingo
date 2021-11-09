package com.envyful.reforged.bingo.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.PositionableConfigItem;
import com.envyful.api.config.type.SQLDatabaseDetails;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigPath("config/ReforgedBingo/config.yml")
@ConfigSerializable
public class BingoConfig extends AbstractYamlConfig {

    private SQLDatabaseDetails database = new SQLDatabaseDetails("Bingo", "0.0.0.0", 3306,
            "admin", "password", "reforged");

    private ConfigInterface configInterface = new ConfigInterface("Bingo", 6, "BLOCK", Maps.newHashMap(ImmutableMap.of(
            "one",
            new ConfigItem("minecraft:stained_glass_pane", 1, (byte) 15,  " ", Lists.newArrayList(), Maps.newHashMap()))));

    private int maximumEvolution = 1;
    private long cardDurationSeconds = 86400;

    private List<String> blacklistedSpawns = Lists.newArrayList();
    private transient List<EnumSpecies> blacklistedSpawnsCache = null;

    private List<String> slotCompleteRewards = Lists.newArrayList("give %player% minecraft:diamond 1");
    private List<String> lineCompleteRewards = Lists.newArrayList("give %player% minecraft:diamond 5");
    private List<String> cardCompleteRewards = Lists.newArrayList("give %player% minecraft:diamond 10");
    private List<String> cardSlotCommands = Lists.newArrayList("pwiki %pokemon%");

    private ConfigItem completeItem = new ConfigItem(
            "minecraft:stained_glass_pane", 1, (byte) 5, "&a&lCOMPLETE", Lists.newArrayList(), Maps.newHashMap()
    );

    private PositionableConfigItem helpItem = new PositionableConfigItem(
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

    public BingoConfig() {
        super();
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

    public List<EnumSpecies> getBlacklistedSpawns() {
        if (this.blacklistedSpawnsCache == null) {
            this.blacklistedSpawnsCache = Lists.newArrayList();

            for (String blacklistedSpawn : this.blacklistedSpawns) {
                EnumSpecies species = EnumSpecies.getFromNameAnyCase(blacklistedSpawn);

                if (species != null) {
                    this.blacklistedSpawnsCache.add(species);
                }
            }
        }

        return this.blacklistedSpawnsCache;
    }

    public List<String> getSlotCompleteRewards() {
        return this.slotCompleteRewards;
    }

    public List<String> getLineCompleteRewards() {
        return this.lineCompleteRewards;
    }

    public List<String> getCardCompleteRewards() {
        return this.cardCompleteRewards;
    }

    public PositionableConfigItem getHelpItem() {
        return this.helpItem;
    }

    public ConfigItem getCompleteItem() {
        return this.completeItem;
    }

    public long getCardDurationSeconds() {
        return this.cardDurationSeconds;
    }
}
