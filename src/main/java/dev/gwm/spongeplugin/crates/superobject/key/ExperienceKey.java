package dev.gwm.spongeplugin.crates.superobject.key;

import dev.gwm.spongeplugin.crates.superobject.key.base.GiveableKey;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.utils.GiveableData;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;

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
            if (experience <= 0) {
                throw new IllegalArgumentException("Experience is equal to or less than 0!");
            }
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public ExperienceKey(String id, boolean doNotWithdraw,
                         GiveableData giveableData, boolean doNotAdd,
                         int experience) {
        super(id, doNotWithdraw, giveableData, doNotAdd);
        if (experience <= 0) {
            throw new IllegalArgumentException("Experience is equal to or less than 0!");
        }
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
