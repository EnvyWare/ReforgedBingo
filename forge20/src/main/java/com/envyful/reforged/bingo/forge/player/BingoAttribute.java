package com.envyful.reforged.bingo.forge.player;

import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.forge.player.attribute.ManagedForgeAttribute;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.player.save.attribute.DataDirectory;
import com.envyful.api.reforged.pixelmon.UtilPokemonInfo;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.api.text.Placeholder;
import com.envyful.reforged.bingo.forge.ReforgedBingo;
import com.envyful.reforged.bingo.forge.config.BingoConfig;
import com.envyful.reforged.bingo.forge.event.BingoSlotCompleteEvent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonFactory;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@DataDirectory("config/players/ReforgedBingo/")
public class BingoAttribute extends ManagedForgeAttribute<ReforgedBingo> {

    protected long started;
    protected CardSlot[][] bingoCard;
    protected int completed = 0;

    public BingoAttribute(UUID id) {
        super(id, ReforgedBingo.getInstance());
    }

    public int getCompleted() {
        return this.completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    @Override
    public void setParent(ForgeEnvyPlayer parent) {
        super.setParent(parent);

        if (this.bingoCard == null) {
            this.generateNewCard();
            return;
        }

        this.checkCardSize();
    }

    private void checkCardSize() {
        if (this.bingoCard.length != ReforgedBingo.getConfig().getHeight()) {
            this.generateNewCard();
            ReforgedBingo.getLogger().error("Card size mismatch for player {}. Regenerating card...", this.id);
        }

        for (CardSlot[] cardSlots : this.bingoCard) {
            if (cardSlots.length != ReforgedBingo.getConfig().getWidth()) {
                this.generateNewCard();
                ReforgedBingo.getLogger().error("Card size mismatch for player {}. Regenerating card...", this.id);
                return;
            }
        }
    }

    public boolean checkCardExpiry() {
        if (ReforgedBingo.getConfig().isStaticResetTimeEnabled()) {
            return System.currentTimeMillis() > this.started;
        }

        return (System.currentTimeMillis() - started) > TimeUnit.SECONDS.toMillis(ReforgedBingo.getConfig().getCardDurationSeconds());
    }

    public void generateNewCard() {
        this.bingoCard = new CardSlot[ReforgedBingo.getConfig().getHeight()][ReforgedBingo.getConfig().getWidth()];
        Map<String, Integer> counters = Maps.newHashMap();

        for (int y = 0; y < ReforgedBingo.getConfig().getHeight(); y++) {
            CardSlot[] currentLine = this.bingoCard[y];

            for (int x = 0; x < ReforgedBingo.getConfig().getWidth(); x++) {
                Species species = PixelmonSpecies.getRandomSpecies();

                while (!this.canPickPokemon(species, counters)) {
                    species = PixelmonSpecies.getRandomSpecies();
                }

                currentLine[x] = new CardSlot(species, false);
            }
        }

        this.started = System.currentTimeMillis();

        if (ReforgedBingo.getConfig().isStaticResetTimeEnabled()) {
            this.started = ReforgedBingo.getConfig().getStaticResetTime().nextExecution(ZonedDateTime.now()).get().toInstant().toEpochMilli();
        }

        this.parent.message(ReforgedBingo.getLocale().getCardReset());
    }

    private boolean canPickPokemon(Species value, Map<String, Integer> counters) {
        if (value.getDefaultForm().getPreEvolutions().size() >= ReforgedBingo.getConfig().getMaximumEvolution()) {
            return false;
        }

        if (this.manager.isBlacklisted(value)) {
            return false;
        }

        Pokemon pokemon = PokemonFactory.create(value);

        for (BingoConfig.BoardFilter filter : ReforgedBingo.getConfig().getFilters()) {
            if (!filter.getSpec().matches(pokemon)) {
                continue;
            }

            int counter = counters.getOrDefault(filter.getSpec().toString(), 0);

            if (counter >= filter.getLimit()) {
                return false;
            }

            counters.put(filter.getSpec().toString(), counter + 1);
        }

        return true;
    }

    public void catchPokemon(Species species) {
        this.checkCardSize();

        for (CardSlot[] cardSlots : this.bingoCard) {
            for (int i = 0; i < cardSlots.length; i++) {
                CardSlot cardSlot = cardSlots[i];
                if (cardSlot.getSpecies().getDex() == species.getDex() && !cardSlot.isComplete()) {
                    cardSlot.setComplete(true);

                    boolean rowComplete = this.checkRowCompletion(cardSlots);
                    boolean cardComplete = this.checkCardCompletion();
                    boolean columnComplete = this.checkColumnCompletion(i);
                    BingoSlotCompleteEvent completeEvent = new BingoSlotCompleteEvent(this.parent,
                            this, rowComplete, cardComplete, columnComplete);
                    MinecraftForge.EVENT_BUS.post(completeEvent);
                    return;
                }
            }
        }
    }

    public boolean completeSlot(int slotY, int slotX) {
        this.checkCardSize();

        if (slotY >= this.bingoCard.length) {
            return false;
        }

        CardSlot[] cardSlots = this.bingoCard[slotY];

        if (slotX >= cardSlots.length) {
            return false;
        }

        this.bingoCard[slotY][slotX].setComplete(true);
        boolean rowComplete = this.checkRowCompletion(cardSlots);
        boolean cardComplete = this.checkCardCompletion();
        boolean columnComplete = this.checkColumnCompletion(slotX);
        BingoSlotCompleteEvent completeEvent = new BingoSlotCompleteEvent(this.parent,
                this, rowComplete, cardComplete, columnComplete);
        MinecraftForge.EVENT_BUS.post(completeEvent);
        return true;
    }

    private boolean checkRowCompletion(CardSlot[] complete) {
        for (CardSlot cardSlot : complete) {
            if (!cardSlot.isComplete()) {
                return false;
            }
        }

        return true;
    }

    private boolean checkCardCompletion() {
        for (CardSlot[] cardSlots : this.bingoCard) {
            for (CardSlot cardSlot : cardSlots) {
                if (!cardSlot.isComplete()) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean checkColumnCompletion(int column) {
        for (CardSlot[] cardSlots : this.bingoCard) {
            if (!cardSlots[column].isComplete()) {
                return false;
            }
        }

        return true;
    }

    public long getTimeRemaining() {
        if (ReforgedBingo.getConfig().isStaticResetTimeEnabled()) {
            return (TimeUnit.MILLISECONDS.toHours(this.started - System.currentTimeMillis()));
        }

        return (TimeUnit.SECONDS.toHours(ReforgedBingo.getConfig().getCardDurationSeconds()) - TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - this.started));
    }

    public void display(Pane pane) {
        this.checkCardSize();

        int slot = 0;

        for (int y = 0; y < ReforgedBingo.getConfig().getHeight(); y++) {
            for (int x = 0; x < ReforgedBingo.getConfig().getWidth(); x++) {
                int position = ReforgedBingo.getConfig().getCardPositions().get(slot);
                ++slot;

                if (this.bingoCard[y][x].isComplete()) {
                    pane.set(position % 9, position / 9, GuiFactory.displayable(UtilConfigItem.fromConfigItem(ReforgedBingo.getConfig().getCompleteItem(),
                            Placeholder.simple("%pokemon%", this.bingoCard[y][x].getSpecies().getLocalizedName()))));
                    continue;
                }

                CardSlot cardSlot = this.bingoCard[y][x];

                List<Component> lore = Lists.newArrayList();

                for (String s : ReforgedBingo.getLocale().getCardSlotLore()) {
                    lore.add(PlatformProxy.parse(s
                            .replace("%biomes%", String.join(ReforgedBingo.getLocale().getBiomeInfoDelimiter(), UtilPokemonInfo.getSpawnBiomes(cardSlot.getSpecies().getDefaultForm())))
                            .replace("%spawn_times%", String.join(
                                    ReforgedBingo.getLocale().getSpawnTimesDelimiter(),
                                    UtilPokemonInfo.getSpawnTimes(cardSlot.getSpecies().getDefaultForm())))));
                }

                pane.set(position % 9, position / 9,
                        GuiFactory.displayableBuilder(ItemStack.class)
                                .itemStack(new ItemBuilder(UtilSprite.getPixelmonSprite(cardSlot.getSpecies()))
                                        .addLore(lore.toArray(new Component[0]))
                                        .name(cardSlot.getSpecies().getLocalizedName())
                                        .build())
                                .asyncClick(false)
                                .clickHandler((envyPlayer, clickType) -> {
                                    for (String cardSlotCommand : ReforgedBingo.getConfig().getCardSlotCommands()) {
                                        envyPlayer.executeCommand(cardSlotCommand.replace(
                                                "%pokemon%",
                                                cardSlot.getSpecies().getLocalizedName()
                                        ));
                                    }
                                })
                                .build());
            }
        }
    }
}
