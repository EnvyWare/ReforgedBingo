package com.envyful.reforged.bingo.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.type.SQLDatabaseDetails;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigPath("config/ReforgedBingo/config.yml")
@ConfigSerializable
public class BingoConfig extends AbstractYamlConfig {

    private SQLDatabaseDetails database = new SQLDatabaseDetails("Bingo", "0.0.0.0", 3306,
            "admin", "password", "reforged");

    private int maximumEvolution = 1;

    private List<String> blacklistedSpawns = Lists.newArrayList();
    private transient List<EnumSpecies> blacklistedSpawnsCache = null;

    private List<String> slotCompleteRewards = Lists.newArrayList();
    private List<String> lineCompleteRewards = Lists.newArrayList();
    private List<String> cardCompleteRewards = Lists.newArrayList();

    public BingoConfig() {
        super();
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
}
