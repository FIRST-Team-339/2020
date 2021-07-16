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
import frc.HardwareInterfaces.KilroyEncoder;
import frc.HardwareInterfaces.KilroySPIGyro;
import frc.HardwareInterfaces.KilroyServo;
import frc.HardwareInterfaces.KilroyUSBCamera;

import frc.HardwareInterfaces.LVMaxSonarEZ;
import frc.HardwareInterfaces.LightSensor;
import frc.HardwareInterfaces.MomentarySwitch;
import frc.HardwareInterfaces.Potentiometer;
import frc.HardwareInterfaces.SingleThrowSwitch;
import frc.HardwareInterfaces.SixPositionSwitch;
import frc.vision.*;
import frc.Utils.*;
import frc.Utils.HoodControl;
import frc.Utils.drive.Drive;
import frc.Utils.Telemetry;
import frc.Utils.Climb;
import frc.HardwareInterfaces.Transmission.TankTransmission;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.cscore.HttpCamera;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.Servo;

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
    public static Servo rotateServo = new Servo(0);

    public static KilroyServo hoodServo = new KilroyServo(1, 180);

    public static Servo testServo = new Servo(3);

    public static KilroyServo climbServo = new KilroyServo(2, 180);

    /**********************************************
     * initializePrevYear() function initializes all Hardware items that are
     * REQUIRED for this year
     *
     * @author R. Brown
     * @date 1/25/2020
     *********************************************/
    public static void initializeCurrentYear() // 2020
    {

        // NOTE current open can betwenn 20 and 29: 24

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

        launcherMotor1 = new CANSparkMax(26, MotorType.kBrushless);
        launcherMotor2 = new CANSparkMax(27, MotorType.kBrushless);

        launcherMotorGroup = new SpeedControllerGroup(launcherMotor1, launcherMotor2);

        conveyorMotor1 = new WPI_TalonSRX(21);
        conveyorMotor2 = new WPI_TalonSRX(22);

        conveyorMotorGroup = new SpeedControllerGroup(conveyorMotor1, conveyorMotor2);

        intakeMotor = new WPI_TalonSRX(23);
        intakeMotor.setInverted(true);

        wheelSpinnerMotor = new WPI_TalonSRX(25);

        climbMotorR = new WPI_TalonSRX(10);

        climbMotorL = new WPI_TalonSRX(24);

        climbMotorGroup = new SpeedControllerGroup(climbMotorR, climbMotorL);

        // ==============DIO INIT=============

        launcherMotorEncoder = new KilroyEncoder((CANSparkMax) launcherMotor2);
        wheelSpinnerEncoder = new KilroyEncoder((WPI_TalonSRX) wheelSpinnerMotor);

        climbEncoder = new KilroyEncoder((WPI_TalonSRX) climbMotorR);

        climbEncoder.setDistancePerPulse(.004507692);
        climbEncoder.setReverseDirection(true);

        // ============ANALOG INIT============

        // ==============RIO INIT=============

        // =============OTHER INIT============

        transmission = new TankTransmission(leftDriveGroup, rightDriveGroup);
        drive = new Drive(transmission, leftDriveEncoder, rightDriveEncoder, gyro);
        // pneumatics

        Hardware.launcherMotorEncoder.setTicksPerRevolution(42);

        intake = new IntakeControl(launchTimer, intakeSolenoid, intakeMotor);

        launcher = new Launcher(launcherMotorGroup, launcherMotorEncoder);

        storage = new StorageControl(intakeRL, lowStoreRL, upStoreRL, firingRL, conveyorMotorGroup, addBallTimer);

        ballCounter = new BallCounter(ballButtonTimer);

        wheelSpinnerEncoder.setDistancePerPulse(WHEEL_ENCODER_DISTANCE_PER_TICK);
        wheelSpinnerEncoder.setTicksPerRevolution(4096);

        colorWheel = new ColorWheel(wheelSpinnerMotor, wheelSpinnerEncoder, colorSensor);

        climb = new Climb(climbMotorGroup, climbServo, climbEncoder);
        hoodControl = new HoodControl(hoodServo);//TODO

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
        intakeMotor.setInverted(true);

        wheelSpinnerMotor = new WPI_TalonSRX(25);

        colorSensor = new ColorSensorV3(i2cPort);

        climbMotorR = new WPI_TalonSRX(29);

        // climbMotorL = new WPI_TalonSRX(28);

        climbMotorGroup = new SpeedControllerGroup(climbMotorR /* , climbMotorL */);

        climbEncoder = new KilroyEncoder((WPI_TalonSRX) climbMotorR);

        climbEncoder.setDistancePerPulse(CURRENT_YEAR_DISTANCE_PER_TICK_CLIMB_MOTOR);

        // ==============DIO INIT=============

        launcherMotorEncoder = new KilroyEncoder(7, 6);

        launcherMotorEncoder.setTicksPerRevolution(400);

        wheelSpinnerEncoder = new KilroyEncoder((WPI_TalonSRX) wheelSpinnerMotor);

        wheelSpinnerEncoder.setDistancePerPulse(WHEEL_ENCODER_DISTANCE_PER_TICK);
        wheelSpinnerEncoder.setTicksPerRevolution(4096);

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

        storage = new StorageControl(intakeRL, lowStoreRL, upStoreRL, firingRL, conveyorMotorGroup, addBallTimer);

        //hoodControl = new HoodControl(hoodServo);

        ballCounter = new BallCounter(ballButtonTimer);

        colorWheel = new ColorWheel(wheelSpinnerMotor, wheelSpinnerEncoder, colorSensor);

        climb = new Climb(climbMotorGroup, climbServo, climbEncoder);

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

    // -------------------------------------------------------------

    public static SpeedController climbMotorR = null;

    public static SpeedController climbMotorL = null;

    public static SpeedControllerGroup climbMotorGroup = null;

    public static KilroyEncoder climbEncoder = null;

    // **********************************************************
    // DIGITAL I/O
    // **********************************************************
    public static I2C.Port i2cPort = I2C.Port.kOnboard;
    public static ColorSensorV3 colorSensor = new ColorSensorV3(i2cPort);

    public static LightSensor intakeRL = new LightSensor(21); // bottom
    public static LightSensor lowStoreRL = new LightSensor(22); // lower middle

    public static LightSensor upStoreRL = new LightSensor(23);// upper middle
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

    public static SingleThrowSwitch demoSwitch = new SingleThrowSwitch(27);

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

    // public static JoystickButton climbReverseButton = new
    // JoystickButton(Hardware.leftDriver, 7 + 8);

    public static MomentarySwitch publishVisionSwitch = new MomentarySwitch(leftOperator, 11, false);

    // ----------------------------------------------------------
    // buttons - for left driver
    // ----------------------------------------------------------
    public static JoystickButton gearDown = new JoystickButton(Hardware.leftDriver, 1);

    // ----------------------------------------------------------
    // buttons - for right driver
    // ----------------------------------------------------------
    public static JoystickButton gearUp = new JoystickButton(Hardware.rightDriver, 1);

    public static MomentarySwitch cameraSwitchButton2 = new MomentarySwitch(rightDriver, 11, false);

    public static JoystickButton limelightButton = new JoystickButton(rightDriver, 9);

    public static JoystickButton cameraSwitchButton2Raw = new JoystickButton(rightDriver, 11);

    // ----------------------------------------------------------
    // buttons - for left operator
    // ----------------------------------------------------------
    public static JoystickButton intakeButton = new JoystickButton(Hardware.leftOperator, 1);

    public static JoystickButton outtakeButton = new JoystickButton(Hardware.leftOperator, 2);

    public static JoystickButton pickupBallVisionButton = new JoystickButton(Hardware.leftOperator, 4);

    public static JoystickButton intakeOverrideButton = new JoystickButton(Hardware.leftOperator, 5);

    public static JoystickButton spinWheelButton = new JoystickButton(Hardware.leftOperator, 6);

    public static JoystickButton spinWheelColorButton = new JoystickButton(Hardware.leftOperator, 7);

    public static JoystickButton takePictureButton1 = new JoystickButton(Hardware.leftOperator, 8);

    public static JoystickButton takePictureButton2 = new JoystickButton(Hardware.leftOperator, 9);

    public static JoystickButton wheelOverrideButton = new JoystickButton(Hardware.leftOperator, 10);

    public static JoystickButton conveyorOverrideButton = new JoystickButton(Hardware.leftOperator, 11);

    // ----------------------------------------------------------
    // buttons - for right operator
    // ----------------------------------------------------------
    public static JoystickButton launchButton = new JoystickButton(Hardware.rightOperator, 1);   //this one

    public static JoystickButton climbMotorDownButton = new JoystickButton(Hardware.rightOperator, 2);

    public static JoystickButton climbMotorUpButton = new JoystickButton(Hardware.rightOperator, 3);

    public static JoystickButton wheelManualSpinButton = new JoystickButton(Hardware.rightOperator, 4);

    public static JoystickButton launchOverrideButton = new JoystickButton(Hardware.rightOperator, 5);  //this one

    public static JoystickButton launcherSpeedOneButton = new JoystickButton(Hardware.rightOperator, 6); //this one 

    public static JoystickButton launcherSpeedTwoButton = new JoystickButton(Hardware.rightOperator, 7);  //this one 

    public static JoystickButton subtractBallButton = new JoystickButton(Hardware.rightOperator, 8);

    public static JoystickButton addBallButton = new JoystickButton(Hardware.rightOperator, 9);

    public static MomentarySwitch cameraSwitchButton1 = new MomentarySwitch(rightOperator, 10, false);

    public static JoystickButton cameraSwitchButton1Raw = new JoystickButton(rightOperator, 10);

    public static JoystickButton hoodOverideButton = new JoystickButton(Hardware.rightOperator, 11);  //this one

    public static JoystickButton hoodUpButton = new JoystickButton(Hardware.leftDriver, 10);  //this one 

    public static JoystickButton hoodDownButton = new JoystickButton(Hardware.leftDriver, 9);  // this one 
    // public static JoystickButton hoodUp = new
    // **********************************************************
    // Kilroy's Ancillary classes
    // **********************************************************
    public static HttpCamera limelightPreview = new HttpCamera("LimeLight Preview", "http://limelight.local:5800/");
    public static KilroyUSBCamera kilroyUSBCamera = new KilroyUSBCamera(cameraSwitchButton1, cameraSwitchButton2);

    // ------------------------------------
    // Utility classes
    // ------------------------------------
    public static Timer autoTimer = new Timer();

    public static Timer getSpeedTimer = new Timer();

    public static Timer telopTimer = new Timer();

    public static Timer launchTimer = new Timer();

    public static Timer ballButtonTimer = new Timer();

    public static Timer addBallTimer = new Timer();

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

    public static Climb climb = null;

    // ------------------------------------------
    // Vision stuff
    // ----------------------------
    public static LimelightDriveWithVision visionDriving = new LimelightDriveWithVision();

    public static LimelightInterface visionInterface = new LimelightInterface();

    public final static double PREV_YEAR_DISTANCE_PER_TICK = 23 / 13.8;// .0346;
    public final static double CURRENT_YEAR_DISTANCE_PER_TICK = .000746;// .000746

    public final static double WHEEL_ENCODER_DISTANCE_PER_TICK = 0.0024305403;

    public final static double PREV_YEAR_WHEEL_SPINNER_DISTANCE_PER_TICK = 0.0024305403;

    public final static double CURRENT_YEAR_DISTANCE_PER_TICK_CLIMB_MOTOR = 1.0;

    // -------------------
    // Subassemblies
    // -------------------
    public static CameraServo cameraServo = new CameraServo();

    } // end class
