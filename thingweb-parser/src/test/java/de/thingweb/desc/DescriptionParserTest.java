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

import org.junit.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.fail;

public class DescriptionParserTest {

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
    	DescriptionParser.fromURL(jsonld);
    	// TODO any further checks?
    }
    
    @Test
    public void testFromURLLed() throws JsonParseException, IOException {
    	URL jsonld = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/led.jsonld");
    	DescriptionParser.fromURL(jsonld);
    	// TODO any further checks?
    }
    
    @Test
    public void testFromURLLed_v02() throws JsonParseException, IOException {
    	URL jsonld = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/led_v02.jsonld");
    	try {
    		DescriptionParser.fromURL(jsonld);
    	    fail();
    	} catch (IOException e) {
    		// OK, expect failure
    	}
    }
    
    @Test
    public void testFromURLOutlet() throws JsonParseException, IOException {
    	URL jsonld = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/outlet.jsonld");
    	DescriptionParser.fromURL(jsonld);
    	// TODO any further checks?
    }
    
    @Test
    public void testFromURLWeather() throws JsonParseException, IOException {
    	URL jsonld = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/weather.jsonld");
    	DescriptionParser.fromURL(jsonld);
    	// TODO any further checks?
    }

    @Test
    public void testFromFile() {
      String happyPath = "jsonld" + File.separator + "led.jsonld";
      // (1) the document is not "compact" as per JSON-LD API spec ('td:' prefix already defined in the context)
      // (2) the property 'label' does not appear in the context (should be dropped)
      // (3) 'encodings' has only one member and is not defined as a list (should be transformed by compaction)
      String altPath = "jsonld" + File.separator + "led_1.jsonld";
      // JSON syntax error (missing comma)
      // note : the JSON-LD API spec is very permissive. Hard to get a JsonLdError...
      String erroneous = "jsonld" + File.separator + "led_2.jsonld";
      
      try {
          DescriptionParser.fromFile(happyPath);
      } catch (Exception e) {
          e.printStackTrace();
          fail();
      }
      
      try {
          DescriptionParser.fromFile(altPath);
      } catch (Exception e) {
          e.printStackTrace();
          fail();
      }
      
      try {
          DescriptionParser.fromFile(erroneous);
          fail();
      } catch (IOException e) {
          if (e instanceof JsonParseException) {
      	// as expected
          } else {
      	e.printStackTrace();
      	fail();
          }
      }
    }
    
    @Test
    public void testReshape() {
      try {
        File f = new File("jsonld/outlet_flattened.jsonld");
        FileReader r = new FileReader(f);
        char[] buf = new char [(int) f.length()];
        r.read(buf);
        r.close();
        String jsonld = DescriptionParser.reshape(new String(buf).getBytes());
        // checks that reshaped jsonld is compliant to description parser's impl.
        DescriptionParser.fromBytes(jsonld.getBytes());
        // TODO any further checks?
      } catch (Exception e) {
        fail(e.getMessage());
      }
    }

}
