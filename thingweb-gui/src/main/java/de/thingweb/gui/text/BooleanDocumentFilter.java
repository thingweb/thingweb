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

package de.thingweb.gui.text;

import javax.swing.text.BadLocationException;

public class BooleanDocumentFilter extends AbstractDocumentFilter {

	final static String BOOLEAN_1 = "1";
	final static String BOOLEAN_0 = "0";
	final static String BOOLEAN_TRUE = "true";
	final static String BOOLEAN_FALSE = "false";

	public BooleanDocumentFilter() {
		super();
	}

	@Override
	Object checkInput(String proposedValue, int offset) throws BadLocationException {
		// "true", "false", "0", "1"
		if (proposedValue.length() > 0) {
			if (BOOLEAN_TRUE.startsWith(proposedValue)) {
				// ok
			} else if (BOOLEAN_FALSE.startsWith(proposedValue)) {
				// ok
			} else if (BOOLEAN_1.startsWith(proposedValue)) {
				// ok
			} else if (BOOLEAN_0.startsWith(proposedValue)) {
				// ok
			} else {
				throw new BadLocationException(proposedValue, offset);
			}
		}
		return proposedValue;
	}
	
	public static boolean getBoolean(String s) throws IllegalArgumentException {
		switch(s) {
		case BOOLEAN_1:
		case BOOLEAN_TRUE:
			return true;
		case BOOLEAN_0:
		case BOOLEAN_FALSE:
			return false;
		default:
			throw new IllegalArgumentException("Value '" + s + "' not convertable to Boolean");
		}
	}
}