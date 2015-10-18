package de.webthing.leddemo;

/**
 * Created by Johannes on 18.10.2015.
 */
public class DemoLedAdapter implements DemoLed {
    private int colorTemperature;
    private byte red;

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
        return red;
    }

    @Override
    public void setRed(byte red) {
        this.red = red;
    }

    @Override
    public byte getGreen() {
        return green;
    }

    @Override
    public void setGreen(byte green) {
        this.green = green;
    }

    @Override
    public byte getBlue() {
        return blue;
    }

    @Override
    public void setBlue(byte blue) {
        this.blue = blue;
    }

    private byte green;
    private byte blue;

    @Override
    public void ledOnOff(boolean target) {

    }

    @Override
    public void setBrightness(short percent) {

    }
}
