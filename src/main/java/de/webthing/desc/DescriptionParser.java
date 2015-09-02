package de.webthing.desc;

import org.json.JSONObject;

import java.io.*;
import java.net.URL;

/**
 * Created by Johannes on 02.09.2015.
 */
public class DescriptionParser {

    private static String readStream(InputStream is) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                sb.append(chars, 0, read);

            return sb.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    private static ThingDescription parseJson(JSONObject json) {
        ThingDescriptionBuilder tdb = ThingDescription.getBuilder();

        //Magic happens here

        return tdb.build();
    }

    public static ThingDescription fromURL(URL url) {

        try (InputStream is = url.openStream()) {
            JSONObject json = new JSONObject(readStream(is));
            return parseJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


    }

    public static ThingDescription fromFile(String fname) {

        try (InputStream is = new FileInputStream(fname)) {
            JSONObject json = new JSONObject(readStream(is));
            return parseJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
