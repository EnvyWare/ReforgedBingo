package com.envyful.reforged.bingo.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.google.common.collect.Lists;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigPath("config/ReforgedBingo/locale.yml")
@ConfigSerializable
public class BingoLocaleConfig extends AbstractYamlConfig {

    private String cardReset = "&e&l(!) &eYour bingo card was just reset!\n&7You have 24 hours to complete it. Good luck!";
    private String slotCompleteMessage = "&e&l(!) &eWell done, you just completed a slot on your bingo card!";
    private String lineCompleteMessage = "&e&l(!) &eWell done, you just completed a line on your bingo card!";
    private String cardCompleteMessage = "&e&l(!) &eWell done, your entire bingo card!";
    private String remainingTimeMessage = "&e&l(!) &eYou have &b%hours%&e hours left to complete your card!";

    private List<String> helpInfo = Lists.newArrayList(
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
    );

    public BingoLocaleConfig() {
        super();
    }

    public String getCardReset() {
        return this.cardReset;
    }

    public String getSlotCompleteMessage() {
        return this.slotCompleteMessage;
    }

    public String getLineCompleteMessage() {
        return this.lineCompleteMessage;
    }

    public String getCardCompleteMessage() {
        return this.cardCompleteMessage;
    }

    public String getRemainingTimeMessage() {
        return this.remainingTimeMessage;
    }

    public List<String> getHelpInfo() {
        return this.helpInfo;
    }
}
