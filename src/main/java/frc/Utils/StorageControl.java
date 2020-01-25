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
            this.intakeLR = intakeLR;
            this.lowerLR = lowerLR;
            this.upperLR = upperLR;
            this.shootLR = shootLR;
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
            if (ballCount < MAX_BALLS)
                ballCount++;
            }
        else if (!getControlledRLOutput(this.intakeLR) && Hardware.intake.outtaking)
            {
            if (ballCount > 0)
                ballCount--;
            }
        if (getControlledRLOutput(this.shootLR) && Hardware.launcher.launching)
            {
            if (ballCount > 0)
                ballCount--;
            }

        return ballCount;
    }

    private boolean prevLR = false;

    public boolean getControlledRLOutput(LightSensor lightSensor)
    {

        if (lightSensor.get() && !prevLR)
            {
            prevLR = true;
            return true;
            }
        if (!lightSensor.get())
            {
            prevLR = false;
            }
        return false;
    }

    private final int MAX_BALLS = 5;
    private final int MIN_BALLS = 0;

    }
