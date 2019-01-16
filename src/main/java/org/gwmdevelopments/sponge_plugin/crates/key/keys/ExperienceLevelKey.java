package org.gwmdevelopments.sponge_plugin.crates.key.keys;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.gwmdevelopments.sponge_plugin.crates.key.AbstractKey;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public class ExperienceLevelKey extends AbstractKey {

    private int experienceLevel;

    public ExperienceLevelKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode experienceLevelNode = node.getNode("EXPERIENCE_LEVEL");
            if (experienceLevelNode.isVirtual()) {
                throw new IllegalArgumentException("EXPERIENCE_LEVEL node does not exist!");
            }
            experienceLevel = experienceLevelNode.getInt();
        } catch (Exception e) {
            throw new SSOCreationException("Failed to create Experience Level Key!", e);
        }
    }

    public ExperienceLevelKey(String type, Optional<String> id,
                              int experienceLevel) {
        super(type, id, true);
        this.experienceLevel = experienceLevel;
    }

    @Override
    public void withdraw(Player player, int amount, boolean force) {
    }

    @Override
    public int get(Player player) {
        return player.get(Keys.EXPERIENCE_LEVEL).orElse(0) >= experienceLevel ? 1 : 0;
    }

    public int getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(int experienceLevel) {
        this.experienceLevel = experienceLevel;
    }
}
