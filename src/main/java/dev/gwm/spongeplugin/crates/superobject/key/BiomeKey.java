package dev.gwm.spongeplugin.crates.superobject.key;

import com.google.common.reflect.TypeToken;
import dev.gwm.spongeplugin.crates.superobject.key.base.AbstractKey;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.biome.BiomeType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BiomeKey extends AbstractKey {

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
            if (tempBiomes.isEmpty()) {
                throw new IllegalArgumentException("No Biomes are configured! At least one Biome is required!");
            }
            biomes = Collections.unmodifiableList(tempBiomes);
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public BiomeKey(String id, boolean doNotWithdraw,
                    boolean whitelistMode, List<BiomeType> biomes) {
        super(id, doNotWithdraw);
        this.whitelistMode = whitelistMode;
        if (biomes.isEmpty()) {
            throw new IllegalArgumentException("No Biomes are configured! At least one Biome is required!");
        }
        this.biomes = Collections.unmodifiableList(biomes);
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
