package frc.Utils;

import frc.Hardware.Hardware;
import frc.HardwareInterfaces.KilroyEncoder;
import frc.HardwareInterfaces.LightSensor;

import java.nio.charset.CharacterCodingException;

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
    public void shootBalls(JoystickButton shootButton, JoystickButton overrideButton, boolean close)
    {
        switch (shootState)
            {
            case PASSIVE:
                break;
            case CHARGE:
                break;
            case LAUNCH:
                break;
            default:

                break;
            }

    }

    public void shootBalls(JoystickButton shootButton, JoystickButton overrideButton, int distance)
    {

    }

    public boolean shootBallsAuto(JoystickButton overrideButton, boolean isClose)
    {
        return false;
    }

    private double speedAdjustment = 0;

    public boolean prepareToShoot(double RPM)
    {
        if (this.encoder.getRate() >= RPM + 100)
            {
            return true;
            }
        else
            {
            if (this.encoder.getRate() < RPM)
                {
                speedAdjustment += .025;
                }
            else if (this.encoder.getRate() > RPM)
                {
                speedAdjustment -= .25;
                }
            }
        this.firingMotors.set(.5 + speedAdjustment);
        return false;
    }

    public int getRPMPerDistance(int distance)
    {
        //TDO
        return 0;
    }

    public boolean launching = false;
    }
