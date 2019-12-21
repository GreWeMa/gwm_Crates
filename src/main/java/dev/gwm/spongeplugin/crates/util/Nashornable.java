package dev.gwm.spongeplugin.crates.util;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.library.GWMLibrary;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.spongepowered.api.Sponge;

import javax.script.SimpleScriptContext;

public interface Nashornable {

    default NashornScriptEngine createEngine() {
        NashornScriptEngine engine = (NashornScriptEngine) new NashornScriptEngineFactory().getScriptEngine();
        engine.setContext(createContext());
        return engine;
    }

    default SimpleScriptContext createContext() {
        SimpleScriptContext context = new SimpleScriptContext();
        context.setAttribute("server", Sponge.getServer(), SimpleScriptContext.ENGINE_SCOPE);
        context.setAttribute("GWMCrates", GWMCrates.getInstance(), SimpleScriptContext.ENGINE_SCOPE);
        GWMLibrary.getInstance().getEconomyService().ifPresent(economyService ->
                context.setAttribute("economyService", economyService, SimpleScriptContext.ENGINE_SCOPE));
        GWMLibrary.getInstance().getPlaceholderService().ifPresent(placeholderService ->
                context.setAttribute("placeholderService", placeholderService, SimpleScriptContext.ENGINE_SCOPE));
        return context;
    }

    default void initEngine(NashornScriptEngine engine, String script) {
        try {
            engine.eval(script);
        } catch (Exception e) {
            throw new RuntimeException("Failed to init engine!", e);
        }
    }
}
