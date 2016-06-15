package de.thingweb.jsruntime;

import de.thingweb.jsruntime.api.WotAPI;
import de.thingweb.servient.ThingServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by Johannes on 08.12.2015.
 */
public class WotJavaScriptRuntime {

    private static final Logger log = LoggerFactory.getLogger(WotJavaScriptRuntime.class);
    protected final WotAPI api;
    private final ScriptEngine engine;

    private WotJavaScriptRuntime(ScriptEngine engine) {
        api = new WotAPI();
        polyfill(engine,api);
        this.engine = engine;
    }

    private WotJavaScriptRuntime(ScriptEngine engine,ThingServer thingServer) {
        api = new WotAPI(thingServer);
        polyfill(engine,api);
        this.engine = engine;
    }

    private static void polyfill(ScriptEngine engine, WotAPI api) {
        log.debug("injecting wot api v{}", api.getVersion());
        engine.put("WoT", api);

        // general polyfills
        try {
            engine.eval("var global = this;\n" +
                    "var console = {};\n" +
                    "console.debug = print;\n" +
                    "console.warn = print;\n" +
                    "console.log = print;");
        } catch (ScriptException e) {
            log.error("error in polyfill");
        }
    }

    public static WotJavaScriptRuntime create() {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        return new WotJavaScriptRuntime(engine);
    }

    public static WotJavaScriptRuntime createOn(ThingServer thingServer) {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        return new WotJavaScriptRuntime(engine,thingServer);
    }

    public void runFile(String fname) throws FileNotFoundException, ScriptException {
        if(engine != null)
            engine.eval(new FileReader(fname));
    }

    public void runFile(File file) throws FileNotFoundException, ScriptException {
        if(engine != null)
            engine.eval(new FileReader(file));
    }

    public void runScript(String script) throws ScriptException {
        if(engine != null) {
             engine.eval(script);
        }
    }

    public ScriptEngine getEngine() {
        return engine;
    }

    public WotAPI getApi() {
        return api;
    }
}
