package dev.gwm.spongeplugin.crates.util;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.util.AbstractSuperObjectCommandElement;
import dev.gwm.spongeplugin.library.util.GWMLibraryUtils;
import dev.gwm.spongeplugin.library.util.Language;
import dev.gwm.spongeplugin.library.util.service.SuperObjectService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ManagerCommandElement extends AbstractSuperObjectCommandElement {

    private final Language language;

    public ManagerCommandElement(@Nullable Text key, Language language) {
        super(key);
        this.language = language;
    }

    @Nullable
    @Override
    protected Manager parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String superObjectId = args.next();
        SuperObject superObject = Sponge.getServiceManager().provide(SuperObjectService.class).get().
                getCreatedSuperObjectById(superObjectId).
                orElseThrow(() ->
                        new ArgumentParseException(GWMLibraryUtils.joinText(language.
                                getTranslation("MANAGER_IS_NOT_FOUND",
                                        new ImmutablePair<>("MANAGER_ID", superObjectId),
                                        source)), superObjectId, 0));
        if (!(superObject instanceof Manager)) {
            throw new ArgumentParseException(GWMLibraryUtils.joinText(language.
                    getTranslation("SUPER_OBJECT_IS_NOT_MANAGER", Arrays.asList(
                            new ImmutablePair<>("SUPER_OBJECT_ID", superObjectId),
                            new ImmutablePair<>("SUPER_OBJECT_CATEGORY", superObject.category().getName()),
                            new ImmutablePair<>("SUPER_OBJECT_TYPE", superObject.type())
                    ), source)), superObjectId, 0);
        }
        return (Manager) superObject;
    }

    @Override
    protected Collection<SuperObject> getSuperObjects() {
        return Sponge.getServiceManager().provide(SuperObjectService.class).get().
                getCreatedSuperObjects().
                stream().
                filter(categoryFilter()).
                collect(Collectors.toSet());
    }

    @Override
    protected Predicate<? super SuperObject> categoryFilter() {
        return superObject -> superObject.category().equals(GWMCratesSuperObjectCategories.MANAGER);
    }

    @Override
    protected Predicate<? super SuperObject> giveableFilter() {
        return superObject -> true;
    }

    @Override
    protected Predicate<? super SuperObject> typeFilter() {
        return superObject -> true;
    }
}
