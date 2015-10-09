package de.webthing.servient.impl;

import de.webthing.binding.AbstractRESTListener;
import de.webthing.thing.Content;
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
	public Content onGet() {
		if (!property.isReadable()) {
			throw new UnsupportedOperationException();
		}

		Content c = multiBindingThingServer.readProperty(property);
		return c;
	}

    @Override
	public void onPut(Content data) {
		if (!property.isWriteable()) {
			throw new UnsupportedOperationException();
		}

		multiBindingThingServer.writeProperty(property, data);
	}
}
