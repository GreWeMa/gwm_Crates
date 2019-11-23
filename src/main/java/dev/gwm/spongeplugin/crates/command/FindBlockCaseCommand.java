package dev.gwm.spongeplugin.crates.command;

import com.flowpowered.math.vector.Vector3d;
import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.caze.BlockCase;
import dev.gwm.spongeplugin.crates.superobject.caze.base.Case;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.library.util.Language;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FindBlockCaseCommand implements CommandExecutor {

    private final Language language;

    public FindBlockCaseCommand(Language language) {
        this.language = language;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {
        if (!(source instanceof Player)) {
            source.sendMessages(language.getTranslation("COMMAND_EXECUTABLE_ONLY_BY_PLAYER", source));
            return CommandResult.empty();
        }
        Player player = (Player) source;
        Manager manager = args.<Manager>getOne(Text.of("manager")).get();
        String managerId = manager.id();
        Optional<Integer> optionalIndex = args.getOne("index");
        if (!player.hasPermission("gwm_crates.command.findblockcase." + managerId)) {
            player.sendMessages(language.getTranslation("HAVE_NOT_PERMISSION", player));
            return CommandResult.empty();
        }
        Case caze = manager.getCase();
        if (!(caze instanceof BlockCase)) {
            player.sendMessages(language.getTranslation("CASE_IS_NOT_BLOCK", Arrays.asList(
                    new ImmutablePair<>("MANAGER_ID", manager.id()),
                    new ImmutablePair<>("MANAGER_NAME", manager.getName()),
                    new ImmutablePair<>("CASE_TYPE", manager.getCase().type())
            ), player));
            return CommandResult.empty();
        }
        BlockCase blockCase = (BlockCase) caze;
        List<Location<World>> locations = blockCase.getLocations();
        if (optionalIndex.isPresent()) {
            int teleportLocationIndex = optionalIndex.get();
            if (teleportLocationIndex < 0 || teleportLocationIndex >= locations.size()) {
                player.sendMessages(language.getTranslation("WRONG_BLOCK_CASE_LOCATION_INDEX", Arrays.asList(
                        new ImmutablePair<>("MANAGER_ID", manager.id()),
                        new ImmutablePair<>("MANAGER_NAME", manager.getName()),
                        new ImmutablePair<>("USED_INDEX", teleportLocationIndex),
                        new ImmutablePair<>("MAX_INDEX", locations.size() - 1)
                ), player));
                return CommandResult.empty();
            }
            player.setLocationAndRotation(locations.get(teleportLocationIndex).add(0.5, 2.0, 0.5), new Vector3d(90, 0, 0));
        }
        for (Location<World> location : locations) {
            new ParticleRunnable(location).run();
        }
        player.sendMessages(language.getTranslation("SUCCESSFULLY_HIGHLIGHTED_BLOCK_CASE", Arrays.asList(
                new ImmutablePair<>("MANAGER_ID", manager.id()),
                new ImmutablePair<>("MANAGER_NAME", manager.getName())
        ), player));
        return CommandResult.success();
    }

    private static class ParticleRunnable implements Runnable {

        private static final int DELAY = 1;
        private static final int AMOUNT = 20;
        private static final ParticleEffect EFFECT = ParticleEffect.builder().
                type(ParticleTypes.REDSTONE_DUST).
                build();

        private final Location<World> location;
        private int amount = 0;

        public ParticleRunnable(Location<World> location) {
            this.location = location;
        }

        @Override
        public void run() {
            World world = location.getExtent();
            Vector3d position = location.getPosition();
            for (double x = 0; x <= 1; x += 0.2) {
                for (double z = 0; z <= 1; z += 0.2) {
                    for (double y = 1; y >= 0; y -= 0.2) {
                        if (x == 0 || x == 1 || z == 0 || z == 1 || y == 1 || y == 0) { //Only borders, not insides
                            world.spawnParticles(EFFECT, position.add(x, y, z));
                        }
                    }
                }
            }
            if (++amount <= AMOUNT) {
                Sponge.getScheduler().createTaskBuilder().
                        delayTicks(DELAY).
                        execute(this).
                        submit(GWMCrates.getInstance());
            }
        }
    }
}
