// ====================================================================
// FILE NAME: KilroyColorSensor.java (Team 339 - Kilroy)
//
// CREATED ON: January 25, 2020
// CREATED BY: Guido Visioni
// ABSTRACT:
// This class deals with LED light level control, color input from field operators, and colormatching for level 3 wheel spinning.
//
// Rev Robotics ColorSensorV3 characteristics:

// The sensor can detect all colors reliably when it is positioned perpendicular to the wheel from up to 2.5 inches away.
// The sensor can detect all colors reliably when it is positioned at up to a 45 degree angle from up to 2 inches away.
//
// NOTE: Please do not release this code without permission from
// Team 339.
// ====================================================================
package frc.HardwareInterfaces;

import frc.Hardware.Hardware;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.I2C;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorMatch;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.hal.SimDevice;
import edu.wpi.first.hal.SimDouble;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.ColorShim;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

// -------------------------------------------------------
/**
 * This class deals with LED light level control, color input from field
 * operators, and colormatching for level 3 wheel spinning. which will return
 * true if the light is reflected.
 *
 * @class KilroyColorSensor
 * @author Guido Visioni
 * @written January 25, 2020
 *          -------------------------------------------------------
 */
public class KilroyColorSensor
    {
    // link for FMS input:
    // https://frc-docs.readthedocs.io/en/latest/docs/software/wpilib-overview/2020-Game-Data.html
    // TODO
    // Find out how to change light level
    // Find out what the field will give us as color imput
    // Color matching

    //need to recieve game data. Make a acolor matcher method that decides what color to look for based on the color that is given to us.
    private static final byte kAddress = 0x52;
    private static final byte kPartID = (byte) 0xC2;

    // This is a transformation matrix given by the chip
    // manufacturer to transform the raw RGB to CIE XYZ
    //private static final double Cmatrix[] = {
    //    0.048112847, 0.289453437, -0.084950826,
    //    0.030754752, 0.339680186, -0.071569905,
    //    -0.093947499, 0.072838494,  0.34024948
    //};

    private I2C m_i2c;
    private SimDevice m_simDevice;
    private SimDouble m_simR, m_simG, m_simB, m_simIR, m_simProx;

    public static class RawColor
        {
        public RawColor(int r, int g, int b, int _ir)
            {
                red = r;
                green = g;
                blue = b;
                ir = _ir;
            }

        public int red;
        public int green;
        public int blue;
        public int ir;
        }

    /**
     * Constructs a ColorSensor.
     *
     * @param port  The I2C port the color sensor is attached to
     */
    public KilroyColorSensor(I2C.Port port)
        {
            m_i2c = new I2C(port, kAddress);

            m_simDevice = SimDevice.create("REV Color Sensor V3", port.value, kAddress);
            if (m_simDevice != null)
                {
                m_simR = m_simDevice.createDouble("Red", false, 0.0);
                m_simG = m_simDevice.createDouble("Green", false, 0.0);
                m_simB = m_simDevice.createDouble("Blue", false, 0.0);
                m_simIR = m_simDevice.createDouble("IR", false, 0.0);
                m_simProx = m_simDevice.createDouble("Proximity", false, 0.0);
                return;
                }

            if (!checkDeviceID())
                {
                return;
                }

            initializeDevice();

            // Clear the reset flag
            hasReset();
        }

    enum Register
        {
        kMainCtrl(0x00), kProximitySensorLED(0x01), kProximitySensorPulses(0x02), kProximitySensorRate(
                0x03), kLightSensorMeasurementRate(0x04), kLightSensorGain(0x05), kPartID(0x06), kMainStatus(
                        0x07), kProximityData(
                                0x08), kDataInfrared(0x0A), kDataGreen(0x0D), kDataBlue(0x10), kDataRed(0x13);

        public final byte bVal;

        Register(int i)
            {
                this.bVal = (byte) i;
            }
        }

    enum MainControl
        {
        kRGBMode(0x04), /* If bit is set to 1, color channels are activated */
        kLightSensorEnable(0x02), /* Enable light sensor */
        kProximitySensorEnable(0x01), /* Proximity sensor active */
        OFF(0x00); /* Nothing on */

        public final byte bVal;

        MainControl(int i)
            {
                this.bVal = (byte) i;
            }
        }

    enum GainFactor
        {
        kGain1x(0x00), kGain3x(0x01), kGain6x(0x02), kGain9x(0x03), kGain18x(0x04);

        public final byte bVal;

        GainFactor(int i)
            {
                this.bVal = (byte) i;
            }
        }

    public enum LEDCurrent
        {
        kPulse2mA(0x00), kPulse5mA(0x01), kPulse10mA(0x02), kPulse25mA(0x03), kPulse50mA(0x04), kPulse75mA(
                0x05), kPulse100mA(0x06), /* default value */
        kPulse125mA(0x07);

        public final byte bVal;

        LEDCurrent(int i)
            {
                this.bVal = (byte) i;
            }
        }

    public enum LEDPulseFrequency
        {
        kFreq60kHz(0x18), /* default value */
        kFreq70kHz(0x40), kFreq80kHz(0x28), kFreq90kHz(0x30), kFreq100kHz(0x38);

        public final byte bVal;

        LEDPulseFrequency(int i)
            {
                this.bVal = (byte) i;
            }
        }

    enum ProximitySensorResolution
        {
        kProxRes8bit(0x00), kProxRes9bit(0x08), kProxRes10bit(0x10), kProxRes11bit(0x18);

        public final byte bVal;

        ProximitySensorResolution(int i)
            {
                this.bVal = (byte) i;
            }
        }

    enum ProximitySensorMeasurementRate
        {
        kProxRate6ms(0x01), kProxRate12ms(0x02), kProxRate25ms(0x03), kProxRate50ms(0x04), kProxRate100ms(
                0x05), /* default value */
        kProxRate200ms(0x06), kProxRate400ms(0x07);

        public final byte bVal;

        ProximitySensorMeasurementRate(int i)
            {
                this.bVal = (byte) i;
            }
        }

    enum ColorSensorResolution
        {
        kColorSensorRes20bit(0x00), kColorSensorRes19bit(0x08), kColorSensorRes18bit(0x10), kColorSensorRes17bit(
                0x18), kColorSensorRes16bit(0x20), kColorSensorRes13bit(0x28);

        public final byte bVal;

        ColorSensorResolution(int i)
            {
                this.bVal = (byte) i;
            }
        }

    enum ColorSensorMeasurementRate
        {
        kColorRate25ms(0), kColorRate50ms(1), kColorRate100ms(2), kColorRate200ms(3), kColorRate500ms(
                4), kColorRate1000ms(5), kColorRate2000ms(7);

        public final byte bVal;

        ColorSensorMeasurementRate(int i)
            {
                this.bVal = (byte) i;
            }
        };

    /**
     * Configure the the IR LED used by the proximity sensor.
     *
     * These settings are only needed for advanced users, the defaults
     * will work fine for most teams. Consult the APDS-9151 for more
     * information on these configuration settings and how they will affect
     * proximity sensor measurements.
     *
     * @param freq      The pulse modulation frequency for the proximity
     *                  sensor LED
     * @param curr      The pulse current for the proximity sensor LED
     * @param pulses    The number of pulses per measurement of the
     *                  proximity sensor LED (0-255)
     */
    public void configureProximitySensorLED(LEDPulseFrequency freq, LEDCurrent curr, int pulses)
    {
        write8(Register.kProximitySensorLED, freq.bVal | curr.bVal);
        write8(Register.kProximitySensorPulses, (byte) pulses);
    }

    /**
     * Configure the proximity sensor.
     *
     * These settings are only needed for advanced users, the defaults
     * will work fine for most teams. Consult the APDS-9151 for more
     * information on these configuration settings and how they will affect
     * proximity sensor measurements.
     *
     * @param res   Bit resolution output by the proximity sensor ADC.
     * @param rate  Measurement rate of the proximity sensor
     */
    public void configureProximitySensor(ProximitySensorResolution res, ProximitySensorMeasurementRate rate)
    {
        write8(Register.kProximitySensorRate, res.bVal | rate.bVal);
    }

    /**
     * Configure the color sensor.
     *
     * These settings are only needed for advanced users, the defaults
     * will work fine for most teams. Consult the APDS-9151 for more
     * information on these configuration settings and how they will affect
     * color sensor measurements.
     *
     * @param res   Bit resolution output by the respective light sensor ADCs
     * @param rate  Measurement rate of the light sensor
     * @param gain  Gain factor applied to light sensor (color) outputs
     */
    public void configureColorSensor(ColorSensorResolution res, ColorSensorMeasurementRate rate, GainFactor gain)
    {
        write8(Register.kLightSensorMeasurementRate, res.bVal | rate.bVal);
        write8(Register.kLightSensorGain, gain.bVal);
    }

    /**
     * Get the most likely color. Works best when within 2 inches and
     * perpendicular to surface of interest.
     *
     * @return  Color enum of the most likely color, including unknown if
     *          the minimum threshold is not met
     */
    public Color getColor()
    {
        double r = (double) getRed();
        double g = (double) getGreen();
        double b = (double) getBlue();
        double mag = r + g + b;
        return new ColorShim(r / mag, g / mag, b / mag);
    }

    /**
     * Get the raw proximity value from the sensor ADC (11 bit). This value
     * is largest when an object is close to the sensor and smallest when
     * far away.
     *
     * @return  Proximity measurement value, ranging from 0 to 2047
     */
    public int getProximity()
    {
        if (m_simDevice != null)
            {
            return (int) m_simProx.get();
            }
        return read11BitRegister(Register.kProximityData);
    }

    /**
     * Get the raw color values from their respective ADCs (20-bit).
     *
     * @return  ColorValues struct containing red, green, blue and IR values
     */
    public RawColor getRawColor()
    {
        return new RawColor(getRed(), getGreen(), getBlue(), getIR());
    }

    /**
     * Get the raw color value from the red ADC
     *
     * @return  Red ADC value
     */
    public int getRed()
    {
        if (m_simDevice != null)
            {
            return (int) m_simR.get();
            }
        return read20BitRegister(Register.kDataRed);
    }

    /**
     * Get the raw color value from the green ADC
     *
     * @return  Green ADC value
     */
    public int getGreen()
    {
        if (m_simDevice != null)
            {
            return (int) m_simG.get();
            }
        return read20BitRegister(Register.kDataGreen);
    }

    /**
     * Get the raw color value from the blue ADC
     *
     * @return  Blue ADC value
     */
    public int getBlue()
    {
        if (m_simDevice != null)
            {
            return (int) m_simB.get();
            }
        return read20BitRegister(Register.kDataBlue);
    }

    /**
     * Get the raw color value from the IR ADC
     *
     * @return  IR ADC value
     */
    public int getIR()
    {
        if (m_simDevice != null)
            {
            return (int) m_simIR.get();
            }
        return read20BitRegister(Register.kDataInfrared);
    }

    /**
     * Indicates if the device reset. Based on the power on status flag in the
     * status register. Per the datasheet:
     *
     * Part went through a power-up event, either because the part was turned
     * on or because there was power supply voltage disturbance (default at
     * first register read).
     *
     * This flag is self clearing
     *
     * @return  bool indicating if the device was reset
     */
    public boolean hasReset()
    {
        ByteBuffer raw = ByteBuffer.allocate(1);

        m_i2c.read(Register.kMainStatus.bVal, 1, raw);

        raw.order(ByteOrder.LITTLE_ENDIAN);
        return (raw.get() & 0x20) != 0;
    }

    private boolean checkDeviceID()
    {
        ByteBuffer raw = ByteBuffer.allocate(1);
        if (m_i2c.read(Register.kPartID.bVal, 1, raw))
            {
            DriverStation.reportError("Could not find REV color sensor", false);
            return false;
            }

        if (kPartID != raw.get())
            {
            DriverStation.reportError("Unknown device found with same I2C addres as REV color sensor", false);
            return false;
            }

        return true;
    }

    private void initializeDevice()
    {
        write8(Register.kMainCtrl, MainControl.kRGBMode.bVal | MainControl.kLightSensorEnable.bVal
                | MainControl.kProximitySensorEnable.bVal);

        write8(Register.kProximitySensorRate,
                ProximitySensorResolution.kProxRes11bit.bVal | ProximitySensorMeasurementRate.kProxRate100ms.bVal);

        write8(Register.kProximitySensorPulses, (byte) 32);
    }

    private int read11BitRegister(Register reg)
    {
        ByteBuffer raw = ByteBuffer.allocate(2);

        m_i2c.read(reg.bVal, 2, raw);

        raw.order(ByteOrder.LITTLE_ENDIAN);
        return raw.getShort() & 0x7FF;
    }

    private int read20BitRegister(Register reg)
    {
        ByteBuffer raw = ByteBuffer.allocate(4);

        m_i2c.read(reg.bVal, 3, raw);

        raw.order(ByteOrder.LITTLE_ENDIAN);
        return raw.getInt() & 0x03FFFF;
    }

    private void write8(Register reg, int data)
    {
        m_i2c.write(reg.bVal, data);
    }
    }
