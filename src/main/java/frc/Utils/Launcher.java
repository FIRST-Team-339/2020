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

    public boolean shootBallsAuto(boolean isClose)
    {
        System.out.println("shootStateAuto: " + shootStateAuto);
        if (Hardware.ballcounter.getBallCount() > 0)
            {
            switch (shootStateAuto)
                {
                case CHARGE:

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

    private double speedAdjustment = 0;
    public boolean spedUp = false;

    //speed in inches per second

    //estimated RPM
    //short = 2300RPM
    //long 5300RPM
    public boolean prepareToShoot(boolean close)
    {
        if (close)
            {
            if (Hardware.launcherMotorEncoder.setRPM(2800, Hardware.launcherMotorGroup))
                {
                return true;
                }
            }
        else
            {
            if (Hardware.launcherMotorEncoder.setRPM(3500, Hardware.launcherMotorGroup))
                {
                return true;
                }
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
    }
