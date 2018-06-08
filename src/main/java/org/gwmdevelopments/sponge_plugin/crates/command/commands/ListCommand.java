package org.gwmdevelopments.sponge_plugin.crates.command.commands;

import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;

import java.util.Iterator;

public class ListCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Iterator<Manager> managerIterator = GWMCrates.getInstance().getCreatedManagers().iterator();
        Text.Builder messageBuilder = Text.builder();
        boolean hasNext = managerIterator.hasNext();
        while (hasNext) {
            Manager next = managerIterator.next();
            if (hasNext = managerIterator.hasNext()) {
                messageBuilder.append(GWMCrates.getInstance().getLanguage().getText("MANAGER_LIST_FORMAT",
                        new Pair<>("%MANAGER_ID%", next.getId()),
                        new Pair<>("%MANAGER_NAME%", next.getName())));
            } else {
                messageBuilder.append(GWMCrates.getInstance().getLanguage().getText("LAST_MANAGER_LIST_FORMAT",
                        new Pair<>("%MANAGER_ID%", next.getId()),
                        new Pair<>("%MANAGER_NAME%", next.getName())));
            }
        }
        if (GWMCrates.getInstance().getLanguage().exists("MANAGER_LIST_HEADER")) {
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("MANAGER_LIST_HEADER"));
        }
        src.sendMessage(messageBuilder.build());
        if (GWMCrates.getInstance().getLanguage().exists("MANAGER_LIST_FOOTER")) {
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("MANAGER_LIST_FOOTER"));
        }
        return CommandResult.success();
    }
}
