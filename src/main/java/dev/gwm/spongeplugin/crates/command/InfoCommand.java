package dev.gwm.spongeplugin.crates.command;

import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.crates.utils.GWMCratesUtils;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.utils.Language;
import dev.gwm.spongeplugin.library.utils.Pair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class InfoCommand implements CommandExecutor {

    private final Language language;

    public InfoCommand(Language language) {
        this.language = language;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {
        Manager manager = args.<Manager>getOne(Text.of("manager")).get();
        String managerId = manager.getId();
        if (!source.hasPermission("gwm_crates.command.info." + managerId)) {
            source.sendMessages(language.getTranslation("HAVE_NOT_PERMISSION", source));
            return CommandResult.empty();
        }
        Optional<List<Text>> optionalCustomInfo = manager.getCustomMessageData().getCustomInfo();
        if (optionalCustomInfo.isPresent()) {
            source.sendMessages(optionalCustomInfo.get());
            return CommandResult.success();
        }
        String formattedDrops = GWMCratesUtils.formatDrops(manager.getDrops());
        source.sendMessages(language.getTranslation("MANAGER_INFO", Arrays.asList(
                new Pair<>("MANAGER_ID", manager.getId()),
                new Pair<>("MANAGER_NAME", manager.getName()),
                new Pair<>("CASE_TYPE", manager.getCase().type()),
                new Pair<>("KEY_TYPE", manager.getKey().type()),
                new Pair<>("OPEN_MANAGER_TYPE", manager.getOpenManager().type()),
                new Pair<>("PREVIEW_TYPE", manager.getPreview().
                        map(SuperObject::type).orElse("No preview")),
                new Pair<>("DROPS", formattedDrops)
        ), source));
        return CommandResult.success();
    }
}
