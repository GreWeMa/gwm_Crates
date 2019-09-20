package dev.gwm.spongeplugin.crates.superobject.drop;

import com.google.common.reflect.TypeToken;
import dev.gwm.spongeplugin.crates.superobject.drop.base.AbstractDrop;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.utils.DefaultRandomableData;
import dev.gwm.spongeplugin.library.utils.GiveableData;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class CommandDrop extends AbstractDrop {

    public static final String TYPE = "COMMAND";

    private final List<String> consoleCommands;
    private final List<String> playerCommands;

    public CommandDrop(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode consoleCommandsNode = node.getNode("CONSOLE_COMMANDS");
            ConfigurationNode playerCommandsNode = node.getNode("PLAYER_COMMANDS");
            if (!consoleCommandsNode.isVirtual()) {
                consoleCommands = Collections.unmodifiableList(consoleCommandsNode.getList(TypeToken.of(String.class)));
            } else {
                consoleCommands = Collections.emptyList();
            }
            if (!playerCommandsNode.isVirtual()) {
                playerCommands = Collections.unmodifiableList(playerCommandsNode.getList(TypeToken.of(String.class)));
            } else {
                playerCommands = Collections.emptyList();
            }
            if (consoleCommands.isEmpty() && playerCommands.isEmpty()) {
                throw new RuntimeException("Both Console Commands and Player Commands are empty! At least one Command is required!");
            }
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public CommandDrop(String id,
                       GiveableData giveableData,
                       Optional<ItemStack> dropItem, Optional<String> customName, boolean showInPreview,
                       DefaultRandomableData defaultRandomableData,
                       List<String> consoleCommands, List<String> playerCommands) {
        super(id, giveableData, dropItem, customName, showInPreview, defaultRandomableData);
        this.consoleCommands = Collections.unmodifiableList(consoleCommands);
        this.playerCommands = Collections.unmodifiableList(playerCommands);
        if (consoleCommands.isEmpty() && playerCommands.isEmpty()) {
            throw new RuntimeException("Both Console Commands and Player Commands are empty! At least one Command is required!");
        }
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void give(Player player, int amount) {
        consoleCommands.
                stream().
                map(command -> command.
                        replace("%PLAYER_NAME%", player.getName()).
                        replace("%PLAYER_UUID%", player.getUniqueId().toString())).
                forEach(command -> Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command));
        playerCommands.
                stream().
                map(command -> command.
                        replace("%PLAYER_NAME%", player.getName()).
                        replace("%PLAYER_UUID%", player.getUniqueId().toString())).
                forEach(command -> Sponge.getCommandManager().process(player, command));
    }

    public List<String> getConsoleCommands() {
        return consoleCommands;
    }

    public List<String> getPlayerCommands() {
        return playerCommands;
    }
}
