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
public class DemoLedAdapter {
    private int colorTemperature;
    private int lastBrightness = (byte) 20;
    private NeoPixelColor currentColor = new NeoPixelColor(0,0,0);

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
        neopixels.colorWipe(currentColor);
        neopixels.setBrightness(20);
    }

    private static String colorToString(NeoPixelColor color) {
        return color.red + "," + color.green + "," + color.blue;
    }

    public int getColorTemperature() {
        return colorTemperature;
    }

    public void setColorTemperature(int colorTemperature) {
        this.colorTemperature = colorTemperature;
    }

    public byte getRed() {
        return currentColor.red;
    }

    public void setRed(byte red) {
        NeoPixelColor color = new NeoPixelColor(red, currentColor.green, currentColor.blue);
        log.info("color set from " + colorToString(currentColor) + " to "  + colorToString(color));
        neopixels.colorWipe(color);
        currentColor = color;
    }

    public byte getGreen() {
        return currentColor.green;
    }

    public void setGreen(byte green) {
        NeoPixelColor color = NeoPixelColor.fromBytes(currentColor.red, green, currentColor.blue);
        log.info("color set from " + colorToString(currentColor) + " to "  + colorToString(color));
        neopixels.colorWipe(color);
        currentColor = color;
    }

    public byte getBlue() {
        return currentColor.blue;
    }

    public void setBlue(byte blue) {
        NeoPixelColor color = NeoPixelColor.fromBytes(currentColor.red, currentColor.green, blue);
        log.info("color set from " + colorToString(currentColor) + " to "  + colorToString(color));
        neopixels.colorWipe(color);
        currentColor = color;
    }

    public void ledOnOff(boolean target) {
        if(target)
            neopixels.setBrightness(lastBrightness);
        else {
            lastBrightness = neopixels.getBrightness();
            neopixels.setBrightness(0);
        }
    }

    public void setBrightnessPercent(short percent) {
        byte target = (byte) (((float) percent / 100.0) * 255);
        neopixels.setBrightness(target);
    }
}
