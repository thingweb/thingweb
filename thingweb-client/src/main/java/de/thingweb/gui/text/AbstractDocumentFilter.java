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

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

public abstract class AbstractDocumentFilter extends DocumentFilter {
	
		Object currentValue;

		public AbstractDocumentFilter() {
		}

		public void insertString(DocumentFilter.FilterBypass fb, int offset,
				String string, AttributeSet attr) throws BadLocationException {

			if (string == null) {
				return;
			} else {
				String newValue;
				Document doc = fb.getDocument();
				int length = doc.getLength();
				if (length == 0) {
					newValue = string;
				} else {
					String currentContent = doc.getText(0, length);
					StringBuilder currentBuffer = new StringBuilder(
							currentContent);
					currentBuffer.insert(offset, string);
					newValue = currentBuffer.toString();
				}
				currentValue = checkInput(newValue, offset);
				fb.insertString(offset, string, attr);
			}
		}

		public void remove(DocumentFilter.FilterBypass fb, int offset,
				int length) throws BadLocationException {

			Document doc = fb.getDocument();
			int currentLength = doc.getLength();
			String currentContent = doc.getText(0, currentLength);
			String before = currentContent.substring(0, offset);
			String after = currentContent.substring(length + offset,
					currentLength);
			String newValue = before + after;
			currentValue = checkInput(newValue, offset);
			fb.remove(offset, length);
		}

		public void replace(DocumentFilter.FilterBypass fb, int offset,
				int length, String text, AttributeSet attrs)
				throws BadLocationException {

			Document doc = fb.getDocument();
			int currentLength = doc.getLength();
			String currentContent = doc.getText(0, currentLength);
			String before = currentContent.substring(0, offset);
			String after = currentContent.substring(length + offset,
					currentLength);
			String newValue = before + (text == null ? "" : text) + after;
			currentValue = checkInput(newValue, offset);
			fb.replace(offset, length, text, attrs);
		}

		
		abstract Object checkInput(String proposedValue, int offset) throws BadLocationException;
	}