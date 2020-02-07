package frc.Utils;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.Hardware.Hardware;
import frc.HardwareInterfaces.LightSensor;

/**
 * code for controlling the conveyor storage system and moving the conveyor to intake and laucnher balls. Referenes BallCounter to add/subtrack balls based off of the redlights. For the 2020 season
 * @author Conner McKevitt
 */
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

    //control state to update what the conveyor should b doing
    private enum ControlState
        {
        INIT, PASSIVE, UP, DOWN
        }

    public ControlState state = ControlState.INIT;

    /**
     * state updated for the conveyor belt. This should always be running in teleop
     */
    public void storageControlState()
    {

        //these puts are not test code. Send important robot data to the robot for the drivers to see
        SmartDashboard.putNumber("", Hardware.ballcounter.getBallCount());

        SmartDashboard.putBoolean("Green", Hardware.visionInterface.getDistanceFromTarget() <= 120);

        SmartDashboard.putBoolean("Red", Hardware.visionInterface.getDistanceFromTarget() > 120);

        switch (state)
            {
            //just in case need later
            case INIT:
                state = ControlState.PASSIVE;
                break;
            case PASSIVE:
                //if moving the conveyor is not being called set the motor to the holding speed
                if (!override)
                    {
                    Hardware.conveyorMotorGroup.set(HOLDING_SPEED);
                    }

                //gets data from the inake Redlight to add or subtract balls from our internal ball count when a ball enters or leaves the system through the bottem
                if (this.intakeRL.get() && prevRL == false)
                    {
                    prevRL = true;
                    if (Hardware.intake.intaking)
                        {
                        System.out.println("adding");
                        Hardware.ballcounter.addBall();
                        }
                    else if (Hardware.intake.outtaking)
                        {
                        System.out.println("subtracting");
                        Hardware.ballcounter.subtractBall();
                        }
                    }
                if (!this.intakeRL.get())
                    {
                    prevRL = false;
                    }

                break;
            case UP:
                //move up towards launcher
                conveyorUp();
                break;
            //move down towards intake
            case DOWN:
                conveyorDown();
                break;
            default:
                state = ControlState.PASSIVE;
                break;
            }
    }

    boolean prevPassive = false;

    /**
     *
     */
    public void intakeStorageControl()
    {
        if (Hardware.intake.intaking)
            {
            //if the intake RL is not triggered
            if (!this.intakeRL.get())
                {
                if (!this.lowerRL.get())
                    {
                    //move down if lower RL is false
                    state = ControlState.DOWN;
                    prevPassive = false;
                    }
                else
                    {
                    //if lower RL is on dont move
                    state = ControlState.PASSIVE;
                    prevPassive = true;
                    }
                }

            else
                {
                //if the intake Rl is true
                if (prevPassive)
                    {
                    //TODO check if we need this. While commenting i dont think we do the other controll state should be good enough to add balls
                    Hardware.ballcounter.addBall();
                    prevPassive = false;
                    }
                if (!this.upperRL.get())
                    {
                    //if the upper RL is not on move up until on
                    state = ControlState.UP;
                    prevPassive = false;
                    }
                }
            }
    }

    public void outtakeStorageControl()
    {
        state = ControlState.DOWN;
    }

    public void conveyorUp()
    {
        //sets the motors to UP_SPEED
        //whats UP_SPEED?
        //SPEED: not much, how about you?

        System.out.println("conveyor up");
        Hardware.conveyorMotorGroup.set(UP_SPEED);
    }

    public void conveyorDown()
    {
        System.out.println("conveyor down");
        Hardware.conveyorMotorGroup.set(DOWN_SPEED);
    }

    private boolean override = false;

    public void overrideConveyor(Joystick joystick, JoystickButton button)
    {
        if (button.get())
            {
            if (joystick.getY() > .3)
                {
                override = true;
                conveyorUp();
                }
            else if (joystick.getY() < .3)
                {
                override = true;
                conveyorDown();
                }
            else
                {
                override = false;
                }
            }
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
                    //TODO this might have to be the upperRL
                    if (this.shootRL.get())
                        {
                        System.out.println("got shoot rl");
                        state = ControlState.PASSIVE;
                        shootState = ShootState.WAIT_FOR_POWER;
                        }
                    else
                        {
                        state = ControlState.UP;
                        }
                    break;
                case WAIT_FOR_POWER:

                    preparedToFire = true;
                    shootState = ShootState.INIT;
                    return true;
                default:
                    shootState = ShootState.INIT;
                    break;
                }
            }

        return false;
    }

    boolean stillShooting = false;

    public boolean loadToFire()
    {

        if (stillShooting)
            {
            if (this.shootRL.get() == false)
                {
                shotBall = true;
                }
            }
        if (Hardware.ballcounter.getBallCount() > 0)
            {
            if (preparedToFire)
                {
                System.out.println("loading");
                if (this.shootRL.get())
                    {

                    System.out.println("shooting ball");
                    state = ControlState.UP;
                    if (!stillShooting)
                        {
                        Hardware.ballcounter.subtractBall();
                        }
                    stillShooting = true;
                    }
                else if (shotBall)
                    {
                    stillShooting = false;
                    shotBall = false;
                    state = ControlState.PASSIVE;
                    if (Hardware.ballcounter.getBallCount() > 0)
                        {
                        System.out.println(" preparing again");
                        prepareToShoot();
                        return true;
                        }
                    else
                        {

                        Hardware.launcher.unchargeShooter();
                        return true;
                        }
                    }
                }
            }
        else
            {
            state = ControlState.PASSIVE;
            }
        return false;
    }

    public boolean clearStorage(JoystickButton button1, JoystickButton button2)
    {
        if (Hardware.ballcounter.getBallCount() == 0)
            {
            state = ControlState.PASSIVE;
            return true;
            }
        else
            {
            state = ControlState.DOWN;
            Hardware.intake.outtake(0);
            }

        return false;
    }

    private static boolean shotBall = false;
    private static boolean prevRL = false;

    private static boolean preparedToFire = false;

    final double HOLDING_SPEED = 0;

    final double UP_SPEED = .2;

    final double DOWN_SPEED = -.2;

    }
