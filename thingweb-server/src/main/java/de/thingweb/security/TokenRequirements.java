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
 * Bean class representing the inputs (validation key, allowed clock skew) and
 * requirements (expected values) for security token validation
 * 
 * Created by Johannes on 22.12.2015.
 */
public class TokenRequirements {

	// The verification key(s) for the security token signatures (a string that
	// represents a JWKS)
	private final String verificationKeys;

	// The allowed clock screw (in secs)
	private final long allowedClockDrift;

	// The expected audience
	private final String audience;

	// The expected issuer
	private final String issuer;

	// The expected caller
	private final String clientId;

	// The expected token type TODO: migrate data type to enum
	private final String tokenType;

	/**
	 * Constructor
	 * 
	 * @param verificationKey
	 *            the signature validation key (a string that represents a JWKS,
	 *            may be <code>null</code>, in that case no signature check is
	 *            done)
	 * @param allowedClockDrift
	 *            the allowed clock screw in secs (may be negative, in this case
	 *            no timeliness check is done)
	 * @param audience
	 *            the expected audience (may be <code>null</code>, in this case
	 *            no audience check is done)
	 * @param issuer
	 *            the expected issuer (may be <code>null</code>, in this case no
	 *            issuer check is done)
	 * @param clientId
	 *            the expected caller (may be <code>null</code>, in this case no
	 *            subject check is done)
	 * @param tokenType
	 *            the expected token type (may be <code>null</code>, in this
	 *            case no token type check is done)
	 */
	public TokenRequirements(String verificationKey, long allowedClockDrift,
			String audience, String issuer, String clientId, String tokenType) {
		this.verificationKeys = verificationKey;
		this.allowedClockDrift = allowedClockDrift;
		this.audience = audience;
		this.issuer = issuer;
		this.clientId = clientId;
		this.tokenType = tokenType;
	}

	/**
	 * A builder method for TokenRequirementsBuilder
	 * 
	 * @return the TokenRequirementsBuilder instance
	 */
	public static TokenRequirementsBuilder build() {
		return new TokenRequirementsBuilder();
	}

	/**
	 * Get the signature validation key(s) (a string that represents a JWKS, may
	 * be <code>null</code>, in that case no signature check is done)
	 * 
	 * @return the signature validation key(s)
	 */
	public String getVerificationKeys() {
		return verificationKeys;
	}

	/**
	 * Determine whether signatures shall be checked
	 * 
	 * @return <code>true</code> if signature values are checked
	 */
	public boolean validateSignature() {
		return verificationKeys != null;
	}

	/**
	 * Get the allowed clock drift in secs (may be negative, in this case no
	 * timeliness check is done)
	 * 
	 * @return the allowed clock drift in secs
	 */
	public long getAllowedClockDriftSecs() {
		return allowedClockDrift;
	}

	/**
	 * Determine whether timeliness shall be checked
	 * 
	 * @return <code>true</code> if timeliness is checked
	 */
	public boolean validateExpiration() {
		return allowedClockDrift >= 0;
	}

	/**
	 * Get the expected audience (may be <code>null</code>, in this case no
	 * audience check is done)
	 * 
	 * @return the expected audience
	 */
	public String getAudience() {
		return audience;
	}

	/**
	 * Determine whether expectations on audience are checked
	 * 
	 * @return <code>true</code> if the expected audience is checked
	 */
	public boolean checkAudience() {
		return audience != null;
	}

	/**
	 * Get the expected issuer (may be <code>null</code>, in this case no issuer
	 * check is done)
	 * 
	 * @return the expected issuer
	 */
	public String getIssuer() {
		return issuer;
	}

	/**
	 * Determine whether expectations on issuers are checked
	 * 
	 * @return <code>true</code> if the expected issuer is checked
	 */
	public boolean checkIssuer() {
		return issuer != null;
	}

	/**
	 * Get the expected client identifier (may be <code>null</code>, in this
	 * case no subject check is done)
	 * 
	 * @return the expected client identifier
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * Determine whether expectations on subject are checked
	 * 
	 * @return <code>true</code> if the expected subject is checked
	 */
	public boolean checkSubject() {
		return clientId != null;
	}

	/**
	 * Get the expected token type (may be <code>null</code>, in this case no
	 * token type check is done)
	 * 
	 * @return the expected token type
	 */
	public String getTokenType() {
		return tokenType;
	}

	/**
	 * Determine whether expectations on the token type are checked
	 * 
	 * @return <code>true</code> if the expected token type is checked
	 */
	public boolean checkTokenType() {
		return tokenType != null;
	}
}