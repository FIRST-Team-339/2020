package frc.Utils;

import frc.Hardware.Hardware;
import frc.HardwareInterfaces.KilroyEncoder;
import frc.HardwareInterfaces.LightSensor;

import java.nio.charset.CharacterCodingException;
import java.util.Timer;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 *
 *
 *
 *
 */
public class Launcher
    {

    // private motortype shootingMotor = null;
    // private motortype intakeMotor = null;
    SpeedController firingMotors = null;
    KilroyEncoder encoder = null;

    public Launcher(SpeedControllerGroup firingMotors, KilroyEncoder encoder)
        {
            this.firingMotors = firingMotors;
            this.encoder = encoder;
        }

    //enum to the main shooting state
    private enum ShootState
        {
        PASSIVE, CHARGE, LAUNCH
        }

    public ShootState shootState = ShootState.PASSIVE;

    /**
     * in case you could not guess this function will shoot balls at whatever you
     * desire. whether it be the target or pesky those builders who have yet to
     * finish the actual launcher
     */
    public void shootBalls(JoystickButton shootButton, JoystickButton overrideButton, boolean isClose)
    {
        // System.out.println("shootState: " + shootState);
        switch (shootState)
            {
            case PASSIVE:
                //until shoot button dont shoot
                //must be held down to shoot multiple balls
                if (shootButton.get())
                    {
                    shootState = ShootState.CHARGE;
                    }
                break;
            case CHARGE:
                //starts charging the launcher and prepares the balls in the conveyor
                if (prepareToShoot(isClose, teleop))
                    {
                    launcherReadyTemp = true;
                    }
                if (conveyorReadyTemp || Hardware.storage.prepareToShoot())
                    {
                    conveyorReadyTemp = true;
                    }
                //if both are prepared
                if (conveyorReadyTemp && launcherReadyTemp)
                    {
                    conveyorReadyTemp = false;
                    launcherReadyTemp = false;
                    shootState = ShootState.LAUNCH;
                    }
                break;
            case LAUNCH:
                //loads a ball and shoots it
                if (Hardware.storage.loadToFire())
                    {
                    //back to passive
                    shootState = ShootState.PASSIVE;
                    }
                break;
            default:

                break;
            }

    }

    //enum for shooting in autp
    private enum ShootStateAuto
        {
        CHARGE, LAUNCH, FINISH
        }

    public ShootStateAuto shootStateAuto = ShootStateAuto.CHARGE;

    //the balls that are in the storage system
    //initialized to 0
    private int startBallCount = 0;
    //boolean to store whether thsi is the first run of the function
    private boolean firstRun = true;

    /**
     *
     * shoots all of the balls that are in storage in auto. Calls storage control to
     * move the conveyor belt to load balls and checks that the RPM is at the right
     * speed
     *
     * @param isClose
     *                    if we want to shoot at the tip of triangle(roughly 40
     *                    inches)
     * @return shot all of the balls
     */
    public boolean shootBallsAuto(boolean isClose)
    {
        // System.out.println("Balls: " + Hardware.ballcounter.getBallCount());
        // SmartDashboard.putString("shootStateAuto: ", shootStateAuto.toString());
        // SmartDashboard.putBoolean("launcer Ready", launcherReadyTemp);
        // SmartDashboard.putBoolean("conveyor ready", conveyorReadyTemp);
        // SmartDashboard.putBoolean("load ready", loadReadyTemp);

        //if more than 0 balls
        if (Hardware.ballcounter.getBallCount() >= 0)
            {
            // System.out.println("Shoot State Launcher: " + shootStateAuto);
            switch (shootStateAuto)
                {

                case CHARGE:
                    // sets the RPM and makes sure that the conveyor is correct
                    if (prepareToShoot(isClose, auto))
                        {
                        // System.out.println("Setting Launcher");

                        launcherReadyTemp = true;
                        }
                    //prepares the balls in the storage system
                    if (conveyorReadyTemp || Hardware.storage.prepareToShoot()
                            || Hardware.ballcounter.getBallCount() == 0)

                        {
                        // System.out.println("prepared to shoot");
                        conveyorReadyTemp = true;
                        }
                    // both a launcher and storage are ready
                    if (launcherReadyTemp && conveyorReadyTemp)
                        {
                        launcherReadyTemp = false;
                        conveyorReadyTemp = false;
                        shootStateAuto = ShootStateAuto.LAUNCH;
                        // System.out.println("State Launch");
                        }
                    break;
                case LAUNCH:
                    //if first time initialize start balls count
                    if (firstRun)
                        {
                        startBallCount = Hardware.ballcounter.getBallCount();
                        firstRun = false;
                        }
                    if (Hardware.ballcounter.getBallCount() > 1)
                        {
                        // System.out.println("loading to fire");
                        //keeps launcher moving at speed
                        Hardware.launcher.prepareToShoot(isClose, auto);
                        //loads a ball into shooter
                        if (Hardware.storage.loadToFire())
                            {
                            loadReadyTemp = true;
                            }
                        //if ball has shot charge next ball
                        if (loadReadyTemp)
                            {
                            loadReadyTemp = false;
                            shootStateAuto = ShootStateAuto.CHARGE;
                            }

                        }
                    else if (Hardware.ballcounter.getBallCount() == 1)
                        {
                        Hardware.launcher.prepareToShoot(isClose, auto);
                        // System.out.println("loading to fire last ball");
                        //load ball to shoot
                        if (Hardware.storage.loadToFire())
                            {
                            loadReadyTemp = true;
                            // System.out.println("load temp set");
                            }
                        //if shot reset stuff
                        if (loadReadyTemp)
                            {
                            loadReadyTemp = false;
                            firstRun = true;
                            return true;
                            }

                        }
                    //if 0 balls reset stuff
                    else
                        {
                        firstRun = true;
                        // System.out.println("Returning true!!!!!!");
                        return true;

                        }
                    break;

                default:
                    break;
                }
            }
        return false;
    }

    //stores if the launcher is at speed
    public boolean spedUp = false;

    public boolean aligned = false;
    // speed in inches per second

    // estimated RPM
    // short = 2300RPM
    // long 5300RPM
    /**
     * prepares the launcher motor to shoot by settingthe RPM to either the close
     * value or the far value
     *
     * @param close
     * @return
     */
    public boolean prepareToShoot(boolean isClose, boolean inAuto)
    {
        if (isClose)
            {
            if (inAuto || Hardware.visionDriving.driveToTarget(35, true, .3))
                {
                aligned = true;
                }
            if (Hardware.robotIdentity == Hardware.yearIdentifier.CurrentYear)
                {
                if (Hardware.launcherMotorEncoder.setRPM(
                        RPM_CLOSE_2020 + (Hardware.rightOperator.getZ() * DRIVER_CHANGE_ALLOWANCE),
                        Hardware.launcherMotorGroup))
                    {
                    spedUp = true;
                    }
                }
            else
                {
                if (Hardware.launcherMotorEncoder.setRPM(
                        RPM_CLOSE_2019 + (Hardware.rightOperator.getZ() * DRIVER_CHANGE_ALLOWANCE),
                        Hardware.launcherMotorGroup))
                    {
                    spedUp = true;
                    }
                }
            }
        else
            {
            if (inAuto || Hardware.visionDriving.driveToTarget(120, true, .3))
                {
                aligned = true;
                }
            if (Hardware.robotIdentity == Hardware.yearIdentifier.CurrentYear)
                {
                if (Hardware.launcherMotorEncoder.setRPM(
                        RPM_FAR_2020 /* + (Hardware.rightOperator.getZ() * DRIVER_CHANGE_ALLOWANCE ) */,
                        Hardware.launcherMotorGroup))
                    {
                    spedUp = true;
                    }
                }
            else
                {
                if (Hardware.launcherMotorEncoder.setRPM(
                        RPM_FAR_2019 /* + (Hardware.rightOperator.getZ() * DRIVER_CHANGE_ALLOWANCE ) */,
                        Hardware.launcherMotorGroup))
                    {
                    spedUp = true;
                    }
                }
            }
        if (aligned && spedUp)
            {
            aligned = false;
            spedUp = false;
            return true;
            }
        return false;
    }

    public boolean unchargeShooter()
    {
        Hardware.launcherMotorGroup.set(0);
        return false;
    }

    public boolean getDistance(double distance)
    {
        return false;
    }

    private boolean launcherReadyTemp = false;

    private boolean conveyorReadyTemp = false;

    private boolean loadReadyTemp = false;

    private boolean auto = true;
    private boolean teleop = false;
    public boolean launching = false;
    private static final double RPM_FAR_2020 = 3500;
    private static final double RPM_CLOSE_2020 = 2800;
    private static final double RPM_FAR_2019 = 900;
    private static final double RPM_CLOSE_2019 = 100;
    private static final double DRIVER_CHANGE_ALLOWANCE = 100;

    }
