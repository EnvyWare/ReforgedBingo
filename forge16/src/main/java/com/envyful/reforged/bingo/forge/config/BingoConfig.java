package com.envyful.reforged.bingo.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.type.*;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.server.UtilForgeServer;
import com.envyful.api.player.SaveMode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigPath("config/ReforgedBingo/config.yml")
@ConfigSerializable
public class BingoConfig extends AbstractYamlConfig {

    private SaveMode saveMode = SaveMode.JSON;
    private SQLDatabaseDetails database = new SQLDatabaseDetails("Bingo", "0.0.0.0", 3306,
            "admin", "password", "reforged");

    private ConfigInterface configInterface = new ConfigInterface("Bingo", 6, "BLOCK", Maps.newHashMap(ImmutableMap.of(
            "one",
            new ConfigItem("minecraft:black_stained_glass_pane", 1, (byte) 15,  " ", Lists.newArrayList(), Maps.newHashMap()))));

    private int maximumEvolution = 1;
    private long cardDurationSeconds = 86400;

    private int height = 4;
    private int width = 7;
    private int startingPosX = 1;
    private int startingPosY = 1;

    private List<String> blacklistedSpawns = Lists.newArrayList();
    private transient List<Species> blacklistedSpawnsCache = null;

    private ConfigRandomWeightedSet<BingoReward> slotCompleteRewards = new ConfigRandomWeightedSet<>(
            new ConfigRandomWeightedSet.WeightedObject<>(10, new BingoReward("Example",
                    Lists.newArrayList("give %player% minecraft:diamond 1", "broadcast %reward%"),
                    Lists.newArrayList("&e&l(!) &eWell done, you just completed a slot on your bingo card!")))
    );

    private ConfigRandomWeightedSet<BingoReward> lineCompleteRewards = new ConfigRandomWeightedSet<>(
            new ConfigRandomWeightedSet.WeightedObject<>(10, new BingoReward("Example",
                    Lists.newArrayList("give %player% minecraft:diamond 1", "broadcast %reward%"),
                    Lists.newArrayList("&e&l(!) &eWell done, you just completed a line on your bingo card!")))
    );

    private ConfigRandomWeightedSet<BingoReward> cardCompleteRewards = new ConfigRandomWeightedSet<>(
            new ConfigRandomWeightedSet.WeightedObject<>(10, new BingoReward("Example",
                    Lists.newArrayList("give %player% minecraft:diamond 1", "broadcast %reward%"),
                    Lists.newArrayList("&e&l(!) &eWell done, your entire bingo card!")))
    );

    private ConfigRandomWeightedSet<BingoReward> columnCompleteRewards = new ConfigRandomWeightedSet<>(
            new ConfigRandomWeightedSet.WeightedObject<>(10, new BingoReward("Example",
                    Lists.newArrayList("give %player% minecraft:diamond 1", "broadcast %reward%"),
                    Lists.newArrayList("&e&l(!) &eWell done, your column complete.")))
    );

    private List<String> cardSlotCommands = Lists.newArrayList("pwiki %pokemon%");

    private ConfigItem completeItem = new ConfigItem(
            "minecraft:lime_stained_glass_pane", 1, (byte) 5, "&a&lCOMPLETE", Lists.newArrayList(), Maps.newHashMap()
    );

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

    public BingoReward getSlotCompleteReward() {
        return this.slotCompleteRewards.getRandom();
    }

    public BingoReward getLineCompleteRewards() {
        return this.lineCompleteRewards.getRandom();
    }

    public BingoReward getCardCompleteRewards() {
        return this.cardCompleteRewards.getRandom();
    }

    public BingoReward getColumnCompleteRewards() {
        return this.columnCompleteRewards.getRandom();
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

    @ConfigSerializable
    public static class BingoReward {

        private String name;
        private List<String> commands;
        private List<String> messages;

        public BingoReward(String name, List<String> commands, List<String> messages) {
            this.name = name;
            this.commands = commands;
            this.messages = messages;
        }

        public BingoReward() {
        }

        public void execute(ServerPlayerEntity player) {
            if (this.commands != null && !this.commands.isEmpty()) {
                for (String command : this.commands) {
                    UtilForgeServer.executeCommand(command.replace("%player%", player.getName().getString()).replace("%reward%", this.name));
                }
            }

            if (this.messages != null && !this.messages.isEmpty()) {
                for (String message : this.messages) {
                    player.sendMessage(UtilChatColour.colour(message.replace("%player%", player.getName().getString()).replace("%reward%", this.name)), Util.NIL_UUID);
                }
            }
        }
    }
}
