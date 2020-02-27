/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved. */
/* Open Source Software - may be modified and shared by FRC teams. The code */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project. */
/*----------------------------------------------------------------------------*/
// ====================================================================
// FILE NAME: Autonomous.java (Team 339 - Kilroy)
//
// CREATED ON: Jan 13, 2015
// CREATED BY: Nathanial Lydick
// MODIFIED ON:
// MODIFIED BY:
// ABSTRACT:
// This file is where almost all code for Kilroy will be
// written. Some of these functions are functions that should
// override methods in the base class (IterativeRobot). The
// functions are as follows:
// -----------------------------------------------------
// Init() - Initialization code for autonomous mode
// should go here. Will be called each time the robot enters
// autonomous mode.
// -----------------------------------------------------
// Periodic() - Periodic code for autonomous mode should
// go here. Will be called periodically at a regular rate while
// the robot is in autonomous mode.
// -----------------------------------------------------
//
// NOTE: Please do not release this code without permission from
// Team 339.
// ====================================================================
package frc.robot;

import java.util.concurrent.TimeUnit;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import frc.Hardware.Hardware;
import frc.Utils.StorageControl;
import frc.Utils.StorageControl.ControlState;

/**
 * An Autonomous class. This class <b>beautifully</b> uses state machines in
 * order to periodically execute instructions during the Autonomous period.
 *
 * This class contains all of the user code for the Autonomous part of the
 * match, namely, the Init and Periodic code
 *
 *
 * @author Michael Andrzej Klaczynski
 * @written at the eleventh stroke of midnight, the 28th of January, Year of our
 *          LORD 2016. Rewritten ever thereafter.
 *
 * @author Nathanial Lydick
 * @written Jan 13, 2015
 */

