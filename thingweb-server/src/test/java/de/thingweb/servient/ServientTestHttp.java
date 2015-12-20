package de.thingweb.servient;

import de.thingweb.desc.DescriptionParser;
import de.thingweb.desc.pojo.ThingDescription;
import de.thingweb.thing.Content;
import de.thingweb.thing.MediaType;
import de.thingweb.thing.Thing;
import de.thingweb.util.encoding.ContentHelper;
import de.thingweb.util.encoding.ValueType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.StreamSupport;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by Johannes on 20.12.2015.
 */
public class ServientTestHttp {

    private ThingServer server;
    private Thread serverThread;

    @Before
    public void setUp() throws Exception {
        ServientBuilder.initialize();
        String json = readResource("simplething.jsonld");
        ThingDescription thingDescription = DescriptionParser.fromBytes(json.getBytes());
        Thing simpleThing = new Thing(thingDescription);
        server = ServientBuilder.newThingServer(simpleThing);
        ServientBuilder.start();
    }

    @Test
    public void setAndReadPropertyDirectly() throws Exception {
        server.setProperty("number",42);
        Integer number = (Integer) server.getProperty("number");
        assertThat("should be Integer",number.getClass(),equalTo(Integer.class));
        assertThat("value is 42", number,is(42));
    }


    @Test
    public void setDirectlyAndReadHttp() throws Exception {
        server.setProperty("number",42);

        String json = fromUrl("http://localhost:8080/things/SimpleThing/number");

        Content resp = new Content(json.getBytes(), MediaType.APPLICATION_JSON);
        Object number = ContentHelper.getValueFromJson(resp);

        assertThat("should be Integer",number.getClass(),equalTo(Integer.class));
        assertThat("value is 42", number,is(42));
    }

    @Test
    public void setDirectlyAndReadHttp_lowercese() throws Exception {
        server.setProperty("number",42);

        String json = fromUrl("http://localhost:8080/things/simplething/number");

        Content resp = new Content(json.getBytes(), MediaType.APPLICATION_JSON);
        Object number = ContentHelper.getValueFromJson(resp);

        assertThat("should be Integer",number.getClass(),equalTo(Integer.class));
        assertThat("value is 42", number,is(42));
    }


    @After
    public void tearDown() throws IOException {
        ServientBuilder.stop();
    }

    private String readResource(String path) throws URISyntaxException, IOException {
        URI uri = this.getClass().getClassLoader().getResource(path).toURI();
        return new String(Files.readAllBytes(Paths.get(uri)), Charset.forName("UTF-8"));
    }

    private String fromUrl(String url) throws Exception {
        Scanner scanner = new Scanner(new URL(url).openStream(), "UTF-8").useDelimiter("\\A");
        String res = scanner.hasNext() ? scanner.next() : "";
        scanner.close();
        return res;
    }

}