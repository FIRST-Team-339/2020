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
                if (shootButton.get())
                    {
                    shootState = ShootState.CHARGE;
                    }
                break;
            case CHARGE:

                if (prepareToShoot(isClose, false) && Hardware.storage.prepareToShoot())
                    {
                    shootState = ShootState.LAUNCH;
                    }
                break;
            case LAUNCH:
                if (Hardware.storage.loadToFire())
                    {
                    System.out.println("loaded");
                    shootState = ShootState.PASSIVE;
                    }
                break;
            default:

                break;
            }

    }

    private enum ShootStateAuto
        {
        CHARGE, LAUNCH, FINISH
        }

    public ShootStateAuto shootStateAuto = ShootStateAuto.CHARGE;

    private int startBallCount = 0;

    private boolean firstRun = true;

    private boolean launcherReadyTemp = false;

    private boolean conveyorReadyTemp = false;

    private boolean loadReadyTemp = false;

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
        SmartDashboard.putString("shootStateAuto: ", shootStateAuto.toString());
        SmartDashboard.putBoolean("launcer Ready", launcherReadyTemp);
        SmartDashboard.putBoolean("conveyor ready", conveyorReadyTemp);
        SmartDashboard.putBoolean("load ready", loadReadyTemp);
        if (Hardware.ballcounter.getBallCount() >= 0)
            {
            // System.out.println("Shoot State Launcher: " + shootStateAuto);
            switch (shootStateAuto)
                {

                case CHARGE:
                    // sets the RPM and makes sure that the conveyor is correct
                    if (launcherReadyTemp || prepareToShoot(isClose, true))
                        {
                        // System.out.println("Setting Launcher");

                        launcherReadyTemp = true;
                        }
                    if (conveyorReadyTemp || Hardware.storage.prepareToShoot()
                            || Hardware.ballcounter.getBallCount() == 0)

                        {
                        // System.out.println("prepared to shoot");
                        conveyorReadyTemp = true;
                        }
                    if (launcherReadyTemp && conveyorReadyTemp)
                        {
                        launcherReadyTemp = false;
                        conveyorReadyTemp = false;
                        shootStateAuto = ShootStateAuto.LAUNCH;
                        // System.out.println("State Launch");
                        }
                    break;
                case LAUNCH:
                    if (firstRun)
                        {
                        startBallCount = Hardware.ballcounter.getBallCount();
                        firstRun = false;
                        }
                    if (Hardware.ballcounter.getBallCount() > 1)
                        {
                        // System.out.println("loading to fire");
                        if (Hardware.storage.loadToFire())
                            {
                            loadReadyTemp = true;
                            }
                        if (Hardware.launcher.prepareToShoot(isClose, true))
                            {
                            launcherReadyTemp = true;
                            }
                        if (loadReadyTemp && launcherReadyTemp)
                            {
                            loadReadyTemp = false;
                            launcherReadyTemp = false;
                            }
                        }
                    else if (Hardware.ballcounter.getBallCount() == 1)
                        {
                        // System.out.println("loading to fire last ball");
                        if (Hardware.storage.loadToFire())
                            {
                            loadReadyTemp = true;
                            // System.out.println("load temp set");
                            }
                        if (Hardware.launcher.prepareToShoot(isClose, true))
                            {
                            launcherReadyTemp = true;
                            // System.out.println("Launcher ready set");
                            }
                        if (loadReadyTemp && launcherReadyTemp)
                            {
                            loadReadyTemp = false;
                            launcherReadyTemp = false;
                            // System.out.println("before state change");
                            shootStateAuto = ShootStateAuto.CHARGE;

                            // Isue here: You set it back to charge so it never checks to see if its at 0

                            // If statement at the top stops as soon as it hits 0 so you cancel the switch
                            // case

                            // firstRun = true;
                            // System.out.println("Returning true!!!!!!");
                            // return true;
                            }

                        }
                    else
                        {
                        firstRun = true;
                        // System.out.println("Returning true!!!!!!");
                        Hardware.intake.intake();
                        return true;

                        }
                    break;

                default:
                    break;
                }
            }
        return false;

    }

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

    public boolean launching = false;
    private static final double RPM_FAR_2020 = 3500;
    private static final double RPM_CLOSE_2020 = 2800;
    private static final double RPM_FAR_2019 = 900;
    private static final double RPM_CLOSE_2019 = 100;
    private static final double DRIVER_CHANGE_ALLOWANCE = 100;

    }
