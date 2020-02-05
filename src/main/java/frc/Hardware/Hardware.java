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
import frc.HardwareInterfaces.KilroyUsbCamera;

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

import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.sensors.CANCoder;
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
     * Identifier that determines which year's robot
     * we are testing.
     *
     * @author R. Brown
     * @date 1/25/2020
     *********************************************/
    public static enum Identifier
        {
        CurrentYear("2020"), PrevYear("2019"), TestBoard("Test");

        private final String name;

        private Identifier(String s)
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

    public static Identifier robotIdentity = Identifier.PrevYear;

    /**********************************************
     * initializePrevYear() function initializes all Hardware
     * items that are REQUIRED for this year
     *
     * @author R. Brown
     * @date 1/25/2020
     *********************************************/
    public static void initializeCurrentYear() // 2020
    {
        // ==============CAN INIT=============
        // Motor Controllers
        leftFrontMotor = new WPI_TalonFX(13);
        rightFrontMotor = new WPI_TalonFX(15);
        leftRearMotor = new WPI_TalonFX(12);
        rightRearMotor = new WPI_TalonFX(14);

        leftDriveGroup = new SpeedControllerGroup(leftRearMotor, leftFrontMotor);
        rightDriveGroup = new SpeedControllerGroup(rightRearMotor, rightFrontMotor);

        leftDriveEncoder = new KilroyEncoder((WPI_TalonFX) leftFrontMotor);
        rightDriveEncoder = new KilroyEncoder((WPI_TalonFX) rightFrontMotor);

        launcherMotor1 = new CANSparkMax(26, MotorType.kBrushless);
        launcherMotor2 = new CANSparkMax(27, MotorType.kBrushless);

        launcherMotorGroup = new SpeedControllerGroup(launcherMotor1, launcherMotor2);

        conveyorMotor1 = new WPI_TalonSRX(21);
        conveyorMotor2 = new WPI_TalonSRX(22);

        conveyorMotorGroup = new SpeedControllerGroup(conveyorMotor1, conveyorMotor2);

        intakeMotor = new WPI_TalonSRX(23);

        wheelSpinnerMotor = new WPI_TalonSRX(25);
        hoodAdjustmentMotor = new WPI_TalonSRX(24);
        // ==============DIO INIT=============

        launcherMotorEncoder = new KilroyEncoder((CANSparkMax) launcherMotor1);

        conveyorMotorEncoder = new KilroyEncoder((WPI_TalonSRX) conveyorMotor1);

        intakeMotorEncoder = new KilroyEncoder((WPI_TalonSRX) intakeMotor);

        wheelSpinnerEncoder = new KilroyEncoder((WPI_TalonSRX) wheelSpinnerMotor);

        // hoodAdjustmentMotorEncoder = new KilroyEncoder((WPI_TalonSRX) hoodAdjustmentMotor);//TODO

        // ============ANALOG INIT============

        // ==============RIO INIT=============

        // =============OTHER INIT============

        //pneumatics
        compressor = new Compressor();

        transmission = new TankTransmission(leftDriveGroup, rightDriveGroup);
        drive = new Drive(transmission, leftDriveEncoder, rightDriveEncoder, gyro);
        Hardware.launcherMotorEncoder.setTicksPerRevolution(42);

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
        // delayPot = new Potentiometer(0);

        // ==============CAN INIT=============
        // Motor Controllers
        leftFrontMotor = new CANSparkMax(13, MotorType.kBrushless);
        rightFrontMotor = new CANSparkMax(15, MotorType.kBrushless);
        leftRearMotor = new WPI_TalonFX(12);
        rightRearMotor = new WPI_TalonFX(14);

        leftDriveGroup = new SpeedControllerGroup(leftFrontMotor, leftRearMotor);
        rightDriveGroup = new SpeedControllerGroup(rightFrontMotor, rightRearMotor);

        launcherMotor1 = new WPI_TalonSRX(26);
        //launcherMotor2 = new WPI_TalonSRX(27); not on 2019

        launcherMotorGroup = new SpeedControllerGroup(launcherMotor1);

        conveyorMotor1 = new WPI_TalonSRX(22);
        //no conveyorMotor2 on 2019

        conveyorMotorGroup = new SpeedControllerGroup(conveyorMotor1);

        intakeMotor = new WPI_TalonSRX(23);

        wheelSpinnerMotor = new WPI_TalonSRX(25);

        hoodAdjustmentMotor = new WPI_TalonSRX(24);

        // ==============DIO INIT=============

        launcherMotorEncoder = new KilroyEncoder((WPI_TalonSRX) launcherMotor1);

        conveyorMotorEncoder = new KilroyEncoder((WPI_TalonSRX) conveyorMotor1);

        intakeMotorEncoder = new KilroyEncoder((WPI_TalonSRX) intakeMotor);

        wheelSpinnerEncoder = new KilroyEncoder((WPI_TalonSRX) wheelSpinnerMotor);

        // hoodAdjustmentMotorEncoder = new KilroyEncoder((WPI_TalonSRX) hoodAdjustmentMotor);//TODO fix

        // ==============RIO INIT==============

        // =============OTHER INIT============

        leftDriveEncoder = new KilroyEncoder((CANSparkMax) leftFrontMotor);

        rightDriveEncoder = new KilroyEncoder((CANSparkMax) rightFrontMotor);

        leftDriveGroup = new SpeedControllerGroup(leftFrontMotor);
        rightDriveGroup = new SpeedControllerGroup(rightFrontMotor);

        //pneumatics
        compressor = new Compressor();

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

        leftDriveEncoder.setDistancePerPulse(DISTANCE_PER_TICK_XIX);
        rightDriveEncoder.setDistancePerPulse(DISTANCE_PER_TICK_XIX);

        server = CameraServer.getInstance().getServer();
        CameraServer.getInstance().removeServer("serve_usb1");

        Hardware.launcherMotorEncoder.setTicksPerRevolution(5175);

    } // end initizliePrevYear()

    public static void initializeTestBoard()
    {
        launcherMotor1 = new CANSparkMax(26, MotorType.kBrushless);
        launcherMotor2 = new CANSparkMax(27, MotorType.kBrushless);

        launcherMotorGroup = new SpeedControllerGroup(launcherMotor1, launcherMotor2);

        launcherMotorEncoder = new KilroyEncoder((CANSparkMax) launcherMotor2);

        colorTestMotor = new WPI_TalonSRX(21);

        colorSensor = new ColorSensorV3(i2cPort);

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

        if (robotIdentity == Identifier.CurrentYear)
            {
            initializeCurrentYear();
            generalInit();
            }
        else if (robotIdentity == Identifier.PrevYear)
            {
            initializePrevYear();
            generalInit();
            }
        else if (robotIdentity == Identifier.TestBoard)
            {
            initializeTestBoard();
            }

    } // end initialize()

    public static void generalInit()
    {
        colorSensor = new ColorSensorV3(i2cPort);

        intakeRL = new LightSensor(12); // bottom
        lowStoreRL = new LightSensor(3); // lower middle
        upStoreRL = new LightSensor(4); // upper middle
        firingRL = new LightSensor(1); // top

        autoSixPosSwitch = new SixPositionSwitch(13, 14, 15, 16, 17, 18);
        autoSwitch = new SingleThrowSwitch(0);

        shootFar = new SingleThrowSwitch(22);
        shootClose = new SingleThrowSwitch(23);
        shootingPlan = new DoubleThrowSwitch(shootFar, shootClose);

        leftAuto = new SingleThrowSwitch(24);
        rightAuto = new SingleThrowSwitch(25);
        autoLocation = new DoubleThrowSwitch(leftAuto, rightAuto);

        // public static SingleThrowSwitch autoZeroBallsIn = new SingleThrowSwitch(24);
        // public static SingleThrowSwitch autoThreeBallsIn = new SingleThrowSwitch(25);
        // public static DoubleThrowSwitch autoTwoBalls = new
        // DoubleThrowSwitch(autoZeroBallsIn, autoThreeBallsIn);

        // **********************************************************
        // ANALOG I/O
        // **********************************************************

        delayPot = new Potentiometer(2);

        hoodPot = new Potentiometer(1);

        frontUltraSonic = new LVMaxSonarEZ(3);
        // **********************************************************
        // PNEUMATIC DEVICES
        // **********************************************************
        intakeSolenoid = new DoubleSolenoid(4, 5);
        liftSolenoid = new DoubleSolenoid(2, 3);

        gyro = new KilroySPIGyro(true);

        storage = new StorageControl(intakeRL, lowStoreRL, upStoreRL, firingRL, conveyorMotorGroup);
    }
    // **********************************************************
    // CAN DEVICES
    // **********************************************************

    public static SpeedController leftRearMotor = null;
    public static SpeedController rightRearMotor = null;
    public static SpeedController leftFrontMotor = null;
    public static SpeedController rightFrontMotor = null;

    public static SpeedController colorTestMotor = null;

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

    public static KilroyEncoder conveyorMotorEncoder = null;

    // ------------------------------------------------------------

    public static SpeedController intakeMotor = null;

    public static KilroyEncoder intakeMotorEncoder = null;

    // ------------------------------------------------------------

    public static SpeedController wheelSpinnerMotor = null;

    public static KilroyEncoder wheelSpinnerEncoder = null;

    // -------------------------------------------------------------

    public static SpeedController hoodAdjustmentMotor = null;

    // **********************************************************
    // DIGITAL I/O
    // **********************************************************
    public static I2C.Port i2cPort = I2C.Port.kOnboard;
    public static ColorSensorV3 colorSensor = null;

    public static LightSensor intakeRL = null; // bottom
    public static LightSensor lowStoreRL = null; // lower middle
    public static LightSensor upStoreRL = null; // upper middle
    public static LightSensor firingRL = null; // top

    public static SixPositionSwitch autoSixPosSwitch = null;
    public static SingleThrowSwitch autoSwitch = null;
    public static SingleThrowSwitch shootFar = null;
    public static SingleThrowSwitch shootClose = null;
    public static DoubleThrowSwitch shootingPlan = null;

    public static SingleThrowSwitch leftAuto = null;
    public static SingleThrowSwitch rightAuto = null;
    public static DoubleThrowSwitch autoLocation = null;

    // public static SingleThrowSwitch autoZeroBallsIn = new SingleThrowSwitch(24);
    // public static SingleThrowSwitch autoThreeBallsIn = new SingleThrowSwitch(25);
    // public static DoubleThrowSwitch autoTwoBalls = new
    // DoubleThrowSwitch(autoZeroBallsIn, autoThreeBallsIn);

    // **********************************************************
    // ANALOG I/O
    // **********************************************************

    public static Potentiometer delayPot = null;

    public static Potentiometer hoodPot = null;

    public static LVMaxSonarEZ frontUltraSonic = null;
    // **********************************************************
    // PNEUMATIC DEVICES
    // **********************************************************
    public static DoubleSolenoid intakeSolenoid = null;
    public static DoubleSolenoid liftSolenoid = null;
    public static Compressor compressor = null;
    // **********************************************************
    // roboRIO CONNECTIONS CLASSES
    // **********************************************************

    public static PowerDistributionPanel pdp = new PowerDistributionPanel(2);

    public static KilroySPIGyro gyro = null;

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

    public static MomentarySwitch invertTempoMomentarySwitch = new MomentarySwitch();

    public static MomentarySwitch publishVisionSwitch = new MomentarySwitch(leftOperator, 11, false);
    public static MomentarySwitch cameraSwitchButton = new MomentarySwitch(leftOperator, 7, false);

    public static JoystickButton publishVisionButton = new JoystickButton(Hardware.leftOperator, 11);

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

    public static JoystickButton substractBall = new JoystickButton(Hardware.leftOperator, 8);

    public static JoystickButton addBall = new JoystickButton(Hardware.leftOperator, 9);

    public static JoystickButton toggleIntake = new JoystickButton(Hardware.leftOperator, 3);

    public static JoystickButton conveyorOverrideButton = new JoystickButton(Hardware.leftOperator, 11);
    // **********************************************************
    // Kilroy's Ancillary classes
    // **********************************************************

    public static VideoSink server;
    public static UsbCamera usbCam0 = CameraServer.getInstance().startAutomaticCapture("usb0", 0);
    public static UsbCamera usbCam1 = CameraServer.getInstance().startAutomaticCapture("usb1", 1);
    public static KilroyUsbCamera kilroyUSBCamera = new KilroyUsbCamera(server, usbCam0, usbCam1, cameraSwitchButton);

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
    public static Drive drive = null;

    public static TankTransmission transmission = null;

    // launcher stuff
    public static IntakeControl intake = new IntakeControl(launchTimer, intakeSolenoid, intakeMotor);

    public static Launcher launcher = new Launcher(launcherMotorGroup, launcherMotorEncoder);

    public static StorageControl storage = null;

    public static HoodControl hoodControl = new HoodControl(hoodAdjustmentMotor, hoodPot);

    public static BallCounter ballcounter = new BallCounter(ballButtonTimer);
    public static ColorWheel colorWheel = new ColorWheel();

    // ------------------------------------------
    // Vision stuff
    // ----------------------------
    public static LimelightDriveWithVision visionDriving = new LimelightDriveWithVision();

    public static LimelightInterface visionInterface = new LimelightInterface();

    public final static double DISTANCE_PER_TICK_XIX = 23 / 13.8;// .0346;
    // -------------------
    // Subassemblies
    // -------------------

    } // end class
