package org.gwmdevelopments.sponge_plugin.crates.drop.drops;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.drop.Drop;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.*;

public final class CommandsDrop extends Drop {

    public static final String TYPE = "COMMANDS";

    private final List<ExecutableCommand> executableCommands;

    public CommandsDrop(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode commandsNode = node.getNode("COMMANDS");
            if (commandsNode.isVirtual()) {
                throw new IllegalArgumentException("COMMANDS node does not exist!");
            }
            List<ExecutableCommand> tempExecutableCommands = new ArrayList<>();
            for (ConfigurationNode commandNode : commandsNode.getChildrenList()) {
                tempExecutableCommands.add(GWMCratesUtils.parseCommand(commandNode));
            }
            if (tempExecutableCommands.isEmpty()) {
                throw new IllegalArgumentException("No commands are configured! At least one command is required!");
            }
            executableCommands = Collections.unmodifiableList(tempExecutableCommands);
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public CommandsDrop(Optional<String> id, Optional<BigDecimal> price, Optional<Currency> sellCurrency, Optional<ItemStack> dropItem, Optional<String> customName, boolean showInPreview, Optional<Integer> level, Optional<Integer> fakeLevel, Map<String, Integer> permissionLevels, Map<String, Integer> permissionFakeLevels, Optional<Long> weight, Optional<Long> fakeWeight, Map<String, Long> permissionWeights, Map<String, Long> permissionFakeWeights,
                        List<ExecutableCommand> executableCommands) {
        super(id, price, sellCurrency, dropItem, customName, showInPreview, level, fakeLevel, permissionLevels, permissionFakeLevels, weight, fakeWeight, permissionWeights, permissionFakeWeights);
        this.executableCommands = executableCommands;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void give(Player player, int amount) {
        ConsoleSource consoleSource = Sponge.getServer().getConsole();
        for (int i = 0; i < amount; i++) {
            for (ExecutableCommand executableCommand : executableCommands) {
                String command = executableCommand.getCommand().replace("%PLAYER%", player.getName());
                boolean console = executableCommand.isConsole();
                Sponge.getCommandManager().process(console ? consoleSource : player, command);
            }
        }
    }

    public static class ExecutableCommand {

        private final String command;
        private final boolean console;

        public ExecutableCommand(String command, boolean console) {
            this.command = command;
            this.console = console;
        }

        public String getCommand() {
            return command;
        }

        public boolean isConsole() {
            return console;
        }
    }

    public List<ExecutableCommand> getExecutableCommands() {
        return executableCommands;
    }
}
