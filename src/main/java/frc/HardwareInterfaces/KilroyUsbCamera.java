/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.HardwareInterfaces;

import frc.Hardware.*;

/**
 * Add your docs here.
 */
public class KilroyUsbCamera
    {
    private static boolean startOfMatch = false;
    private static boolean cam0 = false;

    /**
     *
     */
    public static void switchCameras()
    {
        if (startOfMatch && Hardware.leftOperator.getRawButton(7))
            {
            Hardware.camTimer1.stop();
            Hardware.camTimer1.reset();
            Hardware.camTimer1.start();
            Hardware.server.setSource(Hardware.usbCam1);
            startOfMatch = false;
            cam0 = false;
            }
        if (Hardware.leftOperator.getRawButton(7) && cam0 && Hardware.camTimer2.get() >= 1.0)
            {
            Hardware.camTimer1.stop();
            Hardware.camTimer1.reset();
            Hardware.camTimer1.start();
            Hardware.server.setSource(Hardware.usbCam1);
            startOfMatch = false;
            cam0 = false;
            }
        if (Hardware.leftOperator.getRawButton(7) && !cam0 && Hardware.camTimer1.get() >= 1.0)
            {
            Hardware.camTimer2.stop();
            Hardware.camTimer2.reset();
            Hardware.camTimer2.start();
            Hardware.server.setSource(Hardware.usbCam0);
            cam0 = true;
            }
    }
    }
