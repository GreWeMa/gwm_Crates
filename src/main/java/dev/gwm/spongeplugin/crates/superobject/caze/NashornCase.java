package dev.gwm.spongeplugin.crates.superobject.caze;

import com.google.common.reflect.TypeToken;
import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.caze.base.GiveableCase;
import dev.gwm.spongeplugin.crates.utils.Nashornable;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.utils.GWMLibraryUtils;
import dev.gwm.spongeplugin.library.utils.GiveableData;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public final class NashornCase extends GiveableCase implements Nashornable {

    public static final String TYPE = "NASHORN";

    private final NashornScriptEngine engine;
    private final String script;

    public NashornCase(ConfigurationNode node) {
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

    public NashornCase(String id,
                       boolean doNotWithdraw, GiveableData giveableData, boolean doNotAdd,
                       String script) {
        super(id, doNotWithdraw, giveableData, doNotAdd);
        engine = createEngine();
        this.script = script;
        initEngine(engine, script);
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void withdraw(Player player, int amount, boolean force) {
        if (!isDoNotWithdraw() || force) {
            try {
                engine.invokeFunction("withdraw", player, amount);
            } catch (Exception e) {
                throw new RuntimeException("Failed to invoke \"withdraw\" function!", e);
            }
        }
    }

    @Override
    public int get(Player player) {
        try {
            return (int) engine.invokeFunction("get", player);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke \"get\" function!", e);
        }
    }

    @Override
    public void give(Player player, int amount, boolean force) {
        if (!isDoNotAdd() || force) {
            try {
                engine.invokeFunction("give", player, amount);
            } catch (Exception e) {
                throw new RuntimeException("Failed to invoke \"give\" function!", e);
            }
        }
    }
}
