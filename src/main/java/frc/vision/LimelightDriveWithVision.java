package frc.vision;

import frc.Hardware.Hardware;
import frc.vision.LimelightInterface;

public class LimelightDriveWithVision {

    /**
     * Aligns to vision targets using a one turn system. For use if already mostly.
     * Will drive up to deposit distance from the targets.
     *
     * @return
     */
    public boolean driveToTarget() {

        // if (!Hardware.visionInterface.filterPass) {
        // System.out.println("have no blobs. Cannot continue");
        // return true;
        // }
        System.out.println("aligning ");
        double offness = Hardware.visionInterface.getXOffSet();

        double adjustmentValueRight = 0;
        double adjustmentValueLeft = 0;

        // System.out.println("camera distance" +
        // Hardware.visionInterface.getDistanceFromTarget());
        if (Hardware.visionInterface.getDistanceFromTarget() >= STOP_DISTANCE_TEST) {
            System.out.println("driving to target");

            if (offness < 0) {

                adjustmentValueLeft = MIN_MOVE_2019 - (Math.abs(offness) * ADJUST_PORP_2019);
                adjustmentValueRight = MIN_MOVE_2019 + (Math.abs(offness) * ADJUST_PORP_2019);
                Hardware.drive.drive(adjustmentValueLeft, adjustmentValueRight);

                Hardware.transmission.driveRaw(adjustmentValueLeft, adjustmentValueRight);
            }

            else if (offness > 0) {

                adjustmentValueLeft = MIN_MOVE_2019 + (Math.abs(offness) * ADJUST_PORP_2019);
                adjustmentValueRight = MIN_MOVE_2019 - (Math.abs(offness) * ADJUST_PORP_2019);

                Hardware.drive.drive(adjustmentValueLeft, adjustmentValueRight);

                Hardware.transmission.driveRaw(adjustmentValueLeft, adjustmentValueRight);
            } else {
                Hardware.transmission.driveRaw(.2, .2);
            }
        } else {
            Hardware.transmission.drive(0, 0);
            return true;
        }
        return false;

    }

    final double MIN_MOVE_2019 = .2;

    final double ADJUST_PORP_2019 = .015;

    final double ADJUST_PORP_2020 = .015;// TODO

    final double MIN_MOVE_2020 = .2;// TODO

    final double STOP_DISTANCE_TEST = 80;

}
