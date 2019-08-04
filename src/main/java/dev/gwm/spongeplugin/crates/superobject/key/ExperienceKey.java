package dev.gwm.spongeplugin.crates.superobject.keys;

import dev.gwm.spongeplugin.crates.exception.SSOCreationException;
import dev.gwm.spongeplugin.crates.superobject.GiveableKey;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.Optional;

public final class ExperienceKey extends GiveableKey {

    public static final String TYPE = "EXPERIENCE";

    private final int experience;

    public ExperienceKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode experienceNode = node.getNode("EXPERIENCE");
            if (experienceNode.isVirtual()) {
                throw new IllegalArgumentException("EXPERIENCE node does not exist!");
            }
            experience = experienceNode.getInt();
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public ExperienceKey(Optional<String> id, boolean doNotWithdraw,
                         Optional<BigDecimal> price, Optional<Currency> sellCurrency, boolean doNotAdd,
                         int experience) {
        super(id, doNotWithdraw, price, sellCurrency, doNotAdd);
        this.experience = experience;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void withdraw(Player player, int amount, boolean force) {
        if (!isDoNotWithdraw() || force) {
            int level = player.get(Keys.TOTAL_EXPERIENCE).orElse(0) - (experience * amount);
            if (level < 0) {
                level = 0;
            }
            player.offer(Keys.TOTAL_EXPERIENCE, level);
        }
    }

    @Override
    public void give(Player player, int amount, boolean force) {
        if (!isDoNotAdd() || force) {
            player.offer(Keys.TOTAL_EXPERIENCE, player.get(Keys.TOTAL_EXPERIENCE).orElse(0) + (experience * amount));
        }
    }

    @Override
    public int get(Player player) {
        return player.get(Keys.TOTAL_EXPERIENCE).orElse(0) / experience;
    }

    public int getExperience() {
        return experience;
    }
}
