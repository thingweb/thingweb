package de.thingweb.servient;

import de.thingweb.desc.DescriptionParser;
import de.thingweb.desc.pojo.ThingDescription;
import de.thingweb.thing.MediaType;
import de.thingweb.thing.Thing;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Created by Johannes on 20.12.2015.
 */
public class TestTools {

    public static void main(String[] args) throws Exception {
        ServientBuilder.initialize();
        String json = readResource("simplething.jsonld");
        ThingDescription thingDescription = DescriptionParser.fromBytes(json.getBytes());
        Thing simpleThing = new Thing(thingDescription);
        ThingServer server = ServientBuilder.newThingServer(simpleThing);
        server.getThing("SimpleThing").setProperty("number",38);
        ServientBuilder.start();
        System.in.read();
    }

    public static String readResource(String path) throws URISyntaxException, IOException {
        URI uri = TestTools.class.getClassLoader().getResource(path).toURI();
        return new String(Files.readAllBytes(Paths.get(uri)), Charset.forName("UTF-8"));
    }

    public static String fromUrl(String url) throws Exception {
        Scanner scanner = new Scanner(new URL(url).openStream(), "UTF-8").useDelimiter("\\A");
        String res = scanner.hasNext() ? scanner.next() : "";
        scanner.close();
        return res;
    }

   public static void postHttpJson(String url, Object value) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("content-type", MediaType.APPLICATION_JSON.mediaType);
        String json = "{ \"value\" : " + value.toString() + " }";
        OutputStream os = connection.getOutputStream();
        os.write(json.getBytes());
        os.close();
        connection.getInputStream().close();
    }
}
