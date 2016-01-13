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

import java.util.List;
import java.util.Map;

import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.resolvers.JwksVerificationKeyResolver;
import org.jose4j.keys.resolvers.VerificationKeyResolver;
import org.jose4j.lang.JoseException;

import de.thingweb.security.SecurityTokenValidator;
import de.thingweb.security.TokenExpiredException;
import de.thingweb.security.TokenRequirements;
import de.thingweb.security.UnauthorizedException;

/**
 * Created by Johannes on 23.12.2015.
 */
public class SecurityTokenValidator4NicePlugfest implements SecurityTokenValidator {
	private TokenRequirements requirements;

	protected JwtConsumer jwtConsumer;

	public SecurityTokenValidator4NicePlugfest(TokenRequirements requirements) {
		setRequirements(requirements);
	}

	@Override
	public void setRequirements(TokenRequirements requirements) {
		this.requirements = requirements;
		// setup jwtConsumer, which validations are inside jose4j is in the following setting up
		JsonWebKeySet jsonWebKeySet;
		try {
			jsonWebKeySet = new JsonWebKeySet(requirements.getVerificationKey());
			VerificationKeyResolver jwksResolver = new JwksVerificationKeyResolver(jsonWebKeySet.getJsonWebKeys());
			JwtConsumerBuilder jwtConsumerBuilder = new JwtConsumerBuilder().setVerificationKeyResolver(jwksResolver);

			if (requirements.checkAudience()) {
				jwtConsumerBuilder.setExpectedAudience(requirements.getAudience());
			}

			if (requirements.checkIssuer()) {
				jwtConsumerBuilder.setExpectedIssuer(requirements.getIssuer());
			}

			if (requirements.checkClient()) {
				jwtConsumerBuilder.setExpectedSubject(requirements.getClientId());
			}

			if (requirements.validateExpiration()) {
				jwtConsumerBuilder.setRequireExpirationTime().setAllowedClockSkewInSeconds(
						(int) requirements.getExpirationTimeOffset());
			}

			// TODO: iat validate

			jwtConsumer = jwtConsumerBuilder.build();
		} catch (JoseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public TokenRequirements getRequirements() {
		return this.requirements;
	}

	public final static String CLAIM_TYP = "typ";
	public final static String CLAIM_ACI = "aci";
	public final static String ACI_RES = "res";
	public final static String ACI_MTH = "mth";

	@Override
	public String checkValidity(String method, String resource, String jwt) throws UnauthorizedException,
			TokenExpiredException {

		JwtClaims claims = null;
		// all checks is base on validate signature
		if (requirements.validateSignature()) {
			if (jwt == null || "".equals(jwt)) {
				throw new UnauthorizedException();
			}
			try {
				claims = jwtConsumer.processToClaims(jwt);
			} catch (InvalidJwtException e) {
				// the error message contain the details
				System.out.println(jwt);
				throw new UnauthorizedException();
			}

			if (requirements.getTokenType() != null) {
				String typ = (String) claims.getClaimValue(CLAIM_TYP);
				if (typ == null || !typ.equals(requirements.getTokenType())) {
					throw new UnauthorizedException();
				}
			}

			// check if method and resource are in aci
			List<Map> aci = (List<Map>) claims.getClaimValue(CLAIM_ACI);
			if (aci == null || aci.size() == 0) {
				// is minimal token, not check the aci
			} else if (aci.size() == 0) {
				// aci is not match the design
				throw new UnauthorizedException();
			} else {
				boolean checkAci = false;
				for (Map ac : aci) {
					String res = (String) ac.get(ACI_RES);
					List<String> mths = (List<String>) ac.get(ACI_MTH);
					if (res != null && ac.get(ACI_MTH) != null) {
						if (res.equals(resource) && mths.contains(method)) {
							checkAci = true;
							break;
						}
					} else {
						// in each one including one res and mths
						throw new UnauthorizedException();
					}
				}
				if (!checkAci) {
					// method and resource are not in aci
					throw new UnauthorizedException();
				}

			}

			String typ = (String) claims.getClaimValue(CLAIM_TYP);
			if (typ == null || !typ.equals(requirements.getTokenType())) {
				throw new UnauthorizedException();
			}

			// return claims - or in our simple case, the subject
			try {
				return claims.getSubject();
			} catch (MalformedClaimException e) {
				new UnauthorizedException();
			}
		}
		// else not validate the jwt return default subject
		return "0c5f83a7-cf08-4f48-8337-bfc65ea149ff";
	}

}
