package frc.Utils;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.Hardware.*;
import frc.HardwareInterfaces.DoubleSolenoid;

/**
 * Code to control the 2020 seasons intake machanism. Includes code control the deploying of the mechanism as well as motor controls. Also includes auto funtions to pick up balls.
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

    /**
     *toggles the intake deployed state via a button
     * @param button
     */
    public void toggleDeployIntake(JoystickButton button)
    {
        if (button.get())
            {
            //if deployed
            if (this.solenoid.getForward())
                {
                //undeploy
                this.solenoid.set(Value.kReverse);
                }
            //if not deployed
            if (!this.solenoid.getForward())
                {
                //deploy
                this.solenoid.set(Value.kForward);
                }
            }
    }

    /**
     * undeploys the intake mechanism via a button
     * @param button
     * @return
     */
    public boolean undeployIntake(JoystickButton button)
    {
        if (button.get())
            {
            if (this.solenoid.getForward())
                {
                this.solenoid.set(Value.kReverse);
                }
            if (!this.solenoid.getForward())
                {
                return true;
                }
            }
        return false;
    }

    /**
    * undeploys the intake mechanism
    * @param button
    * @return
    */
    public boolean undeployIntake()
    {
        if (this.solenoid.getForward())
            {
            this.solenoid.set(Value.kReverse);
            }
        if (!this.solenoid.getForward())
            {
            return true;
            }
        return false;
    }

    /**
    * deploys the intake mechanism via a button
    * @param button
    * @return
    */
    public boolean deployIntake(JoystickButton button)
    {
        if (button.get())
            {
            if (this.solenoid.getForward())
                {
                this.solenoid.set(Value.kReverse);
                }
            if (!this.solenoid.getForward())
                {
                return true;
                }
            }
        return false;
    }

    /**
    * deploys the intake mechanism
    * @param button
    * @return
    */
    public boolean deployIntake()
    {
        if (this.solenoid.getForward())
            {
            this.solenoid.set(Value.kReverse);
            }
        if (!this.solenoid.getForward())
            {
            return true;
            }
        return false;
    }

    /**
     *
     * @return if intake is deployed
     */
    public boolean getDeployed()
    {
        return this.solenoid.getForward();// TODO
    }

    /**
     * makes the intake passive in not intaking or outtaking must be constantly called
     * @param intakeButton
     * @param outtakeButton
     */
    public void makePassive(JoystickButton intakeButton, JoystickButton outtakeButton)
    {
        if (!intakeButton.get() && !outtakeButton.get() && !intaking && !outtaking)
            {
            this.undeployIntake();
            this.intakeMotor.set(PASSIVE_SPEED);
            }
    }

    /**
    * makes the intake passive in not intaking or outtaking. Must be constantly called
    */
    public void makePassive()
    {
        if (!intaking && !outtaking)
            {
            this.undeployIntake();
            this.intakeMotor.set(PASSIVE_SPEED);
            }
    }

    /**
     * intakes balls based of joystick input
     * @param intakeButton
     * @param overrideButton
     */
    public void intake(JoystickButton intakeButton, JoystickButton overrideButton)
    {
        intaking = true;
        // dont intake al there is already 5 balls stored unless override button pressed
        if (Hardware.ballCounter.getBallCount() < 5 || overrideButton.get())
            {
            //if intake deployed
            if (this.getDeployed())
                {
                //if got button
                if (intakeButton.get())
                    {
                    //sets intaking boolean to true
                    // System.out.println("intaking");
                    this.intakeMotor.set(INTAKE_SPEED);
                    }
                }
            else
                {
                //deploy if not deployed
                this.deployIntake();
                }
            }
        else
            {
            //un deploy intake if button not pressed
            this.undeployIntake();
            }
    }

    /**
     * intake with out joystick input
     */
    public void intake()
    {
        if (getDeployed())
            {
            intaking = true;
            this.intakeMotor.set(INTAKE_SPEED);
            }
        else
            {
            this.deployIntake();
            }
    }

    /**
     * outake  based off of joystick inputs
     * @param outtakeButton
     * @param overrideButton
     */
    public void outtake(JoystickButton outtakeButton, JoystickButton overrideButton)
    {
        outtaking = true;
        // dont outtake al there are no balls stored unless override button pressed
        if (Hardware.ballCounter.getBallCount() > 0 || overrideButton.get())
            {
            //if intake deployed
            if (this.getDeployed())
                {
                //if got button
                if (outtakeButton.get())
                    {
                    //sets intaking boolean to true
                    // System.out.println("intaking");
                    this.intakeMotor.set(OUTTAKE_SPEED);
                    }
                }
            else
                {
                //deploy if not deployed
                this.deployIntake();
                }
            }
        else
            {
            //un deploy intake if button not pressed
            this.undeployIntake();
            }
    }

    /**
     * out take until there are a certain number of balls remaining
     */
    public boolean outtake(int remainingBalls)
    {
        //if at wanted number of balls
        if (Hardware.ballCounter.getBallCount() == remainingBalls)
            {
            //undeploy
            this.undeployIntake();
            outtaking = false;
            return true;
            }
        else
            {
            outtaking = true;
            if (this.getDeployed())
                {
                this.intakeMotor.set(OUTTAKE_SPEED);
                }
            else
                {
                this.deployIntake();
                }
            }
        return false;
    }

    // if first loop
    boolean pickUpBallsFirstTime = true;

    int startBallCount = 0;

    /**
     * chases and picks up balls in auto.Runs until the storage is full or picked up all 3 balls in the trench
     * @return picked up all ballsd
     */
    public boolean pickUpBallsVision()
    {
        if (pickUpBallsFirstTime)
            {
            startBallCount = Hardware.ballCounter.getBallCount();
            pickUpBallsFirstTime = false;
            }
        //set pipe
        Hardware.visionInterface.setPipeline(BALL_VISION_PIPE);
        //intake
        this.intake();
        //drive to balls
        Hardware.visionDriving.driveToTargetNoDistance(VISION_SPEED_BALLS);
        //if pickup all the balls in trench
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

    //store if we are current intaking
    public boolean intaking = false;

    //store if we are outtaking
    public boolean outtaking = false;

    //the pipe line to chase balls
    private final int BALL_VISION_PIPE = 2;

    //the pipeline find vision target
    private final int TARGET_VISION_PIPE = 0;

    //speed to drive towards balls at
    private final double VISION_SPEED_BALLS = .2;

    //number of balls in the trench
    private final int BALLS_IN_TRENCH = 3;

    //intake motor power
    private final double INTAKE_SPEED = .5;// TODO

    //outtake motor power
    private final double OUTTAKE_SPEED = -.5;

    //no intake or outtake speed
    private final double PASSIVE_SPEED = 0;
    }
