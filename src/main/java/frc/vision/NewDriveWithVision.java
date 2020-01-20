package frc.vision;

import frc.Hardware.Hardware;
import frc.vision.NewVisionInterface;

public class NewDriveWithVision {

    /**
     * Aligns to vision targets using a one turn system. For use if already mostly.
     * Will drive up to deposit distance from the targets.
     *
     * @return
     */
    public boolean driveToTarget() {

        // if (!Hardware.visionInterface.filterPass) {
        //     System.out.println("have no blobs. Cannot continue");
        //     return true;
        // }
        System.out.println("aligning ");
        double offness = Hardware.visionInterface.getXOffSet();

        double adjustmentValueRight = 0;
        double adjustmentValueLeft = 0;

       // System.out.println("camera distance" + Hardware.visionInterface.getDistanceFromTarget());
        if (Hardware.visionInterface.getDistanceFromTarget() >= STOP_DISTANCE_TEST)
        {
            System.out.println("driving to target");

            if (offness < 0) {

                adjustmentValueLeft = MIN_MOVE - (Math.abs(offness) * ADJUST_PORP);
                adjustmentValueRight = MIN_MOVE + (Math.abs(offness) * ADJUST_PORP);
                Hardware.drive.drive(adjustmentValueLeft,
                adjustmentValueRight);
                
               Hardware.transmission.driveRaw(adjustmentValueLeft, adjustmentValueRight);
            }

            else
             if (offness > 0) 
            {

                adjustmentValueLeft = MIN_MOVE + (Math.abs(offness) * ADJUST_PORP);
                adjustmentValueRight = MIN_MOVE - (Math.abs(offness) * ADJUST_PORP);

                Hardware.drive.drive(adjustmentValueLeft,
                adjustmentValueRight);
                
               Hardware.transmission.driveRaw(adjustmentValueLeft, adjustmentValueRight);
            }
            else
            {
            Hardware.transmission.driveRaw(.2, .2);
            }
        } else {
            return true;
        }
        return false;

    }


    final double MIN_MOVE = .15;

    final double ADJUST_PORP = .01;

    final double STOP_DISTANCE_TEST = 80;

}
