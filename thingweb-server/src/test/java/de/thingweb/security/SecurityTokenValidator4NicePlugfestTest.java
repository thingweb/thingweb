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

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import de.thingweb.servient.TestTools;

/**
 * Test class for SecurityTokenValidator4NicePlugfest. This class tests
 * "minimal" tokens signed with ES256 and HS256. It also tests "normal" tokens
 * signed with ES256 and HS256
 * 
 * Created by Johannes on 03.01.2016.
 */
public class SecurityTokenValidator4NicePlugfestTest {

	/*
	 * Static members to obtain or provide 'as-is' objects/values
	 */
	private static final String NICE_PLUGFEST_JWT_TESTVECTOR_FILE = "nice-plugfest-jwt-testvectors.properties";

	private static final String MIN_TOKEN_HS256_KEYWORD = "min-token.HS256";

	private static final String GOOD_CASE_KEYWORD = ".goodCase";

	private static final String MIN_TOKEN_ES256_KEYWORD = "min-token.ES256";
	
	private static final String VALIDATION_KEY_HS256 = "{\"keys\":[{\"kty\": \"oct\",\"kid\": \"018c0ae5-4d9b-471b-bfd6-eef314bc7037\",\"use\": \"sig\",\"alg\": \"HS256\",\"k\": \"aEp0WElaMnVTTjVrYlFmYnRUTldicGRtaGtWOEZKRy1PbmJjNm14Q2NZZw==\"}]}";

	private static final String VALIDATION_KEY_ES256 = "{\"keys\":[{\"kty\": \"EC\",\"d\": \"_hysUUk5sRGAHhl7RJN7x5UhBMiy6pl6kHR5-ZaWzpU\",\"use\": \"sig\",\"crv\": \"P-256\",\"kid\": \"PlugFestNice\",\"x\": \"CQsJZUvJWx5yB5EwuipDXRDye4Ybg0wwqxpGgZtcl3w\",\"y\": \"qzYskD2N7GrGDSgo6N9pPLXMIwr6jowFGyqsTJGmpz4\",\"alg\": \"ES256\"}]}";

	/*
	 * Static members to represent 'expected' objects/values
	 */
	private static final String MIN_TOKEN_RESOURCE_DEFAULT = "/";

	private static final String MIN_TOKEN_METHOD_DEFAULT = "GET";

	private static final String MIN_TOKEN_SUBJECT_DEFAULT = "0c5f83a7-cf08-4f48-8337-bfc65ea149ff";

	private static final String MIN_TOKEN_TYPE_DEFAULT = "org:w3:wot:jwt:as:min";

	private static final String MIN_TOKEN_ISSUER_DEFAULT = "NicePlugfestAS";

	private static final String MIN_TOKEN_AUDIENCE_DEFAULT = "NicePlugfestRS";

	/*
	 * Instances that represent requirements/validators
	 */
	// The TokenRequirementsBuilder instance for "minimal" tokens signed with ES
	// 256
	private TokenRequirementsBuilder minTokenES256Reqs;

	// The SecurityTokenValidator instance for "minimal" tokens signed with
	// ES256
	private SecurityTokenValidator minTokenES256Validator;

	// The TokenRequirementsBuilder instance for "minimal" tokens signed with HS
	// 256
	private TokenRequirementsBuilder minTokenHS256Reqs;

	// The SecurityTokenValidator instance for "minimal" tokens signed with
	// HS256
	private SecurityTokenValidator minTokenHS256Validator;

	// The JWT test vectors
	private Properties testVectors;

