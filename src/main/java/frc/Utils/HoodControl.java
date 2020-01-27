package frc.Utils;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.HardwareInterfaces.Potentiometer;
import edu.wpi.first.wpilibj.Joystick;

public class HoodControl
    {
    SpeedController motor = null;
    Potentiometer pot = null;

    public HoodControl(SpeedController motor, Potentiometer hoodPot)
        {
            this.motor = motor;
            this.pot = hoodPot;
        }

    public boolean setAngle(int angle)
    {
        adjusting = true;
        if (angle > this.pot.get())
            {
            this.motor.set(ADJUST_SPEED);
            }
        if (angle < this.pot.get())
            {
            this.motor.set(-ADJUST_SPEED);
            }
        else
            {
            this.motor.set(0);
            return true;
            }

        return false;
    }

    private boolean adjusting = false;

    public void setAngle(Joystick joystick)
    {
        if (this.pot.get() <= MAX_ANGLE && this.pot.get() >= MIN_ANGLE)
            {
            if (joystick.getY() > .1)
                {
                this.motor.set(ADJUST_SPEED);
                }
            else if (joystick.getY() < -.1)
                {
                this.motor.set(-ADJUST_SPEED);
                }
            else if (adjusting = false)
                {
                this.motor.set(0);
                }
            }
    }

    private final double ADJUST_SPEED = .3;

    private final double MIN_ANGLE = 0;

    private final double MAX_ANGLE = 8;

    }
