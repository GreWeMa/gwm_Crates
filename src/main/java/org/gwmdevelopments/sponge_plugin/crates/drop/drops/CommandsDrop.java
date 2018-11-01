package org.gwmdevelopments.sponge_plugin.crates.drop.drops;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.drop.AbstractDrop;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CommandsDrop extends AbstractDrop {

    private List<ExecutableCommand> executableCommands;

    public CommandsDrop(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode commandsNode = node.getNode("COMMANDS");
            if (commandsNode.isVirtual()) {
                throw new RuntimeException("COMMANDS node does not exist!");
            }
            executableCommands = new ArrayList<>();
            for (ConfigurationNode command_node : commandsNode.getChildrenList()) {
                executableCommands.add(GWMCratesUtils.parseCommand(command_node));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Commands Drop!", e);
        }
    }

    public CommandsDrop(Optional<String> id, Optional<BigDecimal> price, Optional<Currency> sellCurrency,
                        int level, Optional<ItemStack> dropItem, Optional<Integer> fakeLevel,
                        Map<String, Integer> permissionLevels, Map<String, Integer> permissionFakeLevels,
                        List<ExecutableCommand> executableCommands) {
        super("COMMANDS", id, price, sellCurrency, level, dropItem, fakeLevel, permissionLevels, permissionFakeLevels);
        this.executableCommands = executableCommands;
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

        private String command;
        private boolean console;

        public ExecutableCommand(String command, boolean console) {
            this.command = command;
            this.console = console;
        }

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public boolean isConsole() {
            return console;
        }

        public void setConsole(boolean console) {
            this.console = console;
        }
    }

    public List<ExecutableCommand> getExecutableCommands() {
        return executableCommands;
    }

    public void setExecutableCommands(List<ExecutableCommand> executableCommands) {
        this.executableCommands = executableCommands;
    }
}
