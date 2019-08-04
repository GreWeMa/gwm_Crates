package dev.gwm.spongeplugin.crates.superobject.keys;

import com.google.common.reflect.TypeToken;
import dev.gwm.spongeplugin.crates.exception.SSOCreationException;
import dev.gwm.spongeplugin.crates.superobject.Key;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.weather.Weather;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class WorldWeatherKey extends Key {

    public static final String TYPE = "WORLD-WEATHER";

    private final boolean whitelistMode;
    private final List<Weather> weathers;
    private final Optional<World> world;

    public WorldWeatherKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode whitelistModeNode = node.getNode("WHITELIST_MODE");
            ConfigurationNode weathersNode = node.getNode("WEATHERS");
            ConfigurationNode worldNode = node.getNode("WORLD");
            if (weathersNode.isVirtual()) {
                throw new IllegalArgumentException("WEATHERS node does not exist!");
            }
            whitelistMode = whitelistModeNode.getBoolean(true);
            List<Weather> tempWeathers = new ArrayList<>();
            for (ConfigurationNode weatherNode : weathersNode.getChildrenList()) {
                tempWeathers.add(weatherNode.getValue(TypeToken.of(Weather.class)));
            }
            weathers = Collections.unmodifiableList(tempWeathers);
            if (!worldNode.isVirtual()) {
                String worldName = worldNode.getString();
                world = Sponge.getServer().getWorld(worldName);
                if (!world.isPresent()) {
                    throw new IllegalArgumentException("WORLD \"" + worldNode + "\" does not exist!");
                }
            } else {
                world = Optional.empty();
            }
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public WorldWeatherKey(Optional<String> id, boolean doNotWithdraw,
                           boolean whitelistMode, List<Weather> weathers, Optional<World> world) {
        super(id, doNotWithdraw);
        this.whitelistMode = whitelistMode;
        this.weathers = weathers;
        this.world = world;
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
            return weathers.contains(world.orElse(player.getLocation().getExtent()).getWeather()) ? 1 : 0;
        } else {
            return weathers.contains(world.orElse(player.getLocation().getExtent()).getWeather()) ? 0 : 1;
        }
    }

    public boolean isWhitelistMode() {
        return whitelistMode;
    }

    public List<Weather> getWeathers() {
        return weathers;
    }

    public Optional<World> getWorld() {
        return world;
    }
}
