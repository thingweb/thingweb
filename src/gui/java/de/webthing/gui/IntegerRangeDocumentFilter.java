package de.webthing.gui;

import javax.swing.text.BadLocationException;

public class IntegerRangeDocumentFilter extends AbstractDocumentFilter {

		int minimum, maximum;

		public IntegerRangeDocumentFilter(int minimum, int maximum) {
			super();
			this.minimum = minimum;
			this.maximum = maximum;
		}

		@Override
		Object checkInput(String proposedValue, int offset)
				throws BadLocationException {
			int newValue = 0;
			if (proposedValue.length() > 0) {
				try {
					newValue = Integer.parseInt(proposedValue);
				} catch (NumberFormatException e) {
					throw new BadLocationException(proposedValue, offset);
				}
			}
			if ((minimum <= newValue) && (newValue <= maximum)) {
				return newValue;
			} else {
				throw new BadLocationException(proposedValue, offset);
			}
		}
	}