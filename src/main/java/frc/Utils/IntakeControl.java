package frc.Utils;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.Hardware.*;
import frc.HardwareInterfaces.DoubleSolenoid;

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

    public void toggleDeployIntake(JoystickButton button)
    {
        if (this.solenoid.getForward())
            {
            this.solenoid.set(Value.kReverse);
            }
        if (!this.solenoid.getForward())
            {
            this.solenoid.set(Value.kForward);
            }
    }

    public void toggleDeployIntake()
    {
        if (this.solenoid.getForward())
            {
            this.solenoid.set(Value.kReverse);
            }
        if (!this.solenoid.getForward())
            {
            this.solenoid.set(Value.kForward);
            }
    }

    public boolean undeployIntake(JoystickButton button)
    {
        // if (this.solenoid.getForward())
        //     {
        //     this.solenoid.set(Value.kReverse);
        //     }
        // if (!this.solenoid.getForward())
        //     {
        return true;
        //     }
        // return false;
    }

    public boolean getDeployed()
    {
        //TODO find out if forward or backs is deployed or not
        return true;/* this.solenoid.getForward(); *///TODO
    }

    public void makePassive(JoystickButton intakeButton, JoystickButton outtakeButton)
    {
        if (!intakeButton.get() || !outtakeButton.get() || !intaking || !outtaking)
            {
            Hardware.intakeMotor.set(PASSIVE_SPEED);
            }
    }

    public void intake(JoystickButton intakeButton, JoystickButton overrideButton)
    {
        if (getDeployed())
            {
            Hardware.storage.intakeStorageControl();
            if (Hardware.ballcounter.getBallCount() <= 5 || overrideButton.get())
                {
                if (intakeButton.get())
                    {
                    intaking = true;
                    System.out.println("intaking");
                    Hardware.intakeMotor.set(INTAKE_SPEED);
                    }

                }
            }
        else
            {
            toggleDeployIntake();
            }
    }

    public boolean intake(int seconds)
    {
        if (getDeployed())
            {
            this.timer.start();
            if (this.timer.get() < seconds)
                {
                intaking = true;
                Hardware.intakeMotor.set(INTAKE_SPEED);

                }
            else
                {
                intaking = false;
                this.timer.stop();
                this.timer.reset();
                return true;
                }
            }
        else
            {
            toggleDeployIntake();
            }
        return false;
    }

    public void outtake(JoystickButton outtakeButton, JoystickButton overrideButton)
    {
        if (getDeployed())
            {
            if (Hardware.ballcounter.getBallCount() <= 5 || overrideButton.get())
                {
                if (outtakeButton.get())
                    {
                    outtaking = true;
                    Hardware.intakeMotor.set(OUTTAKE_SPEED);
                    }
                }
            }
        else
            {
            toggleDeployIntake();
            }
    }

    public boolean outtake(int remainingBalls)
    {
        if (Hardware.ballcounter.getBallCount() == remainingBalls)
            {
            return true;
            }
        else
            {
            Hardware.intakeMotor.set(OUTTAKE_SPEED);
            }
        return false;
    }

    public boolean intaking = false;
    public boolean outtaking = false;

    private final double INTAKE_SPEED = .5;// TODO
    private final double OUTTAKE_SPEED = -.5;
    private final double PASSIVE_SPEED = 0;
    }
