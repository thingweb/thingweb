package de.webthing.servient.impl;

import de.webthing.binding.AbstractRESTListener;
import de.webthing.thing.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Johannes on 07.10.2015.
 */
class PropertyListener extends AbstractRESTListener {
    private final Property property;
    private MultiBindingThingServer multiBindingThingServer;
	private static final Logger log = LoggerFactory.getLogger(PropertyListener.class);

	public PropertyListener(MultiBindingThingServer multiBindingThingServer, Property property) {
        this.property = property;
        this.multiBindingThingServer = multiBindingThingServer;
    }

    @Override
	public byte[] onGet() {
		if (!property.isReadable()) {
			throw new UnsupportedOperationException();
		}

		Object o = multiBindingThingServer.readProperty(property);
		if (o instanceof byte[]) {
			return ((byte[]) o);
		} else if (o instanceof String) {
			return ((String) o).getBytes();
		} else {
			// TODO how to best inform?
			log.warn("property " + property + " does not return value of type byte[]");
			return new byte[0];
		}
	}

    @Override
	public void onPut(byte[] data) {
		if (!property.isWriteable()) {
			throw new UnsupportedOperationException();
		}

		multiBindingThingServer.writeProperty(property, data);
	}
}
