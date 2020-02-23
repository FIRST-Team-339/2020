/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved. */
/* Open Source Software - may be modified and shared by FRC teams. The code */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project. */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.TimedRobot;
import frc.Hardware.Hardware;
import frc.Utils.StorageControl;
import frc.Utils.StorageControl.ControlState;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */

public class Robot extends TimedRobot
    {

    /**
     * ------------------------------------------------------- This function is run
     * when the robot is first started up and should be used for any initialization
     * code for the robot.
     *
     * @author Bob Brown
     * @written Jan 2, 2011 -------------------------------------------------------
     */
    @Override
    public void robotInit()
    {
        System.out.println("Started robotInit()");
        // ---------------------------------------
        // start setup - tell the user we are beginning
        // setup
        // ---------------------------------------

        Hardware.initialize();

        if (Hardware.robotIdentity.equals(Hardware.yearIdentifier.PrevYear))
            {
            Hardware.visionInterface.setCameraHeight(PREV_YEAR_CAMERA_HEIGHT);
            Hardware.visionInterface.setTargetHeight(PREV_YEAR_TARGET_HEIGHT);
            Hardware.visionInterface.setMountingAngle(PREV_YEAR_MOUNTING_ANGLE);
            Hardware.drive.setJoystickDeadband(0.2);
            }
        else if (Hardware.robotIdentity.equals(Hardware.yearIdentifier.CurrentYear))
            {
            Hardware.visionInterface.setCameraHeight(_CURRENT_YEAR_CAMERA_HEIGHT);
            Hardware.visionInterface.setTargetHeight(CURRENT_YEAR_TARGET_HEIGHT);
            Hardware.visionInterface.setMountingAngle(CURRENT_YEAR_MOUNTING_ANGLE);
            Hardware.conveyorMotor1.setInverted(false);
            Hardware.conveyorMotor2.setInverted(true);
            Hardware.drive.setJoystickDeadband(0.2);
            // TODO uncomment
            // Hardware.climbEncoder.reset();
            }
        // Servo initial starting position
        // Hardware.rotateServo.setAngle(90);
        Hardware.cameraServo.setCameraAngleUp();
        Hardware.hoodServo.setAngle(SERVO_START_VALUE);
        Hardware.climbServo.setAngle(SERVO_START_VALUE);

        // Clearing TalonFX motor ticks
        Hardware.leftDriveEncoder.reset();
        Hardware.rightDriveEncoder.reset();

        // AIR
        // Hardware.compressor.start();

        // gyro
        // Hardware.gyro.calibrate();//TODO
        // Hardware.gyro.reset();

        // Hardware.frontUltraSonic.setOffsetDistanceFromNearestBumper(3);

        Hardware.launcherMotorEncoder.setTicksPerRevolution(44.1); //5175

        // ---------------------------------------
        // done setup - tell the user we are complete
        // setup
        // ---------------------------------------

        System.out
                .println("Kilroy " + Hardware.robotIdentity.toString() + " has started.  All hardware items created.");
    } // end robotInit()

    /**
     * This function is called every robot packet, no matter the mode. Use this for
     * items like diagnostics that you want ran during disabled, autonomous,
     * teleoperated and test.
     *
     * <p>
     * This runs after the mode specific periodic functions, but before LiveWindow
     * and SmartDashboard integrated updating.
     */
    @Override
    public void robotPeriodic()
    {
    } // end robotPeriodic()

    /**
     * ------------------------------------------------------- Initialization code
     * for autonomous mode should go here. Will be called once when the robot enters
     * autonomous mode.
     *
     * @author Bob Brown
     * @written Jan 2, 2011
     *
     *          -------------------------------------------------------
     */
    @Override
    public void autonomousInit()
    {
        System.out.println("Started AutonousInit().");
        // ---------------------------------------
        // start setup - tell the user we are beginning
        // setup
        // ---------------------------------------

        Autonomous.init();

        // ---------------------------------------
        // done setup - tell the user we are complete
        // setup
        // ---------------------------------------
        System.out.println("Completed AutonousInit().");
    } // end autonomousInit()

    /**
     * ------------------------------------------------------- Non-User Periodic
     * code for autonomous mode should go here. Will be called periodically at a
     * regular rate while the robot is in autonomous mode. This in turn calls the
     * Autonomous class's Periodic function, which is where the user code should be
     * placed.
     *
     * @author Bob Brown
     * @written Jan 2, 2011
     *
     *          -------------------------------------------------------
     */
    @Override
    public void autonomousPeriodic()
    {
        Autonomous.periodic();

    }// end autonomousPeriodic()

    /**
     * ------------------------------------------------------- Initialization code
     * for disabled mode should go here. Will be called once when the robot enters
     * disabled mode.
     *
     * @author Bob Brown
     * @written Jan 2, 2011 -------------------------------------------------------
     */
    @Override
    public void disabledInit()
    {
        System.out.println("Started DisabledInit().");
        // ---------------------------------------
        // start setup - tell the user we are beginning
        // setup
        // ---------------------------------------
        Hardware.launcherMotorGroup.set(0);
        Hardware.launcherMotorEncoder.speed = 0;//TODO

        Hardware.leftDriveGroup.set(0);
        Hardware.rightDriveGroup.set(0);
        Hardware.visionInterface.setPipeline(0);
        Hardware.intake.makePassive();
        StorageControl.setStorageControlState(ControlState.PASSIVE);

        // ---------------------------------------
        // done setup - tell the user we are complete
        // setup
        // ---------------------------------------
        System.out.println("Completed DisabledInit().");
    } // end disabledInit()

    /**
     * ------------------------------------------------------- Periodic code for
     * disabled mode should go here. Will be called periodically at a regular rate
     * while the robot is in disabled mode. Code that can be "triggered" by a
     * joystick button can go here. This can set up configuration things at the
     * driver's station for instance before a match.
     *
     * @author Bob Brown
     * @written Jan 2, 2011 -------------------------------------------------------
     */
    @Override
    public void disabledPeriodic()
    {

    } // end disabledPeriodic()

    /**
     * ------------------------------------------------------ Non-User
     * initialization code for teleop mode should go here. Will be called once when
     * the robot enters teleop mode, and will call the Teleop class's Init function,
     * where the User code should be placed.
     *
     * @author Bob Brown
     * @written Jan 2, 2011 -------------------------------------------------------
     */
    @Override
    public void teleopInit()
    {
        System.out.println("Started teleopInit().");
        // ---------------------------------------
        // start setup - tell the user we are beginning
        // setup
        // ---------------------------------------

        Teleop.init();

        // ---------------------------------------
        // done setup - tell the user we are complete
        // setup
        // ---------------------------------------
        System.out.println("Completed TeleopInit().");
    } // end teleopInit()

    /**
     * ------------------------------------------------------- Non-User Periodic
     * code for teleop mode should go here. Will be called periodically at a regular
     * rate while the robot is in teleop mode, and will in turn call the Teleop
     * class's Periodic function.
     *
     * @author Bob Brown
     * @written Jan 2, 2011 -------------------------------------------------------
     */
    @Override
    public void teleopPeriodic()
    {
        // -------------------------------------
        // Call the Teleop class's Periodic function,
        // which contains the user code.
        // -------------------------------------

        Teleop.periodic();

    } // end teleopPeriodic()

    /**
     * ------------------------------------------------------- Initialization code
     * for test mode should go here. Will be called once when the robot enters test
     * mode.
     *
     * @author Bob Brown
     * @written Jan 2, 2015 ------------------------------------------------------
     */
    @Override
    public void testInit()
    {
        this.teleopInit();

    } // end testInit()

    /**
     * This function is called periodically during test mode.
     */
    @Override
    public void testPeriodic()
    {

        Teleop.periodic();
    } // end testPeriodic()

    private final double PREV_YEAR_CAMERA_HEIGHT = 34.25;
    private final double PREV_YEAR_TARGET_HEIGHT = 83.7;
    private final double PREV_YEAR_MOUNTING_ANGLE = 35;

    private final double _CURRENT_YEAR_CAMERA_HEIGHT = 34.25;
    private final double CURRENT_YEAR_TARGET_HEIGHT = 83.7;
    private final double CURRENT_YEAR_MOUNTING_ANGLE = 35;

    public static final int SERVO_START_VALUE = 25;
    }
