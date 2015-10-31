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
import java.math.BigInteger;

public class IntegerRangeDocumentFilter extends AbstractDocumentFilter {

		BigInteger minimum, maximum;
		
		public IntegerRangeDocumentFilter(long minimum, long maximum) {
			this(BigInteger.valueOf(minimum), BigInteger.valueOf(maximum));
		}
		
		public IntegerRangeDocumentFilter(BigInteger minimum, BigInteger maximum) {
			super();
			this.minimum = minimum;
			this.maximum = maximum;
		}

		@Override
		Object checkInput(String proposedValue, int offset)
				throws BadLocationException {
			BigInteger newValue = BigInteger.ZERO;
			if (proposedValue.length() > 0) {
				try {
					newValue = new BigInteger(proposedValue);
					// newValue = Integer.parseInt(proposedValue);
				} catch (NumberFormatException e) {
					throw new BadLocationException(proposedValue, offset);
				}
			}
			if(minimum.compareTo(newValue) <= 0 && newValue.compareTo(maximum) <= 0) {
			//if ((minimum <= newValue) && (newValue <= maximum)) {
				return newValue;
			} else {
				throw new BadLocationException(proposedValue, offset);
			}
		}
	}