package org.gwmdevelopments.sponge_plugin.crates.command;

import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ManagerCommandElement extends CommandElement {

    public ManagerCommandElement(@Nullable Text key) {
        super(key);
    }

    @Override
    public Manager parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String managerId = args.next();
        return GWMCratesUtils.getManager(managerId).orElseThrow(() ->
                new ArgumentParseException(GWMCrates.getInstance().getLanguage().getText("MANAGER_NOT_EXIST",
                        new Pair<>("%MANAGER%", managerId)), managerId, 0));
    }

    @Override
    public List<String> complete(CommandSource source, CommandArgs args, CommandContext context) {
        Optional<String> optionalArg = args.nextIfPresent();
        if (optionalArg.isPresent()) {
            String arg = optionalArg.get().toLowerCase();
            List<String> suggestions = new ArrayList<>();
            for (Manager manager : GWMCrates.getInstance().getCreatedManagers()) {
                String managerId = manager.getId();
                if (managerId.toLowerCase().startsWith(arg)) {
                    suggestions.add(managerId);
                }
            }
            return suggestions;
        } else {
            return Collections.emptyList();
        }
    }
}
