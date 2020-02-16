// ====================================================================
// FILE NAME: Hardware.java (Team 339 - Kilroy)
//
// CREATED ON: Jan 2, 2011
// CREATED BY: Bob Brown
// MODIFIED ON: June 24, 2019
// MODIFIED BY: Ryan McGee
// ABSTRACT:
// This file contains all of the global definitions for the
// hardware objects in the system
//
// NOTE: Please do not release this code without permission from
// Team 339.
// ====================================================================
package frc.Hardware;

import frc.HardwareInterfaces.DoubleSolenoid;
import frc.HardwareInterfaces.DoubleThrowSwitch;
import frc.HardwareInterfaces.IRSensor;
import frc.HardwareInterfaces.KilroyEncoder;
import frc.HardwareInterfaces.KilroySPIGyro;
import frc.HardwareInterfaces.KilroyUSBCamera;

import frc.HardwareInterfaces.LVMaxSonarEZ;
import frc.HardwareInterfaces.LightSensor;
import frc.HardwareInterfaces.MomentarySwitch;
import frc.HardwareInterfaces.Potentiometer;
import frc.HardwareInterfaces.LightSensor;
import frc.HardwareInterfaces.SingleThrowSwitch;
import frc.HardwareInterfaces.SixPositionSwitch;
import frc.HardwareInterfaces.UltraSonic;
import frc.vision.*;
import frc.Utils.*;
import frc.Utils.HoodControl;
import frc.Utils.drive.Drive;

import frc.Utils.Telemetry;
import frc.HardwareInterfaces.Transmission.TankTransmission;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.sensors.CANCoder;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSink;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.Servo;
import static edu.wpi.first.wpilibj.DoubleSolenoid.Value.*;

/**
 * ------------------------------------------------------- puts all of the
 * hardware declarations into one place. In addition, it makes them available to
 * both autonomous and teleop.
 *
 * @class HardwareDeclarations
 * @author Bob Brown
 *
 * @written Jan 2, 2011 -------------------------------------------------------
 */

