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

                if (prepareToShoot(isClose) && Hardware.storage.prepareToShoot())
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
        CHARGE, LAUNCH
        }

    public ShootStateAuto shootStateAuto = ShootStateAuto.CHARGE;

    /**
     *
     * shoots all of the balls that are in storage in auto. Calls storage control to move the conveyor belt to load balls and checks that the RPM is at the right speed
     *
     * @param isClose if we want to shoot at the tip of triangle(roughly 40 inches)
     * @return shot all of the balls
     */
    public boolean shootBallsAuto(boolean isClose)
    {
        System.out.println("shootStateAuto: " + shootStateAuto);
        if (Hardware.ballcounter.getBallCount() > 0)
            {
            switch (shootStateAuto)
                {
                case CHARGE:
                    //sets the RPM and makes sure that the conveyor is correct
                    if (prepareToShoot(isClose) && Hardware.storage.prepareToShoot())
                        {
                        shootState = ShootState.LAUNCH;
                        }
                    break;
                case LAUNCH:
                    for (int i = Hardware.ballcounter.getBallCount(); i > 0; i++)
                        {
                        if (Hardware.storage.loadToFire())
                            {
                            System.out.println("loaded");
                            shootState = ShootState.PASSIVE;
                            if (i == 1)
                                {
                                return true;
                                }
                            }
                        }

                    break;
                default:

                    break;
                }
            }
        else
            {
            return true;
            }
        return false;
    }

    public boolean spedUp = false;

    public boolean aligned = false;
    //speed in inches per second

    //estimated RPM
    //short = 2300RPM
    //long 5300RPM
    /**
     * prepares the launcher motor to shoot by settingthe RPM to either the  close value or the far value
     * @param close
     * @return
     */
    public boolean prepareToShoot(boolean isClose)
    {
        if (isClose)
            {
            if (Hardware.visionDriving.driveToTarget(35, true, .3))
                {
                aligned = true;
                }
            if (Hardware.launcherMotorEncoder.setRPM(
                    RPM_CLOSE + (Hardware.rightOperator.getZ() * DRIVER_CHANGE_ALLOWANCE), Hardware.launcherMotorGroup))
                {
                spedUp = true;
                }
            }
        else
            {
            if (Hardware.visionDriving.driveToTarget(120, true, .3))
                {
                aligned = true;
                }
            if (Hardware.launcherMotorEncoder.setRPM(
                    RPM_FAR + (Hardware.rightOperator.getZ() * DRIVER_CHANGE_ALLOWANCE), Hardware.launcherMotorGroup))
                {
                spedUp = true;
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
    private static final double RPM_FAR = 3500;
    private static final double RPM_CLOSE = 2800;
    private static final double DRIVER_CHANGE_ALLOWANCE = 100;

    }
