package de.thingweb.runtime;

import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.FileReader;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Johannes on 08.12.2015.
 */
public class WotJavaScriptRuntime {

    // yes, I know I need to split this mess up
    // but look at the commit time!

    public static class ExposedThing {
        public void onInvoke(String actionName, Function callback) {}
        public void onUpdate(String propertyName, Consumer callback) {}
    }

    public static class ConsumedThing {
        public JsPromise setProperty(String propertyName, Object property) {
            JsPromise promise = new JsPromise();

            //do async:
            //set property on thing
            //then
            //promise.resolve(null_or_new_value);
            //if things go south
            //promise.reject(error);

            return promise;
        }

        public JsPromise getProperty(String propertyName) {
            JsPromise promise = new JsPromise();

            //do async:
            //get property from thing
            //then
            //promise.resolve(value_of_property);
            //if things go south
            //promise.reject(error);


            return promise;
        }

        public JsPromise callAction(String actionName, Object param) {
            JsPromise promise = new JsPromise();

            //do async:
            //call action on thing
            //then
            //promise.resolve(null_or_new_value);
            //if things go south
            //promise.reject(error);

            return promise;
        }

    }

    // naiive ES6-like promise implementation for nashorn (do futures work there?)
    public static class JsPromise {
        private Consumer onSuccess;
        private Consumer onError;

        public void resolve(Object param) {
            if(onSuccess!=null)
                onSuccess.accept(param);
        }

        public void reject(Object error) {
            if(onError != null) {
                onError.accept(error);
            }
        }

        public JsPromise then(Consumer onSuccessCallBack) {
            onSuccess = onSuccessCallBack;
            return this;
        }

        public JsPromise then(JsPromise chainedPromise) {
            onSuccess.andThen(chainedPromise.onSuccess);
            return this;
        }

        //cannot be named catch
        public JsPromise _catch(Consumer onErrorCallBack) {
            onError = onErrorCallBack;
            return this;
        }
    }

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

        public ExposedThing expose(String jsonld) {

            // parse TD

            ExposedThing thing = new ExposedThing();
            // ... create from TD ...
            return thing;
        }

        public ConsumedThing consume(String jsonld) {

            // parse TD

            ConsumedThing thing = new ConsumedThing();
            // ... create stubs from TD ...
            return thing;
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
