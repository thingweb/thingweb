/*
 *
 *  * The MIT License (MIT)
 *  *
 *  * Copyright (c) 2016 Siemens AG and the thingweb community
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in
 *  * all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  * THE SOFTWARE.
 *
 */

package de.thingweb.jsruntime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.thingweb.desc.ThingDescriptionParser;
import de.thingweb.servient.ServientBuilder;
import de.thingweb.servient.ThingInterface;
import de.thingweb.servient.ThingServer;
import de.thingweb.thing.*;
import de.thingweb.typesystem.jsonschema.JsonTypeHelper;
import de.thingweb.util.encoding.ContentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Johannes on 02.02.2016.
 */
public class ServientLauncher {

    private static final Logger log = LoggerFactory.getLogger(ServientLauncher.class);
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private WotJavaScriptRuntime jsrt;
    private ThingServer server;


    public ServientLauncher() throws Exception {
        ServientBuilder.getHttpBinding().setPort(8088);
        ServientBuilder.getCoapBinding().setPort(5688);
    }

    public static void main(String[] args) throws Exception {
        ServientLauncher launcher = new ServientLauncher();
        launcher.start();
    }

    public void start() throws Exception {
        ServientBuilder.initialize();
        server = ServientBuilder.newThingServer();
        jsrt = WotJavaScriptRuntime.createOn(server);
        addServientInterfaceHandlers(server);
        ServientBuilder.start();
        runAutoLoad();
        runAutostart();
    }

    public void restart() throws Exception {
        ServientBuilder.stop();
        ServientBuilder.deinit();
        start();
    }

    public void runAutoLoad() throws IOException {
        String sAutoLoadFolder = "./autoload";
        Path p = Paths.get(sAutoLoadFolder);
        if (Files.exists(p)) {
            Files.walk(p)
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            server.addThing(ThingDescriptionParser.fromFile(path.toString()));
                        } catch (IOException e) {
                            log.error("error loading thing description file " + path, e);
                        }
                    });
        }
    }

    public void runAutostart() throws IOException {
        Files.walk(Paths.get("./autorun"))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .forEach(file -> {
                    try {
                        jsrt.runFile(file);
                    } catch (FileNotFoundException | ScriptException e) {
                        log.error("error running autostart file " + file, e);
                    }
                });
    }

    public void addServientInterfaceHandlers(ThingServer server) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();

        Thing srvThing = new Thing("servient");

        srvThing.addProperties(
            Property.getBuilder("numberOfThings").setValueType(JsonTypeHelper.integerType()).setWriteable(false).build(),
            Property.getBuilder("securityEnabled").setValueType(JsonTypeHelper.booleanType()).setWriteable(true).build()
        );

        srvThing.addActions(
            Action.getBuilder("createThing").setInputType(JsonTypeHelper.stringType()).build(),
            Action.getBuilder("addScript").setInputType(JsonTypeHelper.stringType()).build(),
            Action.getBuilder("reset").build()
        );

        ThingInterface serverInterface = server.addThing(srvThing);

        serverInterface.setProperty("numberOfThings", server.getThings().size());
        serverInterface.setProperty("securityEnabled", false);

        serverInterface.onPropertyUpdate("securityEnabled", (nV) -> {
            final Boolean protectionEnabled = ContentHelper.ensureClass(nV, Boolean.class);
            server.getThings().stream()
                    .filter((thing1 -> !thing1.equals(srvThing)))
                    .forEach(thing -> thing.setProtection(protectionEnabled));
        });

        serverInterface.onActionInvoke("createThing", (data) -> {
            final LinkedHashMap jsonld = ContentHelper.ensureClass(data, LinkedHashMap.class);

            try {
                final Thing newThing = ThingDescriptionParser.fromJavaMap(jsonld);
                server.addThing(newThing);
                serverInterface.setProperty("numberOfThings", server.getThings().size());
                return newThing.getMetadata();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        serverInterface.onActionInvoke("addScript", (data) -> {
            final String script = ContentHelper.ensureClass(data, String.class);

            try {
                jsrt.runScript(script);
            } catch (ScriptException e) {
                throw new RuntimeException(e);
            }
            return new Content(new byte[0], MediaType.APPLICATION_JSON);
        });

        serverInterface.onActionInvoke("reset",(data) -> {
            log.warn("invoking requested restart...");
            try {
                this.restart();
                log.info("restarted");
                return ContentHelper.wrap("Restarted",MediaType.TEXT_PLAIN);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
