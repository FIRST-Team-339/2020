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

import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
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
        CurrentYear("2020"), PrevYear("2019");

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

        // ============ANALOG INIT============

        // ==============RIO INIT=============

        // =============OTHER INIT============

        //pneumatics
        compressor = new Compressor();

        transmission = new TankTransmission(leftDriveGroup, rightDriveGroup);
        drive = new Drive(transmission, leftDriveEncoder, rightDriveEncoder, gyro);

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

        leftDriveGroup = new SpeedControllerGroup(leftFrontMotor);
        rightDriveGroup = new SpeedControllerGroup(rightFrontMotor);

        launcherMotor1 = new WPI_TalonSRX(26);

        launcherMotorGroup = new SpeedControllerGroup(launcherMotor1);

        conveyorMotor1 = new WPI_TalonSRX(22);//TODO

        conveyorMotorGroup = new SpeedControllerGroup(conveyorMotor1);

        intakeMotor = new WPI_TalonSRX(23);//TODO 23

        wheelSpinnerMotor = new WPI_TalonSRX(25);

        hoodAdjustmentMotor = new WPI_TalonSRX(24);

        // ==============DIO INIT=============

        launcherMotorEncoder = new KilroyEncoder((WPI_TalonSRX) launcherMotor1);

        conveyorMotorEncoder = new KilroyEncoder((WPI_TalonSRX) conveyorMotor1);

        intakeMotorEncoder = new KilroyEncoder((WPI_TalonSRX) intakeMotor);

        wheelSpinnerEncoder = new KilroyEncoder((WPI_TalonSRX) wheelSpinnerMotor);

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

    } // end initizliePrevYear()

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
            }
        else if (robotIdentity == Identifier.PrevYear)
            {
            initializePrevYear();
            }

    } // end initialize()

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
    public static ColorSensorV3 colorSensor = new ColorSensorV3(i2cPort);

    public static LightSensor intakeRL = new LightSensor(12); // bottom
    public static LightSensor lowStoreRL = new LightSensor(3); // lower middle
    public static LightSensor upStoreRL = new LightSensor(4); // upper middle
    public static LightSensor firingRL = new LightSensor(1); // top

    public static SixPositionSwitch autoSixPosSwitch = new SixPositionSwitch(13, 14, 15, 16, 17, 18);
    public static SingleThrowSwitch autoSwitch = new SingleThrowSwitch(0);

    public static SingleThrowSwitch shootFar = new SingleThrowSwitch(22);
    public static SingleThrowSwitch shootClose = new SingleThrowSwitch(23);
    public static DoubleThrowSwitch shootingPlan = new DoubleThrowSwitch(shootFar, shootClose);

    public static SingleThrowSwitch leftAuto = new SingleThrowSwitch(24);
    public static SingleThrowSwitch rightAuto = new SingleThrowSwitch(25);
    public static DoubleThrowSwitch autoLocation = new DoubleThrowSwitch(leftAuto, rightAuto);

    // public static SingleThrowSwitch autoZeroBallsIn = new SingleThrowSwitch(24);
    // public static SingleThrowSwitch autoThreeBallsIn = new SingleThrowSwitch(25);
    // public static DoubleThrowSwitch autoTwoBalls = new
    // DoubleThrowSwitch(autoZeroBallsIn, autoThreeBallsIn);

    // **********************************************************
    // ANALOG I/O
    // **********************************************************

    public static Potentiometer delayPot = new Potentiometer(0);

    public static Potentiometer hoodPot = new Potentiometer(1);

    public static LVMaxSonarEZ frontUltraSonic = new LVMaxSonarEZ(3);
    // **********************************************************
    // PNEUMATIC DEVICES
    // **********************************************************
    public static DoubleSolenoid iDoubleSolenoid = new DoubleSolenoid(4, 5);
    public static DoubleSolenoid lifDoubleSolenoid = new DoubleSolenoid(2, 3);
    public static Compressor compressor = null;
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

    public static MomentarySwitch invertTempoMomentarySwitch = new MomentarySwitch();

    public static MomentarySwitch publishVisionSwitch = new MomentarySwitch(leftOperator, 11, false);

    public static JoystickButton publishVisionButton = new JoystickButton(Hardware.leftOperator, 11);

    public static JoystickButton cancelAuto = new JoystickButton(Hardware.rightDriver, 5);
    public static JoystickButton gearUp = new JoystickButton(Hardware.rightDriver, 1);
    public static JoystickButton gearDown = new JoystickButton(Hardware.leftDriver, 1);
    public static JoystickButton launchButton = new JoystickButton(Hardware.rightOperator, 1);
    public static JoystickButton intakeButton = new JoystickButton(Hardware.leftOperator, 1);
    public static JoystickButton outtakeButton = new JoystickButton(Hardware.leftOperator, 2);
    public static JoystickButton intakeOverrideButton = new JoystickButton(Hardware.leftOperator, 5);
    public static JoystickButton pictureButton1 = new JoystickButton(Hardware.leftOperator, 8);
    public static JoystickButton pictureButton2 = new JoystickButton(Hardware.leftOperator, 9);
    // **********************************************************
    // Kilroy's Ancillary classes
    // **********************************************************

    // public static UsbCamera usbCam0 =
    // CameraServer.getInstance().startAutomaticCapture("usb0", 0);
    // public static UsbCamera usbCam1 =
    // CameraServer.getInstance().addSwitchedCamera(null)

    public static MjpegServer server = new MjpegServer("Robot camera", 1189);
    public static UsbCamera usbCam0 = new UsbCamera("usb0", 0);
    public static UsbCamera usbCam1 = new UsbCamera("usb1", 1);

    // ------------------------------------
    // Utility classes
    // ------------------------------------
    public static Timer autoTimer = new Timer();

    public static Timer telopTimer = new Timer();

    public static Timer camTimer1 = new Timer();

    public static Timer camTimer2 = new Timer();

    public static Timer launchTimer = new Timer();

    public static Timer ballButtonTimer = new Timer();

    public static Telemetry telemetry = new Telemetry(driverStation);

    // ------------------------------------
    // Drive system
    // ------------------------------------
    public static Drive drive = null;

    public static TankTransmission transmission = null;

    // launcher stuff
    public static IntakeControl intake = new IntakeControl(launchTimer, iDoubleSolenoid, intakeMotor);

    public static Launcher launcher = new Launcher(launcherMotorGroup, launcherMotorEncoder);

    public static StorageControl storage = new StorageControl(intakeRL, lowStoreRL, upStoreRL, firingRL,
            conveyorMotorGroup);

    public static HoodControl hoodControl = new HoodControl(hoodAdjustmentMotor, hoodPot);

    public static BallCounter ballcounter = new BallCounter(ballButtonTimer);

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
