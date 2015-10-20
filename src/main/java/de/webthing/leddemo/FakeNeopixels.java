package de.webthing.leddemo;

import com.github.h0ru5.neopixel.NeoPixelColor;
import com.github.h0ru5.neopixel.Neopixels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mchn1210 on 20.10.2015.
 */
public class FakeNeopixels implements Neopixels {

    private static Logger log = LoggerFactory.getLogger(FakeNeopixels.class);

    private static String colorToString(NeoPixelColor color) {
        return color.red + "," + color.green + "," + color.blue;
    }

    @Override
    public int getBrightness() {
        return 0;
    }

    @Override
    public void init() {

    }

    @Override
    public void render() {

    }

    @Override
    public void setBrightness(int i) {
        log.info("brightness set to "  + i);
    }

    @Override
    public void setColor(int i, NeoPixelColor neoPixelColor) {

    }

    @Override
    public void setColor(int i, byte b, byte b1, byte b2) {

    }

    @Override
    public void setColor(int i, long l) {

    }

    @Override
    public void colorWipe(NeoPixelColor neoPixelColor) {

    }

    @Override
    public NeoPixelColor getColor(int i) {
        return null;
    }
}
