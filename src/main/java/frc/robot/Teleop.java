/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved. */
/* Open Source Software - may be modified and shared by FRC teams. The code */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project. */
/*----------------------------------------------------------------------------*/
// ====================================================================
// FILE NAME: Teleop.java (Team 339 - Kilroy)
//
// CREATED ON: Jan 13, 2015
// CREATED BY: Nathanial Lydick
// MODIFIED ON: June 20, 2019
// MODIFIED BY: Ryan McGee
// ABSTRACT:
// This file is where almost all code for Kilroy will be
// written. All of these functions are functions that should
// override methods in the base class (IterativeRobot). The
// functions are as follows:
// -----------------------------------------------------
// Init() - Initialization code for teleop mode
// should go here. Will be called each time the robot enters
// teleop mode.
// -----------------------------------------------------
// Periodic() - Periodic code for teleop mode should
// go here. Will be called periodically at a regular rate while
// the robot is in teleop mode.
// -----------------------------------------------------
//
// ====================================================================
package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.Hardware.Hardware;

/**
 * This class contains all of the user code for the Autonomous part of the
 * match, namely, the Init and Periodic code
 *
 * @author Nathanial Lydick
 * @written Jan 13, 2015
 */
public class Teleop
    {

    /**
     * User Initialization code for teleop mode should go here. Will be called once
     * when the robot enters teleop mode.
     *
     * @author Nathanial Lydick
     * @written Jan 13, 2015
     */
    public static void init()
    {

        // Gear Inits

        if (Hardware.robotIdentity.equals(Hardware.Identifier.PrevYear))
            {
            Hardware.drive.setGearPercentage(0, FIRST_GEAR);
            Hardware.drive.setGearPercentage(1, SECOND_GEAR);
            Hardware.drive.setGearPercentage(2, FORBIDDEN_THIRD_GEAR);
            }
        else
            {
            // TODO
            }

        Hardware.drive.setGear(0);

    } // end Init

    /**
     * User Periodic code for teleop mode should go here. Will be called
     * periodically at a regular rate while the robot is in teleop mode.
     *
     * @author Nathanial Lydick
     * @written Jan 13, 2015
     */

    public static boolean testBoolean = false;

    public static void periodic()
    {
        // =============== AUTOMATED SUBSYSTEMS ===============
        Hardware.visionInterface.updateValues();
        Hardware.visionInterface.publishValues(Hardware.publishVisionSwitch);
       /* int ballCount = 0;
        if(Hardware.rightOperator.getRawButton(6) == true /*&& ballCount >= 0 || ballCount < 5){
           ballCount++;
            SmartDashboard.putNumber("Ball Count", ballCount);
        }
            SmartDashboard.putNumber("Ball Count", ballCount);
*/

        if (Hardware.leftOperator.getRawButton(4))
            {
            testBoolean = true;
            }
        if (testBoolean)
            {
            if (Hardware.visionDriving.driveToTarget())
                {
                testBoolean = false;
                }
            }
        if (testBoolean == false)
            {

            teleopDrive();
            }
        // ================= OPERATOR CONTROLS ================

        // ================== DRIVER CONTROLS =================

         individualTest();
        // teleopDrive();

    } // end Periodic()

    public static void teleopDrive()
    {
        Hardware.drive.drive(Hardware.leftDriver, Hardware.rightDriver);

        // System.out.println("Speed levels: leftDriver" + Hardware.leftDriver.getY());
        // System.out.println("Speed levels: rightDriver" +
        // Hardware.rightDriver.getY());
        // System.out.println("Curent Gear" + Hardware.drive.getCurrentGear());

        Hardware.drive.shiftGears(Hardware.gearUp.get(), Hardware.gearDown.get());

        if (Hardware.drive.getCurrentGear() >= MAX_GEAR_NUMBER)
            {
            Hardware.drive.setGear(MAX_GEAR_NUMBER - 1);
            }

    }

    public static void individualTest()
    {
        // people test functions
        // connerTest();
        // craigTest();
         chrisTest();
        // dionTest();
        // patrickTest();
    }

    public static void connerTest()
    {

    }

    public static void craigTest()
    {

        if (Hardware.rightDriver.getRawButton(4) == true)
            {
            if (Hardware.invertTempoMomentarySwitch.isOn())
                {
                Hardware.invertTempoMomentarySwitch.setValue(false);
                }
            else
                {
                Hardware.invertTempoMomentarySwitch.setValue(true);
                }
            }
        if (Hardware.invertTempoMomentarySwitch.isOn())
            {
            // System.out.println("Should be inverted");
            Hardware.rightFrontMotor.setInverted(true);
            Hardware.leftFrontMotor.setInverted(true);
            }
        else
            {
            Hardware.leftFrontMotor.setInverted(false);
            Hardware.rightFrontMotor.setInverted(false);
            }

        // System.out.println(Hardware.rightFrontMotor.getInverted());
        System.out.println("Ticks: " + Hardware.rightDriveEncoder.getRate());

    }

    public static void dionTest()
    {
        if (Hardware.leftOperator.getRawButton(7) && cam0 && (Hardware.camTimer2.get() > 1 || startOfMatch))
            {
            Hardware.camTimer1.stop();
            Hardware.camTimer1.reset();

            Hardware.usbCam0.close();

            CameraServer.getInstance().removeServer("usb0");
            CameraServer.getInstance().removeCamera("usb0");

            // Hardware.usbCam0 = CameraServer.getInstance().
            // Hardware.camTimer1.start();

            System.out.println("Cam 1 on");
            cam0 = false;
            startOfMatch = false;

            }
        if (Hardware.leftOperator.getRawButton(8))
            {

            Hardware.usbCam0 = CameraServer.getInstance().startAutomaticCapture(4);

            }
        if (Hardware.leftOperator.getRawButton(7) && !cam0 && Hardware.camTimer1.get() > 1)
            {
            // Hardware.camTimer2.stop();
            // Hardware.camTimer2.reset();
            // Hardware.usbCam1.close();
            // Hardware.usbCam0 = CameraServer.getInstance().startAutomaticCapture(0);
            // Hardware.camTimer2.start();
            // System.out.println("Cam 0 on");
            // cam0 = true;
            }

    }

    public static void chrisTest()
    {
        int ballCount = 0;
        if(Hardware.rightOperator.getRawButton(6) == true && ballCount >= 0 || ballCount < 5){
           ballCount++;
            SmartDashboard.putNumber("Ball Count", ballCount);
        }
            SmartDashboard.putNumber("Ball Count", ballCount);
    }

    public static void patrickTest()
    {

    }

    public static void printStatements()
    {
        // ========== INPUTS ==========

        // ---------- DIGITAL ----------

        // Encoder Distances
        // Hardware.telemetry.printToConsole("L. Encoder Dist: " +
        // Hardware.leftDriveEncoder.getDistance());
        // Hardware.telemetry.printToConsole("R. Encoder Dist: " +
        // Hardware.rightDriveEncoder.getDistance());

        // Encoder Raw Values
        // Hardware.telemetry.printToConsole("L. Encoder Raw: " +
        // Hardware.leftDriveEncoder.get());
        // Hardware.telemetry.printToConsole("R. Encoder Raw: " +
        // Hardware.rightDriveEncoder.get());

        // Switch Values
        // Hardware.telemetry.printToConsole("Six Pos Sw: " +
        // Hardware.autoSixPosSwitch.getPosition());
        // Hardware.telemetry.printToConsole("Auto Disable Sw: " +
        // Hardware.autoDisableSwitch.isOn());

        // ---------- ANALOG -----------

        // Hardware.telemetry.printToConsole("Gyro: " + Hardware.gyro.getAngle());

        System.out.println("Delay Pot: " + Hardware.delayPot.get());

        // ----------- CAN -------------

        // -------- SUBSYSTEMS ---------

        // ---------- OTHER ------------

        // Left Driver
        // Hardware.telemetry.printToConsole("Left Driver X: " +
        // Hardware.leftDriver.getX());
        // Hardware.telemetry.printToConsole("Left Driver Y: " +
        // Hardware.leftDriver.getY());
        // Hardware.telemetry.printToConsole("Left Driver Z: " +
        // Hardware.leftDriver.getZ());

        // Right Driver
        // Hardware.telemetry.printToConsole("Right Driver X: " +
        // Hardware.rightDriver.getX());
        // Hardware.telemetry.printToConsole("Right Driver Y: " +
        // Hardware.rightDriver.getY());
        // Hardware.telemetry.printToConsole("Right Driver Z: " +
        // Hardware.rightDriver.getZ());

        // Left Operator
        // Hardware.telemetry.printToConsole("Left Op X: " +
        // Hardware.leftOperator.getX());
        // Hardware.telemetry.printToConsole("Left Op Y: " +
        // Hardware.leftOperator.getY());
        // Hardware.telemetry.printToConsole("Left Op Z: " +
        // Hardware.leftOperator.getZ());

        // Right Operator
        // Hardware.telemetry.printToConsole("Right Op X: " +
        // Hardware.rightOperator.getX());
        // Hardware.telemetry.printToConsole("Right Op Y: " +
        // Hardware.rightOperator.getY());
        // Hardware.telemetry.printToConsole("Right Op Z: " +
        // Hardware.rightOperator.getZ());

        // ========== OUTPUTS ==========

        // ---------- DIGITAL ----------

        // ---------- ANALOG -----------

        // ----------- CAN -------------

        // Motor Percentages
        // Hardware.telemetry.printToConsole("L.R. Motor: " +
        // Hardware.leftRearMotor.get());
        // Hardware.telemetry.printToConsole("R.R. Motor: " +
        // Hardware.rightRearMotor.get());
        // Hardware.telemetry.printToConsole("L.F. Motor: " +
        // Hardware.leftFrontMotor.get());
        // Hardware.telemetry.printToConsole("R.F. Motor: " +
        // Hardware.rightFrontMotor.get());

        // -------- SUBSYSTEMS ---------

        // ---------- OTHER ------------

    }

    private static boolean cam0 = true;

    private static boolean startOfMatch = true;

    private final static int MAX_GEAR_NUMBER = 2;

    private final static double FIRST_GEAR = .3;

    private final static double SECOND_GEAR = .5;

    private final static double FORBIDDEN_THIRD_GEAR = 1.0;

    } // end class
