/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Siemens AG and the thingweb community
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package de.thingweb.client.security;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import de.thingweb.client.impl.HttpClientImpl;

public class Security4NicePlugfest {

	private static final Logger log = LoggerFactory.getLogger(HttpClientImpl.class);
	// private static final Logger log = LoggerFactory.getLogger(Security4NicePlugfest.class);

	static final String HTTPS_PREFIX = "https://";
	static final String HOST = "ec2-54-154-59-218.eu-west-1.compute.amazonaws.com";
	static final String REQUEST_REGISTRATION_AM = "/iam-services/0.1/oidc/am/register";
	static final String REQUEST_REGISTRATION_AS = "/iam-services/0.1/oidc/as/register";

	static final String REQUEST_TOKEN_AQUISITION = "/iam-services/0.1/oidc/am/token";
	static final String REQUEST_HEADER_HOST = "ec2-54-154-59-218.eu-west-1.compute.amazonaws.com";

	public static String CLIENT_NAME_PREFIX = "thingweb-gui-";
	
	public Security4NicePlugfest() {
		// Install the all-trusting trust manager
		// TODO setup trust-manager properly (not meant for production)
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			TrustManager[] trustAllCerts = new TrustManager[] { new AllTrustingX509TrustManager() };
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (GeneralSecurityException e) {
			log.error(e.getMessage());
		}
	}

	// Authorization manager (AM) : less/non-constrained, represents multiple Cs
	public Registration requestRegistrationAM() throws IOException {

		Registration registration = null;

		String clientName = CLIENT_NAME_PREFIX + System.currentTimeMillis();
		String clientCredentials = "client_credentials";
		String requestBodyRegistration = "{\"client_name\": \"" + clientName + "\",\"grant_types\": [\""
				+ clientCredentials + "\"]}";

		// Registration
		URL urlRegistration = new URL(HTTPS_PREFIX + HOST + REQUEST_REGISTRATION_AM);

		HttpsURLConnection httpConRegistration = (HttpsURLConnection) urlRegistration.openConnection();
		httpConRegistration.setDoOutput(true);
		httpConRegistration.setRequestProperty("Host", REQUEST_HEADER_HOST);
		httpConRegistration.setRequestProperty("Content-Type", "application/json");
		httpConRegistration.setRequestProperty("Accept", "application/json");
		httpConRegistration.setRequestMethod("POST");

		OutputStream outRegistration = httpConRegistration.getOutputStream();
		outRegistration.write(requestBodyRegistration.getBytes());
		outRegistration.close();

		int responseCodeRegistration = httpConRegistration.getResponseCode();
		log.info("responseCode Registration for " + urlRegistration + ": " + responseCodeRegistration);

		if (responseCodeRegistration == 201) {
			// everything ok
			InputStream isR = httpConRegistration.getInputStream();
			byte[] bisR = getBytesFromInputStream(isR);
			String jsonResponseRegistration = new String(bisR);
			log.info(jsonResponseRegistration);

			// extract the value of client_id (this value is called <c_id>in
			// the following) and the value of client_secret (called
			// <c_secret> in the following) from the JSON response

			ObjectMapper mapper = new ObjectMapper();
			JsonFactory factory = mapper.getFactory();
			JsonParser jp = factory.createParser(bisR);
			JsonNode actualObj = mapper.readTree(jp);

			JsonNode c_id = actualObj.get("client_id");
			JsonNode c_secret = actualObj.get("client_secret");

			if (c_id == null || c_id.getNodeType() != JsonNodeType.STRING || c_secret == null
					|| c_secret.getNodeType() != JsonNodeType.STRING) {
				log.error("client_id: " + c_id);
				log.error("client_secret: " + c_secret);
			} else {
				// ok so far
				// Store <c_id> and <c_secret> for use during the token
				// acquisition
				log.info("client_id: " + c_id);
				log.info("client_secret: " + c_secret);

				registration = new Registration(c_id.textValue(), c_secret.textValue());
			}

		} else {
			// error
			InputStream error = httpConRegistration.getErrorStream();
			byte[] berror = getBytesFromInputStream(error);
			log.error(new String(berror));
	}
		httpConRegistration.disconnect();

		return registration;
	}

