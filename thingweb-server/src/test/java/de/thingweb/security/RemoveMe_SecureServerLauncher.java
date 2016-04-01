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

package de.thingweb.security;

import de.thingweb.desc.ThingDescriptionParser;
import de.thingweb.servient.ServientBuilder;
import de.thingweb.servient.TestTools;
import de.thingweb.servient.ThingServer;
import de.thingweb.thing.Thing;

/**
 * This class totally does not belong here and should not exist at all.
 * Expect it to disappear without any warning.
 * Fnord.
 *
 * Created by Johannes on 13.01.2016.
 */
public class RemoveMe_SecureServerLauncher {

    private static final String ISSUER = "NicePlugfestAS";
    private static final String AUDIENCE = "NicePlugfestRS";
    private static final String PUBKEY_ES256 = "{\"keys\":[{\"kty\": \"EC\",\"d\": \"_hysUUk5sRGAHhl7RJN7x5UhBMiy6pl6kHR5-ZaWzpU\",\"use\": \"sig\",\"crv\": \"P-256\",\"kid\": \"PlugFestNice\",\"x\": \"CQsJZUvJWx5yB5EwuipDXRDye4Ybg0wwqxpGgZtcl3w\",\"y\": \"qzYskD2N7GrGDSgo6N9pPLXMIwr6jowFGyqsTJGmpz4\",\"alg\": \"ES256\"},{\"kty\": \"oct\",\"kid\": \"018c0ae5-4d9b-471b-bfd6-eef314bc7037\",\"use\": \"sig\",\"alg\": \"HS256\",\"k\": \"aEp0WElaMnVTTjVrYlFmYnRUTldicGRtaGtWOEZKRy1PbmJjNm14Q2NZZw==\"}]}";
    private static final String SUBJECT = "0c5f83a7-cf08-4f48-8337-bfc65ea149ff";
    private static final String TYPE = "org:w3:wot:jwt:as:min";

    public static void main(String[] args) throws Exception {
        final TokenRequirements tokenRequirements = TokenRequirements.build()
                .setIssuer(ISSUER)
                .setAudience(AUDIENCE)
                .setVerificationKeys(PUBKEY_ES256)
                .setTokenType(TYPE)
                .createTokenRequirements();

        ServientBuilder.initialize();
        String json = TestTools.readResource("simplething.jsonld");
        Thing theThing = ThingDescriptionParser.fromBytes(json.getBytes());
        theThing.setProtection(true);
        ThingServer server = ServientBuilder.newThingServer(tokenRequirements, theThing);
        server.setTokenRequirements(tokenRequirements);
        ServientBuilder.start();
    }
}
