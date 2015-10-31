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

package de.thingweb.leddemo;

import com.github.h0ru5.neopixel.NeoPixelColor;
import com.github.h0ru5.neopixel.Neopixels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mchn1210 on 20.10.2015.
 */
public class FakeNeopixels implements Neopixels {

    private static Logger log = LoggerFactory.getLogger(FakeNeopixels.class);
    private NeoPixelColor myColor = NeoPixelColor.fromValue(0);
    private int brightness =0;

    private static String colorToString(NeoPixelColor color) {
        return (255+color.red) + "," + (255+color.green) + "," + (255+color.blue);
    }

    @Override
    public int getBrightness() {
        return brightness;
    }

    @Override
    public void init() {

    }

    @Override
    public void render() {

    }

    @Override
    public void setBrightness(int i) {
        brightness = i;
        log.info("brightness set to "  + i);
    }

    @Override
    public void setColor(int i, NeoPixelColor neoPixelColor) {
        myColor = neoPixelColor;
        log.info("color of LED " + i + " set to "  + colorToString(myColor));
    }

    @Override
    public void setColor(int i, byte r, byte g, byte b) {
        myColor = NeoPixelColor.fromBytes(r,g,b);
        log.info("color of LED " + i + " set to "  + colorToString(myColor));
    }

    @Override
    public void setColor(int i, long l) {
        myColor = NeoPixelColor.fromValue(l);
        log.info("color of LED " + i + " set to "  + colorToString(myColor));
    }

    @Override
    public void colorWipe(NeoPixelColor neoPixelColor) {
        myColor = neoPixelColor;
        log.info("color set to "  + colorToString(neoPixelColor));
    }

    @Override
    public NeoPixelColor getColor(int i) {
        return myColor;
    }
}
