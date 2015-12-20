package de.thingweb.servient;

import de.thingweb.desc.DescriptionParser;
import de.thingweb.desc.pojo.ThingDescription;
import de.thingweb.thing.Thing;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * Created by Johannes on 20.12.2015.
 */
public class ServientSetupTests {

    @Before
    public void setUp() throws Exception {
        ServientBuilder.initialize();
    }

    @Test
    public void readJson() throws IOException, URISyntaxException {
        String json = readResource("simplething.jsonld");
        System.out.println(json);
    }

    @Test
    public void parseTDFromJson() throws IOException, URISyntaxException {
        String json = readResource("simplething.jsonld");
        ThingDescription thingDescription = DescriptionParser.fromBytes(json.getBytes());
        assertThat(thingDescription.getMetadata().getName(),equalTo("SimpleThing"));
    }

    @Test
    public void createThing() throws IOException, URISyntaxException {
        String json = readResource("simplething.jsonld");
        ThingDescription thingDescription = DescriptionParser.fromBytes(json.getBytes());
        Thing simpleThing = new Thing(thingDescription);
        assertThat(simpleThing.getAction("testaction"),notNullValue());
        assertThat(simpleThing.getProperty("number"),notNullValue());
    }

    @Test
    public void createSingleThingServient() throws Exception {
        String json = readResource("simplething.jsonld");
        ThingDescription thingDescription = DescriptionParser.fromBytes(json.getBytes());
        Thing simpleThing = new Thing(thingDescription);
        ThingServer server = ServientBuilder.newThingServer(simpleThing);
        ServientBuilder.start();
    }

    @Test
    public void createThingServientAndAddThing() throws Exception {
        String json = readResource("simplething.jsonld");
        ThingDescription thingDescription = DescriptionParser.fromBytes(json.getBytes());
        Thing simpleThing = new Thing(thingDescription);
        ThingServer server = ServientBuilder.newThingServer();
        server.addThing(simpleThing);
        ServientBuilder.start();
    }

    @Test
    public void startEmptyServient() throws Exception {
        ThingServer server = ServientBuilder.newThingServer();
        ServientBuilder.start();
    }

    @Test
    public void addAfterStart() throws Exception {
        String json = readResource("simplething.jsonld");
        ThingDescription thingDescription = DescriptionParser.fromBytes(json.getBytes());
        Thing simpleThing = new Thing(thingDescription);
        ThingServer server = ServientBuilder.newThingServer();
        ServientBuilder.start();
        server.addThing(simpleThing);
    }

    @After
    public void tearDown() throws IOException {
        ServientBuilder.stop();
    }



    private String readResource(String path) throws URISyntaxException, IOException {
        URI uri = this.getClass().getClassLoader().getResource(path).toURI();
        return new String(Files.readAllBytes(Paths.get(uri)), Charset.forName("UTF-8"));
    }


}