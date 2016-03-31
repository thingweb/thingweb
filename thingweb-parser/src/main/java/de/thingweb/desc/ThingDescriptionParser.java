/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Siemens AG and the thingweb community
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package de.thingweb.desc;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import com.siemens.ct.exi.exceptions.EXIException;

import de.thingweb.desc.pojo.ThingDescription;
import de.thingweb.encoding.json.exi.EXI4JSONParser;
import de.thingweb.thing.Thing;

import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ThingDescriptionParser
{

  public static final String WOT_TD_CONTEXT = "http://w3c.github.io/wot/w3c-wot-td-context.jsonld";
  
  // default JSON-LD context for TDs
  private static Object context;

  static
  {
    try
    {
      context = JsonUtils.fromURL(new URL("http://w3c.github.io/wot/w3c-wot-td-context.jsonld"));
    }
    catch (IOException e)
    {
      e.printStackTrace();
      context = null;
    }
  }

  private static Object compactJson(Object jsonld) throws IOException
  {
    if (context == null)
    {
      throw new IOException("Default TD context could not be retrieved");
    }

    try
    {
      jsonld = JsonLdProcessor.compact(jsonld, context, new JsonLdOptions());
      return jsonld;
    }
    catch (JsonLdError e)
    {
      throw new IOException("The input object is not valid JSON-LD", e);
    }
  }

  @SuppressWarnings("unchecked")
  // note: the jsonld-java implementation uses java.util.LinkedHashMap to store JSON objects
  // see http://wiki.fasterxml.com/JacksonInFiveMinutes
  public static Thing mapJson(Object jsonld) throws IOException
  {
    // ensures keys are reduced to those in the default context
    JSONObject json = new JSONObject((Map<String, Object>) compactJson(jsonld));

    ObjectMapper mapper = new ObjectMapper();
    ThingDescription td = mapper.readValue(json.toString(), ThingDescription.class);
    return td;
  }

  @SuppressWarnings("unchecked")
  // same as for mapJson()
  private static Object removeBlankNodesRec(Object jsonld)
  {
    if (jsonld instanceof Map)
    { // JSON object
      List<String> toRemove = new ArrayList<>();

      for (Entry<String, Object> e : ((Map<String, Object>) jsonld).entrySet())
      {
        if (e.getKey().equals("@id"))
        {
          if (e.getValue().toString().startsWith("_"))
          { // blank node
            toRemove.add(e.getKey());
          }
        }
        else
        {
          removeBlankNodesRec(e.getValue());
        }
      }

      for (String k : toRemove)
      {
        ((Map<String, Object>) jsonld).remove(k);
      }
    }
    else if (jsonld instanceof List)
    { // JSON array
      for (Object o : ((List<Object>) jsonld))
      {
        removeBlankNodesRec(o);
      }
    }
    return jsonld;
  }

  @SuppressWarnings("unchecked")
  // same as for mapJson()
  private static JSONObject removeBlankNodes(Object jsonld)
  {
    return new JSONObject((Map<String, Object>) removeBlankNodesRec(jsonld));
  }

  public static Thing fromURL(URL url) throws JsonParseException, IOException
  {

    InputStream is = new BufferedInputStream(url.openStream());
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    int b;
    while ((b = is.read()) != -1)
    {
      baos.write(b);
    }
    return fromBytes(baos.toByteArray());
  }

  public static Thing fromBytes(byte[] data) throws JsonParseException, IOException
  {
    ByteArrayInputStream bais = new ByteArrayInputStream(data);

    // check whether we deal with an exified JSON file
    bais.mark(5); // latest after 4 byte cookie and 2 distinguishing bits it is clear whether we
                  // deal with an EXI file
    try
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      EXI4JSONParser e4j = new EXI4JSONParser(new PrintStream(baos));
      e4j.parse(new InputSource(bais));

      // adapt to new input stream
      bais = new ByteArrayInputStream(baos.toByteArray());

    }
    catch (EXIException | SAXException e)
    {
      // something went wrong with EXI --> reset & try "plain-text" json
      bais.reset();
    }

    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readValue(bais, JsonNode.class);

    // TODO validate object

    Thing thing = new Thing(root.get("name").asText());

    // TODO add properties and actions

    return thing;
  }

  public static Thing fromFile(String fname) throws FileNotFoundException, IOException
  {
    Path path = Paths.get(fname);
    byte[] data = Files.readAllBytes(path);
    return fromBytes(data);
  }
  
  public static byte[] toBytes(Thing thing)
  {
    // TODO
    return null;
  }

  /**
   * reshapes the input JSON-LD object using the standard Thing Description context and having the
   * thing description resource as object root.
   * 
   * @param data
   *          UTF-8 encoded JSON-LD object
   * @return the reshaped JSON-LD object
   * @throws IOException
   *           error
   */
  public static String reshape(byte[] data) throws IOException
  {
    ObjectMapper om = new ObjectMapper();

    try
    {
      Object jsonld = JsonUtils.fromInputStream(new ByteArrayInputStream(data));
      // TODO put the frame online instead
      Object frame = om.readValue("{\"http://www.w3c.org/wot/td#hasMetadata\":{}}", HashMap.class);

      jsonld = JsonLdProcessor.frame(jsonld, frame, new JsonLdOptions());
      return removeBlankNodes(compactJson(jsonld)).toString();
    }
    catch (JsonLdError e)
    {
      throw new IOException("Can't reshape triples", e);
    }
  }

  public static void main(String[] args) throws FileNotFoundException, IOException
  {
    Thing thing = fromFile("jsonld" + File.separator + "led.v2.jsonld");
  }

}
