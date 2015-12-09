package de.thingweb.jsruntime;

import de.thingweb.jsruntime.api.WotAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by Johannes on 08.12.2015.
 */
public class WotJavaScriptRuntime {

    private static final Logger log = LoggerFactory.getLogger(WotJavaScriptRuntime.class);
    private final ScriptEngine engine;

    private WotJavaScriptRuntime(ScriptEngine engine) {
        WotAPI api = new WotAPI();
        log.debug("injecting wot api v{}", api.getVersion());
        engine.put("WoT", api);
        this.engine = engine;
    }

    public static WotJavaScriptRuntime create() {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        return new WotJavaScriptRuntime(engine);
    }

    public void runFile(String fname) throws FileNotFoundException, ScriptException {
        if(engine != null)
            engine.eval(new FileReader(fname));
    }

    public void runScript(String script) throws ScriptException {
        if(engine != null) {
             engine.eval(script);
        }
    }

    public ScriptEngine getEngine() {
        return engine;
    }
}
