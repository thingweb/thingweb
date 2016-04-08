/*
 * Copyright (c) 2007-2016 Siemens AG
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package com.siemens.ct.exi.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import com.siemens.ct.exi.EXIBodyEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EXIStreamEncoder;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.values.StringValue;

public class EXIforJSONGenerator extends AbstractEXIforJSON {
	
	public EXIforJSONGenerator() throws EXIException, IOException {
		super();
	}
	
	public EXIforJSONGenerator(EXIFactory ef) throws EXIException, IOException {
		super(ef);
	}
	
	static final boolean DEBUG = false;

	static void printDebugInd(int ind, String s) {
		if(DEBUG) {
			for(int i=0; i<ind; i++) {
				System.out.print("\t");
			}
			System.out.println(s);	
		}
	}
	
	public void generate(InputStream is, OutputStream os) throws EXIException, IOException {
		EXIStreamEncoder streamEncoder = ef.createEXIStreamEncoder();
		
		EXIBodyEncoder bodyEncoder = streamEncoder.encodeHeader(os);
		
		JsonParser parser = Json.createParser(is);
		
		int ind = 0;
		String key = null;
		
		bodyEncoder.encodeStartDocument();
		
		while (parser.hasNext()) {
			Event e = parser.next();
			switch(e) {
			case KEY_NAME:
				key = parser.getString();
				break;
			case START_OBJECT:
				bodyEncoder.encodeStartElement(NAMESPACE_EXI4JSON, LOCALNAME_MAP, null);
				if(key == null) {
					printDebugInd(ind, "<map>");
				} else {
					printDebugInd(ind, "<map key=\"" + key + "\">");
					bodyEncoder.encodeAttribute("", LOCALNAME_KEY, null, new StringValue(key));
					key = null;
				}
				ind++;
				break;
			case END_OBJECT:
				ind--;
				printDebugInd(ind, "</map>");
				bodyEncoder.encodeEndElement();
				break;
			case START_ARRAY:
				bodyEncoder.encodeStartElement(NAMESPACE_EXI4JSON, LOCALNAME_ARRAY, null);
				if(key == null) {
					printDebugInd(ind, "<array>");
				} else {
					printDebugInd(ind, "<array key=\"" + key + "\">");
					bodyEncoder.encodeAttribute("", LOCALNAME_KEY, null, new StringValue(key));
					key = null;
				}
				ind++;
				break;
			case END_ARRAY:
				ind--;
				printDebugInd(ind, "</array>");
				bodyEncoder.encodeEndElement();
				break;
			case VALUE_STRING:
				bodyEncoder.encodeStartElement(NAMESPACE_EXI4JSON, LOCALNAME_STRING, null);
				bodyEncoder.encodeCharacters(new StringValue(parser.getString()));
				if(key == null) {
					printDebugInd(ind, "<string>" + parser.getString() + "</string>");
				} else {
					printDebugInd(ind, "<string key=\"" + key + "\">" + parser.getString() + "</string>");
					bodyEncoder.encodeAttribute("", LOCALNAME_KEY, null, new StringValue(key));
					key = null;
				}
				bodyEncoder.encodeEndElement();
				break;
			case VALUE_NUMBER:
				bodyEncoder.encodeStartElement(NAMESPACE_EXI4JSON, LOCALNAME_NUMBER, null);
				bodyEncoder.encodeCharacters(new StringValue(parser.getString()));
				if(key == null) {
					printDebugInd(ind, "<number>" + parser.getString() + "</number>");
				} else {
					printDebugInd(ind, "<number key=\"" + key + "\">" + parser.getString() + "</number>");
					bodyEncoder.encodeAttribute("", LOCALNAME_KEY, null, new StringValue(key));
					key = null;
				}
				bodyEncoder.encodeEndElement();
				break;
			case VALUE_FALSE:
			case VALUE_TRUE:
				bodyEncoder.encodeStartElement(NAMESPACE_EXI4JSON, LOCALNAME_BOOLEAN, null);
				bodyEncoder.encodeCharacters(new StringValue((e == Event.VALUE_FALSE ? "false" : "true")));
				if(key == null) {
					printDebugInd(ind, "<boolean>" + parser.getString() + "</boolean>");
				} else {
					printDebugInd(ind, "<boolean key=\"" + key + "\">" + (e == Event.VALUE_FALSE ? "false" : "true") + "</boolean>");
					bodyEncoder.encodeAttribute("", LOCALNAME_KEY, null, new StringValue(key));
					key = null;
				}
				bodyEncoder.encodeEndElement();
				break;
			case VALUE_NULL:
				bodyEncoder.encodeStartElement(NAMESPACE_EXI4JSON, LOCALNAME_NULL, null);
				if(key == null) {
					printDebugInd(ind, "<null />" );
				} else {
					printDebugInd(ind, "<null key=\"" + key + "\" />");
					bodyEncoder.encodeAttribute("", LOCALNAME_KEY, null, new StringValue(key));
					key = null;
				}
				bodyEncoder.encodeEndElement();
				break;
			default:
				throw new RuntimeException("Not supported JSON event: " + e);
			}
		}
		
		bodyEncoder.encodeEndDocument();
		bodyEncoder.flush();
	}
	
}
