
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

import frc.HardwareInterfaces.IRSensor;
import frc.HardwareInterfaces.KilroyEncoder;
import frc.HardwareInterfaces.KilroySPIGyro;
import frc.HardwareInterfaces.Potentiometer;
import frc.HardwareInterfaces.SingleThrowSwitch;
import frc.HardwareInterfaces.SixPositionSwitch;
import frc.vision.*;
import frc.Utils.drive.Drive;
import frc.Utils.drive.DrivePID;
import frc.Utils.Telemetry;
import frc.HardwareInterfaces.Transmission.TankTransmission;

import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.CANCoderConfiguration;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.DriverStation;
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

public class Hardware {

    enum Identifier {
        CurrentYear, PrevYear
    };

    public static Identifier robotIdentity = Identifier.PrevYear;

    public static void initialize() {


        if (robotIdentity == Identifier.CurrentYear) {

             // ==============Buttons=============
          cancelAuto  = new JoystickButton(Hardware.rightDriver, 5);
          gearUp = new JoystickButton(Hardware.rightDriver, 1);
          gearDown = new JoystickButton(Hardware.leftDriver, 1);
          launchButton = new JoystickButton(Hardware.leftOperator, 1);

            // ==============CAN INIT=============
            // Motor Controllers
           // leftFrontMotor = new WPI_TalonFX(13);
            //rightFrontMotor = new WPI_TalonFX(15);
            // leftRearMotor = new WPI_TalonFX(12);
            // rightRearMotor = new WPI_TalonFX(14);

            // leftDriveGroup = new SpeedControllerGroup(leftRearMotor, leftFrontMotor);
            // rightDriveGroup = new SpeedControllerGroup(rightRearMotor, rightFrontMotor);
            // ==============DIO INIT=============
            //red light sensors - there should be four! 20 Jan. 2020

            // ============ANALOG INIT============


            // ==============RIO INIT=============

            // =============OTHER INIT============
            visionInterface = new NewVisionInterface();
            visionDriving = new NewDriveWithVision();

        } else if (robotIdentity == Identifier.PrevYear) {

            // ==============DIO INIT=============
            autoDisableSwitch = new SingleThrowSwitch(0);
            autoSixPosSwitch = new SixPositionSwitch(1, 2, 3, 4, 5, 6);

            // ============ANALOG INIT============
            delayPot = new Potentiometer(0);

            // ==============CAN INIT=============
            // Motor Controllers
           // leftFrontMotor = new CANSparkMax(13, MotorType.kBrushless);
            //rightFrontMotor = new CANSparkMax(15, MotorType.kBrushless);
            // leftRearMotor = new CANSparkMax(2, MotorType.kBrushless);
            // rightRearMotor = new CANSparkMax(3, MotorType.kBrushless);

            leftFrontMotor = new WPI_TalonFX(13);
           // rightFrontMotor = new WPI_TalonFX(15);

            // Encoders
            // leftEncoder = new KilroyEncoder((CANSparkMax) leftFrontMotor);
            // rightEncoder = new KilroyEncoder((CANSparkMax) rightFrontMotor);


            boardEncoder = new CANCoder(0);


            leftDriveGroup = new SpeedControllerGroup(/*leftRearMotor,*/ leftFrontMotor);
           // rightDriveGroup = new SpeedControllerGroup(/*rightRearMotor,*/ rightFrontMotor);
            // ==============RIO INIT==============
            gyro = new KilroySPIGyro(false);
            // =============OTHER INIT============
            // transmission = new TankTransmission(leftDriveGroup, rightDriveGroup);
            // drive = new Drive(transmission, null, null, gyro);
            // drivePID = new DrivePID(transmission, leftEncoder, rightEncoder, gyro);

            visionInterface = new NewVisionInterface();
            visionDriving = new NewDriveWithVision();

        }
        System.out.println("CANCoder Obj = " + boardEncoder);
    }


    // **********************************************************
    // Buttons
    // **********************************************************
    public static JoystickButton cancelAuto = null;

    public static JoystickButton gearUp = null;
    public static JoystickButton gearDown = null;
    public static JoystickButton gearOverride = null;

    public static JoystickButton launchButton = null;
    // **********************************************************
    // CAN DEVICES
    // **********************************************************



    public static SpeedController leftRearMotor = null;
    public static SpeedController rightRearMotor = null;
    public static SpeedController leftFrontMotor = null;
    public static SpeedController rightFrontMotor = null;


    public static CANCoder boardEncoder = null;

    public static SpeedControllerGroup leftDriveGroup = null;
    public static SpeedControllerGroup rightDriveGroup = null;

    public static KilroyEncoder leftEncoder = null;
    public static KilroyEncoder rightEncoder = null;

    // **********************************************************
    // DIGITAL I/O
    // **********************************************************

    public static IRSensor intakeRL = new LightSensor(12);
    public static IRSensor lowStoreRL = new LightSensor(3);
    public static IRSensr upStoreRL = new LightSensor(4);
    public static IRSensor firingRL = new LightSensor(1);


    public static WPI_TalonFX boardMotor = null;//Can ID 18 in Initilization

    public static SixPositionSwitch autoSixPosSwitch = null;
    public static SingleThrowSwitch autoDisableSwitch = null;

    // **********************************************************
    // ANALOG I/O
    // **********************************************************

    public static  IRSensor intakeRL = null;
    public static IRSensor lowStoreRL = null;
    public static IRSensor upStoreRL = null;
    public static IRSensor firingRL = null;

    public static Potentiometer delayPot = null;

    // **********************************************************
    // PNEUMATIC DEVICES
    // **********************************************************

    // **********************************************************
    // roboRIO CONNECTIONS CLASSES
    // **********************************************************

    public static PowerDistributionPanel pdp = new PowerDistributionPanel(2);

    public static KilroySPIGyro gyro = null;

    // **********************************************************
    // DRIVER STATION CLASSES
    // **********************************************************

    public static DriverStation driverStation = DriverStation.getInstance();

    public static Joystick leftDriver = new Joystick(1);
    public static Joystick rightDriver = new Joystick(0);
    public static Joystick leftOperator = new Joystick(3);
    public static Joystick rightOperator = new Joystick(2);

    // **********************************************************
    // Kilroy's Ancillary classes
    // **********************************************************

    UsbCamera usbCam0 = new UsbCamera("USB Cam 0", 0);
    UsbCamera usbCam1 = new UsbCamera("USB Cam 1", 1);

    // ------------------------------------
    // Utility classes
    // ------------------------------------
    public static Timer autoTimer = new Timer();

    public static Timer telopTimer = new Timer();

    public static Telemetry telemetry = new Telemetry(driverStation);

    // ------------------------------------
    // Drive system
    // ------------------------------------
    public static Drive drive = null;

    public static DrivePID drivePID = null;

    public static TankTransmission transmission = null;

    // ------------------------------------------
    // Vision stuff
    // ----------------------------

    public static NewDriveWithVision visionDriving = null;

    public static NewVisionInterface visionInterface = null;
    // -------------------
    // Subassemblies
    // -------------------

} // end class
