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

    public void storageUpdate()
    {
        getBallCount();
        getControlledRLOutput(intakeLR);
        //other stuff
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
                didThing = true;
            ballCount++;
            }
        else if (!getControlledRLOutput(this.intakeLR) && Hardware.intake.outtaking)
            {
            didThing = true;
            if (ballCount > 0)
                ballCount--;
            }
        if (getControlledRLOutput(this.shootLR) && Hardware.launcher.launching)
            {
            didThing = true;
            if (ballCount > 0)
                ballCount--;
            }

        return ballCount;
    }

    private boolean didThing = false;
    private boolean LRTriggered = false;

    public boolean getControlledRLOutput(LightSensor lightSensor)
    {
        if (didThing)
            {
            return false;
            }

        if (lightSensor.get() && !didThing)
            {

            return true;
            }
        if (!lightSensor.get())
            {
            return false;
            }
        return false;
    }

    public void jamAway()
    {
        //move the motors randomly enough that we unjam stuff

    }

    public void getBallPosition()
    {

    }

    public boolean prepareToShoot()
    {

        return false;
    }

    private final int MAX_BALLS = 5;
    private final int MIN_BALLS = 0;

    }
