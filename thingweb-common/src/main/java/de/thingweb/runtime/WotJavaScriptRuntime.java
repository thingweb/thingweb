package de.thingweb.runtime;

import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.FileReader;
import java.util.function.Function;

/**
 * Created by Johannes on 08.12.2015.
 */
public class WotJavaScriptRuntime {

    public static class WotAPI {
        public String version = "0.0.1";

        public String getVersion() {
            return version;
        }

        public void callJava() {
            System.out.println("Java function called from js");
        }

        public void toJava(ScriptObjectMirror obj) {
            System.out.println("Java got an object from js: " + obj.getClassName());
        }

        public void callMe(Function fun) {
            System.out.println("Java got a callback from js: " + fun.getClass());
            fun.apply("hello!");
        }
    }

 public static void main(String[] args) throws Exception {
     ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
     if(engine instanceof NashornScriptEngine) {
         NashornScriptEngine nashorn = (NashornScriptEngine) engine;

         WotAPI api = new WotAPI();
         System.out.println("injecting wot api v" + api.getVersion());
         nashorn.put("wot",api);

         nashorn.eval(new FileReader("testwot.js"));
     }
 }
}
