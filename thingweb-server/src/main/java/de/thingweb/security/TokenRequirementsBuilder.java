/*
 *
 *  * The MIT License (MIT)
 *  *
 *  * Copyright (c) 2015 Siemens AG and the thingweb community
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

public class TokenRequirementsBuilder {
    private String issuer = null;
    private String audience = null;
    private boolean expirationTimeDefined = false;
    private String verificationKey = null;
    private String client = null;

    public TokenRequirementsBuilder setIssuer(String issuer) {
        this.issuer = issuer;
        return this;
    }

    public TokenRequirementsBuilder setAudience(String audience) {
        this.audience = audience;
        return this;
    }

    public TokenRequirementsBuilder setExpirationTimeDefined(boolean expirationTimeDefined) {
        this.expirationTimeDefined = expirationTimeDefined;
        return this;
    }

    public TokenRequirementsBuilder setVerificationKey(String verificationKey) {
        this.verificationKey = verificationKey;
        return this;
    }

    public TokenRequirementsBuilder setClient(String client) {
        this.client = client;
        return this;
    }

    public TokenRequirements createTokenRequirements() {
        return new TokenRequirements(issuer, audience, expirationTimeDefined, verificationKey, client);
    }

    public static TokenRequirements createDefault() {
        //by default, just verify that it can expire
        return TokenRequirements.build()
                .setExpirationTimeDefined(true)
                .createTokenRequirements();
    }
}