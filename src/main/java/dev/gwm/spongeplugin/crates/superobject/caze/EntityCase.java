package dev.gwm.spongeplugin.crates.superobject.caze;

import com.google.common.reflect.TypeToken;
import dev.gwm.spongeplugin.crates.superobject.caze.base.AbstractCase;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;

import java.util.*;

public final class EntityCase extends AbstractCase {

    public static final String TYPE = "ENTITY";

    private final List<UUID> entityUuids;
    private final boolean startPreviewOnLeftClick;

    public EntityCase(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode entityUuidsNode = node.getNode("ENTITY_UUIDS");
            ConfigurationNode startPreviewOnLeftClickNode = node.getNode("START_PREVIEW_ON_LEFT_CLICK");
            if (entityUuidsNode.isVirtual()) {
                throw new IllegalArgumentException("ENTITY_UUID node does not exist!");
            }
            List<UUID> tempEntityUuids = new ArrayList<>();
            if (!entityUuidsNode.isVirtual()) {
                for (ConfigurationNode innerEntityUuidNode : entityUuidsNode.getChildrenList()) {
                    tempEntityUuids.add(innerEntityUuidNode.getValue(TypeToken.of(UUID.class)));
                }
            }
            if (tempEntityUuids.isEmpty()) {
                throw new IllegalArgumentException("No Entity Uuids are configured! At least one Entity Uuid is required!");
            }
            entityUuids = Collections.unmodifiableList(tempEntityUuids);
            startPreviewOnLeftClick = startPreviewOnLeftClickNode.getBoolean(false);
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public EntityCase(String id,
                      List<UUID> entityUuids, boolean startPreviewOnLeftClick) {
        super(id, true);
        if (entityUuids.isEmpty()) {
            throw new IllegalArgumentException("No Entity Uuids are configured! At least one Entity Uuid is required!");
        }
        this.entityUuids = Collections.unmodifiableList(entityUuids);
        this.startPreviewOnLeftClick = startPreviewOnLeftClick;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void withdraw(Player player, int amount, boolean force) {
    }

    @Override
    public int get(Player player) {
        return 1;
    }

    public List<UUID> getEntityUuids() {
        return entityUuids;
    }

    public boolean isStartPreviewOnLeftClick() {
        return startPreviewOnLeftClick;
    }
}
