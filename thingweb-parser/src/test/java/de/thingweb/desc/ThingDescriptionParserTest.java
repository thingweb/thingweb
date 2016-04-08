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

import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.json.EXIforJSONGenerator;

import de.thingweb.thing.Thing;

public class ThingDescriptionParserTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testFromURLDoor() throws JsonParseException, IOException {
    	URL jsonld = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/door.jsonld");
    	ThingDescriptionParser.fromURL(jsonld);
    	// TODO any further checks?
    }
    
    @Test
    public void testFromURLLed() throws JsonParseException, IOException {
    	URL jsonld = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/led.jsonld");
    	ThingDescriptionParser.fromURL(jsonld);
    	// TODO any further checks?
    }
    
	@Test
	public void testFromURLLedEXI4JSON() throws JsonParseException, IOException, EXIException {
		URL jsonld = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/led.jsonld");

		InputStream is = jsonld.openStream();

		File f = File.createTempFile("jsonld", "e4j");
		OutputStream os = new FileOutputStream(f);

		EXIFactory ef = DefaultEXIFactory.newInstance();
		ef.setSharedStrings(ThingDescriptionParser.SHARED_STRINGS_EXI_FOR_JSON);
		
		EXIforJSONGenerator e4j = new EXIforJSONGenerator(ef);
		e4j.generate(is, os);

		is.close();
		os.close();

		ThingDescriptionParser.fromFile(f.getAbsolutePath());
		// TODO any further checks?
	}
    
    
    
    @Test
    public void testFromURLLed_v02() throws JsonParseException, IOException {
    	URL jsonld = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/led_v02.jsonld");
    	try {
    		ThingDescriptionParser.fromURL(jsonld);
    		// TODO are not recognized fields are ignored
//    	    fail();
    	} catch (IOException e) {
    		// OK, expect failure
    	}
    }
    
    @Test
    public void testFromURLOutlet() throws JsonParseException, IOException {
    	URL jsonld = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/outlet.jsonld");
    	ThingDescriptionParser.fromURL(jsonld);
    	// TODO any further checks?
    }
    
    @Test
    public void testFromURLWeather() throws JsonParseException, IOException {
    	URL jsonld = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/weather.jsonld");
    	ThingDescriptionParser.fromURL(jsonld);
    	// TODO any further checks?
    }
    
    @Test
    public void testLocalStrangeCharacters() throws JsonParseException, IOException {
    	String foo = "{\"metadata\":{\"name\":\"Ugly strange n\u00E4ime\",\"protocols\":{\"CoAP\":{\"uri\":\"coap://MD1EWQUC/things/ugly+strange+n%c3%a4ime\",\"priority\":1},\"HTTP\":{\"uri\":\"http://MD1EWQUC:8080/things/ugly+strange+n%c3%a4ime\",\"priority\":2}},\"encodings\":[\"JSON\"]},\"interactions\":[{\"@type\":\"Property\",\"name\":\"not url komp\u00E4tibel\",\"writable\":false,\"outputData\":\"xsd:string\"},{\"@type\":\"Action\",\"name\":\"wierdly named \u00E4ktschn\",\"inputData\":null,\"outputData\":\"\"}],\"@context\":\"http://w3c.github.io/wot/w3c-wot-td-context.jsonld\"}";
   		@SuppressWarnings("unused")
		Thing td = ThingDescriptionParser.fromBytes(foo.getBytes());
   		// TODO any further checks?
    }
    
 
    @Test
    public void testFromFile() {
      String happyPath = "jsonld" + File.separator + "led.v2.jsonld";
      // should use parser for deprecated Thing Descriptions
      String happyPathOld = "jsonld" + File.separator + "led.jsonld";
      // should fail (required field not found)
      String invalid = "jsonld" + File.separator + "led.v2.invalid.jsonld";

      try {
          ThingDescriptionParser.fromFile(happyPath);
      } catch (Exception e) {
          e.printStackTrace();
          fail();
      }
      
      try {
          ThingDescriptionParser.fromFile(happyPathOld);
      } catch (Exception e) {
          e.printStackTrace();
          fail();
      }
      
      try {
          ThingDescriptionParser.fromFile(invalid);
          fail();
      } catch (IOException e) {
          if (e instanceof IOException) {
            // as expected
          } else {
          	e.printStackTrace();
          	fail();
          }
      }
    }
    
    @Test
    public void testToBytes() throws Exception
    {
      String filename = "jsonld" + File.separator + "led.v2.plain.jsonld";
      ObjectMapper mapper = new ObjectMapper();
      
      JsonNode original = mapper.readValue(new File(filename), JsonNode.class);
      JsonNode generated = mapper.readValue(ThingDescriptionParser.toBytes(ThingDescriptionParser.fromFile(filename)), JsonNode.class);
      
      // TODO uncomment as soon as events got parsed
//      assertTrue(original.equals(generated));
    }
    
//    @Test
//    public void testReshape() {
//      try {
//        File f = new File("jsonld/outlet_flattened.jsonld");
//        FileReader r = new FileReader(f);
//        char[] buf = new char [(int) f.length()];
//        r.read(buf);
//        r.close();
//        String jsonld = ThingDescriptionParser.reshape(new String(buf).getBytes());
//        // checks that reshaped jsonld is compliant to description parser's impl.
//        ThingDescriptionParser.fromBytes(jsonld.getBytes());
//        // TODO any further checks?
//      } catch (Exception e) {
//        fail(e.getMessage());
//      }
//    }

}
