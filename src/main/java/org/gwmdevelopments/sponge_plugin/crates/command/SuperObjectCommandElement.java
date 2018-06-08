package org.gwmdevelopments.sponge_plugin.crates.command;

import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.crates.util.Giveable;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObject;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;

import javax.annotation.Nullable;
import java.util.*;

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
    public SuperObject parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String superObjectId = args.next();
        return GWMCratesUtils.getSavedSuperObject(superObjectId).orElseThrow(() ->
                new ArgumentParseException(GWMCrates.getInstance().getLanguage().getText("SSO_NOT_EXIST",
                        new Pair<>("%SUPER_OBJECT%", superObjectId)), superObjectId, 0));
    }

    @Override
    public List<String> complete(CommandSource source, CommandArgs args, CommandContext context) {
        Optional<String> optionalArg = args.nextIfPresent();
        if (optionalArg.isPresent()) {
            String arg = optionalArg.get().toLowerCase();
            List<String> suggestions = new ArrayList<>();
            for (Map.Entry<Pair<SuperObjectType, String>, SuperObject> entry : GWMCrates.getInstance().getSavedSuperObjects().entrySet()) {
                Pair<SuperObjectType, String> pair = entry.getKey();
                SuperObjectType superObjectType = pair.getKey();
                String savedId = pair.getValue();
                SuperObject superObject = entry.getValue();
                if (savedId.toLowerCase().startsWith(arg)) {
                    if (type.isPresent() && !type.get().equals(superObjectType)) {
                        continue;
                    }
                    if (onlyGiveable && !(superObject instanceof Giveable)) {
                        continue;
                    }
                    suggestions.add(savedId);
                }
            }
            return suggestions;
        } else {
            return Collections.emptyList();
        }
    }
}
