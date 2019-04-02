package org.gwmdevelopments.sponge_plugin.crates.key.keys;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.gwmdevelopments.sponge_plugin.crates.key.GiveableKey;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.Optional;

public final class ExperienceLevelKey extends GiveableKey {

    public static final String TYPE = "EXPERIENCE_LEVEL";

    private final int experienceLevel;

    public ExperienceLevelKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode experienceLevelNode = node.getNode("EXPERIENCE_LEVEL");
            if (experienceLevelNode.isVirtual()) {
                throw new IllegalArgumentException("EXPERIENCE_LEVEL node does not exist!");
            }
            experienceLevel = experienceLevelNode.getInt();
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public ExperienceLevelKey(Optional<String> id, boolean doNotWithdraw,
                         Optional<BigDecimal> price, Optional<Currency> sellCurrency, boolean doNotAdd,
                         int experienceLevel) {
        super(id, doNotWithdraw, price, sellCurrency, doNotAdd);
        this.experienceLevel = experienceLevel;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void withdraw(Player player, int amount, boolean force) {
        if (!isDoNotWithdraw() || force) {
            int level = player.get(Keys.EXPERIENCE_LEVEL).orElse(0) - (experienceLevel * amount);
            if (level < 0) {
                level = 0;
            }
            player.offer(Keys.EXPERIENCE_LEVEL, level);
        }
    }

    @Override
    public void give(Player player, int amount, boolean force) {
        if (!isDoNotAdd() || force) {
            player.offer(Keys.EXPERIENCE_LEVEL, player.get(Keys.EXPERIENCE_LEVEL).orElse(0) + (experienceLevel * amount));
        }
    }

    @Override
    public int get(Player player) {
        return player.get(Keys.EXPERIENCE_LEVEL).orElse(0) / experienceLevel;
    }

    public int getExperienceLevel() {
        return experienceLevel;
    }
}
