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

import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class HintTextFieldUI extends BasicTextFieldUI 
implements FocusListener {

	private String hint;
	private boolean hideOnFocus;
	private Color color;

	public HintTextFieldUI(String hint) {
		this(hint, false);
	}

	public HintTextFieldUI(String hint, boolean hideOnFocus) {
		this(hint, hideOnFocus, null);
	}

	public HintTextFieldUI(String hint, boolean hideOnFocus, Color color) {
		this.hint = hint;
		this.hideOnFocus = hideOnFocus;
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		repaint();
	}

	private void repaint() {
		if (getComponent() != null) {
			getComponent().repaint();
		}
	}

	public boolean isHideOnFocus() {
		return hideOnFocus;
	}

	public void setHideOnFocus(boolean hideOnFocus) {
		this.hideOnFocus = hideOnFocus;
		repaint();
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
		repaint();
	}


	@Override
	protected void paintSafely(Graphics g) {
		super.paintSafely(g);
		JTextComponent comp = getComponent();
		if (hint != null && comp.getText().length() == 0
				&& (!(hideOnFocus && comp.hasFocus()))) {
			if (color != null) {
				g.setColor(color);
			} else {
				g.setColor(comp.getForeground().brighter().brighter()
						.brighter());
			}
			int padding = (comp.getHeight() - comp.getFont().getSize()) / 2;
			g.drawString(hint, 2, comp.getHeight() - padding - 1);
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (hideOnFocus)
			repaint();

	}

	@Override
	public void focusLost(FocusEvent e) {
		if (hideOnFocus)
			repaint();
	}

	@Override
	protected void installListeners() {
		super.installListeners();
		getComponent().addFocusListener(this);
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();
		getComponent().removeFocusListener(this);
	}
}