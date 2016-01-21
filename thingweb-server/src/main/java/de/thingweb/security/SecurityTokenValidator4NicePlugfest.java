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

import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.resolvers.JwksVerificationKeyResolver;
import org.jose4j.keys.resolvers.VerificationKeyResolver;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * This class implements the SecurityTokenValidator interface for the Nice
 * Plugfest of the W3C IG WoT
 * 
 * Created by Johannes on 23.12.2015.
 */
public class SecurityTokenValidator4NicePlugfest implements
		SecurityTokenValidator {

	private static final Logger LOG = LoggerFactory
			.getLogger(SecurityTokenValidator4NicePlugfest.class);

	// The keyword word for the 'type' claim
	private final static String CLAIM_TYP = "typ";

	// The keyword word for the 'aci' claim
	private final static String CLAIM_ACI = "aci";

	// The keyword word the resource abstraction in an 'aci' claim
	private final static String ACI_RES = "res";

	// The keyword word the method abstraction in an 'aci' claim
	private final static String ACI_MTH = "mth";
	private static final String MIN_TOKEN_TYPE = "org:w3:wot:jwt:as:min";

	// The TokenRequirements instance
	private TokenRequirements requirements;

	// The JwtConsumer instance
	private JwtConsumer jwtConsumer;

	/**
	 * Constructor
	 * 
	 * @param requirements
	 *            the TokenRequirements instance
	 * @throws JoseException error
	 */
	public SecurityTokenValidator4NicePlugfest(TokenRequirements requirements)
			throws JoseException {
		setRequirements(requirements);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.thingweb.security.SecurityTokenValidator#setRequirements(de.thingweb
	 * .security.TokenRequirements)
	 */
	@Override
	public void setRequirements(TokenRequirements requirements)
			throws JoseException {
		if (requirements == null) {
			requirements = TokenRequirementsBuilder.createDefault();
		}
		this.requirements = requirements;
		JwtConsumerBuilder jwtConsumerBuilder = new JwtConsumerBuilder();
		if (requirements.validateSignature()) {
			JsonWebKeySet jsonWebKeySet = new JsonWebKeySet(
					requirements.getVerificationKeys());
			VerificationKeyResolver jwksResolver = new JwksVerificationKeyResolver(
					jsonWebKeySet.getJsonWebKeys());
			jwtConsumerBuilder.setVerificationKeyResolver(jwksResolver);
		}
		if (requirements.validateExpiration()) {
			jwtConsumerBuilder.setRequireExpirationTime()
					.setAllowedClockSkewInSeconds(
							(int) requirements.getAllowedClockDriftSecs())
					.setRequireIssuedAt();
		}
		if (requirements.checkAudience()) {
			jwtConsumerBuilder.setExpectedAudience(requirements.getAudience());
		}
		if (requirements.checkIssuer()) {
			jwtConsumerBuilder.setExpectedIssuer(requirements.getIssuer());
		}
		if (requirements.checkSubject()) {
			jwtConsumerBuilder.setExpectedSubject(requirements.getClientId());
		}
		this.jwtConsumer = jwtConsumerBuilder.build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.thingweb.security.SecurityTokenValidator#getRequirements()
	 */
	@Override
	public TokenRequirements getRequirements() {
		return this.requirements;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.thingweb.security.SecurityTokenValidator#checkValidity(java.lang.String
	 * , java.lang.String, java.lang.String)
	 */
	@Override
	public String checkValidity(String jwt, String method, String resource)
			throws UnauthorizedException, TokenExpiredException {
		JwtClaims claims = null;
		// Perform the basic checks: signature validation, checking of expected
		// audience, issuer, subject and timeliness - see setRequirements()
		try {
			claims = jwtConsumer.processToClaims(jwt);
		} catch (InvalidJwtException e) {
			LOG.error("Error when validating token: ", jwt);
			throw new UnauthorizedException();
		}
		// Perform supplementary checks: token type and request method/resource
		if (requirements.checkTokenType()) {
			if (!requirements.getTokenType().equals(
					claims.getClaimValue(CLAIM_TYP))) {
				throw new UnauthorizedException("Unexpected token type: "
						+ claims.getClaimValue(CLAIM_TYP));
			}
		}
		if (method != null && resource != null) {
			// Skip the following check in case of 'minimal' tokens - they do
// not contain an ACI that could be checked
			if (!MIN_TOKEN_TYPE.equals(claims.getClaimValue(CLAIM_TYP))) {

				List<Map> aci = (List<Map>) claims.getClaimValue(CLAIM_ACI);
			if (aci == null || aci.size() == 0) {
				throw new UnauthorizedException("No ACI claims in token");
			} else {
				boolean aciOkay = false;
				for (Map ac : aci) {
					String res = (String) ac.get(ACI_RES);
					List<String> mths = (List<String>) ac.get(ACI_MTH);
					if (resource.equals(res)
							&& (mths != null && mths.contains(method))) {
						aciOkay = true;
						break;
					}
				}
				if (!aciOkay) {
					throw new UnauthorizedException(
							"Resource/method is not in ACI");
				}
			}
			}
		}
		// Return the subject claim value
		try {
			return claims.getSubject();
		} catch (MalformedClaimException e) {
			throw new UnauthorizedException("Getting the subject claim failed");
		}
	}
}