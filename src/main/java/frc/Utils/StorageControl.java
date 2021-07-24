package frc.Utils;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.Hardware.Hardware;
import frc.HardwareInterfaces.LightSensor;
import edu.wpi.first.wpilibj.Timer;

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
    Timer addBallTimer = null;

    SpeedControllerGroup conveyorMotors = null;

    public StorageControl(LightSensor intakeRL, LightSensor lowerRL, LightSensor upperRL, LightSensor shootRL,
            SpeedControllerGroup conveyorMotors, Timer addBallTimer)
        {
            this.intakeRL = intakeRL;
            this.lowerRL = lowerRL;
            this.upperRL = upperRL;
            this.shootRL = shootRL;
            this.conveyorMotors = conveyorMotors;
            this.addBallTimer = addBallTimer;
        }

    // control state to update what the conveyor should b doing
    public enum ControlState
        {
        INIT, PASSIVE, UP, DOWN
        }

    public boolean shooting = false;

    private static ControlState state = ControlState.INIT;

    private boolean prevShootRLCounting = false;

    private boolean addBallFirstTry = true;

    private double timeToDelayBeforeCountNewBall = .75;

    /**
     * state updated for the conveyor belt. This should always be running in teleop
     */
    public void storageControlState()
    {

        // these puts are not test code. Send important robot drjata to the robot for the
        // drivers to see
        SmartDashboard.putNumber("", Hardware.ballCounter.getBallCount());

        SmartDashboard.putBoolean("Green", Hardware.visionInterface.getDistanceFromTarget() <= 180);
        SmartDashboard.putNumber("distance from target", Hardware.visionInterface.getDistanceFromTarget());
        SmartDashboard.putString("conveyor state: ", state.toString());

        // System.out.println("storage state: " + state);
        // takes the current intake RL and previous intake RL states to add or subtract
        // balls when triggered

        //time check to switch addBallFirstTry back to true to allow for another bal to be counted
        if (this.addBallTimer.get() > timeToDelayBeforeCountNewBall)
            {
            addBallFirstTry = true;
            this.addBallTimer.reset();
            }

        if (this.intakeRL.isOn() && prevRL == false && Hardware.intake.intaking == true)
            {
            prevRL = true;
            if ((Hardware.intake.intaking && addBallFirstTry))// || (Hardware.intake.intaking && this.addBallTimer.get() > .5))
                {
                // System.out.println("adding");
                Hardware.ballCounter.addBall();
                addBallFirstTry = false;
                this.addBallTimer.reset();
                this.addBallTimer.start();
                }
            else if (Hardware.intake.outtaking)
                {
                // System.out.println("subtracting");
                Hardware.ballCounter.subtractBall();
                }
            }

        //TODO @ANE if ballcount still broken look at upperRL and factor in
        //when determining whether to subtract balls
        if (!this.shootRL.isOn() && prevShootRLCounting == true)
            {
            prevShootRLCounting = false;
            // if (Hardware.launchButton.get() || Hardware.launchOverrideButton.get())
            // {
            Hardware.ballCounter.subtractBall();
            // }
            }

        if (!this.intakeRL.isOn())
            {
            prevRL = false;
            }
        if (this.shootRL.isOn())
            {
            prevShootRLCounting = true;
            }

        // System.out.println("conveyor state: " + state);

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
                if (!override && !Hardware.launchOverrideButton.get())
                    {
                    this.conveyorMotors.set(HOLDING_SPEED);
                    // Hardware.conveyorMotorGroup.set(HOLDING_SPEED);
                    }
                if (!Hardware.launchButton.get() && !Hardware.launchOverrideButton.get())
                    {
                    this.resetLoadValues();
                    }

                // gets data from the inake Redlight to add or subtract balls from our internal
                // ball count when a ball enters or leaves the system through the bottem

                break;
            case UP:
                // move up towards launcher
                // System.out.println("conveyor up");
                if (Hardware.launchButton.get() || Hardware.launchOverrideButton.get() || shooting)
                    {
                    conveyorUpShoot();
                    }
                else
                    {
                    conveyorUp();
                    }
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

    private boolean prevLowerRL = false;

    private boolean prevUpperRL = false;

    boolean isMovingNewBall = false;

    /**
     * controls the conveyor belt if the robot is intaking balls. Note: Must be
     * called continuously in Teleop and auto
     */
    public void intakeStorageControl()
    {
        // System.out.println("intaking: " + Hardware.intake.intaking);
        // System.out.println(getStorageControlState());

        //
        // @TODO
        // -McGee, 7/15/2021
        // Whats wrong:
        // Conveyor goes UP with no balls present, should be DOWN
        // Conveyor goes DOWN when detecting first ball in intake, should go UP
        // Maybe conveyor is reversed?

        if (Hardware.intake.intaking == true /* && this.shootRL.isOn() == false */ /* && !doingATHninginLoad */)
            {

            // There are no balls in the lower conveyor
            if (this.intakeRL.isOn() == false && this.lowerRL.isOn() == false && isMovingNewBall == false)
                {
                setStorageControlState(ControlState.DOWN);
                }
            // there is a ball in the lower conveyor
            else if (this.lowerRL.isOn() == true)
                {

                if (isMovingNewBall && prevLowerRL == false)
                    {
                    isMovingNewBall = false;
                    }
                prevLowerRL = true;
                // A new ball is available, while we have a ball now
                //@ANE if balls get stuck when intaking a thord ball look here first
                if (this.intakeRL.isOn() == true)
                    {
                    setStorageControlState(ControlState.UP);
                    isMovingNewBall = true;
                    }
                // A new ball is NOT available, keep the current ball where it is
                else if (isMovingNewBall = false)
                    {
                    setStorageControlState(ControlState.PASSIVE);
                    }
                }
            // Intake RL has detected a new ball, and there is no ball in the lower conveyor
            else if (this.intakeRL.isOn() == true)
                {
                setStorageControlState(ControlState.UP);
                isMovingNewBall = true;
                }
            // Check if we were moving a new ball. If so, move it up. If not, run the intake "down" constantly
            else
                {
                prevLowerRL = false;

                if (isMovingNewBall == true)
                    {
                    setStorageControlState(ControlState.UP);
                    }
                else if (this.lowerRL.isOn() == false)
                    {
                    setStorageControlState(ControlState.DOWN);
                    }
                else
                    {
                    setStorageControlState(ControlState.PASSIVE);
                    }
                }

            // if ((this.intakeRL.isOn() == true || this.getPrevIntakeRL() == true) && this.shootRL.isOn() == false)
            //     {
            //     // if the intake Rl
            //     // is true or the previous intake was true
            //     this.setPrevIntakeRL(true);
            //     //
            //     System.out.println("going up in intakeStorageControl");
            //     setStorageControlState(ControlState.UP);
            //     }

            // if (this.intakeRL.isOn() == false && this.getPrevIntakeRL() == false
            //         && this.lowerRL.isOn() == false /* && prevLowerRL == false && prevUpperRL == false */)
            //     {
            //     // is all false go down
            //     prevLowerRL = false;
            //     this.setPrevIntakeRL(false);
            //     // System.out.println("down in intakeStorageControl");
            //     setStorageControlState(ControlState.DOWN);

            //     }

            // //@TODO @ANE TRUE TRUE TRUE not covered
            // if (this.intakeRL.isOn() == false && this.getPrevIntakeRL() == false && this.lowerRL.isOn() == true)
            //     {
            //     prevLowerRL = true;
            //     // if only the lower is true go passive
            //     // System.out.println("ball hit lower setting passive");
            //     setStorageControlState(ControlState.PASSIVE);
            //     this.setPrevIntakeRL(false);
            //     }

            // if (this.intakeRL.isOn() == false && this.lowerRL.isOn() == true)
            //     {
            //     // if intake is is false and ball has hit lower stop moving
            //     // System.out.println("ball hit lower setting passive, not prev if");
            //     setStorageControlState(ControlState.PASSIVE);
            //     this.setPrevIntakeRL(false);
            //     }
            }
        else if (Hardware.intake.outtaking == true)
            {
            setStorageControlState(ControlState.DOWN);
            }
        else if (Hardware.launchButton.get() == false && !Hardware.launchOverrideButton.get()
                && !Hardware.launchButton.get())
            {
            this.setPrevIntakeRL(false);
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

    public void conveyorUpShoot()
    {
        this.conveyorMotors.set(UP_SPEED_SHOOT);
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
        else
            {
            // sets override boolean to false
            override = false;
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
        // SmartDashboard.putString("prepare conveyor", shootState.toString());

        if (Hardware.ballCounter.getBallCount() > 0 || true)//TODO
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
                    if (this.shootRL.isOn())
                        {
                        // System.out.println("got shoot rl");
                        // balls is ready to shoot
                        preparedToFire = false;
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

    private boolean doingATHninginLoad = false;
    private boolean tellOtherTHingwearedoingTHing = true;

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
    public boolean loadToFire()//TODO intake
    {
        // System.out.println("loading balls aokfasklsDFSKNLknadsds");
        //if (Hardware.ballCounter.getBallCount() == 0)
        //   {
        //   setStorageControlState(ControlState.PASSIVE);
        //   return true;
        //   }
        // else
        //     {
        setStorageControlState(ControlState.UP);
        //    }

        return false;
        // if (tellOtherTHingwearedoingTHing)
        // {
        // doingATHninginLoad = true;
        // }
        // SmartDashboard.putBoolean("stillshooting", stillShooting);
        // SmartDashboard.putBoolean("prevShootRL", prevShootRL);
        // SmartDashboard.putBoolean("shotball", shotBall);
        // if (stillShooting)
        // {
        // // if the ball is not longer in the conveyor system
        // if (this.shootRL.isOn() == false && prevShootRL == true)
        // {
        // // we have shot a ball and are no longer shooting

        // shotBall = true;
        // stillShooting = false;
        // prevShootRL = false;
        // // Hardware.ballCounter.subtractBall();
        // }
        // }
        // if (Hardware.ballCounter.getBallCount() > 0)
        // {
        // // if prepared to fire as notified true
        // if (preparedToFire || this.shootRL.isOn())
        // {
        // System.out.println("loading");
        // // if ball is proprer shoot position this is a second check
        // if (this.shootRL.isOn() || prevShootRL == true)
        // {
        // this.prevShootRL = true;
        // // System.out.println("shooting ball");
        // // move ball up into the launcher
        // setStorageControlState(ControlState.UP);
        // if (!stillShooting)
        // {
        // // System.out.println("subtract in load to fire");
        // // Hardware.ballCounter.subtractBall();
        // // extra check to see if there are balls left to continue the further states
        // if (Hardware.ballCounter.getBallCount() == 0)
        // {
        // prevShootRL = false;
        // setStorageControlState(ControlState.PASSIVE);
        // doingATHninginLoad = false;
        // tellOtherTHingwearedoingTHing = true;
        // return true;
        // }
        // }
        // stillShooting = true;
        // }
        // if (shotBall)
        // {
        // // Hardware.ballCounter.subtractBall();
        // // if we shot a ball we are not shoot
        // // reset shotBall info
        // stillShooting = false;
        // shotBall = false;
        // prevShootRL = false;
        // // stop moving conveyor
        // setStorageControlState(ControlState.PASSIVE);
        // // if we still have balls
        // if (Hardware.ballCounter.getBallCount() > 0)
        // {
        // // System.out.println(" preparing again");
        // // prepared the next ball
        // prepareToShoot();
        // return true;
        // }
        // }
        // else if (Hardware.ballCounter.getBallCount() > 0 && this.shootRL.isOn() ==
        // false
        // && stillShooting == false)
        // ;
        // {
        // setStorageControlState(ControlState.UP);
        // }
        // }
        // // if (this.shootRL.isOn() == true && prevShootRL == true)
        // // {
        // // prevShootRL = false;
        // // Hardware.ballCounter.subtractBall();
        // // }
        // }
        // else
        // {
        // // if 0 balls stop moving conveyor
        // state = ControlState.PASSIVE;
        // return true;
        // }
        // return false;
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
     * gets the State of the Storage Control controller
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
    final double UP_SPEED = .5;//.22;// .11 @ANE
    final double UP_SPEED_SHOOT = 1.0;//.7;// .25
    // move down speed
    final double DOWN_SPEED = -.6;//-.45;// -.3
    // amount needed to move JOYSTICK to override
    private final double JOYSTICK_DEADBAND_STORAGE = .3;

    }
