package de.webthing.leddemo;

/**
 * Created by Johannes on 18.10.2015.
 */
public interface DemoLed {
    int getColorTemperature();

    void setColorTemperature(int colorTemperature);

    byte getRed();

    void setRed(byte red);

    byte getGreen();

    void setGreen(byte green);

    byte getBlue();

    void setBlue(byte blue);

    void ledOnOff(boolean target);

    void setBrightness(short percent);
}
