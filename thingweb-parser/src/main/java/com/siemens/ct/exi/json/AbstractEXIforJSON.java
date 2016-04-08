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
import java.net.URL;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

public abstract class AbstractEXIforJSON {

	static final String NAMESPACE_EXI4JSON = "http://www.w3.org/2015/EXI/json";
	
	static final String LOCALNAME_MAP = "map";
	static final String LOCALNAME_ARRAY = "array";
	static final String LOCALNAME_STRING = "string";
	static final String LOCALNAME_NUMBER = "number";
	static final String LOCALNAME_BOOLEAN = "boolean";
	static final String LOCALNAME_NULL = "null";
	static final String LOCALNAME_KEY = "key";
	
	
	final EXIFactory ef;
	
	public AbstractEXIforJSON() throws EXIException, IOException {
		this(DefaultEXIFactory.newInstance());
	}
	
	public AbstractEXIforJSON(EXIFactory ef) throws EXIException, IOException {
		this.ef = ef;
		
		// setup EXI schema
		URL urlXSD = new URL("http://www.w3.org/XML/EXI/docs/json/schema-for-json.xsd");
		// URL urlXSD  = this.getClass().getResource("/schema-for-json.xsd");
		InputStream isXSD = urlXSD.openStream();
		Grammars g = GrammarFactory.newInstance().createGrammars(isXSD);
		isXSD.close();
		ef.setGrammars(g);
		
		// set to strict
		ef.getFidelityOptions().setFidelity(FidelityOptions.FEATURE_STRICT, true);
	}
	
}
