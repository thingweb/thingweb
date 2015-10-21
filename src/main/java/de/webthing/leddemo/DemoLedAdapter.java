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

package de.webthing.leddemo;

import com.github.h0ru5.neopixel.NeoPixelColor;
import com.github.h0ru5.neopixel.Neopixels;
import com.github.h0ru5.neopixel.NeopixelsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Johannes on 18.10.2015.
 */
public class DemoLedAdapter implements DemoLed {
    private int colorTemperature;
    private int lastBrightness = (byte) 255;

    private Neopixels neopixels;
    private static final String libname = "rpi_ws281x";

    private static final Logger log = LoggerFactory.getLogger(DemoLedAdapter.class);


    public DemoLedAdapter()
    {
        try {
            log.info("loading library for LED...");
            System.loadLibrary(libname);
            neopixels = NeopixelsImpl.createWithDefaults(64);
        } catch (UnsatisfiedLinkError e) {
            log.error("could not find lib {} in {}",libname,System.getProperty("java.library.path"));
            log.error("creating mockup");
            neopixels = new FakeNeopixels();
        }

        neopixels.init();
        neopixels.colorWipe(NeoPixelColor.fromValue(0));
        neopixels.setBrightness(255);
    }

    @Override
    public int getColorTemperature() {
        return colorTemperature;
    }

    @Override
    public void setColorTemperature(int colorTemperature) {
        this.colorTemperature = colorTemperature;
    }

    @Override
    public byte getRed() {
        NeoPixelColor color = neopixels.getColor(0);
        return color.red;
    }

    @Override
    public void setRed(byte red) {
        NeoPixelColor color = neopixels.getColor(0);
        color.red = red;
        neopixels.colorWipe(color);
    }

    @Override
    public byte getGreen() {
        NeoPixelColor color = neopixels.getColor(0);
        return color.green;
    }

    @Override
    public void setGreen(byte green) {
        NeoPixelColor color = neopixels.getColor(0);
        color.green = green;
        neopixels.colorWipe(color);
    }

    @Override
    public byte getBlue() {
        NeoPixelColor color = neopixels.getColor(0);
        return color.blue;
    }

    @Override
    public void setBlue(byte blue) {
        NeoPixelColor color = neopixels.getColor(0);
        color.blue = blue;
        neopixels.colorWipe(color);
    }

    @Override
    public void ledOnOff(boolean target) {
        if(target)
            neopixels.setBrightness(lastBrightness);
        else {
            lastBrightness = neopixels.getBrightness();
            neopixels.setBrightness(0);
        }
    }

    @Override
    public void setBrightness(short percent) {
        byte target = (byte) (((float) percent / 100.0) * 255);
        neopixels.setBrightness(target);
    }
}
