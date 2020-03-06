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

import java.util.Arrays;

//import frc.HardwareInterfaces.KilroyColorSensor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.Hardware.Hardware;
import frc.Utils.Launcher;
import frc.Utils.StorageControl;
import frc.Utils.StorageControl.ControlState;
// import com.revrobotics.ColorSensorV3;
import frc.vision.LimelightInterface.LedMode;

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
        if (Hardware.robotIdentity.equals(Hardware.yearIdentifier.PrevYear))
            {
            Hardware.drive.setGearPercentage(0, PREV_YEAR_FIRST_GEAR);
            Hardware.drive.setGearPercentage(1, PREV_YEAR_SECOND_GEAR);
            Hardware.drive.setGearPercentage(2, SO_YOU_EVER_HEAR_OF_SONIC);
            Hardware.launcherMotorEncoder.reset();
            }
        else
            {
            Hardware.drive.setGearPercentage(0, CURRENT_YEAR_FIRST_GEAR);
            Hardware.drive.setGearPercentage(1, CURRENT_YEAR_SECOND_GEAR);
            Hardware.drive.resetEncoders();
            }

        Hardware.drive.setGear(0);

        Hardware.intake.intaking = false;
        Hardware.intake.outtaking = false;

        Hardware.intake.usingVisionIntake = false;

        Hardware.launcher.resetShootTemps();

        StorageControl.setStorageControlState(ControlState.PASSIVE);

        Hardware.cameraServo.setCameraAngleUp();
        Hardware.storage.shooting = false;
        Hardware.visionInterface.setLedMode(LedMode.OFF);

    } // end Init

    /**
     * User Periodic code for teleop mode should go here. Will be called
     * periodically at a regular rate while the robot is in teleop mode.
     *
     * @author Nathanial Lydick
     * @written Jan 13, 2015
     */

    public static boolean testBoolean1 = false;

    public static boolean testBoolean2 = false;

    public static double timer = 2.0;

    public static double timeDown = .5;

    public static boolean wheelManualSpinBoolean = false;

    private static boolean disableTeleOpDrive = false;

    private static boolean firstRun = true;
    private static boolean secondRun = false;
    static int prevAngle = 0;

    public static void periodic()
    {

        if (secondRun == true)
            {
            Hardware.ballCounter.setBallCount(Robot.endAutoBallCount);
            secondRun = false;
            }
        if (firstRun == true)
            {
            secondRun = true;
            firstRun = false;
            }
        // =============== AUTOMATED SUBSYSTEMS ===============
        // System.out.println("RPM" + Hardware.launcherMotorEncoder.getRPM());
        Hardware.visionInterface.updateValues();
        Hardware.hoodControl.stopHoodMotor();
        // Hardware.visionInterface.publishValues(Hardware.publishVisionSwitch);
        Hardware.storage.intakeStorageControl();
        Hardware.storage.storageControlState();
        // System.out.println("distance" + Hardware.climbEncoder.getDistance());

        Hardware.climb.prepareToClimb(Hardware.climbMotorUpButton);
        Hardware.climb.climb(Hardware.climbMotorDownButton);
        // SmartDashboard.putString("Climb State: ",
        // Hardware.climb.climbState.toString());
        SmartDashboard.putNumber("Climb Distance", Hardware.climbEncoder.getDistance());

        // end control loops ==========================

        // ================= OPERATOR CONTROLS ================

        // ================= COLORWHEEL CONTROLS ==============
        // Press Right Operator button 4 to start manual spin. Press again to stop
        // manual spin
        // System.out.println("Distance" + Hardware.wheelSpinnerEncoder.getDistance());
        // System.out.println("Color Spin" + Hardware.spinWheelColorButton.get());
        // System.out.println("Spin" + Hardware.spinWheelButton.get());
        if (Hardware.wheelManualSpinButton.get())
            {
            wheelManualSpinBoolean = true;
            Hardware.colorWheel.manualSpin();
            }
        if (Hardware.wheelManualSpinButton.get() == false && wheelManualSpinBoolean)
            {
            wheelManualSpinBoolean = !wheelManualSpinBoolean;
            Hardware.wheelSpinnerMotor.set(0);
            }

        // will spin the control panel setNumberofSpins() amount of times.
        if (Hardware.spinWheelButton.get() == true)
            {
            // To change the number of spins.
            Hardware.colorWheel.setNumberOfSpins(4);
            // To change the speed
            Hardware.colorWheel.setSpeed(.65);
            // To enable spinControlPanel to start
            Hardware.colorWheel.start();
            }
        Hardware.colorWheel.spinControlPanel();

        // Will align the given color with the field sensor. Gets the color
        // automatically
        if (Hardware.spinWheelButton.get() && Hardware.colorWheel.getTimeToStopColorAlign() == false)
            {
            Hardware.colorWheel.setTimeToStopColorAlign(true);
            }
        else if (Hardware.spinWheelColorButton.get() == true)
            {
            // To change the speed
            Hardware.colorWheel.setSpeed(.4);

            // To enable spinControlPanelToColor to start
            Hardware.colorWheel.colorStart();
            }

        Hardware.colorWheel.spinControlPanelToColor();

        // ================== DRIVER CONTROLS =================

        // override convyor movement
        Hardware.storage.overrideConveyor(Hardware.leftOperator, Hardware.conveyorOverrideButton);
        // Hardware.launcherMotorGroup.set(.4);
        // if (Hardware.launchButton.get())
        // {
        // testRPM = 3001;
        // if (testRPM == 5000)
        // {
        // testRPM = 500;
        // }
        // }
        // Hardware.launcherMotorEncoder.setRPM(testRPM, Hardware.launcherMotorGroup);
        // System.out.println("RPM: " + Hardware.launcherMotorEncoder.getRPM());
        // System.out.println("wanted RPM: " + testRPM);
        // System.out.println("motor 1: " + Hardware.launcherMotor1.get());
        // System.out.println("motor 2: " + Hardware.launcherMotor2.get());
        // // System.out.println("motor ticks " +
        // Hardware.launcherMotorEncoder.getRPM());

        // shoot balls

        // pick up balls with vision
        Hardware.intake.pickUpBallsVisionTeleop(Hardware.pickupBallVisionButton);

        // intake controls
        if (Hardware.intake.usingVisionIntake == false || !Hardware.pickupBallVisionButton.get())
            {
            Hardware.launcher.shootBalls(Hardware.launchButton, Hardware.launchOverrideButton);
            // System.out.println("conveyor motor: " + Hardware.conveyorMotorGroup.get());
            // intake
            Hardware.intake.intake(Hardware.intakeButton, Hardware.intakeOverrideButton);

            Hardware.intake.outtake(Hardware.outtakeButton, Hardware.intakeOverrideButton);

            // this is necessary becuase I organized the code wrong and its too late to
            // rewrite intake
            // makes conveyor stop if not intakeing or outtaking
            Hardware.intake.makePassive(Hardware.intakeButton, Hardware.outtakeButton, Hardware.launchButton);
            }

        // ball counter code==============================
        // subtract ball
        Hardware.ballCounter.subtractBall(Hardware.subtractBallButton);
        // add ball
        Hardware.ballCounter.addBall(Hardware.addBallButton);
        // sets count to 0
        Hardware.ballCounter.clearCount(Hardware.subtractBallButton, Hardware.addBallButton);
        // end ball counter code===================

        // switch usb cameras
        Hardware.kilroyUSBCamera.switchCameras(Hardware.cameraSwitchButton1, Hardware.cameraSwitchButton2);

        if (!disableTeleOpDrive)
            {
            teleopDrive();
            }

        // individualTest();
        // printStatements();
    } // end Periodic()

    /**
     * turns offor on teleopdrive
     */
    public static boolean setDisableTeleOpDrive(boolean value)
    {
        disableTeleOpDrive = value;
        return disableTeleOpDrive;
    }

    /**
     * returns the disableTeleOpDrive boolean
     */
    public static boolean getDisableTeleOpDrive()
    {
        return disableTeleOpDrive;
    }

    public static void teleopDrive()
    {
        // System.out.println("teleop drive");
        Hardware.drive.drive(Hardware.leftDriver, Hardware.rightDriver);

        // System.out.println("Speed levels: leftDriver" + Hardware.leftDriver.getY());
        // System.out.println("Speed levels: rightDriver" +
        // Hardware.rightDriver.getY());
        // System.out.println("Curent Gear" + Hardware.drive.getCurrentGear());

        Hardware.drive.shiftGears(Hardware.gearUp.get(), Hardware.gearDown.get());

        if (Hardware.robotIdentity == Hardware.yearIdentifier.PrevYear)
            {
            if (Hardware.drive.getCurrentGear() >= PREV_YEAR_MAX_GEAR_NUMBER)
                {
                Hardware.drive.setGear(PREV_YEAR_MAX_GEAR_NUMBER - 1);
                }
            }
        else
            {
            if (Hardware.drive.getCurrentGear() >= CURRENT_YEAR_MAX_GEAR_NUMBER)
                {
                Hardware.drive.setGear(CURRENT_YEAR_MAX_GEAR_NUMBER - 1);
                }
            }

    }

    public static void individualTest()
    {
        // people test functions
        // connerTest();
        // craigTest();
        // chrisTest();
        // dionTest();
        // chrisTest();
        // dionTest();
        // patrickTest();
        // colourTest();
    }

    public static void connerTest()
    {

    }

    public static boolean flag = true;

    public static void craigTest()
    {
        // System.out.println("Distance: " +
        // Hardware.frontUltraSonic.getDistanceFromNearestBumper());
        // System.out.println("Delay: " + Hardware.delayPot.getValue());

        // System.out.println("TESTINGGGGGGG");
        // momentary settup

        // if (Hardware.rightDriver.getRawButton(4) == true)
        // {
        // if (Hardware.invertTempoMomentarySwitch.isOn())
        // {
        // Hardware.invertTempoMomentarySwitch.setValue(false);
        // }
        // else
        // {
        // Hardware.invertTempoMomentarySwitch.setValue(true);
        // }
        // }
        // if (Hardware.invertTempoMomentarySwitch.isOn())
        // {

        // }
        // else
        // {

        // }

    }

    public static void colouuurTest()
    {
        // Color detectedColor = Hardware.colorSensor.getColor();

        // final ColorMatch colorMatcher = new ColorMatch();
        // final Color kBlueTarget = ColorMatch.makeColor(0.143, 0.427, 0.429);
        // final Color kGreenTarget = ColorMatch.makeColor(0.197, 0.561, 0.240);
        // final Color kRedTarget = ColorMatch.makeColor(0.561, 0.232, 0.114);
        // final Color kYellowTarget = ColorMatch.makeColor(0.361, 0.524, 0.113);

        // colorMatcher.addColorMatch(kBlueTarget);
        // colorMatcher.addColorMatch(kGreenTarget);
        // colorMatcher.addColorMatch(kRedTarget);
        // colorMatcher.addColorMatch(kYellowTarget);

        // String colorString;
        // ColorMatchResult match = colorMatcher.matchClosestColor(detectedColor);

        // if (match.color == kBlueTarget)

        // int ballCount = 0;
        // if (Hardware.rightOperator.getRawButton(6) == true && ballCount >= 0 ||
        // ballCount < 5)
        // {
        // ballCount++;
        // SmartDashboard.putNumber("Ball Count", ballCount);
        // }
        // SmartDashboard.putNumber("Ball Count", ballCount);
        // if (Hardware.rightOperator.getRawButton(6) == true && ballCount >= 0 ||
        // ballCount < 5)
        // {
        // ballCount++;
        // SmartDashboard.putNumber("Ball Count", ballCount);
        // }
        // SmartDashboard.putNumber("Ball Count", ballCount);

        // if (Hardware.intakeButton.get() || Hardware.outtakeButton.get())
        // {
        // Hardware.intake.intake(Hardware.intakeButton);
        // Hardware.intake.outtake(Hardware.outtakeButton);
        // }
        // else
        // {
        // Hardware.intakeMotor.set(0);
        // }
        // SmartDashboard.putNumber("ball count", Hardware.storage.getBallCount());

        // if (Hardware.leftOperator.getRawButton(4))
        // {
        // colorString = "Blue";
        // }
        // else if (match.color == kRedTarget)
        // {
        // colorString = "Red";
        // }
        // else if (match.color == kGreenTarget)
        // {
        // colorString = "Green";
        // }
        // else if (match.color == kYellowTarget)
        // {
        // colorString = "Yellow";
        // }
        // else
        // {
        // colorString = "Unknown";
        // }

        // SmartDashboard.putNumber("Red", detectedColor.red);
        // SmartDashboard.putNumber("Green", detectedColor.green);
        // SmartDashboard.putNumber("Blue", detectedColor.blue);
        // SmartDashboard.putString("Detected Color", colorString);
    }

    public static void dionTest()
    {

    }

    public static void chrisTest()
    {

        /*
         * double timer = 2;
         *
         * if (Hardware.rightOperator.getRawButton(10) == true &&
         * Hardware.telopTimer.get() < timer) { Hardware.telopTimer.start(); //Start
         * timer Hardware.climbMotor.set(.5); //Start motor }
         *
         * if (Hardware.telopTimer.get() >= timer) { Hardware.climbMotor.set(0);
         * Hardware.telopTimer.stop(); //Stop timer Hardware.telopTimer.reset(); //Reset
         * timer } if (Hardware.rightOperator.getRawButton(7) == true) {
         * Hardware.liftSolenoid.set(Value.kForward); //Bring pistons down } if
         * (Hardware.rightOperator.getRawButton(8) == true &&
         * Hardware.rightOperator.getRawButton(9) == true) {
         * Hardware.climbMotor.set(-.5); } if (Hardware.rightOperator.getRawButton(8) ==
         * false && Hardware.rightOperator.getRawButton(9) == false &&
         * Hardware.telopTimer.get() == 0) { Hardware.climbMotor.set(0); } if
         * (Hardware.rightOperator.getRawButton(6) == true) {
         * Hardware.liftSolenoid.set(Value.kReverse); //Brings pistons up }
         */

        // For Test && full resets motor and piston
        /*
         * if(Hardware.leftOperator.getRawButton(10) == true){
         * Hardware.wheelSpinnerMotor.set(0); Hardware.telopTimer.stop();
         * Hardware.telopTimer.reset(); Hardware.liftSolenoid.set(Value.kReverse);
         * Hardware.liftSolenoid.set(Value.kOff); }
         */
    }

    public static void patrickTest()
    {

    }

    public static void printStatements()
    {
        // ========== INPUTS ==========

        // ---------- DIGITAL ----------
        // encoders:
        // Encoder Distances
        // Hardware.telemetry.printToShuffleboard("L encoder ticks", "" +
        // Hardware.leftDriveEncoder.get());
        // Hardware.telemetry.printToConsole("L. Encoder Dist: " +
        // Hardware.leftDriveEncoder.getDistance());
        // Hardware.telemetry.printToConsole("L. Encoder Raw: " +
        // Hardware.leftDriveEncoder.getRaw());
        // Hardware.telemetry.printToShuffleboard("R encoder ticks", "" +
        // Hardware.rightDriveEncoder.get());
        // Hardware.telemetry.printToConsole("R. Encoder Dist: " +
        // Hardware.rightDriveEncoder.getDistance());
        // Hardware.telemetry.printToConsole("R. Encoder Raw: " +
        // Hardware.rightDriveEncoder.getRaw());
        // Hardware.telemetry.printToConsole("launch encoder: " +
        // Hardware.launcherMotorEncoder.get());
        // Hardware.telemetry.printToConsole("wheel spin encoder: " +
        // Hardware.wheelSpinnerEncoder.get());
        // Hardware.telemetry.printToConsole("hood adjust encoder: " +
        // Hardware.hoodAdjustmentMotorEncoder.get());
        // Encoder Raw Values
        // Hardware.telemetry.printToConsole("launch encoder raw: " +
        // Hardware.launcherMotorEncoder.getRaw());
        // Hardware.telemetry.printToConsole("wheel spin encoder raw: " +
        // Hardware.wheelSpinnerEncoder.getRaw());
        // Hardware.telemetry
        // .printToConsole("Wheel spin distance per pulse: " +
        // Hardware.wheelSpinnerEncoder.getDistancePerPulse());
        // Hardware.telemetry.printToConsole("wheel spin distance: " +
        // Hardware.wheelSpinnerEncoder.getDistance());
        // Hardware.telemetry.printToConsole("Circumference: " +
        // Hardware.colorWheel.getCircumference());
        // Hardware.telemetry.printToConsole("hood adjust encoder raw: " +
        // Hardware.hoodAdjustmentMotorEncoder.getRaw());
        // System.out.println("Launch motor rpm: " +
        // Hardware.launcherMotorEncoder.getRPM());

        // System.out.println("launch motor 1: " + Hardware.launcherMotor1.get());
        // System.out.println("launch motor 2: " + Hardware.launcherMotor2.get());

        // Switch Values
        // Hardware.telemetry.printToConsole(("Start Balls:" +
        // Hardware.ballStart.isOn()));
        // Hardware.telemetry.printToConsole("Auto Switch: " +
        // Hardware.autoSwitch.isOn());
        Hardware.telemetry.printToConsole("Six Pos Sw: " + Hardware.autoSixPosSwitch.getPosition());
        // Hardware.telemetry.printToConsole("shoot switch: " +
        // Hardware.shootingPlan.getPosition());
        // Hardware.telemetry.printToConsole("autoLo cation: " +
        // Hardware.autoLocation.getPosition());
        // red lights
        // Hardware.telemetry.printToConsole("intake RL: " + Hardware.intakeRL.isOn());
        // Hardware.telemetry.printToConsole("lowStoreRL: " +
        // Hardware.lowStoreRL.isOn());
        // Hardware.telemetry.printToConsole("upStoreRL: " + Hardware.upStoreRL.isOn());
        // Hardware.telemetry.printToConsole("firingRL: " + Hardware.firingRL.isOn());

        // ---------- ANALOG -----------
        // Hardware.telemetry.printToConsole("Gyro: " + Hardware.gyro.getAngle());
        // System.out.println("Delay Pot: " + Hardware.delayPot.get());
        // System.out.println("Hood Pot: " + Hardware.hoodPot.get());
        // Hardware.telemetry.printToConsole(
        // "frontUltraSonic bumper distance: " +
        // Hardware.frontUltraSonic.getDistanceFromNearestBumper());

        // ----------- CAN -------------

        // -------- SUBSYSTEMS ---------

        // -------- JOYSTICKS ----------
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

        // ----------- VISION -----------
        // Hardware.telemetry.printToConsole("usb cam 0 target distance: " +
        // Hardware.usbCam0.);

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
        // Hardware.telemetry.printToConsole("launch motor #1: " +
        // Hardware.launcherMotor1.get());
        // Hardware.telemetry.printToConsole("launch motor #2: " +
        // Hardware.launcherMotor2.get());
        // Hardware.telemetry.printToConsole("conveyor motor #1: " +
        // Hardware.conveyorMotor1.get());
        // Hardware.telemetry.printToConsole("conveyor motor #2: " +
        // Hardware.conveyorMotor2.get());
        // Hardware.telemetry.printToConsole("intake motor: " +
        // Hardware.intakeMotor.get());
        // Hardware.telemetry.printToConsole("wheel spin motor: " +
        // Hardware.wheelSpinnerMotor.get());
        // Hardware.telemetry.printToConsole("hood adjust motor: " +
        // Hardware.hoodAdjustmentMotor.get());

        // -------- SUBSYSTEMS ---------

        // ---------- OTHER ------------ pdp, pneumatics
        // Hardware.telemetry.printToConsole("PDP voltage: " +
        // Hardware.pdp.getVoltage());
        // Hardware.telemetry.printToConsole("iDoubleSolenoid forward: " +
        // Hardware.iDoubleSolenoid.getForward());
        // Hardware.telemetry.printToConsole("iDoubleSolenoid reverse: " +
        // Hardware.iDoubleSolenoid.getReverse());
        // Hardware.telemetry.printToConsole("lifDoubleSolenoid forward: " +
        // Hardware.lifDoubleSolenoid.getForward());
        // Hardware.telemetry.printToConsole("lifDoubleSolenoid reverse: " +
        // Hardware.lifDoubleSolenoid.getReverse());
        // Hardware.telemetry.printToConsole("compressor, is low: " +
        // Hardware.compressor.getPressureSwitchValue());

    }

    private final static int PREV_YEAR_MAX_GEAR_NUMBER = 2;

    private final static int CURRENT_YEAR_MAX_GEAR_NUMBER = 2;

    private final static double PREV_YEAR_FIRST_GEAR = .3;

    private final static double PREV_YEAR_SECOND_GEAR = .5;

    private final static double CURRENT_YEAR_FIRST_GEAR = .3;

    private final static double CURRENT_YEAR_SECOND_GEAR = .5;

    private final static double SO_YOU_EVER_HEAR_OF_SONIC = .9;

    } // end class
