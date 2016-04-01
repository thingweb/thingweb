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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import com.siemens.ct.exi.exceptions.EXIException;

import de.thingweb.encoding.json.exi.EXI4JSONParser;
import de.thingweb.thing.Action;
import de.thingweb.thing.Property;
import de.thingweb.thing.Property.Builder;
import de.thingweb.thing.Thing;

import org.apache.commons.io.output.WriterOutputStream;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ThingDescriptionParser
{

  private static final String WOT_TD_CONTEXT = "http://w3c.github.io/wot/w3c-wot-td-context.jsonld";

  @SuppressWarnings("unchecked")
  // note: the jsonld-java implementation uses java.util.LinkedHashMap to store JSON objects
  // see http://wiki.fasterxml.com/JacksonInFiveMinutes
  public static Thing fromJavaMap(Object json) throws IOException
  {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readValue(json.toString(), JsonNode.class);
    
    try {
      return parse(root);
    } catch (Exception e) {
      return parseOld(root);
    }
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

    try {
      return parse(root);
    } catch (Exception e) {
      // try old parser if by chance it was an old TD
      return parseOld(root);
    }
  }

  public static Thing fromFile(String fname) throws FileNotFoundException, IOException
  {
    Path path = Paths.get(fname);
    byte[] data = Files.readAllBytes(path);
    return fromBytes(data);
  }
  
  public static byte[] toBytes(Thing thing) throws IOException
  {
    JsonNodeFactory factory = new JsonNodeFactory(false);
    
    ObjectNode td = factory.objectNode();
    td.put("@context", WOT_TD_CONTEXT);
    td.put("name", thing.getName());
    
    if (thing.getMetadata().contains("encodings")) {
      ArrayNode encodings = factory.arrayNode();
      for (String e : thing.getMetadata().getAll("encodings")) {
        encodings.add(e);
      }
      td.put("encodings", encodings);
    }
     
    if (thing.getMetadata().contains("uris")) {
      ArrayNode uris = factory.arrayNode();
      for (String uri : thing.getMetadata().getAll("uris")) {
        uris.add(uri);
      }
      td.put("uris", uris);
    }
    
    ArrayNode properties = factory.arrayNode();
    for (Property prop : thing.getProperties()) {
      ObjectNode p = factory.objectNode();
      p.put("name", prop.getName());
      p.put("writable", prop.isWriteable());
      p.put("valueType", prop.getXsdType());
      
      if (prop.getHrefs().size() > 0) {
        ArrayNode hrefs = factory.arrayNode();
        for (String href : prop.getHrefs()) {
          hrefs.add(href);
        }
        p.put("hrefs", hrefs);
      }
      
      properties.add(p);
    }
    td.put("properties", properties);
    
    ArrayNode actions = factory.arrayNode();
    for (Action action : thing.getActions()) {
      ObjectNode a = factory.objectNode();
      a.put("name", action.getName());
      
      ObjectNode in = factory.objectNode();
      in.put("valueType", action.getInputType());
      a.put("inputData", in);
      
      ObjectNode out = factory.objectNode();
      out.put("valueType", action.getOutputType());
      a.put("outputData", out);
      
      if (action.getHrefs().size() > 0) {
        ArrayNode hrefs = factory.arrayNode();
        for (String href : action.getHrefs()) {
          hrefs.add(href);
        }
        a.put("hrefs", hrefs);
      }
      
      actions.add(a);
    }
    td.put("actions", actions);
    
    // TODO events

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(baos, td);
    return baos.toByteArray();
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
      return null;
//      return removeBlankNodes(compactJson(jsonld)).toString();
    }
    catch (JsonLdError e)
    {
      throw new IOException("Can't reshape triples", e);
    }
  }
  
  @Deprecated
  private static Thing parseOld(JsonNode td) {
    try {
      Thing thing = new Thing(td.get("metadata").get("name").asText());
      
      Iterator<String> tdIterator = td.fieldNames();
      while (tdIterator.hasNext()) {
        switch (tdIterator.next()) {
          case "metadata":
            Iterator<String> metaIterator = td.get("metadata").fieldNames();
            while (metaIterator.hasNext()) {
              switch (metaIterator.next()) {
                case "encodings":
                  for (JsonNode encoding : td.get("metadata").get("encodings")) {
                    thing.getMetadata().add("encodings", encoding.asText());
                  }
                  break;
                  
                case "protocols":
                  TreeMap<Long, String> orderedURIs = new TreeMap<>();
                  for (JsonNode protocol : td.get("metadata").get("protocols")) {
                    orderedURIs.put(protocol.get("priority").asLong(), protocol.get("uri").asText());
                  }
                  for (String uri : orderedURIs.values()) {
                    // values returned in ascending order
                    thing.getMetadata().add("uris", uri);
                  }
                  break;
              }
            }
            break;
            
          case "interactions":
            for (JsonNode inter : td.get("interactions")) {
              if (inter.get("@type").asText().equals("Property")) {
                Property.Builder builder = Property.getBuilder(inter.get("name").asText());
                Iterator<String> propIterator = inter.fieldNames();
                while (propIterator.hasNext()) {
                  switch (propIterator.next()) {
                    case "outputData":
                      builder.setXsdType(inter.get("outputData").asText());
                      break;
                    case "writable":
                      builder.setWriteable(inter.get("writable").asBoolean());
                      break;
                  }
                }
                thing.addProperty(builder.build());
              } else if (inter.get("@type").asText().equals("Action")) {
                Action.Builder builder = Action.getBuilder(inter.get("name").asText());
                Iterator<String> actionIterator = inter.fieldNames();
                while (actionIterator.hasNext()) {
                  switch (actionIterator.next()) {
                    case "inputData":
                      builder.setInputType(inter.get("inputData").asText());
                      break;
                    case "outputData":
                      builder.setOutputType(inter.get("outputData").asText());
                      break;
                  }
                }
                thing.addAction(builder.build());
              } else if (inter.get("@type").asText().equals("Event")) {
                // TODO
              }
            }
            break;
        }
      }
      
      return thing;
    } catch (Exception e) { // anything could happen here
      return null;
    }
  }
  
  private static Thing parse(JsonNode td) throws Exception {
    // TODO validate data
    
    Thing thing = new Thing(td.get("name").asText());
    
    Iterator<String> tdIterator = td.fieldNames();
    while (tdIterator.hasNext()) {
      switch (tdIterator.next()) {
        case "uris":
          for (JsonNode uri : td.get("uris")) {
            thing.getMetadata().add("uris", uri.asText());
          }
          break;
          
        case "properties":
          for (JsonNode prop : td.get("properties")) {
            Property.Builder builder = Property.getBuilder(prop.get("name").asText());
            Iterator<String> it = prop.fieldNames();
            while (it.hasNext()) {
              switch (it.next()) {
                case "valueType":
                  builder.setXsdType(prop.get("valueType").asText());
                  break;
                case "writable":
                  builder.setWriteable(prop.get("writable").asBoolean());
                  break;
                case "hrefs":
                  builder.setHrefs(prop.findValuesAsText("hrefs"));
                  break;
              }
            }
            thing.addProperty(builder.build());
          }
          break;
          
        case "actions":
          for (JsonNode action : td.get("actions")) {
            Action.Builder builder = Action.getBuilder(action.get("name").asText());
            Iterator<String> it = action.fieldNames();
            while (it.hasNext()) {
              switch (it.next()) {
                case "inputData":
                  builder.setInputType(action.get("inputData").get("valueType").asText());
                  break;
                case "outputData":
                  builder.setOutputType(action.get("outputData").get("valueType").asText());
                  break;
                case "hrefs":
                  builder.setHrefs(action.findValuesAsText("hrefs"));
                  break;
              }
            }
            thing.addAction(builder.build());
          }
          break;
          
        case "events":
          // TODO
          break;
          
        case "encodings":
          for (JsonNode encoding : td.get("encodings")) {
            thing.getMetadata().add("encodings", encoding.asText());
          }
          break;
      }
    }
    
    return thing;
  }

  public static void main(String[] args) throws FileNotFoundException, IOException
  {
    Thing thing = fromFile("jsonld" + File.separator + "led.v2.jsonld");
  }

}
