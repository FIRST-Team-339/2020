package frc.Utils;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.HardwareInterfaces.KilroyServo;
import frc.HardwareInterfaces.Potentiometer;
import edu.wpi.first.wpilibj.Joystick;

/**
 * code to control the angle of the launcher or the 2020 season
 *
 * @author Conner McKevitt
 */
public class HoodControl
    {
    KilroyServo servo = null;
    Potentiometer pot = null;

    public HoodControl(KilroyServo servo, Potentiometer hoodPot)
        {
            this.servo = servo;
            this.pot = hoodPot;
        }

    /**
     * sets the angle based off of input
     * @return at angle
     */
    public boolean setAngle(double angle)
    {
        //make sure we dont go above or below the min/max angles
        if (angle > MAX_ANGLE)
            {
            targetAngle = MAX_ANGLE;
            }
        else if (angle < MIN_ANGLE)
            {
            targetAngle = MIN_ANGLE;
            }
        else
            {
            targetAngle = angle;
            }

        adjusting = true;
        //if to big
        servo.set(targetAngle);
        if (servo.getAngle() == targetAngle)
            {
            return true;
            }

        return false;
    }

    //adjust the angle based off of the joystick
    public void setAngle(Joystick joystick)
    {
        //if with the range
        if (this.getAngle() <= MAX_ANGLE && this.getAngle() >= MIN_ANGLE)
            {
            if (adjusting == false)
                {
                if (joystick.getY() > JOYSTICK_DEADZONE)
                    {
                    this.servo.set(this.servo.getAngle() + 1);
                    }
                if (joystick.getY() < -JOYSTICK_DEADZONE)
                    {
                    this.servo.set(this.servo.getAngle() - 1);
                    }
                }

            }
    }

    /**
     * gives the current angle of the hood
     * @return current angle
     */
    public double getAngle()
    {
        return this.servo.getAngle();
    }

    private double targetAngle = 0;

    private boolean adjusting = false;

    private final double ANGLE_DEADBAND = 1;

    private final double JOYSTICK_DEADZONE = .2;

    private final double ADJUST_SPEED = .3;

    //MINIMUM angle
    private final double MIN_ANGLE = 30;//TODO
    //MAXIMUM angle
    private final double MAX_ANGLE = 70;//TODO

    //angle for shooting close
    public final double CLOSE_ANGLE = 37;

    //angle for shooting far
    public final double FAR_ANGLE = 58;

    }
