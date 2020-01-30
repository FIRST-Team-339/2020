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
    public void shootBalls(JoystickButton shootButton, JoystickButton overrideButton)
    {
        System.out.println("shootState: " + shootState);
        switch (shootState)
            {
            case PASSIVE:
                if (shootButton.get())
                    {
                    shootState = ShootState.CHARGE;
                    }
                break;
            case CHARGE:

                if (prepareToShoot(5) && Hardware.storage.prepareToShoot())
                    {
                    shootState = ShootState.LAUNCH;
                    }
                break;
            case LAUNCH:
                if (Hardware.storage.loadToFire())
                    {
                    shootState = ShootState.PASSIVE;
                    }
                break;
            default:

                break;
            }

    }

    public boolean shootBallsAuto(JoystickButton overrideButton, boolean isClose)
    {
        return false;
    }

    private double speedAdjustment = 0;
    public boolean spedUp = false;

    //speed in inches per second

    public boolean prepareToShoot(double speed)
    {
        Hardware.getSpeedTimer.start();

        if (Hardware.launcherMotorEncoder.getRate() > speed - .1)
            {
            Hardware.getSpeedTimer.stop();
            Hardware.getSpeedTimer.reset();
            spedUp = true;
            System.out.println("super sped");
            return true;
            }
        else
            {
            if (Hardware.launcherMotorEncoder.getRate() < speed)
                {
                spedUp = false;
                speedAdjustment += .005;
                }
            }
        Hardware.launcherMotorGroup.set(.5 + speedAdjustment);
        return false;
    }

    public int getRPMPerDistance(int distance)
    {
        //TDO
        return 0;
    }

    public boolean launching = false;
    }
