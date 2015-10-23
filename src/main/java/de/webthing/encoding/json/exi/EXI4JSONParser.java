package de.webthing.encoding.json.exi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.api.sax.EXISource;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.values.BooleanValue;

import de.webthing.client.Client;
import de.webthing.client.ClientFactory;
import de.webthing.client.UnsupportedException;

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
			exiFactory.setGrammars(GrammarFactory.newInstance().createGrammars("./schemas/schema-for-json-strings.xsd"));
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
	
	
	public static void main(String[] args) throws EXIException, IOException, SAXException, TransformerException, UnsupportedException {

		boolean inFile = true;
		
		if(inFile) {
			File f = File.createTempFile("json4exi", ".json");
			OutputStream output = new FileOutputStream(f);
			PrintStream ps = new PrintStream(output);
			
			EXI4JSONParser e4j = new EXI4JSONParser(ps);
			e4j.parse(new InputSource("./src/dist/schemas/demo.jsonld.xml.exi"));

			output.close();
			
			ClientFactory cf = new ClientFactory();
			Client c = cf.getClient(f.getAbsolutePath());
			System.out.println(c);
			
		} else {

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			
			EXI4JSONParser e4j = new EXI4JSONParser(ps);
			e4j.parse(new InputSource("./src/dist/schemas/demo.jsonld.xml.exi"));
			
			String json = baos.toString();
			System.out.println(json);
		}
	}

}
