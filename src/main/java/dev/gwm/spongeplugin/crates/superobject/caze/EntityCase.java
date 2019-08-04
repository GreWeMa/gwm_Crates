package dev.gwm.spongeplugin.crates.caze;

import com.google.common.reflect.TypeToken;
import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.exception.SSOCreationException;
import dev.gwm.spongeplugin.crates.superobject.Case;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;

import java.util.*;

public final class EntityCase extends Case {

    public static final String TYPE = "ENTITY";

    private final List<UUID> entityUuids;
    private final boolean startPreviewOnLeftClick;

    public EntityCase(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode entityUuidNode = node.getNode("ENTITY_UUID");
            ConfigurationNode entityUuidsNode = node.getNode("ENTITY_UUIDS");
            ConfigurationNode startPreviewOnLeftClickNode = node.getNode("START_PREVIEW_ON_LEFT_CLICK");
            List<UUID> tempEntityUuids = new ArrayList<>();
            if (!entityUuidsNode.isVirtual()) {
                for (ConfigurationNode innerEntityUuidNode : entityUuidsNode.getChildrenList()) {
                    tempEntityUuids.add(innerEntityUuidNode.getValue(TypeToken.of(UUID.class)));
                }
            }
            //Backwards compatibility
            else if (!entityUuidNode.isVirtual()) {
                GWMCrates.getInstance().getLogger().warn("[BACKWARD COMPATIBILITY] ENTITY_UUIDS node does not exist! Trying to use ENTITY_UUID node!");
                tempEntityUuids.add(entityUuidNode.getValue(TypeToken.of(UUID.class)));
            } else {
                throw new IllegalArgumentException("None of ENTITY_UUIDS and ENTITY_UUID nodes exist!");
            }
            if (tempEntityUuids.isEmpty()) {
                throw new IllegalArgumentException("No entity uuids are configured! At least one entity uuid is required!");
            }
            entityUuids = Collections.unmodifiableList(tempEntityUuids);
            startPreviewOnLeftClick = startPreviewOnLeftClickNode.getBoolean(false);
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public EntityCase(Optional<String> id,
                      List<UUID> entityUuids, boolean startPreviewOnLeftClick) {
        super(id, true);
        this.entityUuids = entityUuids;
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
