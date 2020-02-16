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
 * code to control the 2020 launcher. includes the control loops and code for launching and preparing to launch
 *
 *
 *@author Conner McKevitt
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

    //enum to the main shooting state
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
                //until shoot button dont shoot
                //must be held down to shoot multiple balls
                if (shootButton.get())
                    {
                    shootState = ShootState.CHARGE;
                    }
                break;
            case CHARGE:
                //starts charging the launcher and prepares the balls in the conveyor
                if (prepareToShoot(isClose, teleop))
                    {
                    launcherReadyTemp = true;
                    }
                if (conveyorReadyTemp || Hardware.storage.prepareToShoot())
                    {
                    conveyorReadyTemp = true;
                    }
                //if both are prepared
                if (conveyorReadyTemp && launcherReadyTemp)
                    {
                    conveyorReadyTemp = false;
                    launcherReadyTemp = false;
                    shootState = ShootState.LAUNCH;
                    }
                break;
            case LAUNCH:
                //loads a ball and shoots it
                if (Hardware.storage.loadToFire())
                    {
                    //back to passive
                    shootState = ShootState.PASSIVE;
                    }
                break;
            default:

                break;
            }

    }

    //enum for shooting in autp
    private enum ShootStateAuto
        {
        CHARGE, LAUNCH, FINISH
        }

    public ShootStateAuto shootStateAuto = ShootStateAuto.CHARGE;

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
        // SmartDashboard.putString("shootStateAuto: ", shootStateAuto.toString());
        // SmartDashboard.putBoolean("launcer Ready", launcherReadyTemp);
        // SmartDashboard.putBoolean("conveyor ready", conveyorReadyTemp);
        // SmartDashboard.putBoolean("load ready", loadReadyTemp);

        //if more than 0 balls
        if (Hardware.ballCounter.getBallCount() >= 0)
            {
            // System.out.println("Shoot State Launcher: " + shootStateAuto);
            switch (shootStateAuto)
                {
                case CHARGE:
                    // sets the RPM and makes sure that the conveyor is correct
                    if (prepareToShoot(isClose, auto))
                        {
                        // System.out.println("Setting Launcher");
                        launcherReadyTemp = true;
                        }
                    //prepares the balls in the storage system
                    if (conveyorReadyTemp || Hardware.storage.prepareToShoot()
                            || Hardware.ballCounter.getBallCount() == 0)
                        {
                        // System.out.println("prepared to shoot");
                        conveyorReadyTemp = true;
                        }
                    // both a launcher and storage are ready
                    if (launcherReadyTemp && conveyorReadyTemp)
                        {
                        launcherReadyTemp = false;
                        conveyorReadyTemp = false;
                        shootStateAuto = ShootStateAuto.LAUNCH;
                        // System.out.println("State Launch");
                        }
                    break;
                case LAUNCH:
                    if (Hardware.ballCounter.getBallCount() > 1)
                        {
                        // System.out.println("loading to fire");
                        //keeps launcher moving at speed
                        this.prepareToShoot(isClose, auto);
                        //loads a ball into shooter
                        if (Hardware.storage.loadToFire())
                            {
                            loadReadyTemp = true;
                            }
                        //if ball has shot charge next ball
                        if (loadReadyTemp)
                            {
                            loadReadyTemp = false;
                            shootStateAuto = ShootStateAuto.CHARGE;
                            }
                        }
                    else if (Hardware.ballCounter.getBallCount() == 1)
                        {
                        this.prepareToShoot(isClose, auto);
                        // System.out.println("loading to fire last ball");
                        //load ball to shoot
                        if (Hardware.storage.loadToFire())
                            {
                            loadReadyTemp = true;
                            // System.out.println("load temp set");
                            }
                        //if shot reset stuff
                        if (loadReadyTemp)
                            {
                            loadReadyTemp = false;
                            return true;
                            }
                        }
                    //if 0 balls reset stuff
                    else
                        {
                        // System.out.println("Returning true!!!!!!");
                        return true;
                        }
                    break;
                default:
                    break;
                }
            }
        return false;
    }

    //stores if the launcher is at speed
    public boolean spedUp = false;
    //stores is aligned by vision
    public boolean aligned = false;
    // speed in inches per second

    /**
     * prepares the launcher motor to shoot by settingthe RPM to either the close
     * value or the far value
     *
     * @param close
     * @return
     */
    public boolean prepareToShoot(boolean isClose, boolean inAuto)
    {
        //checks the wanted position from the target
        if (isClose)
            {
            //align to target
            if (inAuto || Hardware.visionDriving.driveToTarget(CLOSE_DISTANCE, VISION_OVERRIDE, VISION_SPEED))
                {
                //aligned to target
                aligned = true;
                }
            //checks the robot year and set appropriate launcher speed
            if (Hardware.robotIdentity == Hardware.yearIdentifier.CurrentYear)
                {
                //if in auto ignore the drive station change
                if (inAuto)
                    {
                    if (this.encoder.setRPM(RPM_CLOSE_2020, this.firingMotors))
                        {
                        //has sped up
                        spedUp = true;
                        }
                    }
                else
                    {
                    //allow drives to change speed
                    if (this.encoder.setRPM(RPM_CLOSE_2020 + (Hardware.rightOperator.getZ() * DRIVER_CHANGE_ALLOWANCE),
                            this.firingMotors))
                        {
                        //has sped up
                        spedUp = true;
                        }
                    }
                }
            else
                {
                if (inAuto)
                    {
                    if (this.encoder.setRPM(RPM_CLOSE_2019, this.firingMotors))
                        {
                        spedUp = true;
                        }
                    }
                else
                    {
                    if (this.encoder.setRPM(RPM_CLOSE_2019 + (Hardware.rightOperator.getZ() * DRIVER_CHANGE_ALLOWANCE),
                            this.firingMotors))
                        {
                        spedUp = true;
                        }
                    }
                }
            }
        //if far away
        else
            {
            //align to target
            if (inAuto || Hardware.visionDriving.driveToTarget(FAR_DISTANCE, VISION_OVERRIDE, VISION_SPEED))
                {
                //aligned to target
                aligned = true;
                }
            //checks the robot year and set appropriate launcher speed
            if (Hardware.robotIdentity == Hardware.yearIdentifier.CurrentYear)
                {
                //if in auto ignore the drive station change
                if (inAuto)
                    {
                    if (this.encoder.setRPM(RPM_FAR_2020, this.firingMotors))
                        {
                        //has sped up
                        spedUp = true;
                        }
                    }
                else
                    {
                    //allow drives to change speed
                    if (this.encoder.setRPM(RPM_FAR_2020 + (Hardware.rightOperator.getZ() * DRIVER_CHANGE_ALLOWANCE),
                            this.firingMotors))
                        {
                        //has sped up
                        spedUp = true;
                        }
                    }
                }
            else
                {
                if (inAuto)
                    {
                    if (this.encoder.setRPM(RPM_FAR_2019, this.firingMotors))
                        {
                        spedUp = true;
                        }
                    }
                else
                    {
                    if (this.encoder.setRPM(RPM_FAR_2019 + (Hardware.rightOperator.getZ() * DRIVER_CHANGE_ALLOWANCE),
                            this.firingMotors))
                        {
                        spedUp = true;
                        }
                    }
                }
            }
        if (aligned && spedUp)
            {
            //if everything is done return true
            aligned = false;
            spedUp = false;
            return true;
            }
        return false;
    }

    /**
     * uncharges the shooter
     * @return if uncharged
     */
    public boolean unchargeShooter()
    {
        this.firingMotors.set(0);
        if (this.firingMotors.get() == 0)
            {
            return true;
            }
        return false;
    }

    private enum Position
        {
        FAR, CLOSE, NULL
        }

    Position position = Position.NULL;

    /**
     *get distance from the visino camera to determine the best position to shoot from based off of where we are located on the field
     * @return the closest shooting position
     */
    public Position getClosestPosition()
    {
        //far if farther than far
        if (Hardware.visionInterface.getDistanceFromTarget() > FAR_DISTANCE)
            {
            return Position.FAR;
            }
        //close if closer than close
        else if (Hardware.visionInterface.getDistanceFromTarget() < CLOSE_DISTANCE)
            {
            return Position.CLOSE;
            }
        //far is farther than close but not too close to close
        else if (Hardware.visionInterface
                .getDistanceFromTarget() > (CLOSE_DISTANCE + ((FAR_DISTANCE - CLOSE_DISTANCE) / 2)))
            {
            return Position.FAR;
            }
        //close if not farther than far or closer than close pr farther than close but not too close to close
        else
            {
            return Position.CLOSE;
            }

    }

    private double closeOffset;
    private double farOffset;

    enum MoveState
        {
        INIT, DRIVE, ALIGN
        }

    MoveState moveState = MoveState.INIT;

    /**
     *Move the robot to the closest shootin position either close or far.
     *
     * @param position
     * @return at position
     */
    public boolean moveRobotToPosition(Position position)
    {

        switch (moveState)
            {
            case INIT:
                //find the off set from the positions
                farOffset = Hardware.visionInterface.getDistanceFromTarget() - FAR_DISTANCE;
                closeOffset = Hardware.visionInterface.getDistanceFromTarget() - CLOSE_DISTANCE;
                moveState = MoveState.DRIVE;
                break;
            case DRIVE:
                //drive straight the the proper distance
                if (position == Position.FAR)
                    {
                    //filter for negative distances
                    if (farOffset > 0)
                        {
                        if (Hardware.drive.driveStraightInches(Math.abs(farOffset), DRIVE_STRAIGHT_SPEED, 0, true))
                            {
                            moveState = MoveState.ALIGN;
                            }
                        }
                    else
                        {
                        if (Hardware.drive.driveStraightInches(Math.abs(farOffset), -DRIVE_STRAIGHT_SPEED, 0, true))
                            {
                            moveState = MoveState.ALIGN;
                            }
                        }
                    }
                else if (position == Position.CLOSE)
                    {
                    if (closeOffset > 0)
                        {
                        if (Hardware.drive.driveStraightInches(Math.abs(closeOffset), DRIVE_STRAIGHT_SPEED, 0, true))
                            {
                            moveState = MoveState.ALIGN;
                            }
                        }
                    else
                        {
                        if (Hardware.drive.driveStraightInches(Math.abs(closeOffset), -DRIVE_STRAIGHT_SPEED, 0, true))
                            {
                            moveState = MoveState.ALIGN;
                            }
                        }
                    }
                break;
            case ALIGN:
                //realign to the target using vision
                if (Hardware.visionDriving.alignToTarget())
                    {
                    moveState = MoveState.INIT;
                    //done
                    return true;
                    }
                break;
            default:
                moveState = MoveState.INIT;
            }

        return false;
    }

    /**
     * return the RPM to set based off distance recieved from the camera
     * TODO
     * @param distance
     * @return
     */
    public double getRPMPerDistance(double distance)
    {
        return 0;
    }

    //temporary store if the launcher is ready
    private boolean launcherReadyTemp = false;

    //temporary store if the conveyor is ready
    private boolean conveyorReadyTemp = false;

    //temporary store if the ball has been shot NOTE: this may be deprecated
    private boolean loadReadyTemp = false;

    //if called in auto
    private boolean auto = true;

    //if called in teleop
    private boolean teleop = false;

    //far RPM to 2020 shooter
    private static final double RPM_FAR_2020 = 3500;

    //close RPM for the 2020 shooter
    private static final double RPM_CLOSE_2020 = 2800;

    //far RPM for the 2019 robot
    private static final double RPM_FAR_2019 = 900;

    //close for the 2019 robot
    private static final double RPM_CLOSE_2019 = 100;

    //speed to drive straight
    private static final double DRIVE_STRAIGHT_SPEED = .4;

    //max RPM the drivers are allowed to change the RPM
    private static final double DRIVER_CHANGE_ALLOWANCE = 100;

    //wanted distance to shoot close
    private static int CLOSE_DISTANCE = 35;

    //wanted distance to shoot far(the white start line)
    private static int FAR_DISTANCE = 120;

    //is override vision with ultrasonic
    private static boolean VISION_OVERRIDE = true;

    //minimum speed to move with the camera
    private static double VISION_SPEED = .3;

    }
