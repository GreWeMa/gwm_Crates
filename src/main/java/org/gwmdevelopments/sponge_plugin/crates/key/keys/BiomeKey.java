package org.gwmdevelopments.sponge_plugin.crates.key.keys;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.gwmdevelopments.sponge_plugin.crates.key.AbstractKey;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.biome.BiomeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BiomeKey extends AbstractKey {

    private boolean whitelistMode;
    private List<BiomeType> biomes;

    public BiomeKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode whitelistModeNode = node.getNode("WHITELIST_MODE");
            ConfigurationNode biomesNode = node.getNode("BIOMES");
            if (biomesNode.isVirtual()) {
                throw new IllegalArgumentException("BIOMES node does not exist!");
            }
            whitelistMode = whitelistModeNode.getBoolean(true);
            biomes = new ArrayList<>();
            for (ConfigurationNode biomeNode : biomesNode.getChildrenList()) {
                biomes.add(biomeNode.getValue(TypeToken.of(BiomeType.class)));
            }
        } catch (Exception e) {
            throw new SSOCreationException("Failed to create Biome Key!", e);
        }
    }

    public BiomeKey(String type, Optional<String> id, boolean doNotWithdraw,
                    boolean whitelistMode, List<BiomeType> biomes) {
        super(type, id, doNotWithdraw);
        this.whitelistMode = whitelistMode;
        this.biomes = biomes;
    }

    @Override
    public void withdraw(Player player, int amount, boolean force) {
    }

    @Override
    public int get(Player player) {
        if (whitelistMode) {
            return biomes.contains(player.getLocation().getBiome()) ? 1 : 0;
        } else {
            return biomes.contains(player.getLocation().getBiome()) ? 0 : 1;
        }
    }
}
