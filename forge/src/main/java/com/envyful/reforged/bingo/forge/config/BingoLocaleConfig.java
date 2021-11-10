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

    private String reloadMessage = "&e&l(!) &eReloaded configs";
    private String rerollCardMessage = "&e&l(!) &eRe-rolled card for %player%";

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

    public String getReloadMessage() {
        return this.reloadMessage;
    }

    public String getRerollCardMessage() {
        return this.rerollCardMessage;
    }
}
