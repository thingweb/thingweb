package de.webthing.thing;


/**
 * This class is immutable.
 */
public class Property {
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
