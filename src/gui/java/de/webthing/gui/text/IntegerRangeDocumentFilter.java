package de.webthing.gui.text;

import java.math.BigInteger;

import javax.swing.text.BadLocationException;

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