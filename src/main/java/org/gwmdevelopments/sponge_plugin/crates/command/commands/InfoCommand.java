package org.gwmdevelopments.sponge_plugin.crates.command.commands;

import me.rojo8399.placeholderapi.PlaceholderService;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.drop.Drop;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObject;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Optional;

public class InfoCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        Manager manager = args.<Manager>getOne(Text.of("manager")).get();
        String managerId = manager.getId();
        if (!src.hasPermission("gwm_crates.command.info." + managerId)) {
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION", src, null));
            return CommandResult.success();
        }
        Optional<Text> optionalCustomInfo = manager.getCustomInfo();
        if (optionalCustomInfo.isPresent()) {
            src.sendMessage(optionalCustomInfo.get());
            return CommandResult.success();
        }
        StringBuilder dropsBuilder = new StringBuilder();
        List<Drop> drops = manager.getDrops();
        for (int i = 0; i < drops.size(); i++) {
            Drop drop = drops.get(i);
            if (i != drops.size() - 1) {
                dropsBuilder.append(GWMCrates.getInstance().getLanguage().getPhrase("DROP_LIST_FORMAT",
                        new Pair<>("%ID%", drop.getId().orElse("Unknown ID"))));
            } else {
                dropsBuilder.append(GWMCrates.getInstance().getLanguage().getPhrase("LAST_DROP_LIST_FORMAT",
                        new Pair<>("%ID%", drop.getId().orElse("Unknown ID"))));
            }
        }
        src.sendMessages(GWMCrates.getInstance().getLanguage().getTextList("MANAGER_INFO_MESSAGE", src, null,
                new Pair<>("%MANAGER_ID%", manager.getId()),
                new Pair<>("%MANAGER_NAME%", manager.getName()),
                new Pair<>("%CASE_TYPE%", manager.getCase().getType()),
                new Pair<>("%KEY_TYPE%", manager.getKey().getType()),
                new Pair<>("%OPEN_MANAGER_TYPE%", manager.getOpenManager().getType()),
                new Pair<>("%PREVIEW_TYPE%", manager.getPreview().
                        map(SuperObject::getType).orElse("No preview")),
                new Pair<>("%SEND_OPEN_MESSAGE%", manager.isSendOpenMessage()),
                new Pair<>("%CUSTOM_OPEN_MESSAGE%", manager.getCustomOpenMessage().
                        orElse("No custom open message")),
                new Pair<>("%DROPS%", dropsBuilder.toString())));
        return CommandResult.success();
    }
}
