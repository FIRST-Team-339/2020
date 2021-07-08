package frc.Utils;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.Hardware.*;
import frc.HardwareInterfaces.DoubleSolenoid;
import frc.robot.Teleop;
import frc.vision.LimelightInterface.LedMode;

/**
 * Code to control the 2020 seasons intake machanism. Includes code control the
 * deploying of the mechanism as well as motor controls. Also includes auto
 * funtions to pick up balls.
 *
 * @author Conner McKevitt
 */
public class IntakeControl
    {

    Timer timer = null;
    SpeedController intakeMotor = null;
    DoubleSolenoid solenoid = null;

 



    public IntakeControl(Timer timer, DoubleSolenoid solenoid, SpeedController intakeMotor)
        {
            this.timer = timer;
            this.timer.reset();
            this.solenoid = solenoid;
            this.intakeMotor = intakeMotor;

        }

   private boolean released = true;
    /**
     * toggles the intake deployed state via a button
     *
     * @param button
     */
    public void toggleDeployIntake(JoystickButton button)
    {
        if (button.get() && released)
            {
            released = false;
            // if deployed
            if (this.solenoid.getForward())
                {
                // undeploy
                this.solenoid.set(Value.kReverse);
                }
            // if not deployed
            if (!this.solenoid.getForward())
                {
                // deploy
                this.solenoid.set(Value.kForward);
                }
            }
        else if (!button.get())
            {
            released = true;
            }
    }

    /**
     * undeploys the intake mechanism via a button
     *
     * @param button
     * @return
     */
    public void undeployIntake(JoystickButton button)
    {
        if (button.get())
            {

            this.solenoid.set(Value.kReverse);
            }
    }

    /**
     * undeploys the intake mechanism
     *
     * @param button
     * @return
     */
    public void undeployIntake()
    {

        this.solenoid.set(Value.kReverse);

    }

    /**
     * deploys the intake mechanism via a button
     *
     * @param button
     * @return
     */
    public void deployIntake(JoystickButton button)
    {
        if (button.get())
            {

            this.solenoid.set(Value.kForward);

            }

    }

    /**
     * deploys the intake mechanism
     *
     * @param button
     * @return
     */
    public void deployIntake()
    {

        this.solenoid.setForward(true);

    }

    /**
     *
     * @return if intake is deployed
     */
    public boolean getDeployed()
    {
        return this.solenoid.getForward();
    }

    /**
     * makes the intake passive in not intaking or outtaking must be constantly
     * called
     *
     * @param intakeButton
     * @param outtakeButton
     */
    public void makePassive(JoystickButton intakeButton, JoystickButton outtakeButton, JoystickButton launchButton)
    {
        if (!intakeButton.get() && !outtakeButton.get() && !intaking && !outtaking && !launchButton.get())
            {
            this.undeployIntake();
            this.intakeMotor.set(PASSIVE_SPEED);
            }
    }

    /**
     * makes the intake passive in not intaking or outtaking. Must be constantly
     * called
     */
    public void makePassive()
    {
        if (!intaking && !outtaking)
            {
            this.undeployIntake();
            this.intakeMotor.set(PASSIVE_SPEED);
            }
    }

    boolean prevIntakeRL = false;

    /**
     * intakes balls based off joystick input
     *
     * @param intakeButton
     * @param overrideButton
     */
    public void intake(JoystickButton intakeButton, JoystickButton overrideButton)
    {

        if (Hardware.intakeRL.isOn() == true)
            {
            prevIntakeRL = true;
            }
        if (Hardware.lowStoreRL.isOn() == true || overrideButton.get())
            {
            prevIntakeRL = false;
            }
        // dont intake al there is already 5 balls stored unless override button pressed
        if (Hardware.ballCounter.getBallCount() < 5/*  && prevIntakeRL == false) */ || overrideButton.get())
            {
            intaking = true;
            // if intake deployed

            // if got button
            if (intakeButton.get())
                {

                this.deployIntake();

                this.intakeMotor.set(INTAKE_SPEED);

                }
            else
                {
                intaking = false;
                //prevIntakeRL = false;
                }
            }
        else if (outtaking == false)
            {
            // prevIntakeRL = false;
            intaking = false;
            }

    }

    /**
     * intake with out joystick input
     */
    public void intake()
    {
        Hardware.storage.intakeStorageControl();
        this.intaking = true;
        this.deployIntake();

        this.intakeMotor.set(INTAKE_SPEED);

    }

    /**
     * outake based off of joystick inputs
     *
     * @param outtakeButton
     * @param overrideButton
     */
    public void outtake(JoystickButton outtakeButton, JoystickButton overrideButton)
    {

        // dont outtake al there are no balls stored unless override button pressed
        if (Hardware.ballCounter.getBallCount() > 0 || overrideButton.get())
            {
            outtaking = true;
            // if intake deployed

            if (outtakeButton.get())
                {
                // sets intaking boolean to true
                // System.out.println("intaking");
                this.deployIntake();
                this.intakeMotor.set(OUTTAKE_SPEED);

                }
            else
                {
                outtaking = false;
                }
            }
        else
            {
            outtaking = false;
            }
    }

    /**
     * out take until there are a certain number of balls remaining
     */
    public boolean outtake(int remainingBalls)
    {
        // if at wanted number of balls
        if (Hardware.ballCounter.getBallCount() == remainingBalls)
            {
            // undeploy
            this.undeployIntake();
            outtaking = false;
            return true;
            }
        else
            {
            outtaking = true;
            this.deployIntake();
            this.intakeMotor.set(OUTTAKE_SPEED);

            }
        return false;
    }

    // if first loop
    boolean pickUpBallsFirstTime = true;

    int startBallCount = 0;

    /**
     * chases and picks up balls in auto.Runs until the storage is full or picked up
     * all 3 balls in the trench
     *
     * @return picked up all ballsd
     */
    public boolean pickUpBallsVision()
    {

        if (pickUpBallsFirstTime)
            {
            startBallCount = Hardware.ballCounter.getBallCount();
            pickUpBallsFirstTime = false;

            }
        // set pipe
        Hardware.visionInterface.setPipeline(BALL_VISION_PIPE);
        Hardware.cameraServo.setCameraAngleDown();
        // intake

        this.intake();
        // drive to balls
        Hardware.visionDriving.driveToTargetNoDistance(VISION_SPEED_BALLS, true);
        // if pickup all the balls in trench
        if ((Hardware.ballCounter.getBallCount() >= startBallCount + BALLS_IN_TRENCH)
                || (Hardware.ballCounter.getBallCount() == 5))
            {
            Hardware.visionInterface.setPipeline(TARGET_VISION_PIPE);
            pickUpBallsFirstTime = true;
            this.intakeMotor.set(0);

            return true;
            }
        return false;
    }

    /**
    * chases and picks up balls in auto.Runs until the storage is full or picked up
    * all 3 balls in the trench
    *
    * @return picked up all ballsd
    */
    public void pickUpBallsVisionTeleop(JoystickButton button)
    {

        if (button.get())
            {
            usingVisionIntake = true;
            Teleop.setDisableTeleOpDrive(true);
            Hardware.visionInterface.setLedMode(LedMode.PIPELINE);
            // set pipe
            Hardware.visionInterface.setPipeline(BALL_VISION_PIPE);
            Hardware.cameraServo.setCameraAngleDown();
            // intake

            this.intake();
            // drive to balls
            Hardware.visionDriving.driveToTargetNoDistance(VISION_SPEED_BALLS, true);
            // if pickup all the balls in trench
            if ((Hardware.ballCounter.getBallCount() == 5))
                {
                Hardware.visionInterface.setPipeline(TARGET_VISION_PIPE);
                Hardware.cameraServo.setCameraAngleUp();
                }
            }
        else if (!setVisionForClimb)
            {
            usingVisionIntake = false;
            Hardware.visionInterface.setPipeline(TARGET_VISION_PIPE);
            Hardware.cameraServo.setCameraAngleUp();
            if (Hardware.launcher.shootingBalls == false && !Hardware.launchOverrideButton.get())
                {
                Teleop.setDisableTeleOpDrive(false);
                Hardware.visionInterface.setLedMode(LedMode.OFF);
                }
            }

    }

    // store if we are current intaking
    public boolean intaking = false;

    // store if we are outtaking
    public boolean outtaking = false;

    //if if we are using vision to intake
    public boolean usingVisionIntake = false;

    public boolean setVisionForClimb = false;

    // the pipe line to chase balls
    private final int BALL_VISION_PIPE = 2;

    // the pipeline find vision target
    private final int TARGET_VISION_PIPE = 0;

    // speed to drive towards balls at
    private final double VISION_SPEED_BALLS = .25;

    // number of balls in the trench
    private final int BALLS_IN_TRENCH = 3;

    // intake motor power
<<<<<<< HEAD
    private final double INTAKE_SPEED = 0.88; // TODO
=======
    private final double INTAKE_SPEED = 0.88;
>>>>>>> 99bf9f62aae6054769b93d2cdd39c6ec88d00c0f

    // outtake motor power
    private final double OUTTAKE_SPEED = -.88;

    // no intake or outtake speed
    private final double PASSIVE_SPEED = 0;
    }
