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

import org.jose4j.lang.JoseException;

/**
 * Validator for security tokens: it checks the token signature and validates
 * contained information against expected values
 * 
 * Created by Johannes on 23.12.2015.
 */
public interface SecurityTokenValidator {

	/**
	 * Set the requirements (inputs, expected values) for security token
	 * validation
	 * 
	 * @param requirements
	 *            the TokenRequirements instance (may be <code>null</code>; in
	 *            this case defaults are used)
	 * @throws JoseException error
	 */
	void setRequirements(TokenRequirements requirements) throws JoseException;

	/**
	 * Get the requirements (inputs, expected values) for security token
	 * validation
	 * 
	 * @return the TokenRequirements instance
	 */
	TokenRequirements getRequirements();

	/**
	 * Check a security token if it validates and fulfills the required
	 * properties also check if it contains access rights for the given resource
	 * and the given method and return the subject the token was issued for
	 * 
	 * @param jwt
	 *            the JWT that is to-be-validated (Base64-encoded String)
	 * @param method
	 *            the method used for the REST call, e.g. GET, PUT, POST or
	 *            DELETE (may be <code>null</code>)
	 * @param resource
	 *            the relative URI of the to-be-accessed resource (may be
	 *            <code>null</code>)
	 * @return the value subject claim if the JWT is valid
	 * @throws UnauthorizedException Unauthorized error
	 * @throws TokenExpiredException TokenExpired error
	 */
	String checkValidity(String jwt, String method, String resource)
			throws UnauthorizedException, TokenExpiredException;
}