package org.gwmdevelopments.sponge_plugin.crates.caze.cases;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.caze.Case;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;
import java.util.UUID;

public final class EntityCase extends Case {

    public static final String TYPE = "ENTITY";

    private final UUID entityUuid;
    private final boolean startPreviewOnLeftClick;

    public EntityCase(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode entityUuidNode = node.getNode("ENTITY_UUID");
            ConfigurationNode startPreviewOnLeftClickNode = node.getNode("START_PREVIEW_ON_LEFT_CLICK");
            if (entityUuidNode.isVirtual()) {
                throw new IllegalArgumentException("ENTITY_UUID node does not exist!");
            }
            entityUuid = entityUuidNode.getValue(TypeToken.of(UUID.class));
            startPreviewOnLeftClick = startPreviewOnLeftClickNode.getBoolean(false);
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public EntityCase(Optional<String> id,
                      UUID entityUuid, boolean startPreviewOnLeftClick) {
        super(id, true);
        this.entityUuid = entityUuid;
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

    public UUID getEntityUuid() {
        return entityUuid;
    }

    public boolean isStartPreviewOnLeftClick() {
        return startPreviewOnLeftClick;
    }
}
