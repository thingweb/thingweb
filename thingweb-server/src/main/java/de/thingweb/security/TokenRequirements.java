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

/**
 * Created by Johannes on 22.12.2015.
 */
public class TokenRequirements {
    private final String issuer;
    private final String audience;
    private final long expirationTimeOffset;
    private final String verificationKey;
    private final String clientId;
    private final boolean validateSignature;
    // should be migrated to enum
    private final String tokenType;

    public TokenRequirements(String issuer, String audience, long expirationTimeOffset, String verificationKey, String clientId, boolean validateSignature, String tokenType) {
        this.issuer = issuer;
        this.audience = audience;
        this.expirationTimeOffset = expirationTimeOffset;
        this.verificationKey = verificationKey;
        this.clientId = clientId;
        this.validateSignature = validateSignature;
        this.tokenType = tokenType;
    }

    public static TokenRequirementsBuilder build() {
        return new TokenRequirementsBuilder();
    }

    public boolean validateSignature() {
        return validateSignature;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getAudience() {
        return audience;
    }

    public String getVerificationKey() {
        return verificationKey;
    }

    public String getClientId() {
        return clientId;
    }

    public long getExpirationTimeOffset() {
        return expirationTimeOffset;
    }

    public boolean checkIssuer() {
        return issuer!=null;
    }

    public boolean checkAudience() {
        return audience!=null;
    }

    public boolean checkVerificationKey() {
        return verificationKey!=null;
    }

    public boolean checkClient() {
        return clientId !=null;
    }

    public boolean validateExpiration() {
        return expirationTimeOffset >= 0;
    }

}
