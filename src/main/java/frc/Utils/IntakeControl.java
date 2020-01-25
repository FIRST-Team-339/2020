package frc.Utils;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.Hardware.*;

public class IntakeControl
    {

    Timer timer = null;
    SpeedController intakeMotor = null;

    public IntakeControl(Timer timer, SpeedController intakeMotor)
        {
            this.timer = timer;
            this.timer.reset();
            this.intakeMotor = intakeMotor;

        }

    public void intake(JoystickButton intakeButton)
    {
        if (intakeButton.get())
            {
            intaking = true;
            intakeMotor.set(INTAKE_SPEED);
            }
        else
            {
            intaking = false;
            }
    }

    public boolean intake(int seconds)
    {
        this.timer.start();
        if (this.timer.get() < seconds)
            {
            intaking = true;
            intakeMotor.set(INTAKE_SPEED);
            }
        else
            {
            intaking = false;
            this.timer.stop();
            this.timer.reset();
            return true;
            }
        return false;
    }

    public void outtake(JoystickButton outtakeButton)
    {
        if (outtakeButton.get())
            {
            outtaking = true;
            // set motor to vomit up the balls it sucked
            this.intakeMotor.set(OUTTAKE_SPEED);
            }
        else
            {
            outtaking = false;
            }
    }

    public boolean outtake()
    {
        if (Hardware.storage.getBallCount() > 0)
            {
            outtaking = true;
            this.intakeMotor.set(OUTTAKE_SPEED);
            }
        else
            {
            outtaking = false;
            return true;
            }
        return false;
    }

    public boolean intaking = false;
    public boolean outtaking = false;

    private final double INTAKE_SPEED = .5;// TODO
    private final double OUTTAKE_SPEED = -.5;
    }
