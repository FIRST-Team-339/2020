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
public class Autonomous {

    /**
     * User Initialization code for autonomous mode should go here. Will run once
     * when the autonomous first starts, and will be followed immediately by
     * periodic().
     */
    public static void init() {

        Hardware.drive.setGearPercentage(4, AUTO_GEAR);
        Hardware.drive.setGear(4);
        /*
         * 2 pos switch: 1: no auto 2: go auto
         */
        if (Hardware.autoSwitch.isOn() == false) {
            autoState = State.FINISH;
        } else {
            autoState = State.INIT;
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

    public static enum Path {
        NOTHING, SHOOT_FAR, SHOOT_CLOSE, MOVE_FORWARD, MOVE_BACKWARDS, DONT_MOVE, DONT_LEAVE, GET_OUT, ALIGN_SQUARE,
        ALIGN_TRENCH, PICKUP_TRENCH, TURN_AND_FIRE
    }

    public static Path path = Path.NOTHING;

    public static enum State {
        INIT, DELAY, CHOOSE_PATH, FINISH
    }

    public static State autoState = State.INIT;

    public static void periodic() {
        // Cancel if the "cancelAuto" button is pressed
        if (Hardware.cancelAuto.get() == true) {
            autoState = State.FINISH;
        }

        switch (autoState) {

        case INIT:
            Hardware.autoTimer.start();

            autoState = State.DELAY;
            break;

        case DELAY:
            if (Hardware.autoTimer.get() > Hardware.delayPot.get(0, 5.0)) {

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
    private static void choosePath() {
        // Statements to determine sates:

        /*
         * 3 pos switch: 1: shoot far 2: shoot close 3: dont move kForward = shoot far
         * kreverse = shoot close off = dont move
         */

        if (Hardware.shootingPlan.getPosition() == Relay.Value.kForward) {
            path = Path.SHOOT_FAR;
        } else if (Hardware.shootingPlan.getPosition() == Relay.Value.kReverse) {
            path = Path.SHOOT_CLOSE;
        } else if (Hardware.shootingPlan.getPosition() == Relay.Value.kOff) {
            path = Path.DONT_MOVE;
        }
        /*
         * 6 pos switch: this will determine the exiting strategy for each path
         */
        if (Hardware.autoSixPosSwitch.getPosition() == 0) {

        } else if (Hardware.autoSixPosSwitch.getPosition() == 1) {

        } else if (Hardware.autoSixPosSwitch.getPosition() == 2) {

        } else if (Hardware.autoSixPosSwitch.getPosition() == 3) {

        } else if (Hardware.autoSixPosSwitch.getPosition() == 4) {

        } else {
            path = Path.NOTHING;
        }

        // Switch case to execute the functions of auto path

        switch (path) {
        case NOTHING:
            // Do Nothing :)
            // Sit there and contemplate your life :)
            // This will be for the insatnce where the robot is not a in a functioning state
            // and auto paths are broken

            break;

        case SHOOT_FAR:
            // The action of not moving before the attempt of shooting
            shootFar();
            break;

        case SHOOT_CLOSE:
            // the action of moving closer before attempting to shoot in the goal
            shootClose();
            break;

        case MOVE_FORWARD:
            // move in a straight line forwards
            moveForward();
            break;

        case MOVE_BACKWARDS:
            // move in a straight line backwards
            moveBackward();
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
            alignSquare();
            break;

        case ALIGN_TRENCH:
            // aligning for trench to allign for balls
            alignTrench();
            break;

        case PICKUP_TRENCH:
            // picking up balls
            pickupTrench();
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

    private static void turnFire() {
    }

    private static void pickupTrench() {
    }

    private static void alignTrench() {
    }

    private static void alignSquare() {
    }

    private static void getOut() {
    }

    private static void moveBackward() {
    }

    private static void moveForward() {
    }

    private static void shootClose() {
    }

    private static void shootFar() {

    }

    /*
     * ============================================================== Constants
     * ==============================================================
     */
    private final static double AUTO_GEAR = 1.0;
}