public class Autonomous
{

/**
 * User Initialization code for autonomous mode should go here. Will run once
 * when the autonomous first starts, and will be followed immediately by
 * periodic().
 */
public static void init ()
{

    Hardware.drive.setGearPercentage(4, AUTO_GEAR);
    Hardware.drive.setGear(4);

    /*
     * 2 pos switch Auto Swtich determines wether we will or will not run auto:
     * if
     * position is off: dont run auto if position is on: auto will run
     */
    if (Hardware.autoSwitch.isOn() == false)
        {

        autoState = State.FINISH;
        }
    else
        {

        autoState = State.INIT;
        }

    /*
     * 3 pos switch for starting location: forward : left reverse : right off :
     * center
     */
    if (Hardware.autoLocation.getPosition() == Relay.Value.kForward)
        {
        position = Position.LEFT;
        }
    else
        if (Hardware.autoLocation.getPosition() == Relay.Value.kReverse)
            {
            position = Position.RIGHT;

            }
        else
            {
            position = Position.CENTER;
            }

    // Switch Determining the plan for shooting, Forward : Shoot Far; Reverse :
    // Shoot Close; Off : Nothing;
    if (Hardware.shootingPlan.getPosition() == Relay.Value.kForward)
        {
        shootingPlan = ShootingPlan.FAR;

        }
    else
        if (Hardware.shootingPlan.getPosition() == Relay.Value.kReverse)
            {
            shootingPlan = ShootingPlan.CLOSE;

            }
        else
            if (Hardware.shootingPlan.getPosition() == Relay.Value.kOff)
                {
                // robot is broken
                shootingPlan = ShootingPlan.NOTHING;

                }

    /*
     * Ball count switch for starting with 3 or no balls
     */
    if (Hardware.ballStart.isOn())
        {
        Hardware.ballCounter.setBallCount(3);
        }
    else
        {
        Hardware.ballCounter.setBallCount(0);
        }


    /*
     * 6 pos switch: this will determine the exiting strategy for each path {
     */

    if (Hardware.autoSixPosSwitch.getPosition() == 0)
        {
        // positioning the robot for the center square
        sixLocation = SixLocation.ZERO;
        }
    else
        if (Hardware.autoSixPosSwitch.getPosition() == 1)
            {
            // position the robot for the trench
            sixLocation = SixLocation.ONE;
            }
        else
            if (Hardware.autoSixPosSwitch.getPosition() == 2)
                {

                sixLocation = SixLocation.TWO;
                }
            else
                if (Hardware.autoSixPosSwitch.getPosition() == 3)
                    {

                    // move yourself out of the way, close to goal
                    sixLocation = SixLocation.THREE;
                    }
                else
                    if (Hardware.autoSixPosSwitch.getPosition() == 4)
                        {
                        // path to move straight forward
                        sixLocation = SixLocation.FOUR;
                        }
                    else
                        {
                        // path to move straight backwards
                        sixLocation = SixLocation.FIVE;
                        }

    // TODO add in switch to determine ball count: Hardware change first


    Hardware.cameraServo.setCameraAngleUp();
    Hardware.launcher.resetShootTemps();

} // end Init

/*
 * User Periodic code for autonomous mode should go here. Will be called
 * periodically at a regular rate while the robot is in autonomous mode.
 *
 * @author Nathanial Lydick
 *
 * @written Jan 13, 2015
 *
 * FYI: drive.stop cuts power to the motors, causing the robot to coast.
 * drive.brake results in a more complete stop. Meghan Brown; 10 February 2019
 */

public static enum Path
    {
    NOTHING, SHOOT_FAR, SHOOT_CLOSE, MOVE_FORWARD, MOVE_BACKWARDS, DONT_MOVE, DONT_LEAVE, GET_OUT, ALIGN_SQUARE, ALIGN_TRENCH, PICKUP_TRENCH, TURN_AND_FIRE
    }

public static enum Exit
    {
    ALIGN_SQUARE, ALIGN_TRENCH, GET_OUT, TURN_AND_FIRE, NOTHING
    }

private static enum Position
    {
    LEFT, RIGHT, CENTER
    }

private static enum ShootingPlan
    {
    FAR, CLOSE, NOTHING
    }

private static enum SixLocation
    {
    ZERO, ONE, TWO, THREE, FOUR, FIVE, OFF
    }



private static ShootingPlan shootingPlan = ShootingPlan.NOTHING;

public static Exit exit = Exit.NOTHING;

public static Path path = Path.NOTHING;

public static SixLocation sixLocation = SixLocation.OFF;

public static Position position = Position.CENTER;

public static enum State
    {
    INIT, DELAY, CHOOSE_PATH, RUN, FINISH
    }

public static State autoState = State.INIT;

/**
 * Description: constant looping function utilied to control the operations of
 * the autonomous system. states handle function of initilizing, delay,
 * starting, and finishing
 *
 *
 * @return void
 *
 * @author Craig Kimball
 * @written 2/17/2020
 */
public static void periodic ()
{

    Hardware.visionInterface.updateValues();
    Hardware.storage.storageControlState();
    Hardware.storage.intakeStorageControl();
    Hardware.intake.makePassive();
    Hardware.visionInterface
            .publishValues(Hardware.publishVisionSwitch);

    switch (autoState)
        {

        case INIT:

            Hardware.autoTimer.start();

            autoState = State.DELAY;
            break;

        case DELAY:
            // System.out.println("AutoTimer: " + Hardware.autoTimer.get());
            // System.out.println("Target: " + Hardware.delayPot.get(0, 5.0));
            if (Hardware.autoTimer.get() > Hardware.delayPot.get(0,
                    5.0))
                {

                autoState = State.CHOOSE_PATH;
                Hardware.autoTimer.stop();

                }

            break;

        case CHOOSE_PATH:
            // if (shootingPlan == ShootingPlan.CLOSE && path != Path.NOTHING)
            // {
            // Hardware.launcher.prepareToShoot(true, true);
            // }
            // else if (shootingPlan == ShootingPlan.FAR && path !=
            // Path.NOTHING)
            // {
            // Hardware.launcher.prepareToShoot(false, true);
            // }

            if (shootingPlan == ShootingPlan.CLOSE
                    && path != Path.NOTHING)
                {
                Hardware.launcher.prepareToShoot();
                }
            else
                if (shootingPlan == ShootingPlan.FAR
                        && path != Path.NOTHING)
                    {
                    Hardware.launcher.prepareToShoot();
                    }
            choosePath();
            autoState = State.RUN;

            break;
        case RUN:
            // System.out.println("In Run State");
            if (runAuto())
                {
                autoState = State.FINISH;
                }
            break;

        case FINISH:
            Hardware.launcher.unchargeShooter();

            StorageControl.setStorageControlState(ControlState.PASSIVE);
            Hardware.drive.drive(0, 0);
            break;

        default:
            autoState = State.FINISH;
            break;

        }

}

/**
 * Description: sets the enum states and decides what paths to folllow
 *
 * @return void
 *
 * @author Craig Kimball
 * @written 2/17/2020
 */
private static void choosePath ()
{
    // Statements to determine sates:
    System.out.println("choose path");
    switch (shootingPlan)
        {
        case CLOSE:
            System.out.println("close");
            path = Path.SHOOT_CLOSE;
            Hardware.launcher.prepareToShoot();
            break;
        case FAR:
            System.out.println("far");
            path = Path.SHOOT_FAR;
            Hardware.launcher.prepareToShoot();
            break;
        case NOTHING:
            System.out.println("nothing");
            path = Path.NOTHING;
            autoState = State.FINISH;
            break;
        }

    /*
     * 6 pos switch: this will determine the exiting strategy for each path
     */
    switch (sixLocation)
        {
        case ZERO:
            exit = Exit.ALIGN_SQUARE;
            break;
        case ONE:
            exit = Exit.ALIGN_TRENCH;
            break;
        case TWO:
            exit = Exit.TURN_AND_FIRE;
            break;
        case THREE:
            exit = Exit.GET_OUT;
            break;
        case FOUR:
            if (shootingPlan != ShootingPlan.NOTHING)
                {
                path = Path.MOVE_FORWARD;
                }
            break;
        case FIVE:
            if (shootingPlan != ShootingPlan.NOTHING)
                {
                path = Path.MOVE_BACKWARDS;
                }
            break;
        case OFF:
            // switch is broke :)
            break;
        }
}

/**
 * Description: Execution of each auto function
 *
 * @return true when the auto path finishes
 *
 * @author Craig Kimball
 * @written 2/17/2020
 */
public static boolean runAuto ()
{
    // System.out.println("Exit: " + exit);
    // System.out.println("Path: " + path);
    // System.out.println("Location: " + position);
    // System.out.println("6 Location: " + sixLocation);

    // System.out.println(path);
    switch (path)
        {

        case NOTHING:

            // This will be for the insatnce where the robot is not a in a
            // functioning state
            // and auto paths are broken
            autoState = State.FINISH;
            break;

        case SHOOT_FAR:
            // Hardware.launcher.prepareToShoot(false, true);
            Hardware.launcher.prepareToShoot();

            if (!hasShot)
                {
                if (shootFar())
                    {
                    hasShot = true;
                    }
                }
            if (hasShot)
                {
                if (exit == Exit.ALIGN_SQUARE)
                    {
                    // This is a function that works if the robot is aligned on
                    // the left side
                    path = Path.ALIGN_SQUARE;
                    }
                else
                    if (exit == Exit.ALIGN_TRENCH)
                        {
                        // this is a function that works if the robot is aligned
                        // on the right side
                        path = Path.ALIGN_TRENCH;
                        }
                    else
                        if (exit == Exit.TURN_AND_FIRE)
                            {
                            // Continuation of Alinging trench, adding the
                            // process of picking up those balls
                            // and attepting to shoot, or alligning to shoot
                            // again.
                            path = Path.ALIGN_TRENCH;
                            }
                        else
                            {
                            path = Path.NOTHING;
                            }

                }

            break;

        case SHOOT_CLOSE:

            // Hardware.launcher.prepareToShoot(true, true);
            Hardware.launcher.prepareToShoot();
            // the action of moving closer before attempting to shoot in the
            // goal
            if (!hasShot)
                {
                if (shootClose())
                    {
                    System.out.println("finished shoot close");
                    hasShot = true;
                    }
                }
            if (hasShot)
                {

                if (exit == Exit.ALIGN_SQUARE)
                    {
                    // This is a function that works if the robot is aligned on
                    // the left side
                    path = Path.ALIGN_SQUARE;
                    }
                else
                    if (exit == Exit.ALIGN_TRENCH)
                        {
                        // this is a function that works if the robot is aligned
                        // on the right side
                        path = Path.ALIGN_TRENCH;
                        }
                    else
                        if (exit == Exit.TURN_AND_FIRE)
                            {
                            // Continuation of Alinging trench, adding the
                            // process of picking up those balls
                            // and attepting to shoot, or alligning to shoot
                            // again.
                            path = Path.ALIGN_TRENCH;
                            }
                        else
                            if (exit.equals(Exit.GET_OUT)
                                    || exit == Exit.GET_OUT)
                                {

                                // Move out of the way, staying close to goal to
                                // retreive missed balls
                                path = Path.GET_OUT;

                                }
                }

            break;

        case MOVE_FORWARD:
            // move in a straight line forwards
            if (moveForward())
                {
                return true;
                }

            break;

        case MOVE_BACKWARDS:

            if (moveBackward())
                {
                return true;
                }

            break;

        case DONT_MOVE:

            break;

        case GET_OUT:

            if (getOut())
                {
                return true;
                }
            break;

        case ALIGN_SQUARE:
            // aligning for center square to allign for balls
            // robot is on left side or center

            if (alignSquare())
                {
                return true;
                }
            break;

        case ALIGN_TRENCH:
            // aligning for trench to allign for balls
            // robot is on the right side or center

            if (alignTrench())
                {

                // this statement determines wether we are picking up balls in
                // the trenchs
                if (exit == Exit.TURN_AND_FIRE)
                    {
                    path = Path.PICKUP_TRENCH;
                    }
                else
                    {
                    return true;
                    }
                }
            break;

        case PICKUP_TRENCH:
            Hardware.intake.deployIntake();
            Hardware.cameraServo.setCameraAngleDown();

            if (pickupTrench())
                {
                Hardware.cameraServo.setCameraAngleUp();
                Hardware.intake.undeployIntake();
                // Continue to shoot again after pickup
                if (exit == Exit.TURN_AND_FIRE)
                    {
                    path = Path.TURN_AND_FIRE;
                    }
                else
                    {
                    return true;
                    }

                }
            break;

        case TURN_AND_FIRE:
            System.out.println("IN TURN AND FIRE");
            // final attempt to turn and shoot more balls from trench
            if (turnFire())
                {
                return true;
                }
            break;

        default:
            path = Path.NOTHING;
            break;

        }
    return false;
}

public static enum TurnAndFireState
    {
    DRIVE_BACK, TURN1, ALIGN, SHOOT, FINISH
    }

public static TurnAndFireState turnAndFire = TurnAndFireState.DRIVE_BACK;

/**
 * Description: Method to handle the function of turn and fire auto states.
 *
 * @return boolean: Return true when auto state is finished and balls have been
 *         shot
 *
 * @author Craig Kimball
 * @written 2/17/2020
 */
private static boolean turnFire ()
{
    switch (turnAndFire)
        {

        case DRIVE_BACK:
            // drive backwards towards line
            if (Hardware.drive.driveStraightInches(
                    TURN_AND_FIRE_GO_BACK_DISTANCE, -DRIVE_SPEED,
                    ACCELERATION,
                    true))
                {
                turnAndFire = TurnAndFireState.TURN1;
                }

            break;
        case TURN1:
            // turn towards target
            if (Hardware.drive.turnDegrees(TURN_AND_FIRE_DEGREES,
                    TURN_SPEED, ACCELERATION, true))
                {
                turnAndFire = TurnAndFireState.ALIGN;
                break;

                }

            break;
        case ALIGN:

            // System.out.println("Aligning");

            if (Hardware.visionDriving.driveToTarget(216, true, .3))// 144,
                                                                    // true, .3
                {

                // System.out.println("Aligned");
                turnAndFire = TurnAndFireState.SHOOT;
                }

            break;
        case SHOOT:
            System.out.println("Shooting");
            if (Hardware.launcher.shootBallsAuto(false))
                {
                turnAndFire = TurnAndFireState.FINISH;
                }
            if (Hardware.ballCounter.getBallCount() == 0)
                {
                turnAndFire = TurnAndFireState.FINISH;
                }
            break;

        case FINISH:

            StorageControl.setStorageControlState(ControlState.PASSIVE);
            Hardware.launcher.unchargeShooter();
            return true;

        }
    return false;

}

public static enum pickupTrenchState
    {
    DRIVE_FORWARD, FINISH
    }

public static pickupTrenchState pickup = pickupTrenchState.DRIVE_FORWARD;

/**
 * Description: Handles the states and functions to pickup balls in the trench
 * during auto
 *
 * @return Boolean: return true when states are finished
 *
 * @author Craig Kimball
 * @written 2/17/2020
 */
private static boolean pickupTrench ()
{
    // drive forward along balls picking them up
    System.out.println("pickup trench");
    switch (pickup)
        {
        case DRIVE_FORWARD:

            // drive for balls
            System.out.println("picking up balls");
            Hardware.visionInterface.setPipeline(2);

            Hardware.cameraServo.setCameraAngleDown();
            if (Hardware.intake.pickUpBallsVision())
                {
                Hardware.visionInterface.setPipeline(0);
                return true;
                }
            break;
        case FINISH:
            return true;
        }
    return false;
}

public static enum AlignTrenchState
    {
    TURN1, FINAL_DRIVE, FINISH
    }

public static AlignTrenchState trench = AlignTrenchState.TURN1;

/**
 * Description: handles the functions and states of aligning to the trench in
 * auto
 *
 * @return Boolean: returns true when the sates are finished
 *
 * @author Craig Kimball
 * @written 2/17/2020
 */
private static boolean alignTrench ()
{
    System.out.println("Trench State: " + trench);
    // System.out.println("shootingPlan: " +
    // Hardware.shootingPlan.getPosition());
    // System.out.println("Position: " + position);

    if (position == Position.RIGHT)
        {
        switch (trench)
            {

            case TURN1:

                if (Hardware.drive.turnDegrees(
                        ALIGN_TRENCH_RIGHT_DEGREES, TURN_SPEED,
                        ACCELERATION, true))
                    {
                    trench = AlignTrenchState.FINAL_DRIVE;

                    }
                break;
            case FINAL_DRIVE:
                // drive up to trench
                if (Hardware.drive.driveStraightInches(
                        ALIGN_TRENCH_RIGHT_DISTANCE, DRIVE_SPEED,
                        ACCELERATION,
                        true))
                    {
                    trench = AlignTrenchState.FINISH;
                    }

                break;
            case FINISH:
                return true;
            }

        }

    return false;

}

public static enum AlignSquareState
    {
    DRIVE_BACK, TURN1, ALIGN, TURN2, FINAL_DRIVE, FINISH
    }

public static AlignSquareState square = AlignSquareState.DRIVE_BACK;

/**
 * Description: handles the functions and states of aligning to the square
 * during auto
 *
 * @return Boolean: return true when all states are finished
 *
 * @author Craig Kimball
 * @written 2/17/2020
 */
private static boolean alignSquare ()
{

    if (position == Position.LEFT)
        {
        switch (square)
            {
            case DRIVE_BACK:
                if (Hardware.drive.driveStraightInches(
                        ALIGN_SQUARE_MOVE_BACK_DISTANCE, -DRIVE_SPEED,
                        ACCELERATION,
                        true))
                    {
                    square = AlignSquareState.TURN1;
                    }
                break;
            case TURN1:
                // turn away from tower
                if (Hardware.drive.turnDegrees(
                        ALIGN_SQUARE_LEFT_DEGREES, TURN_SPEED,
                        ACCELERATION, true))
                    {
                    square = AlignSquareState.ALIGN;
                    break;
                    }

                break;
            case ALIGN:
                // drive away from tower
                if (Hardware.drive.driveStraightInches(
                        ALIGN_SQUARE_LEFT_DISTANCE, DRIVE_SPEED,
                        ACCELERATION, true))
                    {

                    square = AlignSquareState.FINISH;
                    }
                break;
            case FINISH:
                return true;
            }
        }

    return false;
}

public static enum GetOutState
    {
    TURN, FINAL_DRIVE, FINISH
    }

public static GetOutState out = GetOutState.TURN;

/**
 * Description: Handles the functions and states of getting the robot out of the
 * way when shooting close
 *
 * @param
 * @return
 *
 * @author Craig Kimball
 * @written 2/17/2020
 */
private static boolean getOut ()
{
    Hardware.launcher.unchargeShooter();
    System.out.println("out state: " + out);
    switch (out)
        {
        case TURN:
            switch (position)
                {
                case RIGHT:
                    if (Hardware.drive.turnDegrees(
                            GET_OUT_RIGHT_DEGREES, TURN_SPEED,
                            ACCELERATION, true))
                        {
                        out = GetOutState.FINAL_DRIVE;
                        }
                    break;
                case LEFT:

                    if (Hardware.drive.turnDegrees(GET_OUT_LEFT_DEGREES,
                            TURN_SPEED, ACCELERATION, true))
                        {
                        out = GetOutState.FINAL_DRIVE;
                        }
                    break;

                case CENTER:
                    out = GetOutState.FINAL_DRIVE;
                    break;

                }
            break;
        case FINAL_DRIVE:
            System.out.println("position thing: " + position);
            switch (position)
                {
                case RIGHT:

                    if (Hardware.drive.driveStraightInches(
                            GET_OUT_RIGHT_DISTANCE, DRIVE_SPEED,
                            ACCELERATION, true))
                        {
                        out = GetOutState.FINISH;
                        }
                    break;

                case LEFT:
                    if (Hardware.drive.driveStraightInches(
                            GET_OUT_LEFT_DISTANCE, DRIVE_SPEED,
                            ACCELERATION, true))
                        {
                        out = GetOutState.FINISH;
                        }

                    break;
                case CENTER:
                    if (Hardware.drive.driveStraightInches(
                            GET_OUT_CENTER_DISTANCE,
                            -GET_OUT_CENTER_SPEED,
                            ACCELERATION, true))
                        {
                        out = GetOutState.FINISH;
                        }
                    break;

                }
            break;
        case FINISH:
            return true;
        }
    return false;
}

/**
 * Description: move backwards off line
 *
 * @return Boolean: Return true when it has moved the correct distance
 *
 * @author Craig Kimball
 * @written 2/17/2020
 */
private static boolean moveBackward ()
{
    if (Hardware.drive.driveStraightInches(OFF_LINE_DISTANCE, -.2,
            ACCELERATION, true))
        {
        return true;
        }
    return false;
}

/**
 * Description: moves forward off of line
 *
 * @return Boolean: returns true once it has moved the correct distance
 *
 * @author Craig Kimball
 * @written 2/17/2020
 */
private static boolean moveForward ()
{
    // this will pull forward the distance declared to leave the line.
    if (Hardware.drive.driveStraightInches(OFF_LINE_DISTANCE, .2,
            ACCELERATION, true))
        {
        return true;
        }
    return false;

}

/**
 * Description: Executes the function to drive forward and shoot
 *
 * @return boolean: returns tree once it has shot all balls
 *
 * @author Craig Kimball
 * @written 2/17/2020
 */
private static boolean shootClose ()
{

    if (Hardware.visionDriving.driveToTarget(45, true, .3))
        {
        if (Hardware.launcher.shootBallsAuto(false))
            {
            StorageControl.setStorageControlState(ControlState.PASSIVE);
            Hardware.launcher.unchargeShooter();
            return true;
            }
        }
    return false;
}

public static enum farState
    {
    DRIVE_BACK, ALIGN, SHOOT, FINISH
    }

public static farState far = farState.DRIVE_BACK;

/**
 * Description: handles the functions to shoot far
 *
 * @return boolean: returns true once all balls are shot
 *
 * @author Craig Kimball
 * @written 2/17/2020
 */
private static boolean shootFar ()
{

    switch (far)
        {

        case DRIVE_BACK:
            if (Hardware.drive.driveStraightInches(
                    SHOOT_FAR_DRIVE_BACK_DISTANCE, -DRIVE_SPEED,
                    ACCELERATION, true))
                {
                Hardware.autoTimer.start();
                far = farState.ALIGN;
                }

            break;
        case ALIGN:

            if (Hardware.visionInterface.getHasTargets())
                {

                if (Hardware.visionDriving.alignToTarget())
                    {
                    far = farState.SHOOT;

                    }
                }
            else
                {
                path = Path.NOTHING;

                }
            break;
        case SHOOT:
            if (Hardware.launcher.shootBallsAuto(false))
                {

                far = farState.FINISH;
                }
            break;

        case FINISH:

            return true;

        }
    return false;
}

private static boolean hasShot = false;
/*
 * ============================================================= Constants
 * ==============================================================
 */

private final static double AUTO_GEAR = 1.0;

private final static int OFF_LINE_DISTANCE = 48;

private final static int SHOOT_FAR_DRIVE_BACK_DISTANCE = 22;

private final static int GET_OUT_LEFT_DEGREES = -120;

private final static int GET_OUT_LEFT_DISTANCE = 36;

private final static int GET_OUT_RIGHT_DEGREES = 120;

private final static int GET_OUT_RIGHT_DISTANCE = 48;

// private final static int GET_OUT_CENTER_DEGREES = 0;
private final static int GET_OUT_CENTER_DISTANCE = 120;

private final static double GET_OUT_CENTER_SPEED = .3;

private final static int ALIGN_SQUARE_MOVE_BACK_DISTANCE = 80;

private final static int ALIGN_SQUARE_LEFT_DEGREES = 120;

private final static int ALIGN_SQUARE_LEFT_DISTANCE = 45;
// private final static int ALIGN_SQUARE_LEFT_FINAL_DEGREES = 45;
// private final static int ALIGN_SQUARE_LEFT_FINAL_DISTANCE = 0;

// private final static int ALIGN_TRENCH_MOVE_BACK_DISTANCE = 96;

private final static int ALIGN_TRENCH_RIGHT_DEGREES = -150;

private final static int ALIGN_TRENCH_RIGHT_DISTANCE = 24;

// if the way i pick up the balls is just driving into them utilize this
// constant
private final static int PICKUP_DISTANCE = 120;

private final static int TURN_AND_FIRE_GO_BACK_DISTANCE = 96;

private final static int TURN_AND_FIRE_DEGREES = 150;

private final static int TURN_AND_FIRE_DISTANCE = 0;

private final static double TURN_SPEED = 0.4;

private final static double DRIVE_SPEED = 0.4;

private final static double ACCELERATION = .1;

private final static Boolean START_BALL_BOOLEAN = false;

}
