package dev.gwm.spongeplugin.crates.superobject.drop;

import com.google.common.reflect.TypeToken;
import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.drop.base.AbstractDrop;
import dev.gwm.spongeplugin.crates.util.Nashornable;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.util.DefaultRandomableData;
import dev.gwm.spongeplugin.library.util.GWMLibraryUtils;
import dev.gwm.spongeplugin.library.util.GiveableData;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Optional;

public final class NashornDrop extends AbstractDrop implements Nashornable {

    public static final String TYPE = "NASHORN";

    private final NashornScriptEngine engine;
    private final String script;

    public NashornDrop(ConfigurationNode node) {
        super(node);
        try {
            engine = createEngine();
            ConfigurationNode scriptNode = node.getNode("SCRIPT");
            ConfigurationNode scriptFileNode = node.getNode("SCRIPT_FILE");
            if (!scriptNode.isVirtual()) {
                script = GWMLibraryUtils.joinString(scriptNode.getList(TypeToken.of(String.class)));
            } else {
                if (!scriptFileNode.isVirtual()) {
                    File file = new File(GWMCrates.getInstance().getScriptsDirectory(), scriptFileNode.getString());
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    script = reader.lines().reduce("", (s1, s2) -> s1 + "\n" + s2);
                } else {
                    throw new IllegalArgumentException("Both SCRIPT and SCRIPT_FILE node do not exist!");
                }
            }
            initEngine(engine, script);
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public NashornDrop(String id,
                       GiveableData giveableData,
                       Optional<ItemStack> dropItem, Optional<String> customName, boolean showInPreview,
                       DefaultRandomableData defaultRandomableData,
                       String script) {
        super(id, giveableData, dropItem, customName, showInPreview, defaultRandomableData);
        engine = createEngine();
        this.script = script;
        initEngine(engine, script);
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void give(Player player, int amount) {
        try {
            engine.invokeFunction("give", player, amount);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke \"give\" function!", e);
        }
    }
}
