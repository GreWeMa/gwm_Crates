package dev.gwm.spongeplugin.crates.command.commands;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.Drop;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ProbabilityTestCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        Manager manager = args.<Manager>getOne(Text.of("manager")).get();
        String managerId = manager.getId();
        int amount = args.<Integer>getOne(Text.of("amount")).get();
        Player player = args.<Player>getOne(Text.of("player")).get();
        boolean fake = args.<Boolean>getOne(Text.of("fake")).orElse(false);
        if (!src.hasPermission("gwm_crates.command.probability_test." + managerId)) {
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION", src, null));
            return CommandResult.success();
        }
        Map<Drop, Integer> map = new HashMap<>();
        for (int i = 0; i < amount; i++) {
            Drop drop = manager.getRandomManager().choose(manager.getDrops(), player, fake);
            if (map.containsKey(drop)) {
                map.put(drop, map.get(drop) + 1);
            } else {
                map.put(drop, 1);
            }
        }
        StringBuilder dropsBuilder = new StringBuilder();
        Iterator<Map.Entry<Drop, Integer>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Drop, Integer> entry = iterator.next();
            Drop drop = entry.getKey();
            int resultValue = entry.getValue();
            double resultPercentage = (resultValue / (amount * 1.0D)) * 100;
            String id = drop.id().orElse("Unknown ID");
            String customName = drop.getCustomName().orElse(id);
            if (iterator.hasNext()) {
                dropsBuilder.append(GWMCrates.getInstance().getLanguage().getPhrase("PROBABILITY_TEST_LIST_FORMAT",
                        new Pair<>("%ID%", id),
                        new Pair<>("%CUSTOM_NAME%", customName),
                        new Pair<>("%RESULT_VALUE%", resultValue),
                        new Pair<>("%RESULT_PERCENTAGE%", String.format("%.2f", resultPercentage))));
            } else {
                dropsBuilder.append(GWMCrates.getInstance().getLanguage().getPhrase("LAST_PROBABILITY_TEST_LIST_FORMAT",
                        new Pair<>("%ID%", id),
                        new Pair<>("%CUSTOM_NAME%", customName),
                        new Pair<>("%RESULT_VALUE%", resultValue),
                        new Pair<>("%RESULT_PERCENTAGE%", String.format("%.2f", resultPercentage))));
            }
        }
        src.sendMessage(GWMCrates.getInstance().getLanguage().getText("PROBABILITY_TEST_MESSAGE", src, null,
                new Pair<>("%RESULTS%", dropsBuilder.toString()),
                new Pair<>("%MANAGER_NAME%", manager.getName()),
                new Pair<>("%MANAGER_ID%", manager.getId()),
                new Pair<>("%PLAYER_NAME%", player.getName()),
                new Pair<>("%AMOUNT%", amount),
                new Pair<>("%FAKE%", fake)));
        return CommandResult.success();
    }
}
