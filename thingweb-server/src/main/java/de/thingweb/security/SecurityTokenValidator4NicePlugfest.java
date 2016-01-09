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
 * Created by Johannes on 23.12.2015.
 */
public class SecurityTokenValidator4NicePlugfest implements SecurityTokenValidator {
    private TokenRequirements requirements;

    public SecurityTokenValidator4NicePlugfest(TokenRequirements requirements) {
        setRequirements(requirements);
    }

    @Override
    public void setRequirements(TokenRequirements requirements) {
        this.requirements = requirements;
        //setup validator according to requirements?
    }

    @Override
    public TokenRequirements getRequirements() {
        return this.requirements;
    }

    @Override
    public String checkValidity(String method, String resource, String jwt) throws UnauthorizedException, TokenExpiredException {
        //decode jwt

        //use validator on decoded jwt

        //apply extended checks

        //check if method and resource are in aic

        //if not valid...

        //throw exception

        if (requirements.validateSignature()) {
            throw new UnauthorizedException();
        }
        //else...

        //return claims - or in our simple case, the subject

        return "0c5f83a7-cf08-4f48-8337-bfc65ea149ff";
    }

}
