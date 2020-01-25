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

import edu.wpi.first.wpilibj.Relay;
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
        INIT, DELAY, CHOOSE_PATH, FINISH
        }

    public static State autoState = State.INIT;

    public static void periodic()
    {
        // Cancel if the "cancelAuto" button is pressed
        if (Hardware.cancelAuto.get() == true)
            {
            autoState = State.FINISH;
            }

        switch (autoState)
            {

            case INIT:
                Hardware.autoTimer.start();

                autoState = State.DELAY;
                break;

            case DELAY:
                if (Hardware.autoTimer.get() > Hardware.delayPot.get(0, 5.0))
                    {

                    autoState = State.CHOOSE_PATH;
                    Hardware.autoTimer.stop();
                    }

                break;

            case CHOOSE_PATH:
                choosePath();
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
            }
        /*
         * 6 pos switch: this will determine the exiting strategy for each path
         */
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
            // move yourself out of the way, close to goal
<<<<<<< HEAD
            exit = Exit.TURN_AND_FIRE;
=======
            exit = Exit.GET_OUT;
>>>>>>> patrick commit
            }
        else if (Hardware.autoSixPosSwitch.getPosition() == 3)
            {
            // if you are going to turn and fire, this must be called after the functions of
            // alligning trench
<<<<<<< HEAD
            exit = Exit.GET_OUT;
=======
            exit = Exit.TURN_AND_FIRE;
>>>>>>> patrick commit
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

        // Switch case to execute the functions of auto path

        switch (path)
            {
            case NOTHING:

                // This will be for the insatnce where the robot is not a in a functioning state
                // and auto paths are broken
                autoState = State.FINISH;
                break;

            case SHOOT_FAR:
                // The action of not moving before the attempt of shooting

                // call methods for shooting far
                shootFar();

                // call Exit Strategy

                if (exit == Exit.ALIGN_SQUARE)
                    {
                    // This is a function that works if the robot is aligned on the left side
                    path = Path.ALIGN_SQUARE;
                    }
                else if (exit == Exit.ALIGN_TRENCH)
                    {
                    // this is a function that works if the robot is aligned on the right side
                    path = Path.ALIGN_TRENCH;
                    }
                else if (exit == Exit.TURN_AND_FIRE)
                    {
                    // Continuation of Alinging trench, adding the process of picking up those balls
                    // and attepting to shoot, or alligning to shoot again.
                    path = Path.ALIGN_TRENCH;
                    }
                else if (exit == Exit.GET_OUT)
                    {
                    // Move out of the way, staying close to goal to retreive missed balls
                    path = Path.GET_OUT;
                    }

                break;

            case SHOOT_CLOSE:
                // the action of moving closer before attempting to shoot in the goal
                shootClose();

                // determining exit stategy
                if (exit == Exit.ALIGN_SQUARE)
                    {
                    // This is a function that works if the robot is aligned on the left side
                    path = Path.ALIGN_SQUARE;
                    }
                else if (exit == Exit.ALIGN_TRENCH)
                    {
                    // this is a function that works if the robot is aligned on the right side
                    path = Path.ALIGN_TRENCH;
                    }
                else if (exit == Exit.TURN_AND_FIRE)
                    {
                    // Continuation of Alinging trench, adding the process of picking up those balls
                    // and attepting to shoot, or alligning to shoot again.
                    path = Path.ALIGN_TRENCH;
                    }
                else if (exit == Exit.GET_OUT)
                    {
                    // Move out of the way, staying close to goal to retreive missed balls
                    path = Path.GET_OUT;
                    }

                break;

            case MOVE_FORWARD:
                // move in a straight line forwards
                moveForward();

                break;

            case DONT_MOVE:
                // doAnything?
                break;

            case GET_OUT:
                // removing yourself from the way of robots
                getOut();
                break;

            case ALIGN_SQUARE:
                // aligning for center square to allign for balls
                // robot is on left side or center

                // call align
                alignSquare();
                break;

            case ALIGN_TRENCH:
                // aligning for trench to allign for balls
                // robot is on the right side or center

                // calling align
                alignTrench();

                // are we collecting?
                if (exit == Exit.TURN_AND_FIRE)
                    {
                    path = Path.PICKUP_TRENCH;
                    }
                break;

            case PICKUP_TRENCH:
                // picking up balls
                // only applicable if Align Trench was previously stated
                pickupTrench();

                // this will be executed if there is time
                path = Path.TURN_AND_FIRE;
                break;

            case TURN_AND_FIRE:
                // final attempt to turn and shoot more balls from trench
                turnFire();
                break;

            default:
                path = Path.NOTHING;
                break;

            }

    }

    private static void turnFire()
    {
        // drive backwards towards line

        // turn towards target

        // hail marry

    }

    private static void pickupTrench()
    {
        // drive forward along balls picking them up
        Hardware.drive.driveStraightInches(PICKUP_DISTANCE, DRIVE_SPEED, ACCELERATION, false);
    }

    private static void alignTrench()
    {
        if (position == Position.RIGHT)
            {
            // robot started in the right position
            // drive backwards
            Hardware.drive.driveStraightInches(ALIGN_TRENCH_MOVE_BACK_DISTANCE, -DRIVE_SPEED, ACCELERATION, false);
            // turn turn degrees right
            Hardware.drive.turnDegrees(ALIGN_TRENCH_RIGHT_DEGREES, TURN_SPEED, ACCELERATION, false);

            // drive up to trench
            Hardware.drive.driveStraightInches(ALIGN_TRENCH_RIGHT_DISTANCE, DRIVE_SPEED, ACCELERATION, false);

            }
<<<<<<< HEAD
=======
        else if (position == Position.CENTER)
            {
            // robot started in the center position
            // turn degrees right (Not the same)
            }
>>>>>>> patrick commit
    }

    private static void alignSquare()
    {
        if (position == Position.LEFT)
            {
            // robot started in the left position
            // drive back towards line
            Hardware.drive.driveStraightInches(ALIGN_SQUARE_MOVE_BACK_DISTANCE, -DRIVE_SPEED, ACCELERATION, false);
            // turn away from tower
            Hardware.drive.turnDegrees(ALIGN_SQUARE_LEFT_DEGREES, TURN_SPEED, ACCELERATION, false);
            // drive away from tower
            Hardware.drive.driveStraightInches(ALIGN_SQUARE_LEFT_DISTANCE, DRIVE_SPEED, ACCELERATION, false);
            // turn towards square
            Hardware.drive.turnDegrees(ALIGN_SQUARE_LEFT_FINAL_DEGREES, TURN_SPEED, ACCELERATION, false);
            // drive towards square final distance
            Hardware.drive.driveStraightInches(ALIGN_SQUARE_LEFT_FINAL_DISTANCE, DRIVE_SPEED, ACCELERATION, false);

            }
    }

    private static void getOut()
    {
        if (position == Position.RIGHT)
            {
            // robot started in the right position
            // turn degrees right
            Hardware.drive.turnDegrees(GET_OUT_RIGHT_DEGREES, TURN_SPEED, ACCELERATION, false);
            // drive towards wall
            Hardware.drive.driveStraightInches(GET_OUT_RIGHT_DISTANCE, DRIVE_SPEED, ACCELERATION, false);

            }
        else if (position == Position.LEFT)
            {
            // robot started in the left position
            // turn degrees left
<<<<<<< HEAD
            Hardware.drive.turnDegrees(GET_OUT_LEFT_DEGREES, TURN_SPEED, ACCELERATION, false);
            // drive towards wall
            Hardware.drive.driveStraightInches(GET_OUT_LEFT_DISTANCE, DRIVE_SPEED, ACCELERATION, false);
            }
        else if (position == Position.CENTER)
            {
            // drives straight back along same path, moves 10 feet back
            Hardware.drive.driveStraightInches(GET_OUT_CENTER_DISTANCE, -DRIVE_SPEED, ACCELERATION, false);

            }

=======
            }
        else if (position == Position.CENTER)
            {
            // robot started in the center position
            // what the fuck
            }
