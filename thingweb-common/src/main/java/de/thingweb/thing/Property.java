/*
 *
 *  * The MIT License (MIT)
 *  *
 *  * Copyright (c) 2016 Siemens AG and the thingweb community
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in
 *  * all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  * THE SOFTWARE.
 *
 */

package de.thingweb.thing;


import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * This class is immutable.
 */
public class Property extends Observable {
	/*
	 * Implementation Note:
	 * Thing relies on this class to be immutable for synchronization purposes!
	 */
	
	private final String m_name;
	private final String m_propertyType;
	private final String m_valueType;
	private final boolean m_isReadable;
	private final boolean m_isWriteable;
	private final List<String> m_hrefs;
	
	

	
	protected Property(String name, String xsdType, boolean isReadable, boolean isWriteable, String propertyType, List<String> hrefs) {
		if (null == name) {
			throw new IllegalArgumentException("name must not be null");
		}
		
		this.m_valueType = xsdType;
		m_name = name;
		m_isReadable = isReadable;
		m_isWriteable = isWriteable;
		m_propertyType = propertyType;
		m_hrefs = hrefs;
	}

	public static Property.Builder getBuilder(String name) {
		return new Property.Builder(name);
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

	public String getValueType() {
		return m_valueType;
	}
	
	public String getPropertyType(){
		return m_propertyType;
	}
	
	public List<String> getHrefs(){
		return m_hrefs;
	}

	public static class Builder {
		private String name;
		private boolean isReadable = true;
		private boolean isWriteable = false;
		private String xsdType = "xsd:string";
		private String propertyType = null;
		private List<String> hrefs = new ArrayList<>();

		public Builder(String name) {
			this.name = name;
		}

		public Builder setXsdType(String xsdType) {
			this.xsdType = xsdType;
			return this;
		}
		
		public Builder setPropertyType(String propertyType) {
			this.propertyType = propertyType;
			return this;
		}
		public Builder setHrefs(List<String> hrefs) {
			this.hrefs = hrefs;
			return this;
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
			return new Property(name, xsdType, isReadable, isWriteable, propertyType, hrefs);
		}
	}

}
