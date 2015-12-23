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

/**
 *  Validator for securitty tokens
 *  checks signature and validates claims against requirements
 */
public interface SecurityTokenValidator {
    /**
     *  Set a new ste of required claims resp. attributes to be checked in the token
     * @param requirements replacement requirements
     */
    void setRequirements(TokenRequirements requirements);

    /**
     * Get currents requirements imposed on tokes to validate
     * @return set of requirements
     */
    TokenRequirements getRequirements();

    /**
     * Check a token if it validates and fulfills the required properties
     * also checkn if it contains access rights for the given resource and the given method
     * and return the subject the token was issued for
     *
     * @param method the method used for the REST call, e.g. GET, PUT, POST or DELETE
     * @param resource the relative uri of the accessed resource
     * @param jwt the JWT that the request contained (base64-encoded String)
     * @return subject claim if validated, null if not
     */
    String checkValidity(String method, String resource, String jwt) throws UnauthorizedException, TokenExpiredException;

    /**
     * Check a token if it is signed correctly and fulfills the requirements
     * @param jwt the token to decode and validate
     * @return subject claim if validated, null if not
     */
    String isValid(String jwt) throws UnauthorizedException, TokenExpiredException;
}
