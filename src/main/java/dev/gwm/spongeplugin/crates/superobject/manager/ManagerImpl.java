package dev.gwm.spongeplugin.crates.superobject.manager;

import dev.gwm.spongeplugin.crates.superobject.caze.base.Case;
import dev.gwm.spongeplugin.crates.superobject.drop.base.Drop;
import dev.gwm.spongeplugin.crates.superobject.key.base.Key;
import dev.gwm.spongeplugin.crates.superobject.openmanager.base.OpenManager;
import dev.gwm.spongeplugin.crates.superobject.preview.base.Preview;
import dev.gwm.spongeplugin.crates.utils.GWMCratesSuperObjectCategories;
import dev.gwm.spongeplugin.crates.utils.GWMCratesUtils;
import dev.gwm.spongeplugin.crates.utils.ManagerCustomMessageData;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.superobject.AbstractSuperObject;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.superobject.randommanager.RandomManager;
import dev.gwm.spongeplugin.library.utils.GWMLibrarySuperObjectCategories;
import dev.gwm.spongeplugin.library.utils.SuperObjectCategory;
import dev.gwm.spongeplugin.library.utils.SuperObjectsService;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;

import java.util.*;

public final class ManagerImpl extends AbstractSuperObject implements Manager {

    private final String name;
    private final RandomManager randomManager;
    private final Case caze;
    private final Key key;
    private final OpenManager openManager;
    private final List<Drop> drops;
    private final Optional<Preview> preview;
    private final ManagerCustomMessageData customMessageData;

    public ManagerImpl(ConfigurationNode node) {
        super(node);
        try {
            if (!id().isPresent()) {
                throw new RuntimeException("The Manager does not have an ID!");
            }
            SuperObjectsService superObjectsService = Sponge.getServiceManager().provide(SuperObjectsService.class).get();
            ConfigurationNode nameNode = node.getNode("NAME");
            ConfigurationNode randomManagerNode = node.getNode("RANDOM_MANAGER");
            ConfigurationNode caseNode = node.getNode("CASE");
            ConfigurationNode keyNode = node.getNode("KEY");
            ConfigurationNode openManagerNode = node.getNode("OPEN_MANAGER");
            ConfigurationNode dropsNode = node.getNode("DROPS");
            ConfigurationNode previewNode = node.getNode("PREVIEW");
            if (caseNode.isVirtual()) {
                throw new IllegalArgumentException("CASE node does not exist!");
            }
            if (keyNode.isVirtual()) {
                throw new IllegalArgumentException("KEY node does not exist!");
            }
            if (openManagerNode.isVirtual()) {
                throw new IllegalArgumentException("OPEN_MANGER node does not exist!");
            }
            if (dropsNode.isVirtual()) {
                throw new IllegalArgumentException("DROPS node does not exist!");
            }
            name = nameNode.isVirtual() ? id().get() : nameNode.getString();
            if (randomManagerNode.isVirtual()) {
                randomManager = GWMCratesUtils.getDefaultRandomManager();
            } else {
                randomManager = superObjectsService.create(GWMLibrarySuperObjectCategories.RANDOM_MANAGER, randomManagerNode);
            }
            caze = superObjectsService.create(GWMCratesSuperObjectCategories.CASE, caseNode);
            key = superObjectsService.create(GWMCratesSuperObjectCategories.KEY, keyNode);
            openManager = superObjectsService.create(GWMCratesSuperObjectCategories.OPEN_MANAGER, openManagerNode);
            List<Drop> tempDrops = new ArrayList<>();
            for (ConfigurationNode dropNode : dropsNode.getChildrenList()) {
                tempDrops.add(superObjectsService.create(GWMCratesSuperObjectCategories.DROP, dropNode));
            }
            if (tempDrops.isEmpty()) {
                throw new IllegalArgumentException("No Drops are configured! At least one Drop is required!");
            }
            drops = Collections.unmodifiableList(tempDrops);
            if (!previewNode.isVirtual()) {
                preview = Optional.of(superObjectsService.create(GWMCratesSuperObjectCategories.PREVIEW, previewNode));
            } else {
                preview = Optional.empty();
            }
            customMessageData = new ManagerCustomMessageData(node);
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public ManagerImpl(Optional<String> id, String name, RandomManager randomManager,
                       Case caze, Key key, OpenManager openManager, List<Drop> drops,
                       Optional<Preview> preview, ManagerCustomMessageData customMessageData) {
        super(id);
        if (!id().isPresent()) {
            throw new RuntimeException("The Manager does not have an ID!");
        }
        this.name = name;
        this.randomManager = randomManager;
        this.caze = caze;
        this.key = key;
        this.openManager = openManager;
        this.drops = Collections.unmodifiableList(drops);
        this.preview = preview;
        this.customMessageData = customMessageData;
    }

    @Override
    public Set<SuperObject> getInternalSuperObjects() {
        Set<SuperObject> set = super.getInternalSuperObjects();
        set.add(randomManager);
        set.add(caze);
        set.add(key);
        set.add(openManager);
        set.addAll(drops);
        preview.ifPresent(set::add);
        return set;
    }

    @Override
    public final SuperObjectCategory<Manager> category() {
        return GWMCratesSuperObjectCategories.MANAGER;
    }

    @Override
    public final String type() {
        return TYPE;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public RandomManager getRandomManager() {
        return randomManager;
    }

    @Override
    public Case getCase() {
        return caze;
    }

    @Override
    public Key getKey() {
        return key;
    }

    @Override
    public OpenManager getOpenManager() {
        return openManager;
    }

    @Override
    public List<Drop> getDrops() {
        return drops;
    }

    @Override
    public Optional<Preview> getPreview() {
        return preview;
    }

    @Override
    public ManagerCustomMessageData getCustomMessageData() {
        return customMessageData;
    }
}
