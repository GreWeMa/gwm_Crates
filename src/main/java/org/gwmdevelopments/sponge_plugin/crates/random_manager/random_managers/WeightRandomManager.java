package org.gwmdevelopments.sponge_plugin.crates.random_manager.random_managers;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.drop.Drop;
import org.gwmdevelopments.sponge_plugin.crates.drop.drops.MultiDrop;
import org.gwmdevelopments.sponge_plugin.crates.random_manager.RandomManager;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public final class WeightRandomManager extends RandomManager {

    public static final String TYPE = "WEIGHT";

    public WeightRandomManager(ConfigurationNode node) {
        super(node);
    }

    public WeightRandomManager(Optional<String> id) {
        super(id);
    }

    @Override
    public Drop choose(Iterable<Drop> iterable, Player player, boolean fake) {
        List<Pair<Long, WeightRandomable>> list = new ArrayList<>();
        for (WeightRandomable randomable : iterable) {
            boolean foundByPermission = false;
            for (Map.Entry<String, Long> entry : fake ?
                    randomable.getPermissionFakeWeights().entrySet() :
                    randomable.getPermissionWeights().entrySet()) {
                String permission = entry.getKey();
                long permissionLevel = entry.getValue();
                if (player.hasPermission(permission)) {
                    list.add(new Pair<>(permissionLevel, randomable));
                    foundByPermission = true;
                }
            }
            if (!foundByPermission) {
                long weight = fake ?
                        randomable.getFakeWeight().orElse(randomable.getWeight().orElse(1L)) :
                        randomable.getWeight().orElse(1L);
                list.add(new Pair<>(weight, randomable));
            }

        }
        long sum = list.stream().reduce(0L, (l, pair) -> l + pair.getKey(), Long::sum);
        long randomPosition = ThreadLocalRandom.current().nextLong(sum) + 1;
        long weightSum = 0L;
        for (Pair<Long, WeightRandomable> pair : list) {
            weightSum += pair.getKey();
            if (weightSum >= randomPosition) {
                WeightRandomable randomable = pair.getValue();
                if (randomable instanceof MultiDrop && ((MultiDrop) randomable).isPrefetch() && !((MultiDrop) randomable).isGiveAll()) {
                    return choose(((MultiDrop) randomable).getDrops(), player, fake);
                }
                return (Drop) randomable;
            }
        }
        throw new AssertionError("Should never happen because sum >= randomPosition, and at the end of the loop sum == weightSum");
    }

    @Override
    public String type() {
        return TYPE;
    }

    public interface WeightRandomable {

        Optional<Long> getWeight();

        Optional<Long> getFakeWeight();

        Map<String, Long> getPermissionWeights();

        Map<String, Long> getPermissionFakeWeights();
    }
}
