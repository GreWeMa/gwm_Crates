package dev.gwm.spongeplugin.crates.util;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.library.GWMLibrary;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import me.rojo8399.placeholderapi.PlaceholderService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.economy.EconomyService;

import javax.script.SimpleScriptContext;
import java.util.Optional;

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
        Optional<EconomyService> optionalEconomyService = GWMLibrary.getInstance().getEconomyService();
        if (optionalEconomyService.isPresent()) {
            context.setAttribute("economyService", optionalEconomyService.get(), SimpleScriptContext.ENGINE_SCOPE);
        }
        Optional<PlaceholderService> optionalPlaceholderService = GWMLibrary.getInstance().getPlaceholderService();
        if (optionalPlaceholderService.isPresent()) {
            context.setAttribute("placeholderService", optionalPlaceholderService.get(), SimpleScriptContext.ENGINE_SCOPE);
        }
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
