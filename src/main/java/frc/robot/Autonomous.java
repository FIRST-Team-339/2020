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
    public static void init()
    {

        Hardware.drive.setGearPercentage(4, AUTO_GEAR);
        Hardware.drive.setGear(4);
        /*
         * 2 pos switch: 1: no auto 2: go auto
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
         * pos switch for starting location: forward : left reverse : right off : center
         */
        if (Hardware.autoLocation.getPosition() == Relay.Value.kForward)
            {
            position = Position.LEFT;
            }
        else if (Hardware.autoLocation.getPosition() == Relay.Value.kReverse)
            {
            position = Position.RIGHT;

            }
        else
            {
            position = Position.CENTER;
            }

        // TODO add in switch to determine ball count: Hardware change first

        Hardware.ballcounter.setBallCount(3);

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

    public static Exit exit = Exit.NOTHING;

    public static Path path = Path.NOTHING;

    public static Position position = Position.CENTER;

    public static enum State
        {
        INIT, DELAY, CHOOSE_PATH, RUN, FINISH
        }

    public static State autoState = State.INIT;

    public static void periodic()
    {
        // ===========================================
        // important code to make stuff do other stuff
        // ============================================
        Hardware.visionInterface.updateValues();
        Hardware.storage.storageControlState();
        Hardware.storage.intakeStorageControl();
        Hardware.visionInterface.publishValues(Hardware.publishVisionSwitch);
        // ==================================================
        // end important code that make stuff do other stuff
        // ===================================================

        // System.out.println("auto state: " + autoState);
        // System.out.println("path: " + path);

        // System.out.println("ultrs" +
        // Hardware.frontUltraSonic.getDistanceFromNearestBumper());
        // System.out.println("vision"
        // Hardware.visionInterface.getDistanceFromTarget());

        System.out.println(Hardware.gyro.getAngle());

        // printing out utilized states:

        // Cancel if the "cancelAuto" button is pressed

        switch (autoState)
            {

            case INIT:

                Hardware.autoTimer.start();

                autoState = State.DELAY;
                break;

            case DELAY:
                // System.out.println("AutoTimer: " + Hardware.autoTimer.get());
                // System.out.println("Target: " + Hardware.delayPot.get(0, 5.0));
                if (Hardware.autoTimer.get() > Hardware.delayPot.get(0, 5.0))
                    {

                    autoState = State.CHOOSE_PATH;
                    Hardware.autoTimer.stop();

                    }

                break;

            case CHOOSE_PATH:

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

                Hardware.drive.drive(0, 0);
                break;

            default:
                autoState = State.FINISH;
                break;

            }

    }

    // =====================================================================
    // Path Methods
    // =====================================================================
    private static void choosePath()
    {
        // Statements to determine sates:

        /*
         * 3 pos switch: 1: shoot far 2: shoot close 3: dont move kForward = shoot far
         * kreverse = shoot close kOff = stay, no auto
         */

        if (Hardware.shootingPlan.getPosition() == Relay.Value.kForward)
            {
            path = Path.SHOOT_FAR;
            }
        else if (Hardware.shootingPlan.getPosition() == Relay.Value.kReverse)
            {
            path = Path.SHOOT_CLOSE;
            }
        else if (Hardware.shootingPlan.getPosition() == Relay.Value.kOff)
            {
            // robot is broken
            path = Path.NOTHING;
            autoState = State.FINISH;

            }
        /*
         * 6 pos switch: this will determine the exiting strategy for each path
         */
        if (Hardware.shootingPlan.getPosition() != Relay.Value.kOff)
            {
            if (Hardware.autoSixPosSwitch.getPosition() == 0)
                {
                // positioning the robot for the center square
                exit = Exit.ALIGN_SQUARE;
                }
            else if (Hardware.autoSixPosSwitch.getPosition() == 1)
                {
                // position the robot for the trench
                exit = Exit.ALIGN_TRENCH;
                }
            else if (Hardware.autoSixPosSwitch.getPosition() == 2)
                {

                exit = Exit.TURN_AND_FIRE;
                }
            else if (Hardware.autoSixPosSwitch.getPosition() == 3)
                {

                // move yourself out of the way, close to goal
                exit = Exit.GET_OUT;
                }
            else if (Hardware.autoSixPosSwitch.getPosition() == 4)
                {
                // path to move straight forward
                path = Path.MOVE_FORWARD;
                }
            else
                {
                // path to move straight backwards
                path = Path.MOVE_BACKWARDS;
                }
            }
        // Switch case to execute the functions of auto path
    }

    public static boolean runAuto()
    {
        // System.out.println("Location: " + position);
        // System.out.println("RunAuto Path: " + path);
        // System.out.println("Exit Path" + exit);
        switch (path)
            {
            case NOTHING:

                // This will be for the insatnce where the robot is not a in a
                // functioning state
                // and auto paths are broken
                autoState = State.FINISH;
                break;

            case SHOOT_FAR:
                System.out.println("has shot the thing: " + hasShotTheEtHInG);
                if (!hasShotTheEtHInG)
                    {
                    if (shootFar())
                        {
                        hasShotTheEtHInG = true;
                        }
                    }
                if (hasShotTheEtHInG)
                    {

                    if (exit == Exit.ALIGN_SQUARE)
                        {
                        // This is a function that works if the robot is aligned on
                        // the left side
                        path = Path.ALIGN_SQUARE;
                        }
                    else if (exit == Exit.ALIGN_TRENCH)
                        {
                        // this is a function that works if the robot is aligned
                        // on the right side
                        path = Path.ALIGN_TRENCH;
                        }
                    else if (exit == Exit.TURN_AND_FIRE)
                        {
                        // Continuation of Alinging trench, adding the
                        // process of picking up those balls
                        // and attepting to shoot, or alligning to shoot
                        // again.
                        path = Path.ALIGN_TRENCH;
                        }
                    else
                        {
                        path = Path.MOVE_BACKWARDS;
                        }
                    // else if (exit.equals(Exit.GET_OUT) || exit == Exit.GET_OUT)
                    // {

                    // // Move out of the way, staying close to goal to retreive
                    // missed balls
                    // path = Path.GET_OUT;

                    // }
                    }
                // determining exit stategy

                break;

            case SHOOT_CLOSE:
                /* TODO set the motors to ramp up here */
                // Hardware.launcher.prepareToShoot(3000);
                // the action of moving closer before attempting to shoot in the
                // goal
                if (!hasShotTheEtHInG)
                    {
                    if (shootClose())
                        {
                        hasShotTheEtHInG = true;
                        }
                    }
                if (hasShotTheEtHInG)
                    {

                    if (exit == Exit.ALIGN_SQUARE)
                        {
                        // This is a function that works if the robot is aligned on
                        // the left side
                        path = Path.ALIGN_SQUARE;
                        }
                    else if (exit == Exit.ALIGN_TRENCH)
                        {
                        // this is a function that works if the robot is aligned
                        // on the right side
                        path = Path.ALIGN_TRENCH;
                        }
                    else if (exit == Exit.TURN_AND_FIRE)
                        {
                        // Continuation of Alinging trench, adding the
                        // process of picking up those balls
                        // and attepting to shoot, or alligning to shoot
                        // again.
                        path = Path.ALIGN_TRENCH;
                        }
                    else if (exit.equals(Exit.GET_OUT) || exit == Exit.GET_OUT)
                        {

                        // Move out of the way, staying close to goal to
                        // retreive missed balls
                        path = Path.GET_OUT;

                        }
                    }
                // determining exit stategy

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
                // doAnything?
                break;

            case GET_OUT:
                // System.out.println("Get Out: " + out);
                // removing yourself from the way of robots

                if (getOut())
                    {
                    return true;
                    }
                break;

            case ALIGN_SQUARE:
                // aligning for center square to allign for balls
                // robot is on left side or center

                // call align

                if (alignSquare())
                    {
                    return true;
                    }
                break;

            case ALIGN_TRENCH:
                // aligning for trench to allign for balls
                // robot is on the right side or center

                // calling align
                if (alignTrench())
                    {

                    // are we collecting?
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
                // TODO Picup drop down
                // TODO angle camera down

                // picking up balls
                // only applicable if Align Trench was previously stated
                if (pickupTrench())
                    {

                    // this will be executed if there is time
                    path = Path.TURN_AND_FIRE;
                    return true;
                    }
                break;

            case TURN_AND_FIRE:

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
        DRIVE_BACK, TURN1, ALIGN, FINAL_DRIVE, FINISH
        }

    public static TurnAndFireState turnAndFire = TurnAndFireState.DRIVE_BACK;

    private static boolean turnFire()
    {
        switch (turnAndFire)
            {

            case DRIVE_BACK:
                // drive backwards towards line
                if (Hardware.drive.driveStraightInches(TURN_AND_FIRE_GO_BACK_DISTANCE, DRIVE_SPEED, ACCELERATION, true))
                    {
                    turnAndFire = TurnAndFireState.TURN1;
                    }

                break;
            case TURN1:
                // turn towards target
                if (Hardware.drive.turnDegrees(TURN_AND_FIRE_DEGREES, TURN_SPEED, ACCELERATION, true))
                    {
                    turnAndFire = TurnAndFireState.ALIGN;
                    break;

                    }

                break;
            case ALIGN:
                if (Hardware.visionDriving.alignToTarget())
                    {
                    turnAndFire = TurnAndFireState.FINISH;
                    }

                break;
            // case FINAL_DRIVE:
            // // hail marry
            // if (Hardware.drive.driveStraightInches(
            // TURN_AND_FIRE_DISTANCE, DRIVE_SPEED, ACCELERATION,
            // true))
            // {
            // turnAndFire = TurnAndFireState.FINISH;
            // }

            // break;
            case FINISH:
                return true;

            }
        return false;

    }

    public static enum pickupTrenchState
        {
        TURN, DRIVE_FORWARD, FINISH
        }

    public static pickupTrenchState pickup = pickupTrenchState.DRIVE_FORWARD;

    private static boolean pickupTrench()
    {
        // drive forward along balls picking them up
        switch (pickup)
            {
            // case TURN:
            // if (Hardware.drive.turnDegrees(ALIGN_SQUARE_LEFT_DEGREES,
            // DRIVE_SPEED, ACCELERATION, true))
            // {

            // }

            // break;
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
        DRIVE_BACK, TURN1, FINAL_DRIVE, FINISH
        }

    public static AlignTrenchState trench = AlignTrenchState.TURN1;

    private static boolean alignTrench()
    {
        if (Hardware.shootingPlan.getPosition() == Relay.Value.kReverse)
            {
            if (position == Position.RIGHT)
                {
                switch (trench)
                    {
                    // case DRIVE_BACK:
                    // // drive backwards
                    // if (Hardware.drive.driveStraightInches(
                    // ALIGN_TRENCH_MOVE_BACK_DISTANCE,
                    // -DRIVE_SPEED,
                    // ACCELERATION, true))
                    // {
                    // trench = AlignTrenchState.TURN1;
                    // }
                    // break;
                    case TURN1:
                        // turn turn degrees right
                        if (Hardware.drive.turnDegrees(ALIGN_TRENCH_RIGHT_DEGREES, TURN_SPEED, ACCELERATION, true))
                            {
                            trench = AlignTrenchState.FINISH;
                            break;
                            }
                        break;
                    // case FINAL_DRIVE:
                    // // drive up to trench
                    // if (Hardware.drive.driveStraightInches(
                    // ALIGN_TRENCH_RIGHT_DISTANCE, DRIVE_SPEED,
                    // ACCELERATION,
                    // true))
                    // {
                    // trench = AlignTrenchState.FINISH;
                    // }

                    // break;
                    case FINISH:
                        return true;
                    }

                }
            }
        else
            {

            if (position == Position.RIGHT)
                {
                switch (trench)
                    {
                    case DRIVE_BACK:
                        // drive backwards

                        trench = AlignTrenchState.TURN1;

                        break;
                    case TURN1:
                        // turn turn degrees right
                        if (Hardware.drive.turnDegrees(ALIGN_TRENCH_RIGHT_DEGREES, TURN_SPEED, ACCELERATION, true))
                            {
                            trench = AlignTrenchState.FINAL_DRIVE;
                            break;
                            }
                        break;
                    case FINAL_DRIVE:
                        // drive up to trench
                        if (Hardware.drive.driveStraightInches(ALIGN_TRENCH_RIGHT_DISTANCE, DRIVE_SPEED, ACCELERATION,
                                true))
                            {
                            trench = AlignTrenchState.FINISH;
                            }

                        break;
                    case FINISH:
                        return true;
                    }

                }
            }

        return false;

    }

    public static enum AlignSquareState
        {
        DRIVE_BACK, TURN1, ALIGN, TURN2, FINAL_DRIVE, FINISH
        }

    public static AlignSquareState square = AlignSquareState.DRIVE_BACK;

    private static boolean alignSquare()
    {
        if (Hardware.shootingPlan.getPosition() == Relay.Value.kReverse)
            {
            if (position == Position.LEFT)
                {
                switch (square)
                    {
                    case DRIVE_BACK:
                        // drive back towards line
                        if (Hardware.drive.driveStraightInches(ALIGN_SQUARE_MOVE_BACK_DISTANCE, -DRIVE_SPEED,
                                ACCELERATION, true))
                            {
                            square = AlignSquareState.TURN1;
                            }

                        break;
                    case TURN1:
                        // turn away from tower
                        if (Hardware.drive.turnDegrees(ALIGN_SQUARE_LEFT_DEGREES, TURN_SPEED, ACCELERATION, true))
                            {
                            square = AlignSquareState.ALIGN;
                            break;
                            }

                        break;
                    case ALIGN:
                        // drive away from tower
                        if (Hardware.drive.driveStraightInches(ALIGN_SQUARE_LEFT_DISTANCE, DRIVE_SPEED, ACCELERATION,
                                true))
                            {
                            // square = AlignSquareState.TURN2;
                            // }
                            // break;
                            // case TURN2:
                            // // turn towards square
                            // if
                            // (Hardware.drive.turnDegrees(ALIGN_SQUARE_LEFT_FINAL_DEGREES,
                            // TURN_SPEED,
                            // ACCELERATION, false))
                            // {
                            // square = AlignSquareState.FINAL_DRIVE;
                            // break;
                            // }

                            // break;
                            // case FINAL_DRIVE:

                            // // drive towards square final distance
                            // if
                            // (Hardware.drive.driveStraightInches(ALIGN_SQUARE_LEFT_FINAL_DISTANCE,
                            // DRIVE_SPEED, ACCELERATION,
                            // true))
                            // {
                            square = AlignSquareState.FINISH;
                            }
                        break;
                    case FINISH:
                        return true;
                    }
                }
            }
        else
            {

            if (position == Position.LEFT)
                {
                switch (square)
                    {
                    case DRIVE_BACK:
                        // drive back towards line

                        square = AlignSquareState.TURN1;

                        break;
                    case TURN1:
                        // turn away from tower
                        if (Hardware.drive.turnDegrees(ALIGN_SQUARE_LEFT_DEGREES, TURN_SPEED, ACCELERATION, true))
                            {
                            square = AlignSquareState.ALIGN;
                            break;
                            }

                        break;
                    case ALIGN:
                        // drive away from tower
                        if (Hardware.drive.driveStraightInches(ALIGN_SQUARE_LEFT_DISTANCE, DRIVE_SPEED, ACCELERATION,
                                true))
                            {
                            // square = AlignSquareState.TURN2;
                            // }
                            // break;
                            // case TURN2:
                            // // turn towards square
                            // if
                            // (Hardware.drive.turnDegrees(ALIGN_SQUARE_LEFT_FINAL_DEGREES,
                            // TURN_SPEED,
                            // ACCELERATION, false))
                            // {
                            // square = AlignSquareState.FINAL_DRIVE;
                            // break;
                            // }

                            // break;
                            // case FINAL_DRIVE:

                            // // drive towards square final distance
                            // if
                            // (Hardware.drive.driveStraightInches(ALIGN_SQUARE_LEFT_FINAL_DISTANCE,
                            // DRIVE_SPEED, ACCELERATION,
                            // true))
                            // {
                            square = AlignSquareState.FINISH;
                            }
                        break;
                    case FINISH:
                        return true;
                    }
                }

            }
        return false;
    }

    public static enum GetOutState
        {
        TURN, FINAL_DRIVE, FINIHS
        }

    public static GetOutState out = GetOutState.TURN;

    private static boolean getOut()
    {

        System.out.println("Out State: " + out);
        switch (out)
            {
            case TURN:

                if (position == Position.RIGHT)
                    {
                    if (Hardware.drive.turnDegrees(GET_OUT_RIGHT_DEGREES, TURN_SPEED, ACCELERATION, true))
                        {
                        out = GetOutState.FINAL_DRIVE;
                        break;
                        }
                    }
                else if (position == Position.LEFT)
                    {

                    if (Hardware.drive.turnDegrees(GET_OUT_LEFT_DEGREES, TURN_SPEED, ACCELERATION, true))
                        {
                        out = GetOutState.FINAL_DRIVE;
                        break;
                        }

                    }
                else
                    {
                    out = GetOutState.FINAL_DRIVE;
                    }

                break;

            case FINAL_DRIVE:
                if (position == Position.RIGHT)
                    {
                    if (Hardware.drive.driveStraightInches(GET_OUT_RIGHT_DISTANCE, DRIVE_SPEED, ACCELERATION, true))
                        {
                        out = GetOutState.FINIHS;
                        }
                    }
                else if (position == Position.LEFT)
                    {
                    if (Hardware.drive.driveStraightInches(GET_OUT_LEFT_DISTANCE, DRIVE_SPEED, ACCELERATION, true))
                        {
                        out = GetOutState.FINIHS;
                        }
                    }
                else
                    {
                    if (Hardware.drive.driveStraightInches(GET_OUT_CENTER_DISTANCE, -GET_OUT_CENTER_SPEED, ACCELERATION,
                            true))
                        {
                        out = GetOutState.FINIHS;
                        }
                    }
                break;
            case FINIHS:
                return true;

            }

        return false;
    }

    private static boolean moveBackward()
    {
        if (Hardware.drive.driveStraightInches(OFF_LINE_DISTANCE, -.2, ACCELERATION, true))
            {
            return true;
            }
        return false;
    }

    private static boolean moveForward()
    {
        // this will pull forward the distance declared to leave the line.
        if (Hardware.drive.driveStraightInches(OFF_LINE_DISTANCE, .2, ACCELERATION, true))
            {
            return true;
            }
        return false;

    }

    private static boolean shootClose()
    {
        // System.out.println("Should Move");
        // Drive Forward Then shoot
        // Drive towards target
        System.out.println("Camera Distance: " + Hardware.visionInterface.getDistanceFromTarget());
        System.out.println("US Distance: " + Hardware.frontUltraSonic.getDistanceFromNearestBumper());

        if (Hardware.visionDriving.driveToTarget(45, true, .3))
            {
            if (Hardware.launcher.shootBallsAuto(false))
                {
                return true;
                }

            }
        return false;

    }

    public static enum farState
        {
        DRIVE_BACK, ALIGN, FINISH
        }

    public static farState far = farState.DRIVE_BACK;

    private static boolean shootFar()
    {
        switch (far)
            {

            case DRIVE_BACK:
                if (Hardware.drive.driveStraightInches(SHOOT_FAR_DRIVE_BACK_DISTANCE, -DRIVE_SPEED, ACCELERATION, true))
                    {
                    Hardware.autoTimer.start();// TODO
                    far = farState.ALIGN;
                    }

                break;
            case ALIGN:

                if (Hardware.visionDriving.alignToTarget())
                    {
                    // TODO
                    if (Hardware.launcher.shootBallsAuto(false))
                        {
                        far = farState.FINISH;
                        }

                    }
                break;

            case FINISH:

                return true;

            }
        return false;
    }

    private static boolean hasShotTheEtHInG = false;
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

    private final static int GET_OUT_RIGHT_DISTANCE = 36;

    // private final static int GET_OUT_CENTER_DEGREES = 0;
    private final static int GET_OUT_CENTER_DISTANCE = 120;

    private final static double GET_OUT_CENTER_SPEED = .3;

    private final static int ALIGN_SQUARE_MOVE_BACK_DISTANCE = 80;

    private final static int ALIGN_SQUARE_LEFT_DEGREES = 120;

    private final static int ALIGN_SQUARE_LEFT_DISTANCE = 45;
    // private final static int ALIGN_SQUARE_LEFT_FINAL_DEGREES = 45;
    // private final static int ALIGN_SQUARE_LEFT_FINAL_DISTANCE = 0;

    // private final static int ALIGN_TRENCH_MOVE_BACK_DISTANCE = 96;

    private final static int ALIGN_TRENCH_RIGHT_DEGREES = -120;

    private final static int ALIGN_TRENCH_RIGHT_DISTANCE = 24;

    // if the way i pick up the balls is just driving into them utilize this
    // constant
    private final static int PICKUP_DISTANCE = 120;

    private final static int TURN_AND_FIRE_GO_BACK_DISTANCE = 120;

    private final static int TURN_AND_FIRE_DEGREES = -150;

    private final static int TURN_AND_FIRE_DISTANCE = 0;

    private final static double TURN_SPEED = 0.4;

    private final static double DRIVE_SPEED = 0.4;

    private final static double ACCELERATION = .5;

    }
