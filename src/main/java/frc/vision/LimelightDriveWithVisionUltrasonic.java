package frc.vision;

import frc.Hardware.Hardware;
import frc.HardwareInterfaces.UltraSonic;
import frc.vision.LimelightInterface;

/**
 * an alternate drive to vision class the also uses the ultrasonic to control
 * distance. This is not good at long distances or while moving at high speeds
 * due to the inconsistance of the ultrasonic
 *
 * @author Conner McKevitt
 */
public class LimelightDriveWithVisionUltrasonic {

    UltraSonic ultraSonic = null;

    public LimelightDriveWithVisionUltrasonic(UltraSonic ultraSonic) {

        this.ultraSonic = ultraSonic;
    }

    /**
     * Aligns to vision targets using a one turn system. For use if already mostly.
     * Will drive up to deposit distance from the targets based off of botht the
     * ultrasonic and vision getDistance.
     *
     * @return
     */
    public boolean driveToTarget() {

        double offness = Hardware.visionInterface.getXOffSet();

        double adjustmentValueRight = 0;
        double adjustmentValueLeft = 0;

        if (Hardware.visionInterface.getDistanceFromTarget() >= STOP_DISTANCE_TEST
                || this.ultraSonic.getDistanceFromNearestBumper() >= STOP_DISTANCE_TEST) {

            if (offness < 0) {

                // adjust the speed for the left and right motors based off their offness and a
                // preset proportional value
                adjustmentValueLeft = MIN_MOVE_2019 - (Math.abs(offness) * ADJUST_PROP_2019);
                adjustmentValueRight = MIN_MOVE_2019 + (Math.abs(offness) * ADJUST_PROP_2019);
                // drive raw so that we dont have to write addition gearing code in teleop
                Hardware.transmission.driveRaw(adjustmentValueLeft, adjustmentValueRight);
            }

            else if (offness > 0) {

                adjustmentValueLeft = MIN_MOVE_2019 + (Math.abs(offness) * ADJUST_PROP_2019);
                adjustmentValueRight = MIN_MOVE_2019 - (Math.abs(offness) * ADJUST_PROP_2019);

                Hardware.transmission.driveRaw(adjustmentValueLeft, adjustmentValueRight);
            } else {
                // drive raw at speed
                Hardware.transmission.driveRaw(DRIVE_AFTER_ALIGN, DRIVE_AFTER_ALIGN);
            }
        } else {
            Hardware.transmission.drive(0, 0);
            return true;
        }
        return false;

    }

    final double MIN_MOVE_2019 = .2;
    final double DRIVE_AFTER_ALIGN = .2;

    // proportion
    final double ADJUST_PROP_2019 = .015;

    final double ADJUST_PROP_2020 = .015;// TODO

    final double MIN_MOVE_2020 = .2;// TODO

    final double STOP_DISTANCE_TEST = 80;

}