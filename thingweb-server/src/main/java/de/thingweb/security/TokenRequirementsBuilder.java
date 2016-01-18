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
 * Builder class for TokenRequirements
 * 
 * Created by Johannes on 22.12.2015.
 */
public class TokenRequirementsBuilder {

	// The verification keys for the security token signatures (a string that
	// represents a JWKS)
	private String verificationKeys = null;

	// The allowed clock screw in secs
	private long allowedClockDrift = -1;

	// The expected audience
	private String audience = null;

	// The expected issuer
	private String issuer = null;

	// The expected caller
	private String clientId = null;

	// The expected token type TODO: migrate data type to enum
	private String tokenType = null;

	/**
	 * Create the TokenRequirementsBuilder instance
	 * 
	 * @return the TokenRequirementsBuilder instance
	 */
	public TokenRequirements createTokenRequirements() {
		return new TokenRequirements(verificationKeys, allowedClockDrift,
				audience, issuer, clientId, tokenType);
	}

	/**
	 * Create the TokenRequirementsBuilder instance providing defaults
	 * 
	 * @return the TokenRequirementsBuilder instance providing defaults
	 */
	public static TokenRequirements createDefault() {
		// by default, just verify that it can expire
		return TokenRequirements.build().validateAt(0)
				.createTokenRequirements();
	}

	/**
	 * Set the verification keys for the security token signatures
	 * 
	 * @param verificationKeys
	 *            the verification keys for the security token signatures (a
	 *            string that represents a JWKS)
	 * @return the TokenRequirementsBuilder instance
	 */
	public TokenRequirementsBuilder setVerificationKeys(String verificationKeys) {
		this.verificationKeys = verificationKeys;
		return this;
	}

	/**
	 * Set the allowed clock screw in secs
	 * 
	 * @param allowedClockDrift
	 *            the allowed clock screw in secs
	 * @return the TokenRequirementsBuilder instance
	 */
	public TokenRequirementsBuilder validateAt(long allowedClockDrift) {
		this.allowedClockDrift = allowedClockDrift;
		return this;
	}

	/**
	 * Set the expected audience
	 * 
	 * @param audience
	 *            the expected audience
	 * @return the TokenRequirementsBuilder instance
	 */
	public TokenRequirementsBuilder setAudience(String audience) {
		this.audience = audience;
		return this;
	}

	/**
	 * Set the expected issuer
	 * 
	 * @param issuer
	 *            the expected issuer
	 * @return the TokenRequirementsBuilder instance
	 */
	public TokenRequirementsBuilder setIssuer(String issuer) {
		this.issuer = issuer;
		return this;
	}

	/**
	 * Set the expected caller
	 * 
	 * @param clientId
	 *            the expected caller
	 * @return the TokenRequirementsBuilder instance
	 */
	public TokenRequirementsBuilder setClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

	/**
	 * Set the expected token type
	 * 
	 * @param tokenType
	 *            the expected token type
	 * @return the TokenRequirementsBuilder instance
	 */
	public TokenRequirementsBuilder setTokenType(String tokenType) {
		this.tokenType = tokenType;
		return this;
	}
}