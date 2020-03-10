// ====================================================================
// FILE NAME: ColorWheel.java (Team 339 - Kilroy)
//
// CREATED ON: January 29, 2020
// CREATED BY: Guido Visioni
// ABSTRACT:
// This class manipulates the control panel (color wheel) for shield
// generator stage 2 and also color matches for shield generator stage 3.
//
// NOTE: Please do not release this code without permission from Team 339.
// ====================================================================
package frc.Utils;

import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import frc.HardwareInterfaces.KilroyEncoder;

// -------------------------------------------------------
/**
 * This class manipulates the control panel (color wheel) for shield generator
 * stage 2 and also color matches and manipulates for shield generator stage 3.
 *
 * @class ColorWheel
 * @author Guido Visioni
 * @written January 29, 2020
 *          -------------------------------------------------------
 */

public class ColorWheel
    {

    private SpeedController motor = null;
    private KilroyEncoder motorEncoder = null;
    private ColorSensorV3 colorSensor = null;

    /** Constructor for ColorWheel class
     *
     * @author Guido Visioni
     * @written March 3, 2020
     * @param motor - wheelSpinnerMotor
     * @param motorEncoder - wheelSpinnerMotorEncoder
     * @param colorSensor - colorSensor
     */
    public ColorWheel(SpeedController motor, KilroyEncoder motorEncoder, ColorSensorV3 colorSensor)
        {
            this.motor = motor;
            this.motorEncoder = motorEncoder;
            this.colorSensor = colorSensor;
        }

    // Gets speed of wheelSpinnerMotor
    public double getSpeed()
    {
        return this.speed;
    }

    // Sets speed of wheelSpinnerMotor
    public void setSpeed(double s)
    {
        this.speed = s;
    }

    // Gets Number of times the color wheel will spin
    public double getNumberOfSpins()
    {
        return this.numberOfSpins;
    }

    // Sets Number of times the color wheel will spin
    public void setNumberOfSpins(double n)
    {
        this.numberOfSpins = n;
    }

    // Gets the circumference of the color wheel
    public double getCircumference()
    {
        return this.circumference;
    }

    // Sets the circumference of the color wheel
    public void setCircumference(double c)
    {
        this.circumference = c;
    }

    /**
    * Takes the range of the proximity sensor 0 (far) - through 2047 (close) and
    * decides if the sensor is in range or out of range.
    * Max Stable distance detected is 3.5 inches
    * @author Guido Visioni
    * @written January 30, 2020
    */
    public boolean inRange()
    {
        if (this.colorSensor.getProximity() >= 230)
            {
            return true;
            }
        return false;
    }

    /**
     * This method gets the FMS data for the shield 3 generator spin color. (What color we need to align with the field sensor)
     * @method getSpinColor
     * @author Guido Visioni
     * @written January 30, 2020
     *          -------------------------------------------------------
     */
    private String getSpinColor()
    {
        String gameData = "";

        // When we reach shield generator stage 3 this will recieve the FMS color
        // data and will return a string. The string will be the first letter of the color.
        gameData = DriverStation.getInstance().getGameSpecificMessage();
        if (gameData.length() > 0)
            {
            switch (gameData.charAt(0))
                {
                case 'B':
                    return "B";
                case 'G':
                    return "G";
                case 'R':
                    return "R";
                case 'Y':
                    return "Y";
                default:
                    return "U";
                }
            }
        // Returns blank if FMS data not detected
        return "";
    }

    /**
    *This method is used with a button to allow the motor to be spun manually
    * @method manualSpin
    * @author Guido Visioni
    * @written February 25, 2020
    *          -------------------------------------------------------
    */
    public void manualSpin()
    {
        this.motor.set(.2);
    }

    /**
     * This method will spin the control panel 3-5 times for shield generator stage
     * 2 (no color sensing capabilities)
     * Parameter distance will be amount of times to spin the control panel x
     * 100.5309. When calling the method distance will be the amount of rotations
     * i.e. 3 for 3 rotations.
     * Circumference of Control Panel = 100.5309
     * Talon_SRX ticks per rotation = 4096
     *
     * @method spinControlPanel
     * @author Guido Visioni
     * @written January 30, 2020
     *          -------------------------------------------------------
     */
    public void spinControlPanel()
    {
        // Check all encoders to see if they've reached the distance. Multiply number of spins * circumference(106.5309)
        if (firstIteration == true)
            {
            if (this.motorEncoder.getDistance() > this.numberOfSpins * this.circumference)
                {
                firstIteration = false;
                this.motor.set(0);
                }
            else
                {
                // Keep Spinning if we have not reached the distance
                this.motor.set(this.speed);
                }
            }
    }

    /**
     * This method will reset the motor encoder and set a boolean to true that will allow the wheel to start spinning
     *
     * @author Dion Marchant
     * @written February 25, 2020
     */
    public void start()
    {
        this.motorEncoder.reset();
        firstIteration = true;
    }

    /**
    * This method will reset the motor encoder and set a boolean to true that will allow the spin to color wheel to start
    * @author Guido Visioni
    * @written March 3, 2020
    */
    public void colorStart()
    {
        timeToStopColorAlign = false;
    }

    //Returns what states timeToStopColorAlign is in
    public boolean getTimeToStopColorAlign()
    {
        return timeToStopColorAlign;
    }

    //Sets the states of timeToStopColorAlign
    public boolean setTimeToStopColorAlign(boolean set)
    {
        return (timeToStopColorAlign = set);
    }

    /**
     * This method will align the specified color from the FMS under the sensor.
     * @method spinControlPanelToColor
     * @author Guido Visioni
     * @written January 30, 2020
     *          -------------------------------------------------------
     */
    public boolean spinControlPanelToColor()
    {
        this.spinColor = this.getSpinColor();
        Color detectedColor = this.colorSensor.getColor();
        String colorString = "U";

        // Adds color targets to the colorMatcher
        colorMatcher.addColorMatch(kBlueTarget);
        colorMatcher.addColorMatch(kGreenTarget);
        colorMatcher.addColorMatch(kRedTarget);
        colorMatcher.addColorMatch(kYellowTarget);

        this.match = colorMatcher.matchClosestColor(detectedColor);
        // When the sensor detects a color it returns a string that represents the color under the control panel sensor. Sets colorString equal to 1 of the 4 colors.

        if (match.color == kBlueTarget)
            {
            colorString = "Y";
            }
        else if (match.color == kRedTarget)
            {
            colorString = "G";
            }
        else if (match.color == kGreenTarget)
            {
            colorString = "B";
            }
        else if (match.color == kYellowTarget)
            {
            colorString = "R";
            }
        else
            {
            colorString = "U";
            }

        if (timeToStopColorAlign == false)
            {
            if (!inRange())
                {
                this.motor.set(0);
                timeToStopColorAlign = true;
                return true;
                }

            // Check to see if the color under the field sensor is the same as the FMS color data
            if (colorString.equals(spinColor))
                {
                this.motor.set(0);
                timeToStopColorAlign = true;
                SmartDashboard.putString("colorString", colorString);
                return true;
                }
            else
                {
                // Spin motor until specified distance has been reached
                SmartDashboard.putString("colorString", colorString);
                this.motor.set(this.speed);
                }
            }
        return false;
    }

    final ColorMatch colorMatcher = new ColorMatch();

    final Color kBlueTarget = ColorMatch.makeColor(0.143, 0.427, 0.429);

    final Color kGreenTarget = ColorMatch.makeColor(0.197, 0.561, 0.240);

    final Color kRedTarget = ColorMatch.makeColor(0.561, 0.232, 0.114);

    final Color kYellowTarget = ColorMatch.makeColor(0.361, 0.524, 0.113);

    private ColorMatchResult match = null;

    private boolean timeToStopColorAlign = true;

    private boolean firstIteration = false;

    private String spinColor = null;

    private double speed = .4;

    private double numberOfSpins = 4; //Distance is the amount of rotations

    private double circumference = 100.5309; //Circumference of the color wheel

    }
