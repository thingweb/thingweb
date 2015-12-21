package de.thingweb.jsruntime;

import de.thingweb.servient.TestTools;
import org.junit.Test;

/**
 * Created by Johannes on 21.12.2015.
 */
public class WotJavaScriptRuntimeTest {

    @Test
    public void testRunFile() throws Exception {
        String script = TestTools.readResource("testwot.js");
        WotJavaScriptRuntime jsrt = WotJavaScriptRuntime.create();

        String testTD = TestTools.readResource("simplething.jsonld");
        jsrt.getEngine().put("testTD",testTD);

        jsrt.runScript(script);
    }
}