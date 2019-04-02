package org.gwmdevelopments.sponge_plugin.crates.command;

import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ManagerCommandElement extends CommandElement {

    public ManagerCommandElement(@Nullable Text key) {
        super(key);
    }

    @Override
    public Manager parseValue(CommandSource src, CommandArgs args) throws ArgumentParseException {
        String managerId = args.next();
        return GWMCratesUtils.getManager(managerId).orElseThrow(() ->
                new ArgumentParseException(GWMCrates.getInstance().getLanguage().getText("MANAGER_NOT_EXIST", src, null,
                        new Pair<>("%MANAGER%", managerId)), managerId, 0));
    }

    @Override
    public List<String> complete(CommandSource source, CommandArgs args, CommandContext context) {
        Optional<String> optionalArg = args.nextIfPresent();
        if (optionalArg.isPresent()) {
            String arg = optionalArg.get().toLowerCase();
            return GWMCrates.getInstance().getCreatedManagers().stream().
                    filter(manager ->
                            source.hasPermission("gwm_crates.command.tab_completion.manager." + manager.getId().toLowerCase()) &&
                            manager.getId().toLowerCase().startsWith(arg)).
                    map(Manager::getId).
                    collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
