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
    private String remainingTimeMessage = "&e&l(!) &eYou have &b%hours%&e hours left to complete your card!";

    private String reloadMessage = "&e&l(!) &eReloaded configs";
    private String rerollCardMessage = "&e&l(!) &eRe-rolled card for %player%";
    private String spawnTimesDelimiter = "&7, &e";

    private List<String> cardSlotLore = Lists.newArrayList(
            "&7",
            "&eClick me&7 for more information about this pokemon!"
    );

    public BingoLocaleConfig() {
        super();
    }

    public List<String> getCardSlotLore() {
        return this.cardSlotLore;
    }

    public String getCardReset() {
        return this.cardReset;
    }

    public String getRemainingTimeMessage() {
        return this.remainingTimeMessage;
    }

    public String getReloadMessage() {
        return this.reloadMessage;
    }

    public String getRerollCardMessage() {
        return this.rerollCardMessage;
    }

    public String getSpawnTimesDelimiter() {
        return this.spawnTimesDelimiter;
    }
}
