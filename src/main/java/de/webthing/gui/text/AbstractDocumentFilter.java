package de.webthing.gui.text;

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