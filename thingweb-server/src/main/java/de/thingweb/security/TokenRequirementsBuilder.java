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

public class TokenRequirementsBuilder {
    private String issuer = null;
    private String audience = null;
    private long expirationTimeOffset = -1;
    private String verificationKey = null;
    private String clientId = null;
    private String tokenType = "ES256";
    private boolean validateSignature = true;

    public static TokenRequirements createDefault() {
        //by default, just verify that it can expire
        return TokenRequirements.build()
                .setValidateSignature(false)
                .validateAt(0)
                .createTokenRequirements();
    }

    public TokenRequirementsBuilder setValidateSignature(boolean validateSignature) {
        this.validateSignature = validateSignature;
        return this;
    }

    public TokenRequirementsBuilder setTokenType(String tokenType) {
        this.tokenType = tokenType;
        return this;
    }

    public TokenRequirementsBuilder setIssuer(String issuer) {
        this.issuer = issuer;
        return this;
    }

    public TokenRequirementsBuilder setAudience(String audience) {
        this.audience = audience;
        return this;
    }

    public TokenRequirementsBuilder validateAt(long expirationTimeOffset) {
        this.expirationTimeOffset = expirationTimeOffset;
        return this;
    }

    public TokenRequirementsBuilder setVerificationKey(String verificationKey) {
        this.verificationKey = verificationKey;
        return this;
    }

    public TokenRequirementsBuilder setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public TokenRequirements createTokenRequirements() {
        return new TokenRequirements(issuer, audience, expirationTimeOffset, verificationKey, clientId, validateSignature, tokenType);
    }
}