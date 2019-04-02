package org.gwmdevelopments.sponge_plugin.crates.command;

import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.util.*;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class SuperObjectCommandElement extends CommandElement {

    private Optional<SuperObjectType> type = Optional.empty();
    private boolean onlyGiveable = false;

    public SuperObjectCommandElement(@Nullable Text key) {
        super(key);
    }

    public SuperObjectCommandElement(@Nullable Text key, Optional<SuperObjectType> type, boolean onlyGiveable) {
        super(key);
        this.type = type;
        this.onlyGiveable = onlyGiveable;
    }

    @Override
    public SuperObject parseValue(CommandSource src, CommandArgs args) throws ArgumentParseException {
        String superObjectId = args.next();
        return GWMCratesUtils.getSavedSuperObject(superObjectId).orElseThrow(() ->
                new ArgumentParseException(GWMCrates.getInstance().getLanguage().getText("SSO_NOT_EXIST", src, null,
                        new Pair<>("%SUPER_OBJECT%", superObjectId)), superObjectId, 0));
    }

    @Override
    public List<String> complete(CommandSource source, CommandArgs args, CommandContext context) {
        Optional<String> optionalArg = args.nextIfPresent();
        if (optionalArg.isPresent()) {
            String arg = optionalArg.get().toLowerCase();
            return GWMCrates.getInstance().getSavedSuperObjects().entrySet().stream().
                    filter(entry ->
                            source.hasPermission("gwm_crates.command.tab_completion.sso." + entry.getValue().id().get().toLowerCase()) &&
                            entry.getKey().getValue().toLowerCase().startsWith(arg) &&
                            !type.isPresent() || type.get().equals(entry.getKey().getKey()) &&
                            !onlyGiveable || entry.getValue() instanceof Giveable).
                    map(entry -> entry.getKey().getValue().toLowerCase()).
                    collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
