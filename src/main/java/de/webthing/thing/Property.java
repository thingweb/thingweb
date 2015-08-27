package de.webthing.thing;


/**
 * This class is immutable.
 */
public class Property {
	/*
	 * Implementation Note:
	 * Thing relies on this class to be immutable for synchronization purposes!
	 */

	public Property(String name, boolean isReadable, boolean isWriteable) {
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
}
