package de.webthing.desc;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.jsonldjava.core.JsonLdError;

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
    public void testFromURL() {
	// TODO
	fail("Not yet implemented");
    }

    @Test
    public void testFromFile() {
	String happyPath = "jsonld" + File.separator + "led.jsonld";
	String notCompact = "jsonld" + File.separator + "led.jsonld";
	String erroneous = "jsonld" + File.separator + "led.jsonld";
	
	try {
	    DescriptionParser.fromFile(happyPath);
	} catch (Exception e) {
	    e.printStackTrace();
	    fail();
	}
	
//	try {
//	    DescriptionParser.fromFile(notCompact);
//	} catch (Exception e) {
//	    e.printStackTrace();
//	    fail();
//	}
//	
//	try {
//	    DescriptionParser.fromFile(erroneous);
//	    fail();
//	} catch (JsonLdError e) {
//	    // as expected
//	} catch (Exception other) {
//	    other.printStackTrace();
//	    fail();
//	}
    }

}
