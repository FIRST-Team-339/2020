package frc.vision;

import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.Hardware.Hardware;
import frc.HardwareInterfaces.MomentarySwitch;
import edu.wpi.first.networktables.*;

/**
 * an interface class for the limelight vision camera
 * 
 * @author Conner McKevitt
 *
 *
 */
public class LimelightInterface {

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

    // to be called continuously
    // updates internal values with those recieved from the network table
    public void updateValues() {

        try {

            hasTargets = hasTargets(tv.getDouble(0));
            x = tx.getDouble(0);
            y = ty.getDouble(0);
            area = ta.getDouble(0);
            skew = ts.getDouble(0);
            latency = tl.getDouble(0);
            shortSide = tshort.getDouble(0);
            longSide = tlong.getDouble(0);
            horizontal = thor.getDouble(0);
            vertical = tvert.getDouble(0);
            pipeline = getpipe.getDouble(0);
            led_Mode = Led_Mode.getDouble(0);

        } catch (NullPointerException exception) {
            System.out.println(exception);
        }

    }

    /**
     * The x offset from the center of camera
     *
     * @return
     */
    public double getXOffSet() {
        return x;

    }

    /**
     * The y offset from the center of camera
     *
     * @return
     */
    public double getYOffSet() {
        return y;
    }

    /**
     * the percent area a blob takes up
     *
     * @return
     */
    public double getArea() {
        return area;
    }

    /**
     * returns the skew of the blob
     *
     * @return
     */
    public double getSkew() {
        return skew;
    }

    /**
     * returns the latency of the limelight
     *
     * @return
     */
    public double getLatency() {
        return latency;
    }

    /**
     *
     * returns the length of the shortest side of the blob
     *
     * @return
     */
    public double getShortSide() {
        return shortSide;
    }

    /**
     *
     * returns the length of the longest side of the blob
     *
     * @return
     */
    public double getLongSide() {
        return longSide;
    }

    /**
     *
     * returns the length of the horizontal side of the blob
     *
     * @return
     */
    public double getHorizontalSide() {
        return horizontal;
    }

    /**
     * returns the length of the vertical side of the blob
     *
     * @return
     */
    public double getVerticalSide() {
        return vertical;
    }

    /**
     * returns the current pipeline that is in use
     *
     * @return
     */
    public double getPipeline() {
        return pipeline;
    }

    /**
     * publishes all of the data provided by the limelight to the smartdashboard for
     * debugging.
     *
     * @param MomentarySwitch switch to enable/disable the publishing of the values
     *
     */
    public void publishValues(MomentarySwitch publishSwitch) {

        publishSwitch.update();

        if (publishSwitch.get()) {
            SmartDashboard.putBoolean("hasTargets", hasTargets);
            SmartDashboard.putNumber("x offset", x);
            SmartDashboard.putNumber("y offset", y);
            SmartDashboard.putNumber("skew ", skew);
            SmartDashboard.putNumber("latency ", latency);
            SmartDashboard.putNumber("shortside", shortSide);
            SmartDashboard.putNumber("longSide ", longSide);
            SmartDashboard.putNumber("horizontal ", horizontal);
            SmartDashboard.putNumber("vertical ", vertical);
            SmartDashboard.putNumber("pipeline ", pipeline);
            SmartDashboard.putNumber("ledMode", led_Mode);
            SmartDashboard.putNumber("distance", getDistanceFromTarget());
        }
    }

    /**
     * returns whether the amount of blobs is > 1
     *
     * @return boolean
     */
    public boolean hasTargets(double targets) {
        if (targets > 0) {
            return true;
        }
        return false;
    }

    /**
     * enum to control the LedMode of the limelight
     */
    private enum LedMode {
        PIPELINE, OFF, BLINK, ON
    }

    private LedMode ledmode = LedMode.PIPELINE;

    // *@parameters
    // 0 pipeline control
    // 1 off
    // 2 blink
    // 3 on

    /**
     * sets the led mode of the limelight. This is TODO as it has yet to be tested
     *
     * @param ledMode 0 =pipleline control 1 = off 2 = blink 3 = on
     */
    public void setLedMode(LedMode ledMode) {
        switch (ledmode) {
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
        }
    }

    /**
     * enum to control the camera mode of the limelight
     */
    private enum CamMode {
        PROCESSOR, CAMERA
    }

    private CamMode camMode = CamMode.PROCESSOR;

    // TODO make enum
    // 0 vision processor
    // 1 driver camera

    /**
     * sets the camera mode for the limelight. TODO, this has been written but has
     * yet to be tested
     *
     *
     * @param mode 0 = vision processor 1 = driver camera
     */
    public void setCamMode(int mode) {
        switch (camMode) {
        case PROCESSOR:
            limelight.getEntry("camMode").setNumber(0);
            break;
        case CAMERA:
            limelight.getEntry("camMode").setNumber(1);
            break;
        }
    }

    /**
     *
     * sets the current processing pipeline for thelimelight
     *
     * @param pipe the pipeline number this has to be made in the web interface for
     *             the limelight
     */
    public void setPipeline(int pipe) {
        limelight.getEntry("pipeling").setNumber(pipe);
    }

    /**
     * takePictureWithButtons passes in two joystick buttons and when they are both
     * pressed, it takes a single picture
     *
     * @author Patrick
     * @param leftOp1
     * @param leftOp2
     */
    public void takePictureWithButtons(JoystickButton leftOp1, JoystickButton leftOp2) {
        if (leftOp1 != null && leftOp2 != null) {
            if (leftOp1.get() && leftOp2.get() && buttonHasBeenPressed && !hasButtonBeenPressed) {

                hasButtonBeenPressed = true;

                NetworkTableInstance.getDefault().getTable("limelight").getEntry("snapshot").setNumber(1);
                System.out.println("Picture has been taken");
            }

            if (leftOp1.get() && leftOp2.get() && !buttonHasBeenPressed)
                buttonHasBeenPressed = true;

            if (!leftOp1.get() && !leftOp2.get()) {
                buttonHasBeenPressed = false;
                hasButtonBeenPressed = false;
            }
        }
    }

    // variable for storing distance
    private double distance = 0;

    /**
     * returns the distance away from a blob. The cameraHeight, cameraAngle, and
     * targetHeight have to be set in the final variables at the end of the class
     *
     * @return distance away from the blob
     */
    public double getDistanceFromTarget() {

        distance = (CAMERA_HEIGHT - TARGET_HEIGHT)
                / Math.tan(Math.toRadians(MOUNTING_ANGLE) - Math.toRadians(Math.abs(getYOffSet())));

        if (hasTargets == true) {

            return Math.abs(distance);
        } else {
            return 0;
        }
    }

    private final double CAMERA_HEIGHT = 35.25;// TODO

    private final double TARGET_HEIGHT = 83.5;// TODO

    final double MOUNTING_ANGLE = 35;// 35;// TODO

    private boolean buttonHasBeenPressed = false;
    private boolean hasButtonBeenPressed = false;
}
