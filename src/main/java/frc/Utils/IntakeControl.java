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

    public boolean deployIntake()
    {
        if (!this.solenoid.getForward())
            {
            this.solenoid.set(Value.kForward);
            }
        if (this.solenoid.getForward())
            {
            return true;
            }
        return false;
    }

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

    public boolean getDeployed()
    {
        //TODO find out if forward or backs is deployed or not
        return this.solenoid.getForward();
    }

    public void intake(JoystickButton intakeButton, JoystickButton overrideButton)
    {
        if (deployIntake())
            {
            if (Hardware.ballcounter.getBallCount() <= 5 || overrideButton.get())
                {
                if (intakeButton.get())
                    {
                    intaking = true;
                    //Hardware.intakeMotor.set(INTAKE_SPEED);//TODO
                    }
                else
                    {
                    if (!outtaking)
                        {
                        Hardware.intakeMotor.set(0);
                        }
                    intaking = false;
                    }
                }
            }
    }

    public boolean intake(int seconds)
    {
        if (deployIntake())
            {
            this.timer.start();
            if (this.timer.get() < seconds)
                {
                intaking = true;
                Hardware.intakeMotor.set(INTAKE_SPEED);

                }
            else
                {
                if (!outtaking)
                    {
                    Hardware.intakeMotor.set(0);
                    }
                intaking = false;
                this.timer.stop();
                this.timer.reset();
                return true;
                }
            }
        return false;
    }

    public void outtake(JoystickButton outtakeButton, JoystickButton overrideButton)
    {
        if (deployIntake())
            {
            if (Hardware.ballcounter.getBallCount() <= 5 || overrideButton.get())
                {
                if (outtakeButton.get())
                    {
                    outtaking = true;
                    //Hardware.intakeMotor.set(OUTTAKE_SPEED);//TODO
                    }
                else
                    {
                    if (!intaking)
                        {
                        Hardware.intakeMotor.set(0);
                        }
                    outtaking = false;
                    }
                }
            }
    }

    public boolean outtake()
    {
        //TODO dont knwo if we need this
        return false;
    }

    public boolean intaking = false;
    public boolean outtaking = false;

    private final double INTAKE_SPEED = .5;// TODO
    private final double OUTTAKE_SPEED = -.5;
    }
