package de.webthing.gui.text;

import javax.swing.text.BadLocationException;

public class BooleanDocumentFilter extends AbstractDocumentFilter {
	
		public BooleanDocumentFilter() {
			super();
		}

		@Override
		Object checkInput(String proposedValue, int offset)
				throws BadLocationException {
			// "true", "false", "0", "1"
			if (proposedValue.length() > 0) {
				if("true".startsWith(proposedValue)) {
					// ok
				} else if("false".startsWith(proposedValue)) {
					// ok
				} else if("1".startsWith(proposedValue)) {
					// ok
				} else if("0".startsWith(proposedValue)) {
					// ok
				} else {
					throw new BadLocationException(proposedValue, offset);
				}
			}
			return proposedValue;
		}
	}