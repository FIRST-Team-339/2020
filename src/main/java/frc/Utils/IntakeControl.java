package frc.Utils;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.Hardware.*;

public class IntakeControl
    {

    Timer timer = null;
    WPI_TalonSRX intakeMotor = null;

    public IntakeControl(Timer timer)
        {
            this.timer = timer;
            this.timer.reset();
            //this.intakeMotor = intakeMotor;

        }

    public void intake(JoystickButton intakeButton, JoystickButton overrideButton)
    {

        if (intakeButton.get())
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
            }
    }

    public boolean intake(int seconds)
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
        return false;
    }

    public void outtake(JoystickButton outtakeButton, JoystickButton overrideButton)
    {
        if (outtakeButton.get())
            {
            outtaking = true;
            // set motor to vomit up the balls it sucked
            Hardware.intakeMotor.set(OUTTAKE_SPEED);
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

    public boolean outtake()
    {
        return false;
    }

    public boolean intaking = false;
    public boolean outtaking = false;

    private final double INTAKE_SPEED = .5;// TODO
    private final double OUTTAKE_SPEED = -.5;
    }
