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

package de.webthing.thing;


import java.util.Observable;

/**
 * This class is immutable.
 */
public class Property extends Observable {
	/*
	 * Implementation Note:
	 * Thing relies on this class to be immutable for synchronization purposes!
	 */

	protected Property(String name, boolean isReadable, boolean isWriteable) {
		if (null == name) {
			throw new IllegalArgumentException("name must not be null");
		}
		
		m_name = name;
		m_isReadable = isReadable;
		m_isWriteable = isWriteable;
	}


	@Override
	public synchronized void setChanged() {
		super.setChanged();
	}

	public String getName() {
		return m_name;
	}
	

	public boolean isReadable() {
		return m_isReadable;
	}

	
	public boolean isWriteable() {
		return m_isWriteable;
	}

	
	private final String m_name;
	
	
	private final boolean m_isReadable;
	
	
	private final boolean m_isWriteable;

	public static Property.Builder getBuilder(String name) {
		return new Property.Builder(name);
	}

	public static class Builder {
		private String name;
		private boolean isReadable = true;
		private boolean isWriteable = false;

		public Builder(String name) {
			this.name = name;
		}

		public Property.Builder setName(String name) {
			this.name = name;
			return this;
		}

		public Property.Builder setReadable(boolean isReadable) {
			this.isReadable = isReadable;
			return this;
		}

		public Property.Builder setWriteable(boolean isWriteable) {
			this.isWriteable = isWriteable;
			return this;
		}

		public Property build() {
			return new Property(name, isReadable, isWriteable);
		}
	}
}
