/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.HardwareInterfaces;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSink;

/**
 * Add your docs here.
 */
public class KilroyUSBCamera
    {
    /**
     * constructor
     *
     * @param server
     * @param cam0
     * @param cam1
     */
    public KilroyUSBCamera(VideoSink server, UsbCamera cam0, UsbCamera cam1)
        {
            initialize(server, cam0, cam1);
        }

    /**
     * constructor
     *
     * @param server
     * @param cam0
     * @param cam1
     * @param button
     */
    public KilroyUSBCamera(VideoSink server, UsbCamera cam0, UsbCamera cam1, MomentarySwitch button)
        {
            initialize(server, cam0, cam1);
            this.button = button;
        }

    /**
     * constructor
     *
     * @param server
     * @param cam0
     * @param cam1
     * @param button1
     * @param button2
     */
    public KilroyUSBCamera(VideoSink server, UsbCamera cam0, UsbCamera cam1, MomentarySwitch button1,
            MomentarySwitch button2)
        {
            initialize(server, cam0, cam1);
            this.button1 = button1;
            this.button2 = button2;
        }

    private void initialize(VideoSink server, UsbCamera cam0, UsbCamera cam1)
    {
        this.server = server;
        this.cam0 = cam0;
        this.cam1 = cam1;
    }

    /**
     * Method for switching between the usb cameras on the robot
     */
    public void switchCameras()
    {
        // TODO test
        System.out.println(this.cam0.isConnected());
        // if (this.cam0.isConnected())
        // {
        // this.server.setSource(this.cam1);
        // }
        // if (this.cam1.isConnected())
        // {
        // this.server.setSource(this.cam0);
        // }
    }

    /**
     * This overload takes in one button and calls the switchCameras() method
     * 
     * @param button
     */
    public void switchCameras(MomentarySwitch button)
    {
        // button starts as off
        // if (/*momentary switch thing*/)
        // {
        // switchCameras();
        // // momentary switch thing
        // }
    }

    /**
     * This overload takes in two buttons and calls the switchCameras() method
     *
     * @param button1
     * @param button2
     */
    public void switchCameras(MomentarySwitch button1, MomentarySwitch button2)
    {
        // // button starts as off
        // if (/*momentary switch thing*/)
        // {
        // switchCameras();
        // // momentary switch thing
        // }
    }

    private VideoSink server = null;
    private MomentarySwitch button = null;
    private MomentarySwitch button1 = null;
    private MomentarySwitch button2 = null;
    private UsbCamera cam0 = null;
    private UsbCamera cam1 = null;
    // end class
    }
