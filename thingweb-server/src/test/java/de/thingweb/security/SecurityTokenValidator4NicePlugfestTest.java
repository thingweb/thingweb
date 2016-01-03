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

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;

/**
 * Created by Johannes on 03.01.2016.
 */
public class SecurityTokenValidator4NicePlugfestTest {

    private static final String ISSUER = "";
    private static final String AUDIENCE = "";
    private static final String PUBKEY = "";
    private static final String SUBJECT = "";

    private SecurityTokenValidator validator;

    @Before
    public void setUp() throws Exception {
        TokenRequirements requirements = TokenRequirements.build()
                .setIssuer(ISSUER)
                .setAudience(AUDIENCE)
                .setVerificationKey(PUBKEY)
                .createTokenRequirements();
        validator  = new SecurityTokenValidator4NicePlugfest(requirements);
    }

    @Test
    public void testValidMinimalES256Token() throws Exception {
        String jwt = "";

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
}