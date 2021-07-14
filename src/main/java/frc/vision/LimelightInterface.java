package frc.vision;

import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.HardwareInterfaces.MomentarySwitch;
import edu.wpi.first.networktables.*;

/**
 * an interface class for the limelight vision camera
 *
 * the limelight can be access while connected to the robot in a web browser at
 * http://limelight.local:5801/
 *
 * the web application can be used to adjust the filtering values
 *
 * currrent camera settings as of 1/23/20 are exposure = 12 black offset
 * level=24 red balance = 1013 blue balance = 500
 *
 * @author Conner McKevitt
 *
 *
 */
public class LimelightInterface
    {

    NetworkTable limelight = NetworkTableInstance.getDefault().getTable("limelight");
    /*
     * Limelight dettings Exposure: 12 Black Level Offset: 24 Red Balance: 1013 Blue
     * Balance 500
     */

    // has target
    NetworkTableEntry tv = limelight.getEntry("tv");

    // horizontal offset
    NetworkTableEntry tx = limelight.getEntry("tx");

    // vertical offset
    NetworkTableEntry ty = limelight.getEntry("ty");

    // % area
    NetworkTableEntry ta = limelight.getEntry("ta");

    // skew
    NetworkTableEntry ts = limelight.getEntry("ts");

    // latency
    NetworkTableEntry tl = limelight.getEntry("tl");

    // length of shortest side
    NetworkTableEntry tshort = limelight.getEntry("tshort");

    // length of longest side
    NetworkTableEntry tlong = limelight.getEntry("tlong");

    // horizontal side length (The "strongest" avenger)
    NetworkTableEntry thor = limelight.getEntry("thor");

    // vertical side length
    NetworkTableEntry tvert = limelight.getEntry("tvert");

    // active pipeline
    NetworkTableEntry getpipe = limelight.getEntry("getpipe");

    // 3d stuff
    NetworkTableEntry camtran = limelight.getEntry("camtran");
    // led mod
    NetworkTableEntry Led_Mode = limelight.getEntry("ledMode");

    NetworkTableEntry tcornx = limelight.getEntry("tcornx");

    NetworkTableEntry tcorny = limelight.getEntry("tcorny");

    // our declarations of the limelight provided information
    private boolean hasTargets;

    private double x;

    private double y;

    private double area;

    private double skew;

    private double latency;

    private double shortSide;

    private double longSide;

    private double horizontal;

    private double vertical;

    private double pipeline;

    private double camTran;

    private double led_Mode;

    private double[] xcorner;

    private double[] ycorner;

    // to be called continuously
    // updates internal values with those recieved from the network table
    public void updateValues()
    {

        // dont let the null pointer monsters attack
        try
            {
            this.hasTargets = this.hasTargets(this.tv.getDouble(0));
            this.x = this.tx.getDouble(0);
            this.y = this.ty.getDouble(0);
            this.area = this.ta.getDouble(0);
            this.skew = this.ts.getDouble(0);
            this.latency = this.tl.getDouble(0);
            this.shortSide = this.tshort.getDouble(0);
            this.longSide = this.tlong.getDouble(0);
            this.horizontal = this.thor.getDouble(0);
            this.vertical = this.tvert.getDouble(0);
            this.pipeline = this.getpipe.getDouble(0);
            this.led_Mode = this.Led_Mode.getDouble(0);

            this.xcorner = this.tcornx.getDoubleArray(new double[0]);
            this.ycorner = this.tcorny.getDoubleArray(new double[0]);
            this.distance = this.getDistanceFromTarget();
            } catch (NullPointerException exception)
            {
            // System.out.println(exception);
            }

    }

    /**
     * The x offset from the center of camera
     *
     * @return
     */
    public double getXOffSet()
    {
        return this.x;

    }

    /**
     * if the vision has targets
     *
     * @return boolean
     */
    public boolean getHasTargets()
    {
        return this.hasTargets;
    }

    /**
     * The y offset from the center of camera
     *
     * @return
     */
    public double getYOffSet()
    {
        return this.y;
    }

    /**
     * the percent area a blob takes up
     *
     * @return
     */
    public double getArea()
    {
        return this.area;
    }

    /**
     * returns the skew of the blob
     *
     * @return
     */
    public double getSkew()
    {
        return this.skew;
    }

    /**
     * returns the latency of the limelight
     *
     * @return
     */
    public double getLatency()
    {
        return this.latency;
    }

    /**
     *
     * returns the length of the shortest side of the blob
     *
     * @return
     */
    public double getShortSide()
    {
        return this.shortSide;
    }

    /**
     *
     * returns the length of the longest side of the blob
     *
     * @return
     */
    public double getLongSide()
    {
        return this.longSide;
    }

    /**
     *
     * returns the length of the horizontal side of the blob
     *
     * @return
     */
    public double getHorizontalSide()
    {
        return this.horizontal;
    }

    /**
     * returns the length of the vertical side of the blob
     *
     * @return
     */
    public double getVerticalSide()
    {
        return this.vertical;
    }

    /**
     * returns the current pipeline that is in use
     *
     * @return
     */
    public double getPipeline()
    {
        return this.pipeline;
    }

    /**
     * publishes all of the data provided by the limelight to the smartdashboard for
     * debugging.
     *
     * @param MomentarySwitch
     *                            switch to enable/disable the publishing of the
     *                            values
     *
     */
    public void publishValues(MomentarySwitch publishSwitch)
    {

        publishSwitch.update();

        if (publishSwitch.get() == true)
            {
            SmartDashboard.putBoolean("hasTargets", this.hasTargets);
            SmartDashboard.putNumber("x offset", this.x);
            SmartDashboard.putNumber("y offset", this.y);
            SmartDashboard.putNumber("skew ", this.skew);
            SmartDashboard.putNumber("latency ", this.latency);
            SmartDashboard.putNumber("shortside", this.shortSide);
            SmartDashboard.putNumber("longSide ", this.longSide);
            SmartDashboard.putNumber("horizontal ", this.horizontal);
            SmartDashboard.putNumber("vertical ", this.vertical);
            SmartDashboard.putNumber("pipeline ", this.pipeline);
            SmartDashboard.putNumber("ledMode", this.led_Mode);
            SmartDashboard.putNumberArray("x corner", this.xcorner);
            SmartDashboard.putNumberArray("y corner", this.ycorner);
            SmartDashboard.putNumber("lowest y degree", this.getLowestYDegree());
            SmartDashboard.putNumber("distance", this.distance);

            }
    }

    /**
     * returns whether the amount of blobs is > 1
     *
     * @return boolean
     */
    public boolean hasTargets(double targets)
    {
        if (targets > 0.0)
            {
            return true;
            }
        return false;
    }

    /**
     * enum to control the LedMode of the limelight
     */
    public enum LedMode
        {
        PIPELINE, OFF, BLINK, ON
        }

    private LedMode ledmode = LedMode.PIPELINE;

    // 0 pipeline control
    // 1 off
    // 2 blink
    // 3 on

    /**
     * sets the led mode of the limelight.
     *
     * @param ledMode
     *                    0 =pipleline control 1 = off 2 = blink 3 = on
     */
    public void setLedMode(LedMode ledMode)
    {
        this.ledmode = ledMode;
        switch (this.ledmode)
            {
            case PIPELINE:
                limelight.getEntry("ledMode").setNumber(0);
                break;
            case OFF:
                limelight.getEntry("ledMode").setNumber(1);
                break;
            case BLINK:
                limelight.getEntry("ledMode").setNumber(2);
                break;
            case ON:
                limelight.getEntry("ledMode").setNumber(3);
                break;
            default:
                break;
            }
    }

    /**
     * enum to control the camera mode of the limelight
     */
    public enum CamMode
        {
        PROCESSOR, CAMERA
        }

    // TODO make enum
    // 0 vision processor
    // 1 driver camera

    /**
     * sets the camera mode for the limelight.
     *
     * TODO return number with an enum and forgo the switch
     *
     * @param mode
     *                 0 = vision processor 1 = driver camera
     */
    public void setCamMode(CamMode mode)
    {
        switch (mode)
            {
            case PROCESSOR:
                limelight.getEntry("camMode").setNumber(0);

                break;
            case CAMERA:
                limelight.getEntry("camMode").setNumber(1);
                break;
            default:
                break;
            }
    }

    /**
     *
     * sets the current processing pipeline for thelimelight
     *
     * @param pipe
     *                 the pipeline number this has to be made in the web interface
     *                 for the limelight
     */
    public void setPipeline(int pipe)
    {
        if (pipe <= 9 && pipe >= 0)
            {

            limelight.getEntry("pipeline").forceSetNumber(pipe);
            limelight.getEntry("pipeline").setNumber(pipe);
            // System.out.println("pipeline " + limelight.getEntry("pipeline
            // ").getDouble(0));
            }
    }

    /**
     * takePictureWithButtons passes in two joystick buttons and when they are both
     * pressed, it takes a single picture. Uses two booleans to make sure the camera
     * code is only run once per each button press, regardless of how long they are
     * held
     *
     * @author Patrick
     *
     * @param leftOp1
     * @param leftOp2
     */
    public void takePictureWithButtons(JoystickButton leftOp1, JoystickButton leftOp2)
    {
        // avoids a null pointer exception
        if (leftOp1 != null && leftOp2 != null)
            {
            // if both buttons are pressed and they aren't already pressed,this takes a
            // picture and registers that the buttons have been pressed so that way they
            // won't take another picture
            if (leftOp1.get() && leftOp2.get() && !this.hasButtonBeenPressed)
                {
                this.setLedMode(LedMode.ON);
                this.hasButtonBeenPressed = true;
                this.takePicture();
                }

            // if both buttons return true, and are not already pressed, than the boolean
            // registers the buttons have been pressed.
            if (leftOp1.get() && leftOp2.get() && !this.buttonHasBeenPressed)
                this.buttonHasBeenPressed = true;

            // if only one of the buttons or neither button is pressed, this makes sure both
            // booleans are false
            if (!leftOp1.get() && !leftOp2.get())
                {
                this.buttonHasBeenPressed = false;
                this.hasButtonBeenPressed = false;
                // this.setLedMode(LedMode.OFF);
                }
            }
    }

    /**
     * Takes a picture with the limelight and stores it on the limelight
     */
    public void takePicture()
    {
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("snapshot").setNumber(1);
        System.out.println("Picture has been taken");
    }

    // TODO implement a class the returns the coordinates of all the points and can
    // give our 3d position on the field NOTE: this would probably get us an award
    // and would be kinda cool

    private double lowestY = 0;
    private double lowestDegree = 0;

    /**
     * returns the lowest x coordinat of the vision target. this is for use
     *
     * @return double
     */
    public double getLowestYDegree()
    {

        if (this.hasTargets)
            {
            lowestY = this.ycorner[0];
            // compare all the x coordinates in order to find the lowest
            for (int i = 0; i < this.ycorner.length; i++)
                {
                if (this.ycorner[i] > lowestY)
                    {
                    lowestY = this.ycorner[i];
                    }
                }
            // get the angle of the lowestY base off of the fov of the camera. lowestY is in
            // pixels with 0 being at the top of the frame
            // this is currently assuming that the resolution is 320x240 the lowest setting
            // for the limelight camera

            if (lowestY < 120)
                {
                lowestDegree = 24.85 - ((lowestY / 120) * 24.85);
                }
            else if (lowestY > 120)
                {
                lowestDegree = -((lowestY - 120) / 120) * (49.7 / 2);
                }
            else if (lowestY == 120)
                {
                lowestDegree = 0;
                }
            }
        return lowestDegree;
    }

    // variable for storing distance
    private double distance = 0;

    /**
     * returns the distance away from a blob. The cameraHeight, cameraAngle, and
     * targetHeight have to be set in the final variables at the end of the class
     *
     * @return distance away from the blob
     */
    public double getDistanceFromTarget()
    {

        // not so fancy trig stuff that is pretty self explanitory
        // distance = (this.cameraHeight - this.targetHeight)
        // / Math.tan(Math.toRadians(this.mountingAngle) -
        // Math.toRadians(Math.abs(getYOffSet())));

        // not so fancy trig stuff that is pretty self explanitory
        distance = (this.cameraHeight - this.targetHeight)
                / Math.tan(Math.toRadians(this.mountingAngle) - Math.toRadians(-this.getLowestYDegree()));

        if (hasTargets == true)
            {
            return Math.abs(distance);
            }
        else
            {
            return 0;
            }
    }

    /**
     * sets the camera height for use in getting distance
     */
    public void setCameraHeight(double cameraHeight)
    {
        this.cameraHeight = cameraHeight;
    }

    /**
     * sets the target height for use in getting distance
     */
    public void setTargetHeight(double targetHeight)
    {
        this.targetHeight = targetHeight;
    }

    /**
     * sets the mounting angle for use in getting distance
     */
    public void setMountingAngle(double mountingAngle)
    {
        this.mountingAngle = mountingAngle;
    }

    /**
     * @return camera height
     */
    public double getCameraHeight()
    {
        return this.cameraHeight;
    }

    /**
     *
     * @return target height
     */
    public double getTargetHeight()
    {
        return this.targetHeight;
    }

    /**
     *
     * @return mounting angle
     */
    public double getMountingAngle()
    {
        return this.mountingAngle;
    }

    private double cameraHeight = 0;

    private double targetHeight = 0;

    private double mountingAngle = 0;

    private boolean buttonHasBeenPressed = false;
    private boolean hasButtonBeenPressed = false;
    }
