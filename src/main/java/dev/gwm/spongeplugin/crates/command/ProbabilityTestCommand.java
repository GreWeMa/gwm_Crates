package dev.gwm.spongeplugin.crates.command;

import dev.gwm.spongeplugin.crates.superobject.drop.base.Drop;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.library.util.GWMLibraryUtils;
import dev.gwm.spongeplugin.library.util.Language;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class ProbabilityTestCommand implements CommandExecutor {

    private final Language language;

    public ProbabilityTestCommand(Language language) {
        this.language = language;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {
        Manager manager = args.<Manager>getOne(Text.of("manager")).get();
        String managerId = manager.id();
        int amount = args.<Integer>getOne(Text.of("amount")).get();
        Player player = args.<Player>getOne(Text.of("player")).get();
        boolean fake = args.hasAny("f");
        if (!source.hasPermission("gwm_crates.command.probability_test." + managerId)) {
            source.sendMessages(language.getTranslation("HAVE_NOT_PERMISSION", source));
            return CommandResult.empty();
        }
        Map<Drop, Integer> map = new HashMap<>();
        for (int i = 0; i < amount; i++) {
            Drop drop = (Drop) manager.getRandomManager().choose(manager.getDrops(), player, fake);
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
            String id = drop.id();
            String customName = drop.getCustomName().orElse(id);
            if (iterator.hasNext()) {
                dropsBuilder.append(GWMLibraryUtils.joinString(language.getSimpleTranslation("PROBABILITY_TEST_LIST_FORMAT", Arrays.asList(
                        new ImmutablePair<>("DROP_ID", id),
                        new ImmutablePair<>("DROP_CUSTOM_NAME", customName),
                        new ImmutablePair<>("RESULT_VALUE", resultValue),
                        new ImmutablePair<>("RESULT_PERCENTAGE", String.format("%.2f", resultPercentage))
                ))));
            } else {
                dropsBuilder.append(GWMLibraryUtils.joinString(language.getSimpleTranslation("LAST_PROBABILITY_TEST_LIST_FORMAT", Arrays.asList(
                        new ImmutablePair<>("DROP_ID", id),
                        new ImmutablePair<>("DROP_CUSTOM_NAME", customName),
                        new ImmutablePair<>("RESULT_VALUE", resultValue),
                        new ImmutablePair<>("RESULT_PERCENTAGE", String.format("%.2f", resultPercentage))
                ))));
            }
        }
        source.sendMessages(language.getTranslation("PROBABILITY_TEST_MESSAGE", Arrays.asList(
                new ImmutablePair<>("RESULTS", dropsBuilder.toString()),
                new ImmutablePair<>("MANAGER_NAME", manager.getName()),
                new ImmutablePair<>("MANAGER_ID", manager.id()),
                new ImmutablePair<>("PLAYER_NAME", player.getName()),
                new ImmutablePair<>("PLAYER_UUID", player.getName()),
                new ImmutablePair<>("AMOUNT", amount),
                new ImmutablePair<>("FAKE", fake)
        ), source));
        return CommandResult.success();
    }
}
