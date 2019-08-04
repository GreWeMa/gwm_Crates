package dev.gwm.spongeplugin.crates.superobject.keys;

import com.google.common.reflect.TypeToken;
import dev.gwm.spongeplugin.crates.exception.SSOCreationException;
import dev.gwm.spongeplugin.crates.superobject.Key;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.biome.BiomeType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class BiomeKey extends Key {

    public static final String TYPE = "BIOME";

    private final boolean whitelistMode;
    private final List<BiomeType> biomes;

    public BiomeKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode whitelistModeNode = node.getNode("WHITELIST_MODE");
            ConfigurationNode biomesNode = node.getNode("BIOMES");
            if (biomesNode.isVirtual()) {
                throw new IllegalArgumentException("BIOMES node does not exist!");
            }
            whitelistMode = whitelistModeNode.getBoolean(true);
            List<BiomeType> tempBiomes = new ArrayList<>();
            for (ConfigurationNode biomeNode : biomesNode.getChildrenList()) {
                tempBiomes.add(biomeNode.getValue(TypeToken.of(BiomeType.class)));
            }
            biomes = Collections.unmodifiableList(tempBiomes);
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public BiomeKey(Optional<String> id, boolean doNotWithdraw,
                    boolean whitelistMode, List<BiomeType> biomes) {
        super(id, doNotWithdraw);
        this.whitelistMode = whitelistMode;
        this.biomes = biomes;
    }

    @Override
    public String type() {
        return TYPE;
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

    public boolean isWhitelistMode() {
        return whitelistMode;
    }

    public List<BiomeType> getBiomes() {
        return biomes;
    }
}
