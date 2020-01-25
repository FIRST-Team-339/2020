package frc.Utils;

import frc.Hardware.Hardware;
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

    private int ballCount = 0;

    public int getBallCount()
    {

        // return the ball count

        if (getControlledRLOutput(this.intakeLR) && Hardware.intake.intaking)
            {
            ballCount++;
            }
        else if (!getControlledRLOutput(this.intakeLR) && Hardware.intake.outtaking)
            {
            ballCount--;
            }
        if (getControlledRLOutput(this.shootLR))
            {
            ballCount--;
            }
        else if (!getControlledRLOutput(this.shootLR))
            {
            ballCount++;
            }

        return 0;
    }

    private boolean prevLR = false;

    public boolean getControlledRLOutput(LightSensor lightSensor)
    {

        if (lightSensor.get())
            {
            prevLR = true;
            }
        else
            {
            prevLR = false;
            }
        if (lightSensor.get() == prevLR)
            {
            return false;
            }
        else
            {
            return true;
            }

    }

    }
