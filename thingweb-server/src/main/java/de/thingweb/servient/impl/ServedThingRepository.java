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

package de.thingweb.servient.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

import de.thingweb.desc.ThingDescriptionParser;
import de.thingweb.discovery.TDRepository;
import de.thingweb.thing.Thing;


/**
 * Allows servients to push/update thing description to TD repository
 */
public class ServedThingRepository {
	
	protected final static Logger log = Logger.getLogger(ServedThingRepository.class.getCanonicalName());

	public static final String TD_REPOSITORY_PROPERTIES_FILE = "td-repository.props";
	public static final String TD_REPOSITORY_PROPERTIES_KEY_URI = "td.repository.uri";
	public static final String TD_REPOSITORY_PROPERTIES_KEY_THINGS_PREFIX = "td.repository.things.";
	
	public static final String TD_REPOSITORY_PROPERTIES_KEY_THINGS_TIMESTAMP_SUFFIX = ".lastUpdate";
	
    public static void updateTDRepository(Thing thing) {
        // load and set TD Repository 
        InputStream propIn = null;
        Properties props = new Properties();
        
        try {
            if(new File(TD_REPOSITORY_PROPERTIES_FILE).exists()) {
            	propIn = new FileInputStream(TD_REPOSITORY_PROPERTIES_FILE);
            	props.load(propIn);            	
            } 
        	
        	String tdRepoUri = props.getProperty(TD_REPOSITORY_PROPERTIES_KEY_URI);
        	if(tdRepoUri == null || tdRepoUri.length() == 0) {
        		tdRepoUri = TDRepository.ETH_URI;
        		props.setProperty(TD_REPOSITORY_PROPERTIES_KEY_URI, tdRepoUri);
        	}
        	
        	TDRepository tdRepository = new TDRepository(tdRepoUri);
        	
        	String tdRepoId = props.getProperty(TD_REPOSITORY_PROPERTIES_KEY_THINGS_PREFIX + thing.getName());
        	if(tdRepoId == null || tdRepoId.length() == 0) {
        		// not known yet to repository --> create one
        		tdRepoId = tdRepository.addTD(ThingDescriptionParser.toBytes(thing));
        		
        	} else {
        		// should be known to repository --> update
        		try {
        			tdRepository.updateTD(tdRepoId, ThingDescriptionParser.toBytes(thing));
        		} catch(Exception eTD) {
        			// Maybe repository does not know anymore about key due to internal clean-up --> add it again
            		tdRepoId = tdRepository.addTD(ThingDescriptionParser.toBytes(thing));
        		}
        	}
        	// update ID and timestamp
        	props.setProperty(TD_REPOSITORY_PROPERTIES_KEY_THINGS_PREFIX + thing.getName(), tdRepoId);
        	String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        	props.setProperty(TD_REPOSITORY_PROPERTIES_KEY_THINGS_PREFIX + thing.getName() + TD_REPOSITORY_PROPERTIES_KEY_THINGS_TIMESTAMP_SUFFIX, timeStamp);
	        
	        // TODO remove/cleanup "old" things that haven't been used for a while by timestamp?
	        
		} catch (Exception e) {
			log.warning("Issues while loading TD repository properties: " + e);
		} finally {
			if(propIn != null) {
				try {
					propIn.close();
				} catch (IOException e) {
					// ignore exception while closing
				}	
			}
		}
        
        // write/update repository file
        OutputStream propOut = null;
        try {
        	propOut = new FileOutputStream(TD_REPOSITORY_PROPERTIES_FILE);
        	props.store(propOut, "Properties for ThingRepository");
		} catch (Exception e) {
			log.warning("Issues while writing TD repository properties:" + e);
		} finally {
			if(propOut != null) {
				try {
					propOut.close();
				} catch (IOException e) {
					// ignore exception while closing
				}	
			}
		}
    }
    

}
