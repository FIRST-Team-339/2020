package frc.Utils;

import frc.Hardware.Hardware;
import frc.Hardware.Hardware.yearIdentifier;
import frc.HardwareInterfaces.KilroyEncoder;
import frc.Utils.StorageControl.ControlState;
import frc.robot.Teleop;
import frc.vision.LimelightInterface.LedMode;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Relay.Value;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * code to control the 2020 launcher. includes the control loops and code for
 * launching and preparing to launch
 *
 *
 * @author Conner McKevitt
 */

public class Launcher
    {

    // private motortype shootingMotor = null;
    // private motortype intakeMotor = null;
    SpeedControllerGroup firingMotors = null;
    KilroyEncoder encoder = null;

    public Launcher(SpeedControllerGroup firingMotors, KilroyEncoder encoder)
        {
            this.firingMotors = firingMotors;
            this.encoder = encoder;
        }

    // enum to the main shooting state
    private enum ShootState
        {
        PASSIVE, CHARGE, LAUNCH
        }

    public ShootState shootState = ShootState.PASSIVE;

    public Position targetPosition = Position.NULL;

    public boolean shootingBalls = false;

    /**
     * in case you could not guess this function will shoot balls at whatever you
     * desire. whether it be the target or pesky those builders who have yet to
     * finish the actual launcher
     */
    public void shootBalls(JoystickButton shootButton, JoystickButton overrideButton)
    {

        // System.out.println("state: " + shootState);
        if (!shootButton.get() && !overrideButton.get())
            {
            // if (this.moveRobotToPosition(this.getClosestPosition()))
            // { conveyorReadyTemp = false;
            hoodReadyTemp = false;
            launcherReadyTemp = false;
            positionReadyTemp = false;
            shootingBalls = false;
            this.shootState = ShootState.PASSIVE;
            this.moveState = MoveState.INIT;
            this.unchargeShooter();
            Hardware.storage.resetLoadValues();
            // }
            }
        // System.out.println("shootState: " + shootState);
        if (!overrideButton.get())
            {
            switch (shootState)
                {
                case PASSIVE:
                    if (Hardware.intake.usingVisionIntake == false)
                        {
                        Teleop.setDisableTeleOpDrive(false);
                        Hardware.visionInterface.setLedMode(LedMode.OFF);
                        }
                    // until shoot button dont shoo t
                    // must be held down to shoot multiple balls
                    if (shootButton.get())
                        {

                        shootingBalls = true;
                        Teleop.setDisableTeleOpDrive(true);
                        Hardware.visionInterface.setLedMode(LedMode.PIPELINE);
                        Hardware.cameraServo.setCameraAngleUp();
                        // if (this.moveRobotToPosition(this.getClosestPosition()))
                        if (Hardware.visionInterface.getHasTargets())
                            {
                            // System.out.println("postion: " + this.getClosestPosition());
                            targetPosition = this.getClosestPosition();
                            this.shootState = ShootState.CHARGE;
                            }
                        // }
                        }
                    else
                        {
                        conveyorReadyTemp = false;
                        hoodReadyTemp = false;
                        launcherReadyTemp = false;
                        positionReadyTemp = false;
                        }
                    break;
                case CHARGE:
                    Hardware.storage.shooting = true;
                    Teleop.setDisableTeleOpDrive(true);
                    Hardware.visionInterface.setLedMode(LedMode.PIPELINE);
                    Hardware.visionInterface.setPipeline(0);
                    // SmartDashboard.putBoolean("conveyor: ", conveyorReadyTemp);
                    // SmartDashboard.putBoolean("hood: ", hoodReadyTemp);
                    // SmartDashboard.putBoolean("launcher: ", launcherReadyTemp);
                    // SmartDashboard.putBoolean("position: ", positionReadyTemp);
                    // SmartDashboard.putString("wanted position: ", targetPosition.toString());
                    // starts charging the launcher and prepares the balls in the conveyor
                    if (moveRobotToPosition(targetPosition) /*&& Hardware.hoodControl.moveToPosition(targetPosition)*/)
                        {
                        Hardware.visionDriving.alignToTarget();
                        positionReadyTemp = true;
                        }

                    if (this.prepareToShoot())
                        {
                        launcherReadyTemp = true;
                        }

                    if (Hardware.storage.prepareToShoot())
                        {
                        conveyorReadyTemp = true;
                        }
                    // if both are prepared
                    // TODO hood
                    if (conveyorReadyTemp && positionReadyTemp && launcherReadyTemp)
                        {
                        conveyorReadyTemp = false;
                        hoodReadyTemp = false;
                        launcherReadyTemp = false;
                        positionReadyTemp = false;
                        this.shootState = ShootState.LAUNCH;
                        }
                    break;
                case LAUNCH:
                    Hardware.visionDriving.alignToTarget();
                    this.prepareToShoot();
                    // loads a ball and shoots it
                    if (Hardware.storage.loadToFire())
                        {
                        // back to passive
                        Hardware.storage.shooting = false;
                        this.shootState = ShootState.PASSIVE;

                        }
                    break;
                default:

                    break;
                }
            }
        else
            {
            // override fire methods
            Hardware.visionInterface.setLedMode(LedMode.PIPELINE);
            StorageControl.setStorageControlState(ControlState.UP);
            this.encoder.setRPM(this.getRPMPerDistance(Hardware.visionInterface.getDistanceFromTarget()),
                    this.firingMotors);

            }

    }

    private enum ShootStateBasic
        {
        PASSIVE, CHARGE, LAUNCH
        }

    public ShootStateBasic shootStateBasic = ShootStateBasic.PASSIVE;

    // enum for shooting in auto

    enum ShootPosition

        {
        FAR, FARTHER
        }

    ShootPosition shootPosition = ShootPosition.FAR;

    private enum ShootStateAuto
        {
        CHARGE, LAUNCH, FINISH
        }

    public ShootStateAuto shootStateAuto = ShootStateAuto.CHARGE;
    boolean movedHood = false;
    Timer jankyTimer = new Timer();

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
    public boolean shootBallsAuto()
    {
        // System.out.println("Balls: " + Hardware.ballcounter.getBallCount());
        // SmartDashboard.putString("shootStateAuto: ", shootStateAuto.toString());
        // SmartDashboard.putBoolean("launcer Ready", launcherReadyTemp);
        // SmartDashboard.putBoolean("conveyor ready", conveyorReadyTemp);
        // SmartDashboard.putBoolean("load ready", loadReadyTemp);

        // if more than 0 balls
        if (Hardware.ballCounter.getBallCount() > 0)
            {
            System.out.println("Shoot State Launcher: " + shootStateAuto);
            switch (shootStateAuto)
                {
                case CHARGE:

                    Hardware.storage.shooting = true;
                    Hardware.visionDriving.alignToTarget();

                    if (this.prepareToShoot())
                        {
                        System.out.println("&&&&&&&&&");
                        launcherReadyTemp = true;
                        }

                    if (Hardware.storage.prepareToShoot())
                        {
                        System.out.println("###########");
                        conveyorReadyTemp = true;
                        }
                    // if both are prepared
                    if (conveyorReadyTemp && launcherReadyTemp)
                        {
                        conveyorReadyTemp = false;
                        hoodReadyTemp = false;
                        launcherReadyTemp = false;
                        positionReadyTemp = false;
                        this.shootStateAuto = ShootStateAuto.LAUNCH;
                        }
                    break;
                case LAUNCH:
                    if (Hardware.ballCounter.getBallCount() > 0)
                        {
                        // System.out.println("loading to fire");
                        // keeps launcher moving at speed
                        this.prepareToShoot();
                        // loads a ball into shooter

                        if (Hardware.storage.loadToFire())
                            {
                            loadReadyTemp = true;
                            }
                        // if ball has shot charge next ball
                        if (loadReadyTemp && Hardware.ballCounter.getBallCount() > 0)
                            {
                            loadReadyTemp = false;
                            shootStateAuto = ShootStateAuto.CHARGE;
                            }
                        else if (loadReadyTemp && Hardware.ballCounter.getBallCount() == 0)
                            {
                            Hardware.storage.shooting = false;
                            loadReadyTemp = false;
                            return true;
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

    public void resetShootTemps()
    {
        positionReadyTemp = false;
        launcherReadyTemp = false;
        conveyorReadyTemp = false;
        hoodReadyTemp = false;
    }

    // stores if the launcher is at speed
    public boolean spedUp = false;
    // stores is aligned by vision
    public boolean aligned = false;
    // speed in inches per second

    /**
     * prepares the launcher motor to shoot by settingthe RPM to either the close
     * value or the far value
     *
     * @param position
     *                     wanted position
     * @param inAuto
     * @deprecated is in AUto
     */
    public boolean prepareToShoot(Position position, boolean inAuto)
    {
        // checks the wanted position from the target
        if (position == Position.CLOSE)
            {
            // align to target
            if (inAuto || Hardware.visionDriving.driveToTarget(CLOSE_DISTANCE, VISION_OVERRIDE, VISION_SPEED))
                {
                // aligned to target
                aligned = true;
                }
            // checks the robot year and set appropriate launcher speed
            if (Hardware.robotIdentity == Hardware.yearIdentifier.CurrentYear)
                {
                // if in auto ignore the drive station change
                if (inAuto)
                    {
                    if (this.encoder.setRPM(RPM_CLOSE_2020, this.firingMotors))
                        {
                        // has sped up
                        spedUp = true;
                        }
                    }
                else
                    {
                    // allow drives to change speed
                    if (this.encoder.setRPM(RPM_CLOSE_2020 + (Hardware.rightOperator.getZ() * DRIVER_CHANGE_ALLOWANCE),
                            this.firingMotors))
                        {
                        // has sped up
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
        // if far away
        else
            {
            // align to target
            if (inAuto || Hardware.visionDriving.driveToTarget(FAR_DISTANCE, VISION_OVERRIDE, VISION_SPEED))
                {
                // aligned to target
                aligned = true;
                }
            // checks the robot year and set appropriate launcher speed
            if (Hardware.robotIdentity == Hardware.yearIdentifier.CurrentYear)
                {
                // if in auto ignore the drive station change
                if (inAuto)
                    {
                    if (this.encoder.setRPM(RPM_FAR_2020, this.firingMotors))
                        {
                        // has sped up
                        spedUp = true;
                        }
                    }
                else
                    {
                    // allow drives to change speed
                    if (this.encoder.setRPM(RPM_FAR_2020 + (Hardware.rightOperator.getZ() * DRIVER_CHANGE_ALLOWANCE),
                            this.firingMotors))
                        {
                        // has sped up
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
            // if everything is done return true
            aligned = false;
            spedUp = false;
            return true;
            }
        return false;
    }

    /**
     * prepares the launcher motor to shoot by scaling the RPM based off the
     * distance away from the target
     *
     */
    public boolean prepareToShoot()
    {
        // System.out.println("preparing to shoot");
        if (this.encoder.setRPM(this.getRPMPerDistance(Hardware.visionInterface.getDistanceFromTarget()),
                this.firingMotors))
            {
            // has sped up
            spedUp = true;
            }

        // TODO
        // if (Hardware.visionDriving.alignToTarget())
        // {
        // aligned = true;
        // }
        if (Hardware.robotIdentity == yearIdentifier.CurrentYear)
            {
            if (/* aligned && */spedUp)// TODO
                {
                // if everything is done return true
                aligned = false;
                spedUp = false;
                return true;
                }
            }
        return false;
    }

    /**
     * prepares the launcher motor to shoot by settingthe RPM to either the close
     * value or the far value via vision
     *
     * @param isClose
     *                    shoot close
     * @param inAuto
     *                    is in auto
     * @return
     * @deprecated
     */
    public boolean prepareToShoot(boolean isClose, boolean inAuto)
    {
        // checks the wanted position from the target
        if (isClose)
            {
            // align to target
            if (inAuto || Hardware.visionDriving.driveToTarget(CLOSE_DISTANCE, VISION_OVERRIDE, VISION_SPEED))
                {
                // aligned to target
                aligned = true;
                }
            // checks the robot year and set appropriate launcher speed
            if (Hardware.robotIdentity == Hardware.yearIdentifier.CurrentYear)
                {
                // if in auto ignore the drive station change
                if (inAuto)
                    {
                    if (this.encoder.setRPM(RPM_CLOSE_2020, this.firingMotors))
                        {
                        // has sped up
                        spedUp = true;
                        }
                    }
                else
                    {
                    // allow drives to change speed
                    if (this.encoder.setRPM(RPM_CLOSE_2020 + (Hardware.rightOperator.getZ() * DRIVER_CHANGE_ALLOWANCE),
                            this.firingMotors))
                        {
                        // has sped up
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
        // if far away
        else
            {
            // align to target
            if (inAuto || Hardware.visionDriving.driveToTarget(FAR_DISTANCE, VISION_OVERRIDE, VISION_SPEED))
                {
                // aligned to target
                aligned = true;
                }
            // checks the robot year and set appropriate launcher speed
            if (Hardware.robotIdentity == Hardware.yearIdentifier.CurrentYear)
                {
                // if in auto ignore the drive station change
                if (inAuto)
                    {
                    if (this.encoder.setRPM(RPM_FAR_2020, this.firingMotors))
                        {
                        // has sped up
                        spedUp = true;
                        }
                    }
                else
                    {
                    // allow drives to change speed
                    if (this.encoder.setRPM(RPM_FAR_2020 + (Hardware.rightOperator.getZ() * DRIVER_CHANGE_ALLOWANCE),
                            this.firingMotors))
                        {
                        // has sped up
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
            // if everything is done return true
            aligned = false;
            spedUp = false;
            return true;
            }
        return false;
    }

    /**
     * uncharges the shooter
     *
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

    public enum Position
        {
        FAR, CLOSE, NULL
        }

    public static Position position = Position.NULL;

    /**
     * get distance from the visino camera to determine the best position to shoot
     * from based off of where we are located on the field
     *
     * @return the closest shooting position
     */
    public Position getClosestPosition()
    {
        // far if farther than far
        if (Hardware.visionInterface.getDistanceFromTarget() > FAR_DISTANCE)
            {
            return Position.FAR;
            }
        // close if closer than close
        else if (Hardware.visionInterface.getDistanceFromTarget() < CLOSE_DISTANCE)
            {
            return Position.CLOSE;
            }
        // far is farther than close but not too close to close
        else if (Hardware.visionInterface
                .getDistanceFromTarget() > (CLOSE_DISTANCE + ((FAR_DISTANCE - CLOSE_DISTANCE) / 2)))
            {
            return Position.FAR;
            }
        // close if not farther than far or closer than close pr farther than close but
        // not too close to close
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

    double distanceFromTarget;

    /**
     * Move the robot to the closest shootin position either close or far.
     *
     * @param position
     * @return at position
     */
    public boolean moveRobotToPosition(Position position)
    {
        // System.out.println(position.toString());
        // System.out.println("move state: " + moveState);
        switch (moveState)
            {
            case INIT:
                // find the off set from the positions
                // farOffset = Hardware.visionInterface.getDistanceFromTarget() - FAR_DISTANCE;
                // closeOffset = Hardware.visionInterface.getDistanceFromTarget() -
                // CLOSE_DISTANCE;
                this.moveState = MoveState.ALIGN;
                break;
            case DRIVE:
                Hardware.visionInterface.updateValues();
                distanceFromTarget = Hardware.visionInterface.getDistanceFromTarget();
                farOffset = distanceFromTarget - FAR_DISTANCE;
                closeOffset = distanceFromTarget - CLOSE_DISTANCE;

                if (position == Position.FAR)
                    {
                    if (Math.abs(farOffset) < MOVE_DISTANCE_DEADBAND)
                        {
                        this.moveState = MoveState.INIT;
                        Hardware.drive.drive(0, 0);
                        // System.out.println("%%%%%%%%%%%%%%%%%%%%%");
                        return true;
                        }
                    }
                if (position == Position.CLOSE)
                    {
                    if (Math.abs(closeOffset) < MOVE_DISTANCE_DEADBAND)
                        {
                        this.moveState = MoveState.INIT;
                        Hardware.drive.drive(0, 0);
                        // System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&");
                        return true;
                        }
                    }

                // SmartDashboard.putNumber("distance from target",
                // Hardware.visionInterface.getDistanceFromTarget());

                // SmartDashboard.putNumber("distance to target distance",
                // Hardware.visionInterface.getDistanceFromTarget() - FAR_DISTANCE);
                // drive straight the the proper distance
                if (position == Position.FAR)
                    {

                    // filter for negative distances
                    if (Hardware.visionInterface.getDistanceFromTarget() > FAR_DISTANCE + MOVE_DISTANCE_DEADBAND)
                        {
                        Hardware.drive.driveStraight(DRIVE_STRAIGHT_SPEED, .1, true);
                        }
                    else if (Hardware.visionInterface.getDistanceFromTarget() < FAR_DISTANCE - MOVE_DISTANCE_DEADBAND)
                        {
                        Hardware.drive.driveStraight(-DRIVE_STRAIGHT_SPEED, .1, true);
                        }
                    else
                        {
                        this.moveState = MoveState.INIT;
                        Hardware.drive.drive(0, 0);
                        // System.out.println("################################");
                        return true;
                        }
                    }
                else if (position == Position.CLOSE)
                    {
                    if (Hardware.visionInterface.getDistanceFromTarget() > CLOSE_DISTANCE + MOVE_DISTANCE_DEADBAND)
                        {
                        Hardware.drive.driveStraight(DRIVE_STRAIGHT_SPEED, .1, true);
                        }
                    else if (Hardware.visionInterface.getDistanceFromTarget() < CLOSE_DISTANCE - MOVE_DISTANCE_DEADBAND)
                        {
                        Hardware.drive.driveStraight(-DRIVE_STRAIGHT_SPEED, .1, true);
                        }
                    else
                        {
                        this.moveState = MoveState.INIT;
                        Hardware.drive.drive(0, 0);
                        // System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                        return true;
                        }
                    }
                break;
            case ALIGN:
                // realign to the target using vision
                if (Hardware.visionDriving.alignToTarget())
                    {
                    this.moveState = MoveState.DRIVE;
                    // done

                    }
                break;
            default:
                this.moveState = MoveState.INIT;
            }

        return false;
    }

    public void setMovePositionState(MoveState state)
    {
        this.moveState = state;
    }

    public MoveState getMovePosition()
    {
        return this.moveState;
    }

    /**
     * return the RPM to set based off distance recieved from the camera
     *
     * @param distance
     * @return
     */
    public double getRPMPerDistance(double distance)
    {
        // quadratic equation from three point (40, 2800) (120, 3500) (180 3800)
        return -0.02679 * (distance * distance) + 13.04 * (distance) + 2321;
    }

    // temporary store if the launcher is ready
    private boolean launcherReadyTemp = false;

    // temporary store if the conveyor is ready
    private boolean conveyorReadyTemp = false;

    // temporary store if the hood is redy
    private boolean hoodReadyTemp = false;

    // temporary store if the position is redy
    private boolean positionReadyTemp = false;
    // temporary store if the ball has been shot NOTE: this may be deprecated
    private boolean loadReadyTemp = false;

    // if called in auto
    private boolean auto = true;

    private static final double MOVE_DISTANCE_DEADBAND = 10;
    // far RPM to 2020 shooter
    private static final double RPM_FAR_2020 = 3500;

    // close RPM for the 2020 shooter
    private static final double RPM_CLOSE_2020 = 2800;

    // far RPM for the 2019 robot
    private static final double RPM_FAR_2019 = 900;

    // close for the 2019 robot
    private static final double RPM_CLOSE_2019 = 100;

    // speed to drive straight
    private static final double DRIVE_STRAIGHT_SPEED = .28;// TODO

    // max RPM the drivers are allowed to change the RPM
    private static final double DRIVER_CHANGE_ALLOWANCE = 100;

    // wanted distance to shoot close
    private static int CLOSE_DISTANCE = 50;

    // wanted distance to shoot far(the white start line)
    private static int FAR_DISTANCE = 145;

    // is override vision with ultrasonic
    private static boolean VISION_OVERRIDE = true;

    // minimum speed to move with the camera
    private static double VISION_SPEED = .3;

    }
