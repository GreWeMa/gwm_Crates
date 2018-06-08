package org.gwmdevelopments.sponge_plugin.crates.command;

import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.command.commands.*;
import org.gwmdevelopments.sponge_plugin.crates.command.commands.buy.BuyCaseCommand;
import org.gwmdevelopments.sponge_plugin.crates.command.commands.buy.BuyDropCommand;
import org.gwmdevelopments.sponge_plugin.crates.command.commands.buy.BuyKeyCommand;
import org.gwmdevelopments.sponge_plugin.crates.command.commands.buy.BuySSOCommand;
import org.gwmdevelopments.sponge_plugin.crates.command.commands.give.GiveCaseCommand;
import org.gwmdevelopments.sponge_plugin.crates.command.commands.give.GiveDropCommand;
import org.gwmdevelopments.sponge_plugin.crates.command.commands.give.GiveKeyCommand;
import org.gwmdevelopments.sponge_plugin.crates.command.commands.give.GiveSSOCommand;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;

import java.util.Optional;

public class GWMCratesCommandUtils {

    public static void registerCommands() {
        CommandSpec helpCommand = CommandSpec.builder().
                description(Text.of("Help command")).
                executor(new HelpCommand()).
                build();
        CommandSpec guiCommand = CommandSpec.builder().
                description(Text.of("GUI for creating crates")).
                executor(new GUICommand()).
                build();
        CommandSpec importToMySQLCommand = CommandSpec.builder().
                permission("gwm_crates.command.import_to_mysql").
                description(Text.of("Import data to MySQL")).
                executor(new ImportToMySQLCommand()).
                arguments(
                        GenericArguments.flags().flag("a").buildWith(GenericArguments.none())).
                build();
        CommandSpec importFromMySQLCommand = CommandSpec.builder().
                permission("gwm_crates.command.import_from_mysql").
                description(Text.of("Import data from MySQL")).
                executor(new ImportFromMySQLCommand()).
                arguments(
                        GenericArguments.flags().flag("a").buildWith(GenericArguments.none())).
                build();
        CommandSpec reloadCommand = CommandSpec.builder().
                permission("gwm_crates.command.reload").
                description(Text.of("Reload plugin")).
                executor(new ReloadCommand()).
                build();
        CommandSpec saveCommand = CommandSpec.builder().
                permission("gwm_crates.command.save").
                description(Text.of("Save plugin configs")).
                executor(new SaveCommand()).
                build();
        CommandSpec openCommand = CommandSpec.builder().
                description(Text.of("Open a crate")).
                executor(new OpenCommand()).
                arguments(
                        new ManagerCommandElement(Text.of("manager"))).
                build();
        CommandSpec forceCommand = CommandSpec.builder().
                description(Text.of("Force open a crate")).
                executor(new ForceCommand()).
                arguments(
                        new ManagerCommandElement(Text.of("manager")),
                        GenericArguments.playerOrSource(Text.of("player"))).
                build();
        CommandSpec previewCommand = CommandSpec.builder().
                description(Text.of("Preview a crate")).
                executor(new PreviewCommand()).
                arguments(
                        new ManagerCommandElement(Text.of("manager")),
                        GenericArguments.playerOrSource(Text.of("player"))).
                build();
        CommandSpec giveCaseCommand = CommandSpec.builder().
                description(Text.of("Give the case to the player")).
                executor(new GiveCaseCommand()).
                arguments(
                        new ManagerCommandElement(Text.of("manager")),
                        GenericArguments.playerOrSource(Text.of("player")),
                        GenericArguments.optional(GenericArguments.integer(Text.of("amount")), 1)
                ).
                build();
        CommandSpec giveKeyCommand = CommandSpec.builder().
                description(Text.of("Give the key to the player")).
                executor(new GiveKeyCommand()).
                arguments(
                        new ManagerCommandElement(Text.of("manager")),
                        GenericArguments.playerOrSource(Text.of("player")),
                        GenericArguments.optional(GenericArguments.integer(Text.of("amount")), 1)
                ).
                build();
        CommandSpec giveDropCommand = CommandSpec.builder().
                description(Text.of("Give the drop to the player")).
                executor(new GiveDropCommand()).
                arguments(
                        new ManagerCommandElement(Text.of("manager")),
                        GenericArguments.string(Text.of("drop")),
                        GenericArguments.playerOrSource(Text.of("player")),
                        GenericArguments.optional(GenericArguments.integer(Text.of("amount")), 1)
                ).
                build();
        CommandSpec giveSSOCommand = CommandSpec.builder().
                description(Text.of("Give the SSO to the player")).
                executor(new GiveSSOCommand()).
                arguments(
                        new SuperObjectCommandElement(Text.of("sso"), Optional.empty(), true),
                        GenericArguments.playerOrSource(Text.of("player")),
                        GenericArguments.optional(GenericArguments.integer(Text.of("amount")), 1)
                ).
                build();
        CommandSpec giveCommand = CommandSpec.builder().
                child(giveCaseCommand, "case").
                child(giveKeyCommand, "key").
                child(giveDropCommand, "drop").
                child(giveSSOCommand, "savedsuperobject", "sso").
                build();
        CommandSpec buyCaseCommand = CommandSpec.builder().
                description(Text.of("Buy the case")).
                executor(new BuyCaseCommand()).
                arguments(
                        new ManagerCommandElement(Text.of("manager")),
                        GenericArguments.optional(GenericArguments.integer(Text.of("amount")), 1)
                ).
                build();
        CommandSpec buyKeyCommand = CommandSpec.builder().
                description(Text.of("Buy the key")).
                executor(new BuyKeyCommand()).
                arguments(
                        new ManagerCommandElement(Text.of("manager")),
                        GenericArguments.optional(GenericArguments.integer(Text.of("amount")), 1)
                ).
                build();
        CommandSpec buyDropCommand = CommandSpec.builder().
                description(Text.of("Buy the case")).
                executor(new BuyDropCommand()).
                arguments(
                        new ManagerCommandElement(Text.of("manager")),
                        GenericArguments.string(Text.of("drop")),
                        GenericArguments.optional(GenericArguments.integer(Text.of("amount")), 1)
                ).
                build();
        CommandSpec buySSOCommand = CommandSpec.builder().
                description(Text.of("Buy the SSO")).
                executor(new BuySSOCommand()).
                arguments(
                        new SuperObjectCommandElement(Text.of("sso"), Optional.empty(), true),
                        GenericArguments.optional(GenericArguments.integer(Text.of("amount")), 1)
                ).
                build();
        CommandSpec buyCommand = CommandSpec.builder().
                child(buyCaseCommand, "case").
                child(buyKeyCommand, "key").
                child(buyDropCommand, "drop").
                child(buySSOCommand, "savedsuperobject", "sso").
                build();
        CommandSpec listCommand = CommandSpec.builder().
                permission("gwm_crates.command.list").
                description(Text.of("List all available crates")).
                executor(new ListCommand()).
                build();
        CommandSpec infoCommand = CommandSpec.builder().
                description(Text.of("Info about a crate")).
                executor(new InfoCommand()).
                arguments(
                        GenericArguments.onlyOne(new ManagerCommandElement(Text.of("manager")))).
                build();
        CommandSpec spec = CommandSpec.builder().
                permission("gwm_crates.command").
                description(Text.of("Main plugin command.")).
                child(helpCommand, "help").
                child(guiCommand, "gui").
                child(importToMySQLCommand, "importtomysql").
                child(importFromMySQLCommand, "importfrommysql").
                child(reloadCommand, "reload").
                child(saveCommand, "save").
                child(openCommand, "open").
                child(forceCommand, "force").
                child(previewCommand, "preview").
                child(giveCommand, "give").
                child(buyCommand, "buy").
                child(listCommand, "list").
                child(infoCommand, "info").
                build();
        Sponge.getCommandManager().register(GWMCrates.getInstance(), spec,
                "gwmcrates", "gwmcrate", "crates", "crate");
    }
}
