package frc.vision;

import frc.Hardware.Hardware;
import edu.wpi.first.wpilibj.Timer;

/**
 * an alternate drive to vision class the also uses the ultrasonic to control
 * distance. This is not good at long distances or while moving at high speeds
 * due to the inconsistance of the ultrasonic
 *
 * @author Conner McKevitt
 */
public class LimelightDriveWithVision
    {

    /**
     * Aligns to vision targets using a one turn system. For use if already mostly.
     * Will drive up to deposit distance from the targets based off of botht the
     * ultrasonic and vision getDistance. speed shoult not be over .3
     *
     * @return
     */

    Timer timer = new Timer();

    public boolean driveToTarget(int distance, boolean overrideUltrasonic, double speed)
    {
        // this.timer.start();
        // System.out.println(this.timer.get() * 10000);

        Hardware.visionInterface.takePicture();

        // offness recieved from network tables
        double offness = Hardware.visionInterface.getXOffSet();

        // left move speed
        double adjustmentValueRight = 0;
        // right move speed
        double adjustmentValueLeft = 0;
        if (overrideUltrasonic)
            {
            if (Hardware.frontUltraSonic.getDistanceFromNearestBumper() < 15)
                {
                this.timer.stop();
                return true;
                }
            }
        // if (!Hardware.visionInterface.getHasTargets())
        // {
        // return true;
        // }

        if (Hardware.visionInterface.getDistanceFromTarget() >= distance)
            {

            if (offness < -ACCEPTABLE_OFFNESS)
                {
                // adjust the speed for the left and right motors based off their offness and a
                // preset proportional value
                adjustmentValueLeft = speed - (Math.abs(offness) * ADJUST_PROPORTION_2019);
                adjustmentValueRight = speed + (Math.abs(offness) * ADJUST_PROPORTION_2019);
                // drive raw so that we dont have to write addition gearing code in teleop
                Hardware.transmission.driveRaw(adjustmentValueLeft, adjustmentValueRight);
                }

            else if (offness > ACCEPTABLE_OFFNESS)
                {

                adjustmentValueLeft = speed + (Math.abs(offness) * ADJUST_PROPORTION_2019);
                adjustmentValueRight = speed - (Math.abs(offness) * ADJUST_PROPORTION_2019);

                Hardware.transmission.driveRaw(adjustmentValueLeft, adjustmentValueRight);
                }
            else
                {
                // drive raw at speed after aligning
                Hardware.transmission.driveRaw(DRIVE_AFTER_ALIGN, DRIVE_AFTER_ALIGN);
                }
            }
        else
            {
            Hardware.transmission.drive(0, 0);
            this.timer.stop();
            return true;
            }
        return false;

    }

    /**
     * For use if auto mostly. will run toward the target until the target no longer
     * exist. Use at your own discretion. Kilroy Robotics and the author of this
     * code holds no responsibility for any damage to property or the guy that was
     * hit with the robot
     *
     * thou speed shalt not be over a percentatage of 30. Put it to one if you want
     * to have fun
     *
     * @return
     */

    public boolean driveToTargetNoDistance(double speed)
    {
        this.timer.start();
        // System.out.println(this.timer.get() * 10000);
        if (this.timer.get() * 100000 > 4)
            {
            Hardware.visionInterface.takePicture();
            this.timer.reset();
            }
        // offness recieved from network tables
        double offness = Hardware.visionInterface.getXOffSet();

        // left move speed
        double adjustmentValueRight = 0;
        // right move speed
        double adjustmentValueLeft = 0;
        // System.out.println("Target: " + Hardware.visionInterface.getHasTargets());

        if (Hardware.visionInterface.getHasTargets())
            {

            if (offness < -ACCEPTABLE_OFFNESS)
                {
                // adjust the speed for the left and right motors based off their offness and a
                // preset proportional value
                adjustmentValueLeft = speed - (Math.abs(offness) * ADJUST_PROPORTION_2019);
                adjustmentValueRight = speed + (Math.abs(offness) * ADJUST_PROPORTION_2019);
                // drive raw so that we dont have to write addition gearing code in teleop
                Hardware.transmission.driveRaw(adjustmentValueLeft, adjustmentValueRight);
                }

            else if (offness > ACCEPTABLE_OFFNESS)
                {

                adjustmentValueLeft = speed + (Math.abs(offness) * ADJUST_PROPORTION_2019);
                adjustmentValueRight = speed - (Math.abs(offness) * ADJUST_PROPORTION_2019);

                Hardware.transmission.driveRaw(adjustmentValueLeft, adjustmentValueRight);
                }
            else
                {
                // drive raw at speed after aligning
                Hardware.transmission.driveRaw(DRIVE_AFTER_ALIGN, DRIVE_AFTER_ALIGN);
                }
            }
        else
            {
            Hardware.transmission.drive(0, 0);
            // this.timer.stop();
            return true;
            }
        return false;

    }

    /**
     * aligns to the target with out driving
     *
     * @return boolean if within deadband
     */
    public boolean alignToTarget()
    {
        // System.out.println("aligning");
        Hardware.visionInterface.takePicture();
        double offness = Hardware.visionInterface.getXOffSet();

        // left move speed
        double adjustmentValueRight = 0;
        // right move speed
        double adjustmentValueLeft = 0;

        if (offness < -ACCEPTABLE_OFFNESS)
            {
            // adjust the speed for the left and right motors based off their offness and a
            // preset proportional value
            adjustmentValueLeft = -(Math.abs(offness) * ADJUST_PROPORTION_2019_ALIGN);

            adjustmentValueRight = (Math.abs(offness) * ADJUST_PROPORTION_2019_ALIGN);
            // drive raw so that we dont have to write addition gearing code in teleop
            Hardware.transmission.driveRaw(adjustmentValueLeft, adjustmentValueRight);
            }

        else if (offness > ACCEPTABLE_OFFNESS)
            {

            adjustmentValueLeft = (Math.abs(offness) * ADJUST_PROPORTION_2019_ALIGN);

            adjustmentValueRight = -(Math.abs(offness) * ADJUST_PROPORTION_2019_ALIGN);

            Hardware.transmission.driveRaw(adjustmentValueLeft, adjustmentValueRight);
            }
        else
            {

            return true;
            }

        return false;
    }

    // minimum speed a motor will move while aligning
    final double MIN_MOVE_2019 = .15;

    // after the robot is align the speed that the robot will continue at
    final double DRIVE_AFTER_ALIGN = .2;
    // an adjustment proportional value. Found with the tried and true method of
    // randomly plugging in number until it works
    final double ADJUST_PROPORTION_2019 = .015;// 0.03

    final double ADJUST_PROPORTION_2019_ALIGN = .015;
    // an adjustment proportional value. Found with the tried and true method of
    // randomly plugging in number until it works
    final double ADJUST_PROPORTION_2020 = .015;// TODO
    // after the robot is align the speed that the robot will continue at
    final double MIN_MOVE_2020 = .2;// TODO

    // distance away from the target that the robot will stop at
    final double STOP_DISTANCE_TEST = 50;// TODO
    final int ULTRA_OVERRIDE = 20;

    final double ACCEPTABLE_OFFNESS = 3;
    }
