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

package de.thingweb.binding;

import de.thingweb.security.SecurityTokenValidator;
import de.thingweb.security.TokenExpiredException;
import de.thingweb.security.TokenRequirements;
import de.thingweb.security.UnauthorizedException;
import de.thingweb.thing.Content;

import java.util.Observable;

public class AbstractRESTListener extends Observable implements RESTListener {
	protected boolean protection = true;

	private SecurityTokenValidator validator;
	public static TokenRequirements requirements;

	@Override
	public String validate(String method, String uri, String jwt) throws UnauthorizedException, TokenExpiredException {
		if (this.validator != null) {
			return this.validator.checkValidity(jwt,method,uri);
		}

		return null;
	}

	@Override
	public boolean hasProtection() {
		return protection;
	}

	@Override
	public void protectWith(SecurityTokenValidator validator) {
		this.protection = true;
		this.validator = validator;
	}

	@Override
	public Content onGet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onPut(Content data) throws UnsupportedOperationException, IllegalArgumentException, RuntimeException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Content onPost(Content data) throws UnsupportedOperationException, IllegalArgumentException, RuntimeException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onDelete() {
		throw new UnsupportedOperationException();
	}
}
