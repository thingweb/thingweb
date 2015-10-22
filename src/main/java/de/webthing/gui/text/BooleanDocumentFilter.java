package de.webthing.gui.text;

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