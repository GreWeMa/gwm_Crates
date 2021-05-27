package dev.gwm.spongeplugin.crates.superobject.changemode;

import com.google.common.reflect.TypeToken;
import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.changemode.base.AbstractDecorativeItemsChangeMode;
import dev.gwm.spongeplugin.crates.util.Nashornable;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.util.GWMLibraryUtils;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.item.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public final class NashornDecorativeItemsChangeMode extends AbstractDecorativeItemsChangeMode implements Nashornable {

    public static final String TYPE = "NASHORN";

    private final NashornScriptEngine engine;
    private final String script;

    public NashornDecorativeItemsChangeMode(ConfigurationNode node) {
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
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        script = reader.lines().reduce("", (s1, s2) -> s1 + "\n" + s2);
                    }
                } else {
                    throw new IllegalArgumentException("Both SCRIPT and SCRIPT_FILE nodes do not exist!");
                }
            }
            initEngine(engine, script);
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public NashornDecorativeItemsChangeMode(String id,
                                            int changeDelay, List<Integer> ignoredIndices,
                                            String script) {
        super(id, changeDelay, ignoredIndices);
        engine = createEngine();
        this.script = script;
        initEngine(engine, script);
    }

    @Override
    public List<ItemStack> change(List<ItemStack> decorativeItems) {
        try {
            return (List<ItemStack>) engine.invokeFunction("change", decorativeItems);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke \"change\" function!", e);
        }
    }

    @Override
    public String type() {
        return TYPE;
    }
}
