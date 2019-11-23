package dev.gwm.spongeplugin.crates.superobject.manager;

import dev.gwm.spongeplugin.crates.superobject.caze.base.Case;
import dev.gwm.spongeplugin.crates.superobject.drop.base.Drop;
import dev.gwm.spongeplugin.crates.superobject.key.base.Key;
import dev.gwm.spongeplugin.crates.superobject.openmanager.base.OpenManager;
import dev.gwm.spongeplugin.crates.superobject.preview.base.Preview;
import dev.gwm.spongeplugin.crates.util.GWMCratesSuperObjectCategories;
import dev.gwm.spongeplugin.crates.util.ManagerCustomMessageData;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.superobject.randommanager.RandomManager;
import dev.gwm.spongeplugin.library.util.SuperObjectCategory;
import dev.gwm.spongeplugin.library.util.SuperObjectIdEqualsPredicate;

import java.util.List;
import java.util.Optional;

public interface Manager extends SuperObject {

    String TYPE = "MANAGER";

    @Override
    default SuperObjectCategory<Manager> category() {
        return GWMCratesSuperObjectCategories.MANAGER;
    }

    @Override
    default String type() {
        return TYPE;
    }

    default Optional<Drop> getDropById(String id) {
        return getDrops().stream().filter(new SuperObjectIdEqualsPredicate(id)).findFirst();
    }

    String getName();

    RandomManager getRandomManager();

    Case getCase();

    Key getKey();

    OpenManager getOpenManager();

    List<Drop> getDrops();

    Optional<Preview> getPreview();

    ManagerCustomMessageData getCustomMessageData();
}
