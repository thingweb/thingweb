package de.thingweb.desc;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.thingweb.security.TokenRequirements;
import de.thingweb.servient.ServientBuilder;
import de.thingweb.servient.ThingInterface;
import de.thingweb.servient.ThingServer;

public class ThingDescriptionRoundtripTest {

	// private static final JsonNodeFactory factory = new JsonNodeFactory(false);
    
    @Test
    public void testRoundtripUris1() throws Exception
    {
    	String tdVoter = "{\r\n" + 
    			"    \"@context\": [\"http://w3c.github.io/wot/w3c-wot-td-context.jsonld\",\r\n" + 
    			"              {\r\n" + 
    			"                 \"vote\": \"http://thingvote.org/\"\r\n" + 
    			"              }],\r\n" + 
    			"  \"name\": \"testvoter\",\r\n" + 
    			"  \"uris\": [\r\n" + 
    			"    \"coap://192.168.178.54/things/voter\",\r\n" + 
    			"    \"http://192.168.178.54:8080/things/voter\"\r\n" + 
    			"  ],\r\n" + 
//    			"  \"uris\": \"coap://192.168.178.54/things/voter\" \r\n" +
//    			"  ,\r\n" + 
    			"  \"properties\": [\r\n" + 
    			"    {\r\n" + 
    			"      \"name\": \"votes\",\r\n" + 
    			"      \"writable\": false,\r\n" + 
    			"      \"valueType\": {\r\n" + 
    			"        \"type\": \"number\"\r\n" + 
    			"      },\r\n" + 
    			"      \"hrefs\": [\r\n" + 
    			"        \"votes\",\r\n" + 
    			"        \"votes\"\r\n" + 
    			"      ]\r\n" + 
    			"    }\r\n" + 
    			"  ],\r\n" + 
    			"  \"actions\": [\r\n" + 
    			"    {\r\n" + 
    			"      \"@type\" : \"vote:tooCold\",\r\n" + 
    			"      \"name\": \"tooCold\",\r\n" + 
    			"      \"hrefs\": [\r\n" + 
    			"        \"tooCold\",\r\n" + 
    			"        \"tooCold\"\r\n" + 
    			"      ]\r\n" + 
    			"    },\r\n" + 
    			"    {\r\n" + 
    			"    \"@type\" : \"vote:tooHot\",\r\n" + 
    			"    \"name\": \"tooHot\",\r\n" + 
    			"      \"hrefs\": [\r\n" + 
    			"        \"tooHot\",\r\n" + 
    			"        \"tooHot\"\r\n" + 
    			"      ]\r\n" + 
    			"    }\r\n" + 
    			"  ],\r\n" + 
    			"  \"events\": [\r\n" + 
    			"    {\r\n" + 
    			"      \"name\": \"tooCold\",\r\n" + 
    			"      \"hrefs\": [\r\n" + 
    			"        \"tooCold\",\r\n" + 
    			"        \"tooCold\"\r\n" + 
    			"      ]\r\n" + 
    			"    },\r\n" + 
    			"    {\r\n" + 
    			"      \"name\": \"tooHot\",\r\n" + 
    			"      \"hrefs\": [\r\n" + 
    			"        \"tooHot\",\r\n" + 
    			"        \"tooHot\"\r\n" + 
    			"      ]\r\n" + 
    			"    }\r\n" + 
    			"  ]\r\n" + 
    			"}";
    	
    	
    	
		ServientBuilder.initialize();
		final TokenRequirements tokenRequirements = NicePlugFestTokenReqFactory.createTokenRequirements();
		ThingServer mbts = ServientBuilder.newThingServer(tokenRequirements);
    	
    	
    	// MultiBindingThingServer mbts = new MultiBindingThingServer();
    	mbts.addThing(ThingDescriptionParser.fromBytes(tdVoter.getBytes()));
    	ThingInterface ti = mbts.getThing("testvoter");
    	ObjectNode generated = ThingDescriptionParser.toJsonObject(ti.getThingModel(), ThingDescriptionVersion.VERSION_1);
    	
    	// Note: on should stil contains array of uris
    	JsonNode jn = generated.get("uris");
    	assertTrue(jn != null);
    	assertTrue("Expected array but was " + jn.getNodeType(), jn.getNodeType() == JsonNodeType.ARRAY);
    	// System.out.println(jn);
    	
    	// Note: Order or entries in uris array causes troubles
    	// ObjectMapper mapper = new ObjectMapper();
    	// JsonNode original = mapper.readValue(tdVoter, JsonNode.class);
    	// assertTrue(original.equals(generated));
    	
    }
    
    
	static class NicePlugFestTokenReqFactory {

		private static final String ISSUER = "NicePlugfestAS";
		private static final String AUDIENCE = "NicePlugfestRS";
		private static final String PUBKEY_ES256 = "{\"keys\":[{\"kty\": \"EC\",\"d\": \"_hysUUk5sRGAHhl7RJN7x5UhBMiy6pl6kHR5-ZaWzpU\",\"use\": \"sig\",\"crv\": \"P-256\",\"kid\": \"PlugFestNice\",\"x\": \"CQsJZUvJWx5yB5EwuipDXRDye4Ybg0wwqxpGgZtcl3w\",\"y\": \"qzYskD2N7GrGDSgo6N9pPLXMIwr6jowFGyqsTJGmpz4\",\"alg\": \"ES256\"},{\"kty\": \"oct\",\"kid\": \"018c0ae5-4d9b-471b-bfd6-eef314bc7037\",\"use\": \"sig\",\"alg\": \"HS256\",\"k\": \"aEp0WElaMnVTTjVrYlFmYnRUTldicGRtaGtWOEZKRy1PbmJjNm14Q2NZZw==\"}]}";
		// private static final String SUBJECT =
		// "0c5f83a7-cf08-4f48-8337-bfc65ea149ff";
		private static final String TYPE = "org:w3:wot:jwt:as:min";

		public static TokenRequirements createTokenRequirements() {
			return TokenRequirements.build().setIssuer(ISSUER).setAudience(AUDIENCE).setVerificationKeys(PUBKEY_ES256)
					.setTokenType(TYPE).createTokenRequirements();
		}
	}
	
}
