package frc.Utils;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import frc.Hardware.Hardware;
import frc.HardwareInterfaces.LightSensor;

public class StorageControl
    {
    LightSensor intakeLR = null;
    LightSensor lowerLR = null;
    LightSensor upperLR = null;
    LightSensor shootLR = null;

    SpeedControllerGroup conveyorMotors = null;

    public StorageControl(LightSensor intakeLR, LightSensor lowerLR, LightSensor upperLR, LightSensor shootLR,
            SpeedControllerGroup conveyorMotors)
        {
            this.intakeLR = intakeLR;
            this.lowerLR = lowerLR;
            this.upperLR = upperLR;
            this.shootLR = shootLR;
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
        INITIAL_UP, WAIT_FOR_POWER, LOAD_BALL
        }

    public boolean prepareToShoot()
    {

        if (Hardware.ballcounter.getBallCount() > 0)
            {

            }

        return false;
    }

    final double HOLDING_SPEED = 0;

    final double UP_SPEED = .2;

    final double DOWN_SPEED = -.2;

    }
