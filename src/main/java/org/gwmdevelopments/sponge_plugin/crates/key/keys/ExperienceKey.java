package org.gwmdevelopments.sponge_plugin.crates.key.keys;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.gwmdevelopments.sponge_plugin.crates.key.GiveableKey;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.Optional;

public class ExperienceKey extends GiveableKey {

    private int experience;

    public ExperienceKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode experienceNode = node.getNode("EXPERIENCE");
            if (experienceNode.isVirtual()) {
                throw new IllegalArgumentException("EXPERIENCE node does not exist!");
            }
            experience = experienceNode.getInt();
        } catch (Exception e) {
            throw new SSOCreationException("Failed to create Experience Key!", e);
        }
    }

    public ExperienceKey(String type, Optional<String> id, boolean doNotWithdraw,
                         Optional<BigDecimal> price, Optional<Currency> sellCurrency, boolean doNotAdd,
                         int experience) {
        super(type, id, doNotWithdraw, price, sellCurrency, doNotAdd);
        this.experience = experience;
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
}
