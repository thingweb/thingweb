package de.webthing.leddemo;

import com.github.h0ru5.neopixel.NeoPixelColor;
import com.github.h0ru5.neopixel.Neopixels;
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
            System.loadLibrary(libname);
        } catch (Exception e) {
            log.warn("could not find lib {} in {}",libname,System.getProperty("java.library.path"));
        }
        neopixels = Neopixels.createWithDefaults(64);
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
        color.red = (byte) 255;
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
        color.red = (byte) 255;
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
        color.red = (byte) 255;
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
