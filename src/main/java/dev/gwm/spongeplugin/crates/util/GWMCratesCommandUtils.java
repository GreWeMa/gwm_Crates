package dev.gwm.spongeplugin.crates.util;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.command.*;
import dev.gwm.spongeplugin.crates.command.buy.BuyCaseCommand;
import dev.gwm.spongeplugin.crates.command.buy.BuyDropCommand;
import dev.gwm.spongeplugin.crates.command.buy.BuyKeyCommand;
import dev.gwm.spongeplugin.crates.command.give.*;
import dev.gwm.spongeplugin.crates.command.withdraw.WithdrawCaseCommand;
import dev.gwm.spongeplugin.crates.command.withdraw.WithdrawKeyCommand;
import dev.gwm.spongeplugin.library.util.Language;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class GWMCratesCommandUtils {

    public static void registerCommands(GWMCrates instance) {
        Language language = instance.getLanguage();
        CommandSpec helpCommand = CommandSpec.builder().
                description(Text.of("Help command")).
                executor(new HelpCommand(language)).
                build();
        CommandSpec reloadCommand = CommandSpec.builder().
                permission("gwm_crates.command.reload").
                description(Text.of("Reload the plugin")).
                executor(new ReloadCommand(language)).
                build();
        CommandSpec saveCommand = CommandSpec.builder().
                permission("gwm_crates.command.save").
                description(Text.of("Save the plugin's configs")).
                executor(new SaveCommand(language)).
                build();
        CommandSpec openCommand = CommandSpec.builder().
                description(Text.of("Open a crate")).
                executor(new OpenCommand(language)).
                arguments(
                        new ManagerCommandElement(Text.of("manager"), language)
                ).
                build();
        CommandSpec forceCommand = CommandSpec.builder().
                description(Text.of("Force open a crate")).
                executor(new ForceCommand(language)).
                arguments(
                        new ManagerCommandElement(Text.of("manager"), language),
                        GenericArguments.playerOrSource(Text.of("player"))
                ).
                build();
        CommandSpec previewCommand = CommandSpec.builder().
                description(Text.of("Preview a crate")).
                executor(new PreviewCommand(language)).
                arguments(
                        new ManagerCommandElement(Text.of("manager"), language),
                        GenericArguments.playerOrSource(Text.of("player"))
                ).
                build();
        CommandSpec checkCommand = CommandSpec.builder().
                description(Text.of("Check player's amount of Cases and Keys")).
                executor(new CheckCommand(language)).
                arguments(
                        new ManagerCommandElement(Text.of("manager"), language),
                        GenericArguments.playerOrSource(Text.of("player"))
                ).
                build();
        CommandSpec checkAllCommand = CommandSpec.builder().
                description(Text.of("Check player's amount of Cases and Keys from all the crates")).
                executor(new CheckAllCommand(language)).
                arguments(
                        GenericArguments.playerOrSource(Text.of("player"))
                ).
                build();
        CommandSpec listCommand = CommandSpec.builder().
                permission("gwm_crates.command.list").
                description(Text.of("List all the available crates")).
                executor(new ListCommand(language)).
                build();
        CommandSpec infoCommand = CommandSpec.builder().
                description(Text.of("Info about a crate")).
                executor(new InfoCommand(language)).
                arguments(
                        new ManagerCommandElement(Text.of("manager"), language)
                ).
                build();
        CommandSpec buyCaseCommand = CommandSpec.builder().
                description(Text.of("Buy a Case")).
                executor(new BuyCaseCommand(language)).
                arguments(
                        new ManagerCommandElement(Text.of("manager"), language),
                        GenericArguments.optional(GenericArguments.integer(Text.of("amount")), 1)
                ).
                build();
        CommandSpec buyKeyCommand = CommandSpec.builder().
                description(Text.of("Buy a Key")).
                executor(new BuyKeyCommand(language)).
                arguments(
                        new ManagerCommandElement(Text.of("manager"), language),
                        GenericArguments.optional(GenericArguments.integer(Text.of("amount")), 1)
                ).
                build();
        CommandSpec buyDropCommand = CommandSpec.builder().
                description(Text.of("Buy a Drop from a crate")).
                executor(new BuyDropCommand(language)).
                arguments(
                        new ManagerCommandElement(Text.of("manager"), language),
                        GenericArguments.string(Text.of("drop")),
                        GenericArguments.optional(GenericArguments.integer(Text.of("amount")), 1)
                ).
                build();
        CommandSpec buyCommand = CommandSpec.builder().
                child(buyCaseCommand, "case").
                child(buyKeyCommand, "key").
                child(buyDropCommand, "drop").
                build();
        CommandSpec giveCaseCommand = CommandSpec.builder().
                description(Text.of("Give a Case to a player")).
                executor(new GiveCaseCommand(language)).
                arguments(
                        new ManagerCommandElement(Text.of("manager"), language),
                        GenericArguments.playerOrSource(Text.of("player")),
                        GenericArguments.optional(GenericArguments.integer(Text.of("amount")), 1),
                        GenericArguments.flags().flag("f").
                                buildWith(GenericArguments.none())
                ).
                build();
        CommandSpec giveKeyCommand = CommandSpec.builder().
                description(Text.of("Give a Key to a player")).
                executor(new GiveKeyCommand(language)).
                arguments(
                        new ManagerCommandElement(Text.of("manager"), language),
                        GenericArguments.playerOrSource(Text.of("player")),
                        GenericArguments.optional(GenericArguments.integer(Text.of("amount")), 1),
                        GenericArguments.flags().flag("f").
                                buildWith(GenericArguments.none())
                ).
                build();
        CommandSpec giveDropCommand = CommandSpec.builder().
                description(Text.of("Give a Drop from a crate to a player")).
                executor(new GiveDropCommand(language)).
                arguments(
                        new ManagerCommandElement(Text.of("manager"), language),
                        GenericArguments.string(Text.of("drop")),
                        GenericArguments.playerOrSource(Text.of("player")),
                        GenericArguments.optional(GenericArguments.integer(Text.of("amount")), 1)
                ).
                build();
        CommandSpec giveCommand = CommandSpec.builder().
                child(giveCaseCommand, "case").
                child(giveKeyCommand, "key").
                child(giveDropCommand, "drop").
                build();
        CommandSpec giveEveryoneCaseCommand = CommandSpec.builder().
                description(Text.of("Give a Case to all the online players")).
                executor(new GiveEveryoneCaseCommand(language)).
                arguments(
                        new ManagerCommandElement(Text.of("manager"), language),
                        GenericArguments.optional(GenericArguments.integer(Text.of("amount")), 1),
                        GenericArguments.flags().flag("f").
                                buildWith(GenericArguments.none())
                ).
                build();
        CommandSpec giveEveryoneKeyCommand = CommandSpec.builder().
                description(Text.of("Give a Key to all the online players")).
                executor(new GiveEveryoneKeyCommand(language)).
                arguments(
                        new ManagerCommandElement(Text.of("manager"), language),
                        GenericArguments.optional(GenericArguments.integer(Text.of("amount")), 1),
                        GenericArguments.flags().flag("f").
                                buildWith(GenericArguments.none())
                ).
                build();
        CommandSpec giveEveryoneDropCommand = CommandSpec.builder().
                description(Text.of("Give a Drop from a manager to all the online players")).
                executor(new GiveEveryoneDropCommand(language)).
                arguments(
                        new ManagerCommandElement(Text.of("manager"), language),
                        GenericArguments.string(Text.of("drop")),
                        GenericArguments.optional(GenericArguments.integer(Text.of("amount")), 1)
                ).
                build();
        CommandSpec giveEveryoneCommand = CommandSpec.builder().
                child(giveEveryoneCaseCommand, "case").
                child(giveEveryoneKeyCommand, "key").
                child(giveEveryoneDropCommand, "drop").
                build();
        CommandSpec withdrawCaseCommand = CommandSpec.builder().
                description(Text.of("Withdraw a Case from a player")).
                executor(new WithdrawCaseCommand(language)).
                arguments(
                        new ManagerCommandElement(Text.of("manager"), language),
                        GenericArguments.playerOrSource(Text.of("player")),
                        GenericArguments.optional(GenericArguments.integer(Text.of("amount")), 1),
                        GenericArguments.flags().flag("f").
                                buildWith(GenericArguments.none())
                ).
                build();
        CommandSpec withdrawKeyCommand = CommandSpec.builder().
                description(Text.of("Withdraw a Key from a player")).
                executor(new WithdrawKeyCommand(language)).
                arguments(
                        new ManagerCommandElement(Text.of("manager"), language),
                        GenericArguments.playerOrSource(Text.of("player")),
                        GenericArguments.optional(GenericArguments.integer(Text.of("amount")), 1),
                        GenericArguments.flags().flag("f").
                                buildWith(GenericArguments.none())
                ).
                build();
        CommandSpec withdrawCommand = CommandSpec.builder().
                child(withdrawCaseCommand, "case").
                child(withdrawKeyCommand, "key").
                build();
        CommandSpec probabilityTestCommand = CommandSpec.builder().
                description(Text.of("Test a probability of each Drop in a crate")).
                executor(new ProbabilityTestCommand(language)).
                arguments(
                        new ManagerCommandElement(Text.of("manager"), language),
                        GenericArguments.integer(Text.of("amount")),
                        GenericArguments.playerOrSource(Text.of("player")),
                        GenericArguments.flags().flag("f").
                                buildWith(GenericArguments.none())
                ).
                build();
        CommandSpec loadCommand = CommandSpec.builder().
                permission("gwm_crates.command.load").
                description(Text.of("Load a crate from a file")).
                executor(new LoadCommand(language)).
                arguments(
                        GenericArguments.remainingJoinedStrings(Text.of("path"))
                ).
                build();
        CommandSpec unloadCommand = CommandSpec.builder().
                description(Text.of("Unload a crate")).
                executor(new UnloadCommand(language)).
                arguments(
                        new ManagerCommandElement(Text.of("manager"), language)
                ).
                build();
        CommandSpec exportToMySQLCommand = CommandSpec.builder().
                permission("gwm_crates.command.export_to_mysql").
                description(Text.of("Export the data to MySQL")).
                executor(new ExportToMySQLCommand(language)).
                arguments(
                        GenericArguments.flags().flag("a").
                                buildWith(GenericArguments.none())
                ).
                build();
        CommandSpec importFromMySQLCommand = CommandSpec.builder().
                permission("gwm_crates.command.import_from_mysql").
                description(Text.of("Import the data from MySQL")).
                executor(new ImportFromMySQLCommand(language)).
                arguments(
                        GenericArguments.flags().flag("a").
                                buildWith(GenericArguments.none())
                ).
                build();
        CommandSpec findBlockCaseCommand = CommandSpec.builder().
                description(Text.of("Highlight a BLOCK Case")).
                executor(new FindBlockCaseCommand(language)).
                arguments(
                        new ManagerCommandElement(Text.of("manager"), language),
                        GenericArguments.flags().valueFlag(GenericArguments.
                                optional(GenericArguments
                                        .integer(Text.of("index")), 0), "t").
                                buildWith(GenericArguments.none())
                ).
                build();
        CommandSpec spec = CommandSpec.builder().
                permission("gwm_crates.command.base").
                description(Text.of("The basic command")).
                child(helpCommand, "help").
                child(reloadCommand, "reload").
                child(saveCommand, "save").
                child(openCommand, "open").
                child(forceCommand, "force").
                child(previewCommand, "preview").
                child(checkCommand, "check").
                child(checkAllCommand, "checkall").
                child(listCommand, "list").
                child(infoCommand, "info").
                child(buyCommand, "buy").
                child(giveCommand, "give").
                child(giveEveryoneCommand, "giveeveryone").
                child(withdrawCommand, "withdraw").
                child(probabilityTestCommand, "probabilitytest", "ptest").
                child(loadCommand, "load").
                child(unloadCommand, "unload").
                child(exportToMySQLCommand, "exporttomysql", "etm").
                child(importFromMySQLCommand, "importfrommysql", "ifm").
                child(findBlockCaseCommand, "findblockcase", "fbc").
                build();
        Sponge.getCommandManager().register(GWMCrates.getInstance(), spec,
                "gwmcrates", "gwmcrate", "crates", "crate");
    }
}