	/**
	 * Set-up for this test
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		testVectors = TestTools
				.loadPropertiesFromResources(NICE_PLUGFEST_JWT_TESTVECTOR_FILE);
		minTokenES256Reqs = TokenRequirements.build()
				.setIssuer(MIN_TOKEN_ISSUER_DEFAULT)
				.setAudience(MIN_TOKEN_AUDIENCE_DEFAULT)
				.setVerificationKeys(VALIDATION_KEY_ES256)
				.setTokenType(MIN_TOKEN_TYPE_DEFAULT);
		minTokenES256Validator = new SecurityTokenValidator4NicePlugfest(
				minTokenES256Reqs.createTokenRequirements());
		minTokenHS256Reqs = TokenRequirements.build()
				.setIssuer(MIN_TOKEN_ISSUER_DEFAULT)
				.setAudience(MIN_TOKEN_AUDIENCE_DEFAULT)
				.setVerificationKeys(VALIDATION_KEY_HS256)
				.setTokenType(MIN_TOKEN_TYPE_DEFAULT);
		minTokenHS256Validator = new SecurityTokenValidator4NicePlugfest(
				minTokenHS256Reqs.createTokenRequirements());
	}

	/**
	 * Test minimal tokens signed with ES256 which are expected to be valid
	 * 
	 * @throws Exception
	 */
	@Test
	public void validMinimalES256Token() throws Exception {
		testVectors
				.keySet()
				.parallelStream()
				.map(key -> (String) key)
				.filter(key -> key.startsWith(MIN_TOKEN_ES256_KEYWORD)
						&& key.endsWith(GOOD_CASE_KEYWORD))
				.forEach(
						(name) -> {
							String jwt = testVectors.getProperty(name);
							try {
								String subClaim = minTokenES256Validator
										.checkValidity(jwt,
												MIN_TOKEN_METHOD_DEFAULT,
												MIN_TOKEN_RESOURCE_DEFAULT);
								assertEquals(MIN_TOKEN_SUBJECT_DEFAULT,
										subClaim);
								System.out.println("Token referred to by key: "
										+ name
										+ " is VALID-as expected. Subject: "
										+ subClaim + " Token details: " + jwt);
							} catch (UnauthorizedException e) {
								fail("False negative for " + name);
							} catch (TokenExpiredException e) {
								// Minimal tokens do not have a lifetime
								fail("Unexpected TokenExpiredException");
							}
						});
	}

	/**
	 * Test minimal tokens signed with HS256 which are expected to be valid
	 * 
	 * @throws Exception
	 */
	@Test
	public void validMinimalHS256Token() throws Exception {
		testVectors
				.keySet()
				.parallelStream()
				.map(key -> (String) key)
				.filter(key -> key.startsWith(MIN_TOKEN_HS256_KEYWORD)
						&& key.endsWith(GOOD_CASE_KEYWORD))
				.forEach(
						(name) -> {
							String jwt = testVectors.getProperty(name);
							try {
								String subClaim = minTokenHS256Validator
										.checkValidity(jwt,
												MIN_TOKEN_METHOD_DEFAULT,
												MIN_TOKEN_RESOURCE_DEFAULT);
								assertEquals(MIN_TOKEN_SUBJECT_DEFAULT,
										subClaim);
								System.out.println("Token referred to by key: "
										+ name
										+ " is VALID-as expected. Subject: "
										+ subClaim + " Token details: " + jwt);
							} catch (UnauthorizedException e) {
								fail("False negative for " + name);
							} catch (TokenExpiredException e) {
								// Minimal tokens do not have a lifetime
								fail("Unexpected TokenExpiredException");
							}
						});
	}

	/**
	 * Test minimal tokens signed with ES256 which are expected to be invalid
	 * 
	 * @throws Exception
	 */
	@Test
	public void invalidMinimalES256Token() throws Exception {
		testVectors
				.keySet()
				.parallelStream()
				.map(key -> (String) key)
				.filter(key -> key.startsWith(MIN_TOKEN_ES256_KEYWORD)
						&& !key.endsWith(GOOD_CASE_KEYWORD))
				.forEach(
						(name) -> {
							String jwt = testVectors.getProperty(name);
							try {
								minTokenES256Validator.checkValidity(
										MIN_TOKEN_METHOD_DEFAULT,
										MIN_TOKEN_RESOURCE_DEFAULT, jwt);
								fail("False positive for " + name);
							} catch (UnauthorizedException e) {
								System.out
										.println("Token referred to by key: "
												+ name
												+ " is INVALID-as expected. Token details: "
												+ jwt);
							} catch (TokenExpiredException e) {
								// Minimal tokens do not have a lifetime
								fail("Unexpected TokenExpiredException");
							}
						});
	}

	/**
	 * Test minimal tokens signed with HS256 which are expected to be invalid
	 * 
	 * @throws Exception
	 */
	@Test
	public void invalidMinimalHS256Token() throws Exception {
		testVectors
				.keySet()
				.parallelStream()
				.map(key -> (String) key)
				.filter(key -> key.startsWith(MIN_TOKEN_HS256_KEYWORD)
						&& !key.endsWith(GOOD_CASE_KEYWORD))
				.forEach(
						(name) -> {
							String jwt = testVectors.getProperty(name);
							try {
								minTokenHS256Validator.checkValidity(
										MIN_TOKEN_METHOD_DEFAULT,
										MIN_TOKEN_RESOURCE_DEFAULT, jwt);
								fail("False positive on " + name);
							} catch (UnauthorizedException e) {
								// Expected
								System.out
										.println("Token referred to by key: "
												+ name
												+ " is INVALID-as expected. Token details: "
												+ jwt);
							} catch (TokenExpiredException e) {
								// Minimal tokens do not have a lifetime
								fail("Unexpected TokenExpiredException");
							}
						});
	}

	// TODO: add tests with "normal" tokens signed with ES256 and HS256
}