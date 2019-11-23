package dev.gwm.spongeplugin.crates.superobject.key;

import dev.gwm.spongeplugin.crates.superobject.key.base.GiveableKey;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.util.GiveableData;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;

public final class ExperienceLevelKey extends GiveableKey {

    public static final String TYPE = "EXPERIENCE-LEVEL";

    private final int experienceLevel;

    public ExperienceLevelKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode experienceLevelNode = node.getNode("EXPERIENCE_LEVEL");
            if (experienceLevelNode.isVirtual()) {
                throw new IllegalArgumentException("EXPERIENCE_LEVEL node does not exist!");
            }
            experienceLevel = experienceLevelNode.getInt();
            if (experienceLevel <= 0) {
                throw new IllegalArgumentException("Experience Level is equal to or less than 0!");
            }
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public ExperienceLevelKey(String id, boolean doNotWithdraw,
                              GiveableData giveableData, boolean doNotAdd,
                              int experienceLevel) {
        super(id, doNotWithdraw, giveableData, doNotAdd);
        if (experienceLevel <= 0) {
            throw new IllegalArgumentException("Experience Level is equal to or less than 0!");
        }
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
