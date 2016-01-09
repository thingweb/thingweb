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

import de.thingweb.servient.TestTools;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.junit.Assert.fail;

/**
 * Created by Johannes on 03.01.2016.
 */
public class SecurityTokenValidator4NicePlugfestTest {

    private static final String ISSUER = "NicePlugfestAS";
    private static final String AUDIENCE = "NicePlugfestRS";
    private static final String PUBKEY_ES256 = "";
    private static final String SUBJECT = "0c5f83a7-cf08-4f48-8337-bfc65ea149ff";

    private SecurityTokenValidator validator;
    private TokenRequirementsBuilder builder;
    private Properties testVectors;

    @Before
    public void setUp() throws Exception {
        builder = TokenRequirements.build()
                .setIssuer(ISSUER)
                .setAudience(AUDIENCE)
                .setVerificationKey(PUBKEY_ES256);
        validator  = new SecurityTokenValidator4NicePlugfest(builder.createTokenRequirements());
        testVectors = TestTools.loadPropertiesFromResources("nice-plugfest-jwt-testvectors.properties");
    }

    @Test
    public void testValidMinimalES256Token() throws Exception {
        String jwt = "";

        //TODO remove this - disabling signature check for now
        builder.setValidateSignature(false);
        validator.setRequirements(builder.createTokenRequirements());

        //the provided method and uri is irrelevant for now
        String subject = validator.checkValidity("GET", "/", jwt);

        assertThat(subject,equalToIgnoringCase(SUBJECT));
    }

    @Test(expected = UnauthorizedException.class)
    public void testInvalidMinimalES256Token() throws Exception {
        String jwt = "";

        //the provided method and uri is irrelevant for now
        validator.checkValidity("GET", "/", jwt);
    }

    @Test
    public void testFailMinTokensES256() throws Exception {
        //TODO setup

        testVectors.keySet().parallelStream()
                .map(key -> (String) key)
                .filter(key -> key.startsWith("min-token.ES256") && !key.endsWith(".goodCase"))
                .forEach((name) -> {
                    String jwt = testVectors.getProperty(name);
                    System.out.println("Key:" + name + "\tToken: " + jwt);
                    try {
                        validator.checkValidity("GET", "/", jwt);
                        fail("false positive on " + name);
                    } catch (UnauthorizedException e) {
                        System.out.println("revoked " + name);
                    } catch (TokenExpiredException e) {
                        fail("Unexpected Expiery Exception");
                    }

                }
        );

        //commented out to keep build
        //validator.checkValidity("GET","/",testVectors.getProperty("min-token.HS256.goodCase"));
    }

    @Test
    public void testMinTokensHS256() throws Exception {
        //TODO setup

        testVectors.keySet().parallelStream()
                .map(key -> (String) key)
                .filter(key -> key.startsWith("min-token.HS256") && !key.endsWith(".goodCase"))
                .forEach((name) -> {
                            String jwt = testVectors.getProperty(name);
                            System.out.println("Key:" + name + "\tToken: " + jwt);
                            try {
                                validator.checkValidity("GET", "/", jwt);
                                fail("false positive on " + name);
                            } catch (UnauthorizedException e) {
                                System.out.println("revoked " + name);
                            } catch (TokenExpiredException e) {
                                fail("Unexpected Expiery Exception");
                            }

                        }
                );

        //commented out to keep build
        //validator.checkValidity("GET","/",testVectors.getProperty("min-token.ES256.goodCase"));
    }
}