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

package de.thingweb.encoding.json.exi;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.api.sax.EXISource;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.values.BooleanValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;

public class EXI4JSONParser extends DefaultHandler {

	private static final Logger log = LoggerFactory.getLogger(EXI4JSONParser.class);

	PrintStream out;
	String lastStartElement;
	boolean pendingComma;
	int indentation;
	EventType lastEvent;
	EXIFactory exiFactory;

	public EXI4JSONParser() {
		this(System.out);
	}

	public EXI4JSONParser(PrintStream out) {
		this.out = out;
	}
	
	public void parse(InputSource is) throws EXIException, IOException, SAXException {
		if(this.exiFactory == null) {
			exiFactory = DefaultEXIFactory.newInstance();
			// schema file is supposed to be located here: thingweb-parser\jsonld\exi4json\schema-for-json-strings.xsd
			// However, it seems safer to just include it
			// exiFactory.setGrammars(GrammarFactory.newInstance().createGrammars("./jsonld/exi4json/schema-for-json-strings.xsd"));
			String xmlSchemaAsString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" targetNamespace=\"http://www.w3.org/2013/XSL/json\" xmlns:j=\"http://www.w3.org/2013/XSL/json\">\n    \n    <!--  Thing string-table -->\n    <xs:simpleType name=\"thingStrings\">\n        <xs:restriction base=\"xs:string\">\n            <!-- JSON-LD strings (to be continued) -->\n            <xs:enumeration value=\"@context\"/>\n            <xs:enumeration value=\"@id\"/>\n            <xs:enumeration value=\"@value\"/>\n            <xs:enumeration value=\"@type\"/>\n            \n            <!-- XML schema datatypes (to be continued) -->\n            <xs:enumeration value=\"xsd:string\"/>\n            <xs:enumeration value=\"xsd:boolean\"/>\n            <xs:enumeration value=\"xsd:unsignedShort\"/>\n            <xs:enumeration value=\"xsd:unsignedByte\"/>\n            \n            <!-- thing vocabulary (to be continued) -->\n\t\t\t<xs:enumeration value=\"http://w3c.github.io/wot/w3c-wot-td-context.jsonld\"/>\n            <xs:enumeration value=\"metadata\"/>\n            <xs:enumeration value=\"name\"/>\n            <xs:enumeration value=\"protocols\"/>\n            <xs:enumeration value=\"uri\"/>\n            <xs:enumeration value=\"priority\"/>\n            <xs:enumeration value=\"encodings\"/>\n            <xs:enumeration value=\"interactions\"/>\n            <xs:enumeration value=\"outputData\"/>\n            <xs:enumeration value=\"inputData\"/>\n            <xs:enumeration value=\"writable\"/>\n            <xs:enumeration value=\"Property\"/>\n            <xs:enumeration value=\"Action\"/>\n            <xs:enumeration value=\"Event\"/>\n            \n            <!-- Other known useful strings (to be continued) -->\n            <xs:enumeration value=\"CoAP\"/>\n            <xs:enumeration value=\"HTTP\"/>\n            <xs:enumeration value=\"JSON\"/>\n            <xs:enumeration value=\"EXI\"/>\n            \n            <!-- Other strings such as thing properties and such should be left out (to be continued) -->\n            <!-- \n            <xs:enumeration value=\"MyLED\"/>\n            <xs:enumeration value=\"colorTemperature\"/>\n            <xs:enumeration value=\"rgbValueRed\"/>\n            ...\n            -->\n        </xs:restriction>\n    </xs:simpleType>\n     \n    \n    <!-- \n     * This is a schema for the XML representation of JSON used as the target for the\n     * XSLT 3.0 function fn:json-to-xml()\n     *\n     * The schema is made available under the terms of the W3C software notice and license\n     * at http://www.w3.org/Consortium/Legal/copyright-software-19980720\n     *\n    -->\n    \n    <xs:element name=\"map\" type=\"j:mapType\">\n        <xs:unique name=\"unique-key\">\n            <xs:selector xpath=\"*\"/>\n            <xs:field xpath=\"@key\"/>\n        </xs:unique>\n    </xs:element>\n    \n    <xs:element name=\"array\" type=\"j:arrayType\"/>\n    \n    <xs:element name=\"string\" type=\"j:stringType\"/>\n    \n    <xs:element name=\"number\" type=\"j:numberType\"/>\n    \n    <xs:element name=\"boolean\" type=\"xs:boolean\"/>\n    \n    <xs:element name=\"null\" type=\"j:nullType\"/>\n    \n    <xs:complexType name=\"nullType\">\n        <xs:sequence/>\n    </xs:complexType>\n    \n    <xs:complexType name=\"stringType\">\n        <xs:simpleContent>\n            <xs:extension base=\"j:thingStrings\">\n                <xs:attribute name=\"escaped\" type=\"xs:boolean\" use=\"optional\" default=\"false\"/>\n            </xs:extension>\n        </xs:simpleContent>\n    </xs:complexType>\n    \n    <xs:simpleType name=\"numberType\">\n        <xs:restriction base=\"xs:double\">\n            <!-- exclude positive and negative infinity, and NaN -->\n            <xs:minExclusive value=\"-INF\"/>\n            <xs:maxExclusive value=\"INF\"/>\n        </xs:restriction>\n    </xs:simpleType>\n    \n    <xs:complexType name=\"arrayType\">\n        <xs:choice minOccurs=\"0\" maxOccurs=\"unbounded\">\n            <xs:element ref=\"j:map\"/>\n            <xs:element ref=\"j:array\"/>\n            <xs:element ref=\"j:string\"/>\n            <xs:element ref=\"j:number\"/>\n            <xs:element ref=\"j:boolean\"/>\n            <xs:element ref=\"j:null\"/>\n        </xs:choice>       \n    </xs:complexType>\n    \n    <xs:complexType name=\"mapType\">\n        <xs:choice minOccurs=\"0\" maxOccurs=\"unbounded\">\n            <xs:element name=\"map\">\n                <xs:complexType>\n                    <xs:complexContent>\n                        <xs:extension base=\"j:mapType\">\n                            <xs:attribute name=\"key\" type=\"j:thingStrings\"/>\n                        </xs:extension>\n                    </xs:complexContent>\n                </xs:complexType>\n                <xs:unique name=\"unique-key-2\">\n                    <xs:selector xpath=\"*\"/>\n                    <xs:field xpath=\"@key\"/>\n                </xs:unique>\n            </xs:element>\n            <xs:element name=\"array\">\n                <xs:complexType>\n                    <xs:complexContent>\n                        <xs:extension base=\"j:arrayType\">\n                            <xs:attributeGroup ref=\"j:key-group\"/>\n                        </xs:extension>\n                    </xs:complexContent>\n                </xs:complexType>\n            </xs:element>\n            <xs:element name=\"string\">\n                <xs:complexType>\n                    <xs:simpleContent>\n                        <xs:extension base=\"j:stringType\">\n                            <xs:attributeGroup ref=\"j:key-group\"/>\n                        </xs:extension>\n                    </xs:simpleContent>\n                </xs:complexType>\n            </xs:element>\n            <xs:element name=\"number\">\n                <xs:complexType>\n                    <xs:simpleContent>\n                        <xs:extension base=\"j:numberType\">\n                            <xs:attributeGroup ref=\"j:key-group\"/>\n                        </xs:extension>\n                    </xs:simpleContent>\n                </xs:complexType>\n            </xs:element>\n            <xs:element name=\"boolean\">\n                <xs:complexType>\n                    <xs:simpleContent>\n                        <xs:extension base=\"xs:boolean\">\n                            <xs:attributeGroup ref=\"j:key-group\"/>\n                        </xs:extension>\n                    </xs:simpleContent>\n                </xs:complexType>\n            </xs:element>\n            <xs:element name=\"null\">\n                <xs:complexType>\n                    <xs:attributeGroup ref=\"j:key-group\"/>\n                </xs:complexType>\n            </xs:element>\n        </xs:choice>\n    </xs:complexType>\n    \n    <xs:attributeGroup name=\"key-group\">\n        <xs:attribute name=\"key\" type=\"j:thingStrings\"/>\n        <xs:attribute name=\"escaped-key\" type=\"xs:boolean\" use=\"optional\" default=\"false\"/>\n    </xs:attributeGroup>\n    \n</xs:schema>";
			exiFactory.setGrammars(GrammarFactory.newInstance().createGrammars(new ByteArrayInputStream(xmlSchemaAsString.getBytes())));
		}
		EXISource exiSource = new EXISource(exiFactory);
		XMLReader exiReader = exiSource.getXMLReader();
		exiReader.setContentHandler(this);
		exiReader.parse(is);	
	}
	

