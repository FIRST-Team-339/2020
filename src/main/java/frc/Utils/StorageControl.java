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

    public boolean prepareToShoot()
    {

        return false;
    }

    }
