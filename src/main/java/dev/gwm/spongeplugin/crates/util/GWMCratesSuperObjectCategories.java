package dev.gwm.spongeplugin.crates.util;

import dev.gwm.spongeplugin.crates.superobject.changemode.base.DecorativeItemsChangeMode;
import dev.gwm.spongeplugin.crates.superobject.openmanager.base.OpenManager;
import dev.gwm.spongeplugin.crates.superobject.preview.base.Preview;
import dev.gwm.spongeplugin.crates.superobject.caze.base.Case;
import dev.gwm.spongeplugin.crates.superobject.drop.base.Drop;
import dev.gwm.spongeplugin.crates.superobject.key.base.Key;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.library.util.SuperObjectCategory;

import java.util.Arrays;
import java.util.List;

public final class GWMCratesSuperObjectCategories {

    private GWMCratesSuperObjectCategories() {
    }

    public static final SuperObjectCategory<Manager> MANAGER = new SuperObjectCategory<>("MANAGER");
    public static final SuperObjectCategory<Case> CASE = new SuperObjectCategory<>("CASE");
    public static final SuperObjectCategory<Key> KEY = new SuperObjectCategory<>("KEY");
    public static final SuperObjectCategory<Drop> DROP = new SuperObjectCategory<>("DROP");
    public static final SuperObjectCategory<OpenManager> OPEN_MANAGER = new SuperObjectCategory<>("OPEN_MANAGER");
    public static final SuperObjectCategory<Preview> PREVIEW = new SuperObjectCategory<>("PREVIEW");
    public static final SuperObjectCategory<DecorativeItemsChangeMode> DECORATIVE_ITEMS_CHANGE_MODE = new SuperObjectCategory<>("DECORATIVE_ITEMS_CHANGE_MODE");

    public static final List<SuperObjectCategory<?>> CATEGORIES = Arrays.asList(
            MANAGER,
            CASE,
            KEY,
            DROP,
            OPEN_MANAGER,
            PREVIEW,
            DECORATIVE_ITEMS_CHANGE_MODE);
}