public class Hardware
    {

    /**********************************************
     * Identifier that determines which year's robot we are testing.
     *
     * @author R. Brown
     * @date 1/25/2020
     *********************************************/
    public static enum yearIdentifier
        {
        CurrentYear("2020"), PrevYear("2019"), TestBoard("Test");

        private final String name;

        private yearIdentifier(String s)
            {
                this.name = s;
            }

        public boolean equalsName(String otherName)
        {
            // (otherName == null) check is not needed because name.equals(null) returns
            // false
            return name.equals(otherName);
        }

        public String toString()
        {
            return this.name;
        }
        };

    // ============Which Year===================
    public static yearIdentifier robotIdentity = yearIdentifier.CurrentYear;

    // ==============Servo==============
    public static Servo rotateServo = new Servo(9);

    //TalonSRX climbMotors = new TalonSRX(29);
    /**********************************************
     * initializePrevYear() function initializes all Hardware items that are
     * REQUIRED for this year
     *
     * @author R. Brown
     * @date 1/25/2020
     *********************************************/
    public static void initializeCurrentYear() // 2020
    {
        // ==============CAN INIT=============
        // Motor Controllers
        leftFrontMotor = new WPI_TalonFX(13);
        leftFrontMotor.setInverted(false);
        rightFrontMotor = new WPI_TalonFX(12);
        rightFrontMotor.setInverted(true);

        leftRearMotor = new WPI_TalonFX(15);
        leftRearMotor.setInverted(false);
        rightRearMotor = new WPI_TalonFX(14);
        rightRearMotor.setInverted(true);

        leftDriveGroup = new SpeedControllerGroup(leftRearMotor, leftFrontMotor);
        rightDriveGroup = new SpeedControllerGroup(rightRearMotor, rightFrontMotor);

        leftDriveEncoder = new KilroyEncoder((WPI_TalonFX) leftFrontMotor);
        leftDriveEncoder.setDistancePerPulse(CURRENT_YEAR_DISTANCE_PER_TICK);
        leftDriveEncoder.setReverseDirection(true);

        rightDriveEncoder = new KilroyEncoder((WPI_TalonFX) rightFrontMotor);
        rightDriveEncoder.setDistancePerPulse(CURRENT_YEAR_DISTANCE_PER_TICK);
        rightDriveEncoder.setReverseDirection(true);

        // launcherMotor1 = new CANSparkMax(26, MotorType.kBrushless);
        // launcherMotor2 = new CANSparkMax(27, MotorType.kBrushless);

        // launcherMotorGroup = new SpeedControllerGroup(launcherMotor1,
        // launcherMotor2);

        // conveyorMotor1 = new WPI_TalonSRX(21);
        // conveyorMotor2 = new WPI_TalonSRX(22);

        // conveyorMotorGroup = new SpeedControllerGroup(conveyorMotor1,
        // conveyorMotor2);

        // intakeMotor = new WPI_TalonSRX(23);

        // wheelSpinnerMotor = new WPI_TalonSRX(25);
        // hoodAdjustmentMotor = new WPI_TalonSRX(24);
        // ==============DIO INIT=============

        // launcherMotorEncoder = new KilroyEncoder((CANSparkMax) launcherMotor1);
        // wheelSpinnerEncoder = new KilroyEncoder((WPI_TalonSRX) wheelSpinnerMotor);

        // hoodAdjustmentMotorEncoder = new KilroyEncoder((WPI_TalonSRX)
        // hoodAdjustmentMotor);//TODO

        // ============ANALOG INIT============

        // ==============RIO INIT=============

        // =============OTHER INIT============

        transmission = new TankTransmission(leftDriveGroup, rightDriveGroup);
        drive = new Drive(transmission, leftDriveEncoder, rightDriveEncoder, gyro);
        // pneumatics

        // Hardware.launcherMotorEncoder.setTicksPerRevolution(42);

    } // end initiaizeCurrentYear()

    /**********************************************
     * initializePrevYear() function initializes all Hardware items that are
     * REQUIRED for this year
     *
     * @author R. Brown
     * @date 1/25/2020
     *********************************************/
    public static void initializePrevYear() // 2019
    {
        // ==============DIO INIT=============

        // ============ANALOG INIT============

        // ==============CAN INIT=============
        // Motor Controllers
        leftFrontMotor = new CANSparkMax(13, MotorType.kBrushless);
        rightFrontMotor = new CANSparkMax(15, MotorType.kBrushless);
        leftRearMotor = new WPI_TalonFX(12);
        rightRearMotor = new WPI_TalonFX(14);

        leftDriveGroup = new SpeedControllerGroup(leftFrontMotor, leftRearMotor);
        rightDriveGroup = new SpeedControllerGroup(rightFrontMotor, rightRearMotor);

        launcherMotor1 = new WPI_TalonSRX(26);
        // launcherMotor2 = new WPI_TalonSRX(27); not on 2019

        launcherMotorGroup = new SpeedControllerGroup(launcherMotor1);

        conveyorMotor1 = new WPI_TalonSRX(22);
        // no conveyorMotor2 on 2019

        conveyorMotorGroup = new SpeedControllerGroup(conveyorMotor1);

        intakeMotor = new WPI_TalonSRX(23);

        wheelSpinnerMotor = new WPI_TalonSRX(25);

        colorSensor = new ColorSensorV3(i2cPort);

        hoodAdjustmentMotor = new WPI_TalonSRX(24);

        climbMotor = new WPI_TalonSRX(29);
        // ==============DIO INIT=============

        launcherMotorEncoder = new KilroyEncoder(7, 6);

        launcherMotorEncoder.setTicksPerRevolution(400);

        wheelSpinnerEncoder = new KilroyEncoder((WPI_TalonSRX) wheelSpinnerMotor);

        // hoodAdjustmentMotorEncoder = new KilroyEncoder((WPI_TalonSRX)
        // hoodAdjustmentMotor);//TODO fix

        // ==============RIO INIT==============

        // =============OTHER INIT============

        leftDriveEncoder = new KilroyEncoder((CANSparkMax) leftFrontMotor);

        rightDriveEncoder = new KilroyEncoder((CANSparkMax) rightFrontMotor);

        leftDriveGroup = new SpeedControllerGroup(leftFrontMotor);
        rightDriveGroup = new SpeedControllerGroup(rightFrontMotor);

        // pneumatics

        // ==============RIO INIT==============

        // =============OTHER INIT============

        transmission = new TankTransmission(leftDriveGroup, rightDriveGroup);
        drive = new Drive(transmission, leftDriveEncoder, rightDriveEncoder, gyro);

        // drivePID = new DrivePID(transmission, leftEncoder, , gyro);

        // intakeMotor = new WPI_TalonSRX(10);
        // shootMotor = new WPI_TalonSRX(23);
        // conveyorMotor = new WPI_TalonSRX(24);

        Hardware.leftFrontMotor.setInverted(false);
        Hardware.rightFrontMotor.setInverted(true);

        leftDriveEncoder.setDistancePerPulse(PREV_YEAR_DISTANCE_PER_TICK);
        rightDriveEncoder.setDistancePerPulse(PREV_YEAR_DISTANCE_PER_TICK);

        intake = new IntakeControl(launchTimer, intakeSolenoid, intakeMotor);

        launcher = new Launcher(launcherMotorGroup, launcherMotorEncoder);

        storage = new StorageControl(intakeRL, lowStoreRL, upStoreRL, firingRL, conveyorMotorGroup);

        hoodControl = new HoodControl(hoodAdjustmentMotor, hoodPot);

        ballCounter = new BallCounter(ballButtonTimer);

        colorWheel = new ColorWheel(wheelSpinnerMotor, wheelSpinnerEncoder, colorSensor);

    } // end initizliePrevYear()

    public static void initializeTestBoard()
    {
        launcherMotor1 = new CANSparkMax(26, MotorType.kBrushless);
        launcherMotor2 = new CANSparkMax(27, MotorType.kBrushless);

        launcherMotorGroup = new SpeedControllerGroup(launcherMotor1, launcherMotor2);

        launcherMotorEncoder = new KilroyEncoder((CANSparkMax) launcherMotor2);
    }

    /**********************************************
     * initialize() function initializes all Hardware items that REQUIRE
     * initialization. It calls a function for either this year or the previous year
     *
     * @author R. Brown
     * @date 1/25/2020
     ***********************************************/
    public static void initialize()
    {

        if (robotIdentity == yearIdentifier.CurrentYear)
            {
            // generalInit();
            initializeCurrentYear();

            }
        else if (robotIdentity == yearIdentifier.PrevYear)
            {
            generalInit();
            initializePrevYear();

            }
        else if (robotIdentity == yearIdentifier.TestBoard)
            {
            initializeTestBoard();
            }

    } // end initialize()

    public static void generalInit()
    {
        // public static SingleThrowSwitch autoZeroBallsIn = new SingleThrowSwitch(24);
        // public static SingleThrowSwitch autoThreeBallsIn = new SingleThrowSwitch(25);
        // public static DoubleThrowSwitch autoTwoBalls = new
        // DoubleThrowSwitch(autoZeroBallsIn, autoThreeBallsIn);

        // **********************************************************
        // ANALOG I/O
        // **********************************************************

        // **********************************************************
        // PNEUMATIC DEVICES
        // **********************************************************

    }
    // **********************************************************
    // CAN DEVICES
    // **********************************************************

    public static SpeedController leftRearMotor = null;
    public static SpeedController rightRearMotor = null;
    public static SpeedController leftFrontMotor = null;
    public static SpeedController rightFrontMotor = null;

    public static SpeedControllerGroup leftDriveGroup = null;
    public static SpeedControllerGroup rightDriveGroup = null;

    public static KilroyEncoder leftDriveEncoder = null;
    public static KilroyEncoder rightDriveEncoder = null;

    // ------------------------------------------------------------

    public static SpeedController launcherMotor1 = null;
    public static SpeedController launcherMotor2 = null;

    public static SpeedControllerGroup launcherMotorGroup = null;

    public static KilroyEncoder launcherMotorEncoder = null;

    // ------------------------------------------------------------

    public static SpeedController conveyorMotor1 = null;
    public static SpeedController conveyorMotor2 = null;

    public static SpeedControllerGroup conveyorMotorGroup = null;

    // ------------------------------------------------------------

    public static SpeedController intakeMotor = null;

    // ------------------------------------------------------------

    public static SpeedController wheelSpinnerMotor = null;

    public static KilroyEncoder wheelSpinnerEncoder = null;

    // -------------------------------------------------------------

    public static SpeedController hoodAdjustmentMotor = null;

    // -------------------------------------------------------------

    public static SpeedController climbMotor = null;

    // **********************************************************
    // DIGITAL I/O
    // **********************************************************
    public static I2C.Port i2cPort = I2C.Port.kOnboard;
    public static ColorSensorV3 colorSensor = new ColorSensorV3(i2cPort);

    public static LightSensor intakeRL = new LightSensor(21); // bottom
    public static LightSensor lowStoreRL = new LightSensor(22); // lower middle
    public static LightSensor upStoreRL = new LightSensor(23); // upper middle
    public static LightSensor firingRL = new LightSensor(24); // top

    public static SixPositionSwitch autoSixPosSwitch = new SixPositionSwitch(13, 14, 15, 16, 17, 18);
    public static SingleThrowSwitch autoSwitch = new SingleThrowSwitch(10);
    public static SingleThrowSwitch shootFar = new SingleThrowSwitch(19);
    public static SingleThrowSwitch shootClose = new SingleThrowSwitch(20);
    public static DoubleThrowSwitch shootingPlan = new DoubleThrowSwitch(shootFar, shootClose);
    public static SingleThrowSwitch ballStart = new SingleThrowSwitch(25);

    public static SingleThrowSwitch leftAuto = new SingleThrowSwitch(11);
    public static SingleThrowSwitch rightAuto = new SingleThrowSwitch(12);
    public static DoubleThrowSwitch autoLocation = new DoubleThrowSwitch(leftAuto, rightAuto);

    // public static SingleThrowSwitch autoZeroBallsIn = new SingleThrowSwitch(24);
    // public static SingleThrowSwitch autoThreeBallsIn = new SingleThrowSwitch(25);
    // public static DoubleThrowSwitch autoTwoBalls = new
    // DoubleThrowSwitch(autoZeroBallsIn, autoThreeBallsIn);

    // **********************************************************
    // ANALOG I/O
    // **********************************************************

    public static Potentiometer delayPot = new Potentiometer(2);

    public static Potentiometer hoodPot = new Potentiometer(1);

    public static LVMaxSonarEZ frontUltraSonic = new LVMaxSonarEZ(3);
    // **********************************************************
    // PNEUMATIC DEVICES
    // **********************************************************
    public static DoubleSolenoid intakeSolenoid = new DoubleSolenoid(4, 5);
    public static DoubleSolenoid liftSolenoid = new DoubleSolenoid(2, 3);
    public static Compressor compressor = new Compressor();

    // **********************************************************
    // roboRIO CONNECTIONS CLASSES
    // **********************************************************
    public static PowerDistributionPanel pdp = new PowerDistributionPanel(2);

    public static KilroySPIGyro gyro = new KilroySPIGyro(true);

    // **********************************************************
    // DRIVER STATION CLASSES
    // **********************************************************
    public static DriverStation driverStation = DriverStation.getInstance();

    public static Joystick leftDriver = new Joystick(0);
    public static Joystick rightDriver = new Joystick(1);
    public static Joystick leftOperator = new Joystick(2);
    public static Joystick rightOperator = new Joystick(3);

    // **********************************************************
    // Buttons
    // **********************************************************

    public static MomentarySwitch publishVisionSwitch = new MomentarySwitch(leftOperator, 11, false);

    public static MomentarySwitch cameraSwitchButton1 = new MomentarySwitch(leftOperator, 7, false);

    public static MomentarySwitch cameraSwitchButton2 = new MomentarySwitch(rightDriver, 11, false);

    public static MomentarySwitch cameraSwitchButton = new MomentarySwitch(leftOperator, 7, false);

    public static JoystickButton gearUp = new JoystickButton(Hardware.rightDriver, 1);

    public static JoystickButton gearDown = new JoystickButton(Hardware.leftDriver, 1);

    public static JoystickButton launchButton = new JoystickButton(Hardware.rightOperator, 1);

    public static JoystickButton launchOverrideButton = new JoystickButton(Hardware.rightOperator, 5);

    public static JoystickButton shootCloseButton = new JoystickButton(Hardware.rightOperator, 4);

    public static JoystickButton shootFarButton = new JoystickButton(Hardware.rightOperator, 3);

    public static JoystickButton intakeButton = new JoystickButton(Hardware.leftOperator, 1);

    public static JoystickButton outtakeButton = new JoystickButton(Hardware.leftOperator, 2);

    public static JoystickButton intakeOverrideButton = new JoystickButton(Hardware.leftOperator, 5);

    public static JoystickButton pictureButton1 = new JoystickButton(Hardware.leftOperator, 8);

    public static JoystickButton pictureButton2 = new JoystickButton(Hardware.leftOperator, 9);

    public static JoystickButton subtractBall = new JoystickButton(Hardware.leftOperator, 8);

    public static JoystickButton addBall = new JoystickButton(Hardware.leftOperator, 9);

    public static JoystickButton toggleIntake = new JoystickButton(Hardware.leftOperator, 3);

    public static JoystickButton conveyorOverrideButton = new JoystickButton(Hardware.leftOperator, 11);

    public static JoystickButton pistonsUpSolenoid = new JoystickButton(Hardware.rightOperator, 6);

    public static JoystickButton pistonsDownSolenoid = new JoystickButton(Hardware.rightOperator, 7);

    public static JoystickButton climbReverse = new JoystickButton(Hardware.rightOperator, 8 + 9);

    public static JoystickButton climbMotorUp = new JoystickButton(Hardware.rightOperator, 10);
    // **********************************************************
    // Kilroy's Ancillary classes
    // **********************************************************
    public static KilroyUSBCamera kilroyUSBCamera = new KilroyUSBCamera(cameraSwitchButton1, cameraSwitchButton2);

    // ------------------------------------
    // Utility classes
    // ------------------------------------
    public static Timer autoTimer = new Timer();

    public static Timer getSpeedTimer = new Timer();

    public static Timer telopTimer = new Timer();

    public static Timer launchTimer = new Timer();

    public static Timer ballButtonTimer = new Timer();

    public static Telemetry telemetry = new Telemetry(driverStation);

    // ------------------------------------
    // Drive system
    // ------------------------------------
    public static TankTransmission transmission = null;

    public static Drive drive = null;

    // launcher stuff
    public static IntakeControl intake = null;

    public static Launcher launcher = null;

    public static StorageControl storage = null;

    public static HoodControl hoodControl = null;

    public static BallCounter ballCounter = null;

    public static ColorWheel colorWheel = null;

    // ------------------------------------------
    // Vision stuff
    // ----------------------------
    public static LimelightDriveWithVision visionDriving = new LimelightDriveWithVision();

    public static LimelightInterface visionInterface = new LimelightInterface();

    public final static double PREV_YEAR_DISTANCE_PER_TICK = 23 / 13.8;// .0346;
    public final static double CURRENT_YEAR_DISTANCE_PER_TICK = .000746;// .000746

    public final static double PREV_YEAR_WHEEL_SPINNER_DISTANCE_PER_TICK = 0.0024305403;

    // -------------------
    // Subassemblies
    // -------------------
    public static CameraServo cameraServo = new CameraServo();

    } // end class
