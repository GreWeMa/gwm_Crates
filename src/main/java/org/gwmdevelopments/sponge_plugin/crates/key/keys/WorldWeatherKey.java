package org.gwmdevelopments.sponge_plugin.crates.key.keys;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.gwmdevelopments.sponge_plugin.crates.key.AbstractKey;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.weather.Weather;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WorldWeatherKey extends AbstractKey {

    private boolean whitelistMode;
    private List<Weather> weathers;

    public WorldWeatherKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode whitelistModeNode = node.getNode("WHITELIST_MODE");
            ConfigurationNode weathersNode = node.getNode("WEATHERS");
            if (weathersNode.isVirtual()) {
                throw new IllegalArgumentException("WEATHERS node does not exist!");
            }
            whitelistMode = whitelistModeNode.getBoolean(true);
            weathers = new ArrayList<>();
            for (ConfigurationNode weatherNode : weathersNode.getChildrenList()) {
                weathers.add(weatherNode.getValue(TypeToken.of(Weather.class)));
            }
        } catch (Exception e) {
            throw new SSOCreationException("Failed to create World Weather Key!", e);
        }
    }

    public WorldWeatherKey(String type, Optional<String> id, boolean doNotWithdraw,
                           boolean whitelistMode, List<Weather> weathers) {
        super(type, id, doNotWithdraw);
        this.whitelistMode = whitelistMode;
        this.weathers = weathers;
    }

    @Override
    public void withdraw(Player player, int amount, boolean force) {
    }

    @Override
    public int get(Player player) {
        if (whitelistMode) {
            return weathers.contains(player.getLocation().getExtent().getWeather()) ? 1 : 0;
        } else {
            return weathers.contains(player.getLocation().getExtent().getWeather()) ? 0 : 1;
        }
    }
}
