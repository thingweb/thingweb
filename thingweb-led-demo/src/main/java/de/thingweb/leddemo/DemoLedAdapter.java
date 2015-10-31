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
import com.github.h0ru5.neopixel.NeopixelsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Johannes on 18.10.2015.
 */
public class DemoLedAdapter {
    private int colorTemperature;
    private int lastBrightness = (byte) 20;
    private NeoPixelColor currentColor = new NeoPixelColor(255,255,255);

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
        byte red= (byte) 0xFF;
        byte green = (byte) 0xFF;
        byte blue = (byte) 0xFF;

        int ct_scaled = colorTemperature / 100;

        if (ct_scaled > 66) {
            double fred = ct_scaled - 60;
            fred = 329.698727446 * Math.pow(fred, -0.1332047592);
            red = doubletoByte(fred);

            double fgreen = ct_scaled - 60;
            fgreen =  288.1221695283 * Math.pow(fgreen, -0.0755148492);
            green = doubletoByte(fgreen);
        } else {
            double fgreen = ct_scaled;
            fgreen = 99.4708025861 * Math.log(fgreen) - 161.1195681661;
            green = doubletoByte(fgreen);

            if(ct_scaled > 19) {
                double fblue = ct_scaled - 10;
                fblue = 138.5177312231 * Math.log(fblue) - 305.0447927307;
                blue = doubletoByte(fblue);
            }
        }

        this.neopixels.colorWipe(NeoPixelColor.fromBytes(red,green,blue));
        this.currentColor = NeoPixelColor.fromBytes(red,green,blue);
        this.colorTemperature = colorTemperature;
    }

    public static byte doubletoByte(double inp) {
        if(inp < 0) inp = 0;
        if(inp > 255) inp = 255;
        return (byte) inp;
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

    public int getBrightnessPercent() {
        return (int) ((neopixels.getBrightness() / 255) * 100.0);
    }
}
