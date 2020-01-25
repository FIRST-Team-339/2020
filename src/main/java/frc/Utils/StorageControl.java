package frc.Utils;

import frc.HardwareInterfaces.LightSensor;

public class StorageControl
    {
    LightSensor intakeLR = null;
    LightSensor lowerLR = null;

    LightSensor upperLR = null;
    LightSensor shootLR = null;

    public StorageControl(LightSensor intakeLR, LightSensor lowerLR, LightSensor upperLR, LightSensor shootLR)
        {

        }

    public boolean ejectBalls()
    {
        if (this.getBallCount() > 0)
            {
            // set motor to outtake

            }
        else
            {
            return true;
            }

        return false;
    }

    public int getBallCount()
    {
        // return the ball count

        return 0;
    }

    }
