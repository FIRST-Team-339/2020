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
     * ultrasonic and vision getDistance.
     *
     * @return
     */

    Timer timer = new Timer();

    public boolean driveToTarget(int distance, boolean overrideUltrasonic)
    {
        this.timer.start();
        System.out.println(this.timer.get() * 10000);
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
        if (overrideUltrasonic)
            {
            if (Hardware.frontUltraSonic.getDistanceFromNearestBumper() < 20)
                {
                this.timer.stop();
                return true;
                }
            }
        if (Hardware.visionInterface.getDistanceFromTarget() >= distance)
            {

            if (offness < 0)
                {
                // adjust the speed for the left and right motors based off their offness and a
                // preset proportional value
                adjustmentValueLeft = MIN_MOVE_2019 - (Math.abs(offness) * ADJUST_PORP_2019);
                adjustmentValueRight = MIN_MOVE_2019 + (Math.abs(offness) * ADJUST_PORP_2019);
                // drive raw so that we dont have to write addition gearing code in teleop
                Hardware.transmission.driveRaw(adjustmentValueLeft, adjustmentValueRight);
                }

            else if (offness > 0)
                {

                adjustmentValueLeft = MIN_MOVE_2019 + (Math.abs(offness) * ADJUST_PORP_2019);
                adjustmentValueRight = MIN_MOVE_2019 - (Math.abs(offness) * ADJUST_PORP_2019);

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
     * TODO function. Will align to the target but not drive towards it
     *
     */
    public boolean alignToTarget()
    {
        return false;
    }

    // minimum speed a motor will move while aligning
    final double MIN_MOVE_2019 = .3;

    // after the robot is align the speed that the robot will continue at
    final double DRIVE_AFTER_ALIGN = .2;
    // an adjustment proportional value. Found with the tried and true method of
    // randomly plugging in number until it works
    final double ADJUST_PORP_2019 = .02;//0.015
    // an adjustment proportional value. Found with the tried and true method of
    // randomly plugging in number until it works
    final double ADJUST_PORP_2020 = .015;// TODO
    // after the robot is align the speed that the robot will continue at
    final double MIN_MOVE_2020 = .2;// TODO

    // distance away from the target that the robot will stop at
    final double STOP_DISTANCE_TEST = 50;// TODO
    }