	// Authorization server (AS) : less/non-constrained, represents multiple RSs
	public Registration requestRegistrationAS() throws IOException {
		String clientName = "opPostmanTestRS"; // CLIENT_NAME_PREFIX +
												// System.currentTimeMillis();
		String clientCredentials = "client_credentials";
		String requestBodyRegistration = "{\"client_name\": \"" + clientName + "\",\"grant_types\": [\""
				+ clientCredentials + "\"], \"id_token_signed_response_alg\":\"" + "HS256" + "\"}";

		// Registration
		URL urlRegistration = new URL(HTTPS_PREFIX + HOST + REQUEST_REGISTRATION_AS);

		HttpsURLConnection httpConRegistration = (HttpsURLConnection) urlRegistration.openConnection();
		httpConRegistration.setDoOutput(true);
		httpConRegistration.setRequestProperty("Host", REQUEST_HEADER_HOST);
		httpConRegistration.setRequestProperty("Content-Type", "application/json");
		httpConRegistration.setRequestProperty("Accept", "application/json");
		httpConRegistration.setRequestMethod("POST");

		OutputStream outRegistration = httpConRegistration.getOutputStream();
		outRegistration.write(requestBodyRegistration.getBytes());
		outRegistration.close();

		int responseCodeRegistration = httpConRegistration.getResponseCode();
		log.info("responseCode Registration for " + urlRegistration + ": " + responseCodeRegistration);

		if (responseCodeRegistration == 201) {
			// everything ok
			InputStream isR = httpConRegistration.getInputStream();
			byte[] bisR = getBytesFromInputStream(isR);
			String jsonResponseRegistration = new String(bisR);
			log.info(jsonResponseRegistration);

			// extract the value of “client_id” (this value is called <c_id>in
			// the following) and the value of “client_secret” (called
			// <c_secret> in the following) from the JSON response

			ObjectMapper mapper = new ObjectMapper();
			JsonFactory factory = mapper.getFactory();
			JsonParser jp = factory.createParser(bisR);
			JsonNode actualObj = mapper.readTree(jp);

			JsonNode c_id = actualObj.get("client_id");
			JsonNode c_secret = actualObj.get("client_secret");

			if (c_id == null || c_id.getNodeType() != JsonNodeType.STRING || c_secret == null
					|| c_secret.getNodeType() != JsonNodeType.STRING) {
				log.error("client_id: " + c_id);
				log.error("client_secret: " + c_secret);
			} else {
				// ok so far
				// Store <c_id> and <c_secret> for use during the token
				// acquisition
				log.info("client_id: " + c_id);
				log.info("client_secret: " + c_secret);

				return new Registration(c_id.textValue(), c_secret.textValue());
			}

		} else {
			// error
			InputStream error = httpConRegistration.getErrorStream();
			byte[] berror = getBytesFromInputStream(error);
			log.error(new String(berror));
		}
		httpConRegistration.disconnect();

		return null;
	}

