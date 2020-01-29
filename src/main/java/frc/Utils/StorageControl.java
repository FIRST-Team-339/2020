package frc.Utils;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import frc.Hardware.Hardware;
import frc.HardwareInterfaces.LightSensor;

public class StorageControl
    {
    LightSensor intakeRL = null;
    LightSensor lowerRL = null;
    LightSensor upperRL = null;
    LightSensor shootRL = null;

    SpeedControllerGroup conveyorMotors = null;

    public StorageControl(LightSensor intakeRL, LightSensor lowerRL, LightSensor upperRL, LightSensor shootRL,
            SpeedControllerGroup conveyorMotors)
        {
            this.intakeRL = intakeRL;
            this.lowerRL = lowerRL;
            this.upperRL = upperRL;
            this.shootRL = shootRL;
            this.conveyorMotors = conveyorMotors;
        }

    private enum ControlState
        {
        INIT, PASSIVE, UP, DOWN
        }

    public ControlState state = ControlState.INIT;

    public void storageControlState()
    {
        switch (state)
            {
            case INIT:
                break;
            case PASSIVE:
                this.conveyorMotors.set(HOLDING_SPEED);
                break;
            case UP:
                conveyorUp();
                break;
            case DOWN:
                conveyorDown();
                break;
            default:
                state = ControlState.PASSIVE;
                break;
            }
    }

    public void conveyorUp()
    {
        //sets the motors to UP_SPEED
        //whats UP_SPEED?
        //SPEED: not much, how about you?
        this.conveyorMotors.set(UP_SPEED);
    }

    public void conveyorDown()
    {
        this.conveyorMotors.set(DOWN_SPEED);
    }

    private enum ShootState
        {
        INITIAL_UP, WAIT_FOR_POWER, INIT
        }

    ShootState shootState = ShootState.INIT;

    public boolean prepareToShoot()
    {

        if (Hardware.ballcounter.getBallCount() > 0)
            {
            switch (shootState)
                {
                case INIT:
                    shootState = ShootState.INITIAL_UP;
                    break;
                case INITIAL_UP:
                    if (this.shootRL.get())
                        {
                        state = ControlState.PASSIVE;
                        shootState = ShootState.WAIT_FOR_POWER;
                        }
                    else
                        {
                        state = ControlState.UP;
                        }
                    break;
                case WAIT_FOR_POWER:
                    if (Hardware.launcher.prepareToShoot(RPM_CLOSE))
                        {
                        preparedToFire = true;
                        return true;
                        }
                    break;
                default:
                    shootState = ShootState.INIT;
                    break;
                }
            }

        return false;
    }

    public boolean loadToFire()
    {
        if (preparedToFire && Hardware.launcher.prepareToShoot(RPM_CLOSE))
            {

            if (this.shootRL.get())
                {
                state = ControlState.UP;
                }
            else
                {
                state = ControlState.PASSIVE;
                if (Hardware.ballcounter.getBallCount() > 1)
                    {
                    prepareToShoot();
                    return true;
                    }
                }
            }
        return false;
    }

    private static boolean preparedToFire = false;

    final double RPM_CLOSE = 1500;//TODO also figure all the unit(it might be inches/second or revs/second)
    final double RPM_FAR = 4000;

    final double HOLDING_SPEED = 0;

    final double UP_SPEED = .2;

    final double DOWN_SPEED = -.2;

    }