>>>>>>> patrick commit
    }

    private static void moveBackward()
    {
        // nothing yet?
    }

    private static void moveForward()
    {
        // this will pull forward the distance declared to leave the line.
        Hardware.drive.driveStraightInches(OFF_LINE_DISTANCE, DRIVE_SPEED, ACCELERATION, false);

    }

    private static void shootClose()
    {
        // Drive Forward Then shoot
        // Drive towards target
        Hardware.visionDriving.driveToTarget();
        // Call methods to shoot

    }

    private static void shootFar()
    {
        // Call functions to Shoot
    }

    /*
<<<<<<< HEAD
     * =============================================================================
     * Constants ==============================================================
=======
     * ============================================================= Constants
     * ==============================================================
>>>>>>> patrick commit
     */

    private final static double AUTO_GEAR = 1.0;

    private final static int OFF_LINE_DISTANCE = 30;

    private final static int GET_OUT_LEFT_DEGREES = -60;
    private final static int GET_OUT_LEFT_DISTANCE = 36;

    private final static int GET_OUT_RIGHT_DEGREES = 60;
    private final static int GET_OUT_RIGHT_DISTANCE = 36;

    private final static int GET_OUT_CENTER_DEGREES = 0;
    private final static int GET_OUT_CENTER_DISTANCE = 120;

    private final static int ALIGN_SQUARE_MOVE_BACK_DISTANCE = 120;
    private final static int ALIGN_SQUARE_LEFT_DEGREES = 150;
    private final static int ALIGN_SQUARE_LEFT_DISTANCE = 120;
    private final static int ALIGN_SQUARE_LEFT_FINAL_DEGREES = 45;
    private final static int ALIGN_SQUARE_LEFT_FINAL_DISTANCE = 0;

    private final static int ALIGN_TRENCH_MOVE_BACK_DISTANCE = 120;
    private final static int ALIGN_TRENCH_RIGHT_DEGREES = 120;
    private final static int ALIGN_TRENCH_RIGHT_DISTANCE = 80;

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
