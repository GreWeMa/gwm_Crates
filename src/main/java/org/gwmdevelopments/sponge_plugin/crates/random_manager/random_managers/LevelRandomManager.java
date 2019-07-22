package org.gwmdevelopments.sponge_plugin.crates.random_manager.random_managers;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.drop.Drop;
import org.gwmdevelopments.sponge_plugin.crates.drop.drops.MultiDrop;
import org.gwmdevelopments.sponge_plugin.crates.random_manager.RandomManager;
import org.gwmdevelopments.sponge_plugin.library.utils.GWMLibraryUtils;
import org.spongepowered.api.entity.living.player.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class LevelRandomManager extends RandomManager {

    public static final String TYPE = "LEVEL";

    public LevelRandomManager(ConfigurationNode node) {
        super(node);
    }

    public LevelRandomManager(Optional<String> id) {
        super(id);
    }

    @Override
    public Drop choose(Iterable<Drop> iterable, Player player, boolean fake) {
        Map<Integer, List<LevelRandomable>> sortedRandomables = new HashMap<>();
        for (LevelRandomable randomable : iterable) {
            boolean foundByPermission = false;
            for (Map.Entry<String, Integer> entry : fake ?
                    randomable.getPermissionFakeLevels().entrySet() :
                    randomable.getPermissionLevels().entrySet()) {
                String permission = entry.getKey();
                int permissionLevel = entry.getValue();
                if (player.hasPermission(permission)) {
                    if (sortedRandomables.containsKey(permissionLevel)) {
                        sortedRandomables.get(permissionLevel).add(randomable);
                        foundByPermission = true;
                        break;
                    } else {
                        List<LevelRandomable> list = new ArrayList<>();
                        list.add(randomable);
                        sortedRandomables.put(permissionLevel, list);
                        foundByPermission = true;
                        break;
                    }
                }
            }
            if (!foundByPermission) {
                int level = fake ?
                        randomable.getFakeLevel().orElse(randomable.getLevel().orElse(1)) :
                        randomable.getLevel().orElse(1);
                if (sortedRandomables.containsKey(level)) {
                    sortedRandomables.get(level).add(randomable);
                } else {
                    List<LevelRandomable> list = new ArrayList<>();
                    list.add(randomable);
                    sortedRandomables.put(level, list);
                }
            }
        }
        int level;
        while (!sortedRandomables.containsKey(level = GWMLibraryUtils.getRandomIntLevel())) {
        }
        List<LevelRandomable> actualRandomables = sortedRandomables.get(level);
        LevelRandomable randomable = actualRandomables.get(ThreadLocalRandom.current().nextInt(actualRandomables.size()));
        if (randomable instanceof MultiDrop && ((MultiDrop) randomable).isPrefetch() && !((MultiDrop) randomable).isGiveAll()) {
            return choose(((MultiDrop) randomable).getDrops(), player, fake);
        }
        return (Drop) randomable;
    }

    @Override
    public String type() {
        return TYPE;
    }

    public interface LevelRandomable {

        Optional<Integer> getLevel();

        Optional<Integer> getFakeLevel();

        Map<String, Integer> getPermissionLevels();

        Map<String, Integer> getPermissionFakeLevels();
    }
}