	@Override
	public void startDocument() throws SAXException {
		log.debug("SD");
		lastStartElement = null;
		pendingComma = false;
		indentation = 0;
		lastEvent = null;
	}
	
	protected void printIndentation() {
		for(int i=0; i<indentation; i++) {
			out.print(" ");
		}
	}

	protected boolean printKey(Attributes atts) {
		if (atts != null && atts.getLength() > 0) {
			int index = atts.getIndex("key");
			if (index != -1) {
				String keyValue = atts.getValue(index);
				printIndentation();
				out.print("\"");
				out.print(keyValue);
				out.print("\": ");
				return true;
			}
		}
		return false;
	}
	
	protected void checkPendingComma() {
		if (pendingComma) {
			out.println();
			printIndentation();
			out.print(", ");
			out.println();
			pendingComma = false;
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		log.debug("SE " + localName);
		checkPendingComma();
		boolean isKey = printKey(atts);
		if ("map".equals(localName)) {
			if(!isKey) {
				printIndentation();
			}
			out.print("{");
			out.println();
			indentation++;
		} else if ("array".equals(localName)) {
			if(!isKey) {
				printIndentation() ;
			}
			out.print("[");
			out.println();
			indentation++;
		} else {

		}

		pendingComma = false;

		lastEvent = EventType.START_ELEMENT;
		lastStartElement = localName;
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		// checkPendingComma();
		if ("string".equals(lastStartElement)) {
			printIndentation();
			out.print("\"");
			out.print(new String(ch, start, length));
			out.print("\"");
			pendingComma = true;
		} else if ("number".equals(lastStartElement)) {
			printIndentation();
			double d = Double.parseDouble(new String(ch, start, length));
			// out.print(new String(ch, start, length));
			out.print(d);
			pendingComma = true;
		} else if ("boolean".equals(lastStartElement)) {
			printIndentation();
			BooleanValue bv = BooleanValue.parse(new String(ch, start, length));
			if(bv.toBoolean()) {
				out.print("true");
			} else {
				out.print(false);
			}
			pendingComma = true;
		}
		lastEvent = EventType.CHARACTERS;
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		log.debug("EE");
		if(lastEvent == EventType.START_ELEMENT) {
			// TODO empty value (or null)
			printIndentation();
			out.print("\"");
			out.print("\"");
			pendingComma = true;
		}
		
		if ("map".equals(localName)) {
			out.println();
			printIndentation();
			out.print("}");
			out.println();
			indentation--;
			pendingComma = true;
		} else if ("array".equals(localName)) {
			out.println();
			printIndentation();
			out.print("]");
			out.println();
			indentation--;
			pendingComma = true;
		} else {
//			checkPendingComma();
		}

//		pendingComma = true;
		lastEvent = EventType.END_ELEMENT;
	}

	@Override
	public void endDocument() throws SAXException {
		log.debug("ED");
	}
	
	
/*	public static void main(String[] args) throws EXIException, IOException, SAXException, TransformerException, UnsupportedException, URISyntaxException {

		boolean inFile = true;
		
		if(inFile) {
			File f = File.createTempFile("json4exi", ".json");
			OutputStream output = new FileOutputStream(f);
			PrintStream ps = new PrintStream(output);
			
			EXI4JSONParser e4j = new EXI4JSONParser(ps);
			e4j.parse(new InputSource("./src/dist/schemas/demo.jsonld.xml.exi"));

			output.close();
			
			ClientFactory cf = new ClientFactory();
			Client c = cf.getClientFile(f.getAbsolutePath());
			System.out.println(c);
			
		} else {

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			
			EXI4JSONParser e4j = new EXI4JSONParser(ps);
			e4j.parse(new InputSource("./src/dist/schemas/demo.jsonld.xml.exi"));
			
			String json = baos.toString();
			System.out.println(json);
		}
	} */

}