	public String requestASToken(Registration registration) throws IOException {
		String asToken = null;

		// Token Acquisition
		// Create a HTTP request as in the following prototype and send
		// it via TLS to the AM
		//
		// Token Acquisition
		// Create a HTTP request as in the following prototype and send
		// it via TLS to the AM
		// Request
		// POST /iam-services/0.1/oidc/am/token HTTP/1.1
		URL urlTokenAcquisition = new URL(HTTPS_PREFIX + HOST + REQUEST_TOKEN_AQUISITION);

		HttpsURLConnection httpConTokenAcquisition = (HttpsURLConnection) urlTokenAcquisition.openConnection();
		httpConTokenAcquisition.setDoOutput(true);
		httpConTokenAcquisition.setRequestProperty("Host", REQUEST_HEADER_HOST);
		httpConTokenAcquisition.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		httpConTokenAcquisition.setRequestProperty("Accept", "application/json");
		// httpConTokenAcquisition.setRequestProperty("Authorization",
		// "Basic Base64(<c_id>:<c_secret>");
		String auth = registration.c_id + ":" + registration.c_secret;
		String authb = "Basic " + new String(Base64.getEncoder().encode(auth.getBytes()));
		httpConTokenAcquisition.setRequestProperty("Authorization", authb);
		httpConTokenAcquisition.setRequestMethod("POST");

		String requestBodyTokenAcquisition = "grant_type=client_credentials";

		OutputStream outTokenAcquisition = httpConTokenAcquisition.getOutputStream();
		outTokenAcquisition.write(requestBodyTokenAcquisition.getBytes());
		outTokenAcquisition.close();

		int responseCodeoutTokenAcquisition = httpConTokenAcquisition.getResponseCode();
		log.info("responseCode TokenAcquisition for " + urlTokenAcquisition + ": " + responseCodeoutTokenAcquisition);

		if (responseCodeoutTokenAcquisition == 200) {
			// everything ok
			InputStream isTA = httpConTokenAcquisition.getInputStream();
			byte[] bisTA = getBytesFromInputStream(isTA);
			String jsonResponseTA = new String(bisTA);
			log.info(jsonResponseTA);

			ObjectMapper mapper = new ObjectMapper();
			JsonFactory factory = mapper.getFactory();
			JsonParser jp = factory.createParser(bisTA);
			JsonNode actualObj = mapper.readTree(jp);

			JsonNode access_token = actualObj.get("access_token");
			if (access_token == null || access_token.getNodeType() != JsonNodeType.STRING) {
				log.error("access_token: " + access_token);
			} else {
				// ok so far
				// access_token provides a JWT structure
				// see Understanding JWT
				// https://developer.atlassian.com/static/connect/docs/latest/concepts/understanding-jwt.html

				log.info("access_token: " + access_token);
				// http://jwt.io/

				// TODO verify signature (e.g., use Jose4J)

				// Note: currently we assume signature is fine.. we just fetch
				// "as_token"
				String[] decAT = access_token.textValue().split("\\.");
				if (decAT == null || decAT.length != 3) {
					log.error("Cannot build JWT tripple structure for " + access_token);
				} else {
					assert (decAT.length == 3);
					// JWT structure
					// decAT[0]; // header
					// decAT[1]; // payload
					// decAT[2]; // signature
					String decAT1 = new String(Base64.getDecoder().decode(decAT[1]));
					JsonParser jpas = factory.createParser(decAT1);
					JsonNode payload = mapper.readTree(jpas);
					JsonNode as_token = payload.get("as_token");
					if (as_token == null || as_token.getNodeType() != JsonNodeType.STRING) {
						log.error("as_token: " + as_token);
					} else {
						log.info("as_token: " + as_token);
						asToken = as_token.textValue();
					}
				}
			}

		} else {
			// error
			InputStream error = httpConTokenAcquisition.getErrorStream();
			byte[] berror = getBytesFromInputStream(error);
			log.error(new String(berror));
		}

		httpConTokenAcquisition.disconnect();

		return asToken;
	}

//	// e.g., acess token validation with Jose4J
//	public void validateInitialAccessToken(String iat) throws InvalidJwtException {
//		JwtConsumer jwtConsumer = new JwtConsumerBuilder().setExpectedIssuer(issuerName).setExpectedAudience(audience)
//				.setRequireExpirationTime().setVerificationKey(publicKey).build();
//		JwtClaims jwtclaims = jwtConsumer.processToClaims(iat);
//
//		if (jwtclaims.getClaimValue(KEYWORD_IAT_CLAIM_CLIENT_NAME).equals(client_name)) {
//			System.out.println("JWT validation succeeded! " + jwtclaims);
//		} else {
//			throw new InvalidJwtException(
//					"Expected client_name=" + client_name + " does not match presented client_name="
//							+ jwtclaims.getClaimValue(KEYWORD_IAT_CLAIM_CLIENT_NAME));
//		}
//	}

	public static void main(String[] args) throws IOException {
		// https://www.w3.org/WoT/IG/wiki/images/0/0c/Security_4_Plugfest_%E2%80%93_HowTo.pdf
		// https://www.w3.org/WoT/IG/wiki/images/e/e9/Security_for_Nice_F2F_Plugfest-Proposal.pdf

		Security4NicePlugfest s4p = new Security4NicePlugfest();

		// ES256
		log.info("ES256 Token");
		Registration reg = s4p.requestRegistrationAM();
		if (reg != null) {
			String ast = s4p.requestASToken(reg);
			log.info("ES256 AS Token: " + ast);
		}

		// HS256
		log.info("HS256 Token");
		Registration regAS = s4p.requestRegistrationAS();
		if (regAS != null) {
			String ast = s4p.requestASToken(regAS);
			log.info("HS256 AS Token: " + ast);
		}
	}

	static byte[] getBytesFromInputStream(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int b;
		while ((b = is.read()) != -1) {
			baos.write(b);
		}

		return baos.toByteArray();
	}

}
