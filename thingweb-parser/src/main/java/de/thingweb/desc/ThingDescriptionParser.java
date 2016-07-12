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

package de.thingweb.desc;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.json.EXIforJSONParser;
import de.thingweb.thing.Action;
import de.thingweb.thing.Event;
import de.thingweb.thing.Property;
import de.thingweb.thing.Thing;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ThingDescriptionParser {

	private static final String WOT_TD_CONTEXT = "http://w3c.github.io/wot/w3c-wot-td-context.jsonld";
	private static final JsonNodeFactory factory = new JsonNodeFactory(false);
	private static final ObjectMapper mapper = new ObjectMapper();

	public static Thing fromJavaMap(Object json) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readValue(json.toString(), JsonNode.class);

		try {
			return parse(root);
		} catch (Exception e) {
			return parseOld(root);
		}
	}

	public static Thing fromURL(URL url) throws JsonParseException, IOException {

		InputStream is = new BufferedInputStream(url.openStream());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int b;
		while ((b = is.read()) != -1) {
			baos.write(b);
		}
		return fromBytes(baos.toByteArray());
	}

	static List<String> SHARED_STRINGS_EXI_FOR_JSON = Arrays.asList(new String[] { "@context", "@id", "@type", "@value",
			"Brightness", "Car", "CoAP", "DecreaseColor", "Distance", "Door", "EXI", "EXI4JSON", "Fan", "HTTP",
			"IncreaseColor", "JSON", "Lamp", "Lighting", "Off", "On", "OnOff", "PowerConsumption", "RGBColor",
			"RGBColorBlue", "RGBColorGreen", "RGBColorRed", "Speed", "Start", "Stop", "Switch", "Temperature", "Thing",
			"Toggle", "TrafficLight", "WS", "actions", "associations", "celsius", "dogont", "encodings", "events",
			"hrefs", "http://w3c.github.io/wot/w3c-wot-td-context.jsonld",
			"https://w3c.github.io/wot/w3c-wot-common-context.jsonld", "inch", "inputData", "interactions", "joule",
			"kelvin", "kmh", "kwh", "lgdo", "m", "max", "mile", "min", "mm", "mph", "name", "outputData", "properties",
			"protocols", "qu", "reference", "schema", "security", "unit", "uris", "valueType", "writable",
			"xsd:boolean", "xsd:byte", "xsd:float", "xsd:int", "xsd:short", "xsd:string", "xsd:unsignedByte",
			"xsd:unsignedInt", "xsd:unsignedShort" });

	public static Thing fromBytes(byte[] data) throws JsonParseException, IOException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			EXIFactory ef = DefaultEXIFactory.newInstance();
			ef.setSharedStrings(SHARED_STRINGS_EXI_FOR_JSON);

			EXIforJSONParser e4j = new EXIforJSONParser(ef);
			e4j.parse(new ByteArrayInputStream(data), baos);

			// push-back EXI-generated JSON
			data = baos.toByteArray();
		} catch (EXIException | IOException e) {
			// something went wrong with EXI --> use "plain-text" json
		}

		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readValue(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"),
				JsonNode.class);

		try {
			return parse(root);
		} catch (Exception e) {
			// try old parser if by chance it was an old TD
			return parseOld(root);
		}
	}

	public static Thing fromFile(String fname) throws FileNotFoundException, IOException {
		Path path = Paths.get(fname);
		byte[] data = Files.readAllBytes(path);
		return fromBytes(data);
	}

	public static byte[] toBytes(Thing thing) throws IOException {
		ObjectNode td = toJsonObject(thing);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// TODO catch the IOException here and throw a runtimeexception
		// as this is no case where a developer could react
		mapper.writeValue(baos, td);

		return baos.toByteArray();
	}

	// TODO set as private (and check it is not called elsewhere)
	public static ObjectNode toJsonObject(Thing thing) {

		ObjectNode td = factory.objectNode();
		if (thing.getMetadata().get("@context") == null
				|| thing.getMetadata().get("@context").getNodeType() == JsonNodeType.NULL) {
			td.put("@context", factory.textNode(WOT_TD_CONTEXT));
		} else {
			td.put("@context", thing.getMetadata().get("@context"));
		}
		td.put("name", thing.getName());
		if (thing.getMetadata().contains("@type")) {
			td.put("@type", thing.getMetadata().get("@type"));
		}

		if (thing.getMetadata().contains("security")) {
			td.put("security", thing.getMetadata().get("security"));
		}

		if (thing.getMetadata().contains("encodings")) {
			// ArrayNode encodings = factory.arrayNode();
			// for (String e : thing.getMetadata().getAll("encodings")) {
			// encodings.add(e);
			// }
			td.put("encodings", thing.getMetadata().get("encodings"));
		}

		if (thing.getMetadata().contains("uris")) {
//			 ArrayNode uris = factory.arrayNode();
//			 for (JsonNode uri : thing.getMetadata().getAll("uris")) {
//				 uris.add(uri);
//			 }
			// // TODO array even if single value?
			// td.put("uris", uris);
			td.put("uris", thing.getMetadata().get("uris"));
		}

		ArrayNode properties = factory.arrayNode();
		for (Property prop : thing.getProperties()) {
			ObjectNode p = factory.objectNode();
			if (prop.getPropertyType() != null && prop.getPropertyType().length() > 0) {
				p.put("@type", prop.getPropertyType());
			}
			p.put("name", prop.getName());
			p.put("writable", prop.isWritable());
			p.put("valueType", prop.getValueType());

			if (prop.getHrefs().size() > 1) {
				ArrayNode hrefs = factory.arrayNode();
				for (String href : prop.getHrefs()) {
					hrefs.add(href);
				}
				p.put("hrefs", hrefs);
			} else if (prop.getHrefs().size() == 1) {
				p.put("hrefs", factory.textNode(prop.getHrefs().get(0)));
			}
			if (prop.getStability() != null) {
				p.put("stability", prop.getStability());
			}

			properties.add(p);
		}
		td.put("properties", properties);

		ArrayNode actions = factory.arrayNode();
		for (Action action : thing.getActions()) {
			ObjectNode a = factory.objectNode();
			if (action.getActionType() != null && action.getActionType().length() > 0) {
				a.put("@type", action.getActionType());
			}
			a.put("name", action.getName());

			if (action.getInputType() != null) {
				ObjectNode in = factory.objectNode();
				in.put("valueType", action.getInputType());
				a.put("inputData", in);
			}

			if (action.getOutputType() != null) {
				ObjectNode out = factory.objectNode();
				out.put("valueType", action.getOutputType());
				a.put("outputData", out);
			}

			if (action.getHrefs().size() > 1) {
				ArrayNode hrefs = factory.arrayNode();
				for (String href : action.getHrefs()) {
					hrefs.add(href);
				}
				a.put("hrefs", hrefs);
			} else if (action.getHrefs().size() == 1) {
				a.put("hrefs", factory.textNode(action.getHrefs().get(0)));
			}

			actions.add(a);
		}
		td.put("actions", actions);

		ArrayNode events = factory.arrayNode();
		for (Event event : thing.getEvents()) {
			ObjectNode a = factory.objectNode();
			if (event.getEventType() != null && event.getEventType().length() > 0) {
				a.put("@type", event.getEventType());
			}
			a.put("name", event.getName());

			if (event.getValueType() != null) {
				a.put("valueType", event.getValueType());
			}

			if (event.getHrefs().size() > 1) {
				ArrayNode hrefs = factory.arrayNode();
				for (String href : event.getHrefs()) {
					hrefs.add(href);
				}
				a.put("hrefs", hrefs);
			} else if (event.getHrefs().size() == 1) {
				a.put("hrefs", factory.textNode(event.getHrefs().get(0)));
			}

			events.add(a);
		}
		td.put("events", events);

		return td;
	}

	/**
	 * reshapes the input JSON-LD object using the standard Thing Description
	 * context and having the thing description resource as object root.
	 * 
	 * @param data
	 *            UTF-8 encoded JSON-LD object
	 * @return the reshaped JSON-LD object
	 * @throws IOException
	 *             error
	 */
	public static String reshape(byte[] data) throws IOException {
		ObjectMapper om = new ObjectMapper();

		try {
			Object jsonld = JsonUtils.fromInputStream(new ByteArrayInputStream(data));
			// TODO put the frame online instead
			Object frame = om.readValue("{\"http://www.w3c.org/wot/td#hasMetadata\":{}}", HashMap.class);

			jsonld = JsonLdProcessor.frame(jsonld, frame, new JsonLdOptions());
			return null;
			// return removeBlankNodes(compactJson(jsonld)).toString();
		} catch (JsonLdError e) {
			throw new IOException("Can't reshape triples", e);
		}
	}

	@Deprecated
	private static Thing parseOld(JsonNode td) throws IOException {
		try {
			Thing thing = new Thing(td.get("metadata").get("name").asText());

			Iterator<String> tdIterator = td.fieldNames();
			while (tdIterator.hasNext()) {
				switch (tdIterator.next()) {
				case "metadata":
					Iterator<String> metaIterator = td.get("metadata").fieldNames();
					while (metaIterator.hasNext()) {
						switch (metaIterator.next()) {
						case "encodings":
							for (JsonNode encoding : td.get("metadata").get("encodings")) {
								thing.getMetadata().add("encodings", encoding);
							}
							break;

						case "protocols":
							TreeMap<Long, String> orderedURIs = new TreeMap<>();
							for (JsonNode protocol : td.get("metadata").get("protocols")) {
								orderedURIs.put(protocol.get("priority").asLong(), protocol.get("uri").asText());
							}
							if (orderedURIs.size() == 1) {
								thing.getMetadata().add("uris", factory.textNode(orderedURIs.get(0)));
							} else {
								ArrayNode an = factory.arrayNode();
								for (String uri : orderedURIs.values()) {
									// values returned in ascending order
									an.add(uri);
								}
								thing.getMetadata().add("uris", an);
							}

							break;
						}
					}
					break;

				case "interactions":
					for (JsonNode inter : td.get("interactions")) {
						if (inter.get("@type").asText().equals("Property")) {
							Property.Builder builder = Property.getBuilder(inter.get("name").asText());
							Iterator<String> propIterator = inter.fieldNames();
							while (propIterator.hasNext()) {
								switch (propIterator.next()) {
								case "outputData":
									builder.setValueType(inter.get("outputData"));
									break;
								case "writable":
									builder.setWriteable(inter.get("writable").asBoolean());
									break;
								}
							}
							thing.addProperty(builder.build());
						} else if (inter.get("@type").asText().equals("Action")) {
							Action.Builder builder = Action.getBuilder(inter.get("name").asText());
							Iterator<String> actionIterator = inter.fieldNames();
							while (actionIterator.hasNext()) {
								switch (actionIterator.next()) {
								case "inputData":
									builder.setInputType(inter.get("inputData").asText());
									break;
								case "outputData":
									builder.setOutputType(inter.get("outputData").asText());
									break;
								}
							}
							thing.addAction(builder.build());
						} else if (inter.get("@type").asText().equals("Event")) {
							Event.Builder builder = Event.getBuilder(inter.get("name").asText());
							Iterator<String> actionIterator = inter.fieldNames();
							while (actionIterator.hasNext()) {
								switch (actionIterator.next()) {
								case "outputData":
									builder.setValueType(inter.get("outputData"));
									break;
								}
							}
							thing.addEvent(builder.build());
						}
					}
					break;
				}
			}

			return thing;
		} catch (Exception e) { // anything could happen here
			throw new IOException("unable to parse Thing Description");
		}
	}

	private static Thing parse(JsonNode td) throws Exception {
		// ProcessingReport report =
		// JsonSchemaFactory.byDefault().getValidator().validate(TD_SCHEMA, td);
		// if (!report.isSuccess()) {
		// throw new IOException("JSON data not valid");
		// }

		Thing thing = new Thing(td.get("name").asText());

		Iterator<String> tdIterator = td.fieldNames();
		while (tdIterator.hasNext()) {
			switch (tdIterator.next()) {
			case "@context":
				if (td.get("@context") == null || td.get("@context").getNodeType() == JsonNodeType.NULL) {
					thing.getMetadata().add("@context", factory.textNode(WOT_TD_CONTEXT));
				} else {
					thing.getMetadata().add("@context", td.get("@context"));
				}
				break;
			case "uris":
				thing.getMetadata().add("uris", td.get("uris"));
				break;
			case "@type":
				thing.getMetadata().add("@type", td.get("@type"));
				break;
			case "security":
				thing.getMetadata().add("security", td.get("security"));
				break;

			case "properties":
				for (JsonNode prop : td.get("properties")) {
					Property.Builder builder = Property.getBuilder(prop.get("name").asText());
					Iterator<String> it = prop.fieldNames();
					while (it.hasNext()) {
						switch (it.next()) {
						case "valueType":
							JsonNode jn = prop.get("valueType");
							builder.setValueType(jn);
							break;
						case "@type":
							builder.setPropertyType(prop.get("@type").asText());
							break;
						case "writable":
							builder.setWriteable(prop.get("writable").asBoolean());
							break;
						case "hrefs":
							builder.setHrefs(stringOrArray(prop.get("hrefs")));
							break;
						case "security":
							builder.setSecurity(prop.get("security"));
							break;
						case "stability":
							builder.setStability(prop.get("stability").asInt());
							break;
						}
					}
					thing.addProperty(builder.build());
				}
				break;

			case "actions":
				for (JsonNode action : td.get("actions")) {
					Action.Builder builder = Action.getBuilder(action.get("name").asText());
					Iterator<String> it = action.fieldNames();
					while (it.hasNext()) {
						switch (it.next()) {
						case "inputData":
							JsonNode jnI = action.get("inputData").get("valueType");
							builder.setInputType(jnI);
							break;
						case "outputData":
							JsonNode jnO = action.get("outputData").get("valueType");
							builder.setOutputType(jnO);
							break;
						case "@type":
							builder.setActionType(action.get("@type").asText());
							break;
						case "hrefs":
							builder.setHrefs(stringOrArray(action.get("hrefs")));
							break;
						case "security":
							builder.setSecurity(action.get("security"));
							break;
						}
					}
					thing.addAction(builder.build());
				}
				break;

			case "events":
				for (JsonNode event : td.get("events")) {
					Event.Builder builder = Event.getBuilder(event.get("name").asText());
					Iterator<String> it = event.fieldNames();
					while (it.hasNext()) {
						switch (it.next()) {
						case "valueType":
							JsonNode jn = event.get("valueType");
							builder.setValueType(jn);
							break;
						case "@type":
							builder.setEventType(event.get("@type").asText());
							break;
						case "hrefs":
							builder.setHrefs(stringOrArray(event.get("hrefs")));
							break;
						case "security":
							builder.setSecurity(event.get("security"));
							break;
						}
					}
					thing.addEvent(builder.build());
				}
				break;

			case "encodings":
				thing.getMetadata().add("encodings", td.get("encodings"));
				break;
			}
		}

		return thing;
	}

	private static List<String> stringOrArray(JsonNode node) {
		List<String> array = new ArrayList<String>();

		if (node.isTextual()) {
			array.add(node.asText());
		} else if (node.isArray()) {
			for (JsonNode subnode : node) {
				array.add(subnode.asText());
			}
		}

		return array;
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		Thing thing = fromFile("jsonld" + File.separator + "led.v2.jsonld");
		System.out.println(new String(toBytes(thing)));
	}

}
