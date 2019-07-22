package org.gwmdevelopments.sponge_plugin.crates.key.keys;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.gwmdevelopments.sponge_plugin.crates.key.Key;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.weather.Weather;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class WorldWeatherKey extends Key {

    public static final String TYPE = "WORLD-WEATHER";

    private final boolean whitelistMode;
    private final List<Weather> weathers;

    public WorldWeatherKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode whitelistModeNode = node.getNode("WHITELIST_MODE");
            ConfigurationNode weathersNode = node.getNode("WEATHERS");
            if (weathersNode.isVirtual()) {
                throw new IllegalArgumentException("WEATHERS node does not exist!");
            }
            whitelistMode = whitelistModeNode.getBoolean(true);
            List<Weather> tempWeathers = new ArrayList<>();
            for (ConfigurationNode weatherNode : weathersNode.getChildrenList()) {
                tempWeathers.add(weatherNode.getValue(TypeToken.of(Weather.class)));
            }
            weathers = Collections.unmodifiableList(tempWeathers);
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public WorldWeatherKey(Optional<String> id, boolean doNotWithdraw,
                           boolean whitelistMode, List<Weather> weathers) {
        super(id, doNotWithdraw);
        this.whitelistMode = whitelistMode;
        this.weathers = weathers;
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
            return weathers.contains(player.getLocation().getExtent().getWeather()) ? 1 : 0;
        } else {
            return weathers.contains(player.getLocation().getExtent().getWeather()) ? 0 : 1;
        }
    }

    public boolean isWhitelistMode() {
        return whitelistMode;
    }

    public List<Weather> getWeathers() {
        return weathers;
    }
}
