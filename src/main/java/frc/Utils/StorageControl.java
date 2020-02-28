package frc.Utils;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.Hardware.Hardware;
import frc.HardwareInterfaces.LightSensor;

/**
 * code for controlling the conveyor storage system and moving the conveyor to
 * intake and laucnher balls. Referenes BallCounter to add/subtrack balls based
 * off of the redlights. For the 2020 season
 *
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

    // control state to update what the conveyor should b doing
    public enum ControlState
        {
        INIT, PASSIVE, UP, DOWN
        }

    private static ControlState state = ControlState.INIT;

    /**
     * state updated for the conveyor belt. This should always be running in teleop
     */
    public void storageControlState()
    {

        // these puts are not test code. Send important robot data to the robot for the
        // drivers to see
        SmartDashboard.putNumber("", Hardware.ballCounter.getBallCount());

        SmartDashboard.putBoolean("Green", Hardware.visionInterface.getDistanceFromTarget() <= 180);
        SmartDashboard.putNumber("distance from target", Hardware.visionInterface.getDistanceFromTarget());
        SmartDashboard.putString("conveyor state: ", state.toString());

        // System.out.println("storage state: " + state);
        // takes the current intake RL and previous intake RL states to add or subtract
        // balls when triggered
        if (this.intakeRL.get() && prevRL == false)
            {
            prevRL = true;
            if (Hardware.intake.intaking)
                {
                // System.out.println("adding");
                Hardware.ballCounter.addBall();
                }
            else if (Hardware.intake.outtaking)
                {
                // System.out.println("subtracting");
                Hardware.ballCounter.subtractBall();
                }
            }
        if (!this.intakeRL.get())
            {
            prevRL = false;
            }

        // System.out.println("conveyor state: " + state);
        // System.out.println("intake: " + this.intakeRL.get());
        // System.out.println("shoot: " + this.shootRL.get());
        // System.out.println("lower: " + this.lowerRL.get());
        // System.out.println("upper: " + this.upperRL.get());
        // main state machine to control the movement of the conveyor
        switch (state)
            {
            // just in case need later
            case INIT:
                setStorageControlState(ControlState.PASSIVE);
                break;
            case PASSIVE:
                // if moving the conveyor is not being called set the motor to the holding speed
                // System.out.println("passive");
                if (!override)
                    {
                    this.conveyorMotors.set(HOLDING_SPEED);
                    // Hardware.conveyorMotorGroup.set(HOLDING_SPEED);
                    }

                // gets data from the inake Redlight to add or subtract balls from our internal
                // ball count when a ball enters or leaves the system through the bottem

                break;
            case UP:
                // move up towards launcher
                // System.out.println("conveyor up");
                conveyorUp();
                break;
            // move down towards intake
            case DOWN:
                // System.out.println("down in Control State");
                conveyorDown();
                break;
            default:
                setStorageControlState(ControlState.PASSIVE);
                break;
            }
    }

    /**
     * sets the prevIntake used in intakeStorageControl
     *
     * @param state
     * @return
     */
    public boolean setPrevIntakeRL(boolean state)
    {
        return (prevIntakeRL = state);
    }

    /**
     * return the state of prevIntakeRL
     *
     * @return boolean
     */
    public boolean getPrevIntakeRL()
    {
        return prevIntakeRL;
    }

    // boolean that stores when the previous conveyor state was PASSIVE
    private boolean prevIntakeRL = false;

    /**
     * controls the conveyor belt if the robot is intaking balls. Note: Must be
     * called continuously in Teleop and auto
     */
    public void intakeStorageControl()
    {
        // System.out.println("intaking: " + Hardware.intake.intaking);
        // System.out.println(getStorageControlState());
        if (Hardware.intake.intaking == true && this.shootRL.get() == false)
            {

            if (this.intakeRL.get() == true || this.getPrevIntakeRL() == true)
                {
                // if the intake Rl is true or the previous intake was true
                this.setPrevIntakeRL(true);
                //
                // System.out.println("going up in intakeStorageControl");
                setStorageControlState(ControlState.UP);
                }
            if (this.intakeRL.get() == false && this.getPrevIntakeRL() == false && this.lowerRL.get() == false)
                {
                // is all false go down
                this.setPrevIntakeRL(false);
                // System.out.println("down in intakeStorageControl");
                setStorageControlState(ControlState.DOWN);

                }
            if (this.intakeRL.get() == false && this.getPrevIntakeRL() == false && this.lowerRL.get() == true)
                {
                // if only the lower is true go passive
                // System.out.println("ball hit lower setting passive");
                setStorageControlState(ControlState.PASSIVE);
                this.setPrevIntakeRL(false);
                }
            if (this.intakeRL.get() == false && this.lowerRL.get() == true)
                {
                // if intake is is false and ball has hit lower stop moving
                // System.out.println("ball hit lower setting passive, not prev if");
                setStorageControlState(ControlState.PASSIVE);
                this.setPrevIntakeRL(false);
                }
            }
        else if (Hardware.intake.outtaking == true)
            {
            setStorageControlState(ControlState.DOWN);
            }
        else if (Hardware.launchButton.get() == false)
            {
            setStorageControlState(ControlState.PASSIVE);
            }
    }

    /**
     * sets the conveyor control state to move down
     */
    public void outtakeStorageControl()
    {
        setStorageControlState(ControlState.DOWN);
    }

    /**
     * set power to the conveyor motors to move them up
     */
    public void conveyorUp()
    {
        // sets the motors to UP_SPEED
        // whats UP_SPEED?
        // SPEED: not much, how about you?
        this.conveyorMotors.set(UP_SPEED);

    }

    /**
     * set power to the conveyor motors to move them down
     */
    public void conveyorDown()
    {
        this.conveyorMotors.set(DOWN_SPEED);
        // Hardware.conveyorMotorGroup.set(DOWN_SPEED);
    }

    // override boolean
    private boolean override = false;

    /**
     * overrides the conveyor movement with joystick controls
     *
     * @param joystick
     * @param button
     */
    public void overrideConveyor(Joystick joystick, JoystickButton button)
    {
        if (button.get())
            {
            if (joystick.getY() > JOYSTICK_DEADBAND_STORAGE)
                {
                // move up and sets the override boolean to true
                override = true;
                conveyorUp();
                }
            else if (joystick.getY() < -JOYSTICK_DEADBAND_STORAGE)
                {
                // move down and sets the override boolean to true
                override = true;
                conveyorDown();
                }
            else
                {
                // sets override boolean to false
                override = false;
                }
            }
    }

    // ENUM for the prepare to shoot switch
    private enum ShootState
        {
        INITIAL_UP, WAIT_FOR_POWER, INIT
        }

    ShootState shootState = ShootState.INIT;

    /**
     * prepares the ball in the storage system to be fired
     */
    public boolean prepareToShoot()
    {
        // SmartDashboard.putString("prepare conveoyr", shootState.toString());

        if (Hardware.ballCounter.getBallCount() > 0)
            {

            switch (shootState)
                {
                case INIT:
                    // init stuff if needed
                    shootState = ShootState.INITIAL_UP;
                    break;
                case INITIAL_UP:
                    // System.out.println("shoot RL" + this.shootRL.get());
                    // moves the balls up to the shootRL(or maybe upperRL) in preparation to be
                    // moved into the rotating shooter
                    // System.out.println("shoot RL: " + this.shootRL.get());
                    if (this.shootRL.get() && !preparedToFire)
                        {
                        // System.out.println("got shoot rl");
                        // balls is ready to shoot
                        preparedToFire = true;
                        // stop conveyor
                        setStorageControlState(ControlState.PASSIVE);
                        // start waiting for the shooter to speed up
                        shootState = ShootState.INIT;
                        return true;
                        }
                    else
                        {
                        // System.out.println("moving conveyor up");
                        // ball not ready
                        preparedToFire = false;
                        // move ball until ready
                        // System.out.println("up in prepared to fired");
                        setStorageControlState(ControlState.UP);
                        }
                    break;
                case WAIT_FOR_POWER:
                    // not currently in use
                    // deprecate based on current launch code
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
    boolean prevShootRL = false;

    // if a balls has been shot
    private static boolean shotBall = false;

    public void resetLoadValues()
    {
        stillShooting = false;
        prevShootRL = false;
        shotBall = false;
    }

    /**
     * this moves the ball into the launcher essentially shooting to the ball
     *
     * @return
     */
    public boolean loadToFire()
    {
        // System.out.println("loading balls aokfasklsDFSKNLknadsds");

        SmartDashboard.putBoolean("stillshooting", stillShooting);
        SmartDashboard.putBoolean("prevShootRL", prevShootRL);
        SmartDashboard.putBoolean("shotball", shotBall);
        if (stillShooting)
            {
            // if the ball is not longer in the conveyor system
            if (this.shootRL.get() == false && prevShootRL == true)
                {
                // we have shot a ball and are no longer shooting

                shotBall = true;
                stillShooting = false;
                prevShootRL = false;
                }
            }
        if (Hardware.ballCounter.getBallCount() > 0)
            {
            // if prepared to fire as notified true
            if (preparedToFire)
                {
                // System.out.println("loading");
                // if ball is proprer shoot position this is a second check
                if (this.shootRL.get() || prevShootRL == true)
                    {
                    prevShootRL = true;
                    // System.out.println("shooting ball");
                    // move ball up into the launcher
                    setStorageControlState(ControlState.UP);
                    if (!stillShooting)
                        {
                        // System.out.println("subtract in load to fire");
                        Hardware.ballCounter.subtractBall();
                        // extra check to see if there are balls left to continue the further states
                        if (Hardware.ballCounter.getBallCount() == 0)
                            {
                            prevShootRL = false;
                            setStorageControlState(ControlState.PASSIVE);
                            return true;
                            }
                        }
                    stillShooting = true;
                    }
                else if (shotBall)
                    {
                    // if we shot a ball we are not shoot
                    // reset shotBall info
                    stillShooting = false;
                    shotBall = false;
                    prevShootRL = false;
                    // stop moving conveyor
                    setStorageControlState(ControlState.PASSIVE);
                    // if we still have balls
                    if (Hardware.ballCounter.getBallCount() > 0)
                        {
                        // System.out.println(" preparing again");
                        // prepared the next ball
                        prepareToShoot();
                        return true;
                        }
                    }
                else if (Hardware.ballCounter.getBallCount() > 0 && this.shootRL.get() == false
                        && stillShooting == false)
                    ;
                    {
                    setStorageControlState(ControlState.UP);
                    }
                }
            }
        else
            {
            // if 0 balls stop moving conveyor
            state = ControlState.PASSIVE;
            return true;
            }
        return false;
    }

    /**
     * empties the storage through the intake
     *
     * @param button1
     * @param button2
     * @return
     */
    public boolean clearStorage(JoystickButton button1, JoystickButton button2)
    {

        if (Hardware.ballCounter.getBallCount() == 0)
            {
            // if no balls return true, stop conveyor
            setStorageControlState(ControlState.PASSIVE);
            return true;
            }
        else
            {
            // conveyor down and intake motors move out
            setStorageControlState(ControlState.DOWN);
            Hardware.intake.outtake(0);
            }

        return false;
    }

    /**
     * resets the State of the Storage Control controller
     *
     * @param ControlState
     *                         state - newState
     * @return - newly set value
     */
    public static ControlState setStorageControlState(ControlState newState)
    {
        return (state = newState);
    }

    /**
     * gets the State of the Storage COntrol controller
     *
     * @return - the present State value
     */
    public static ControlState getStorageControlState()
    {
        return (state);
    }

    // store info on the previous state of an RL
    private static boolean prevRL = false;

    // boolean used to store whether a ball is in the proper position to shoot
    private static boolean preparedToFire = false;

    // dont move conveyor speed
    final double HOLDING_SPEED = 0;
    // move up speed
    final double UP_SPEED = .3;
    // move down speed
    final double DOWN_SPEED = -.3;
    // amount needed to move JOYSTICK to override
    private final double JOYSTICK_DEADBAND_STORAGE = .3;

    }
