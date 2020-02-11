/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.HardwareInterfaces;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSink;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

/**
 * Class for controlling the USB cameras on the robot
 */
public class KilroyUSBCamera
    {

    /**
     * constructor
     */
    public KilroyUSBCamera(boolean twoFeeds)
        {
            if (twoFeeds == true)
                {
                this.cam0 = CameraServer.getInstance().startAutomaticCapture("usb0", 0);
                this.cam1 = CameraServer.getInstance().startAutomaticCapture("usb1", 1);
                }
            else
                {
                this.cam0 = CameraServer.getInstance().startAutomaticCapture("usb0", 0);
                }
        }

    /**
     * constructor
     *
     * @param button
     */
    public KilroyUSBCamera(MomentarySwitch button)
        {
            this.cam0 = CameraServer.getInstance().startAutomaticCapture("usb0", 0);
            this.cam1 = CameraServer.getInstance().startAutomaticCapture("usb1", 1);
            CameraServer.getInstance().removeServer("serve_usb1");
            this.server = CameraServer.getInstance().getServer("serve_usb0");
            this.button = button;
        }

    /**
     * constructor
     *
     * @param switch1
     * @param switch2
     */
    public KilroyUSBCamera(MomentarySwitch switch1, MomentarySwitch switch2)
        {
            this.cam0 = CameraServer.getInstance().startAutomaticCapture("usb0", 0);
            this.cam1 = CameraServer.getInstance().startAutomaticCapture("usb1", 1);
            CameraServer.getInstance().removeServer("serve_usb1");
            this.server = CameraServer.getInstance().getServer("serve_usb0");
            this.switch1 = switch1;
            this.switch2 = switch2;
        }

    /**
     * constructor
     *
     * @param button1
     * @param button2
     */
    public KilroyUSBCamera(JoystickButton button1, JoystickButton button2)
        {
            cam0 = CameraServer.getInstance().startAutomaticCapture("usb0", 0);
            cam1 = CameraServer.getInstance().startAutomaticCapture("usb1", 1);
            CameraServer.getInstance().removeServer("serve_usb1");
            server = CameraServer.getInstance().getServer("serve_usb0");
            this.button1 = button1;
            this.button2 = button2;
        }

    /**
     * Sets a given camera to be diplayed to the driver's station
     *
     * @param cameraNum
     */
    public void setCamera(int cameraNum)
    {
        if (cameraNum == 0)
            {
            this.server.setSource(this.cam0);
            }
        if (cameraNum == 1)
            {
            this.server.setSource(this.cam1);
            }
    }

    /**
     * Toggles which camera is being displayed on the driver's station
     */
    public void switchCameras()
    {
        if (this.cam1 != null)
            {
            if (this.cam0.isEnabled())
                {
                server.setSource(this.cam1);
                }
            else
                {
                server.setSource(this.cam0);
                }
            }
    }

    /**
     * Toggles the cameras with a momentary switch
     *
     * @param button
     */
    public void switchCameras(MomentarySwitch button)
    {
        if (this.button.isOnCheckNow() && firstCheck == true)
            {
            switchCameras();
            firstCheck = false;
            }
        if (this.button.isOnCheckNow() == false && firstCheck != true)
            {
            switchCameras();
            firstCheck = true;
            }
    }

    public void switchCameras(MomentarySwitch switch1, MomentarySwitch switch2)
    {
        if (this.switch1.isOnCheckNow() && firstCheck == true)
            {
            switchCameras();
            firstCheck = false;
            }
        if (this.switch1.isOnCheckNow() == false && firstCheck != true)
            {
            switchCameras();
            firstCheck = true;
            }
        if (this.switch2.isOnCheckNow() && firstCheck2 == true)
            {
            switchCameras();
            firstCheck2 = false;
            }
        if (this.switch2.isOnCheckNow() == false && firstCheck2 != true)
            {
            switchCameras();
            firstCheck2 = true;
            }
    }

    /**
     * button1 sets camera1 to be displayed to the driver's station, button2 sets camera0 to be displayed to the driver's station
     *
     * @param button1
     * @param button2
     */
    public void switchCameras(JoystickButton button1, JoystickButton button2)
    {
        if (this.cam1 != null)
            {
            if (this.button1.get())
                {
                server.setSource(this.cam1);
                }
            if (this.button2.get())
                {
                server.setSource(this.cam0);
                }
            }
    }

    private UsbCamera cam0 = null;

    private UsbCamera cam1 = null;

    private VideoSink server;

    private MomentarySwitch button = null;

    private MomentarySwitch switch1 = null;

    private MomentarySwitch switch2 = null;

    private JoystickButton button1 = null;

    private JoystickButton button2 = null;

    private boolean firstCheck = true;

    private boolean firstCheck2 = true;

    // end class
    }
