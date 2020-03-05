package frc.Utils;

import frc.Hardware.Hardware;
import frc.HardwareInterfaces.KilroyServo;
import frc.HardwareInterfaces.Potentiometer;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

/**
 * code to control the angle of the launcher or the 2020 season
 *
 * @author Conner McKevitt
 */
public class HoodControl
    {
    Servo servo = null;
    Potentiometer pot = null;

    public HoodControl(Servo servo)
        {
            this.servo = servo;

        }

    Timer timer = new Timer();
    boolean raising = false;
    boolean lowering = false;
    public boolean isUp = false;

    public void raiseHood(JoystickButton button)
    {
        System.out.println("time: " + timer.get());
        if (button.get() && !raising)
            {
            timer.stop();
            timer.reset();
            raising = true;
            timer.start();
            }
        if (raising && !isUp)
            {
            if (timer.get() < UP_TIME)
                {
                this.servo.set(.2);
                isUp = false;
                }
            else
                {
                timer.stop();
                timer.reset();
                raising = false;
                }
            }
        else if (!lowering && !raising)
            {
            this.servo.set(.5);
            }
    }

    boolean firstRunRaise = true;

    public boolean raiseHood()
    {

        System.out.println("time: " + timer.get());
        if (firstRunRaise && !raising)
            {
            timer.stop();
            timer.reset();
            raising = true;
            firstRunRaise = false;
            timer.start();
            }
        if (raising && !isUp)
            {
            if (timer.get() < UP_TIME)
                {

                this.servo.set(.2);
                }
            else
                {
                isUp = true;
                firstRunRaise = true;
                timer.stop();
                timer.reset();
                raising = false;
                return true;
                }
            }
        else if (!lowering && !raising)
            {
            this.servo.set(.5);
            }
        return false;
    }

    public void lowerHood(JoystickButton button)
    {

        System.out.println("time: " + timer.get());
        if (button.get() && !lowering)
            {
            timer.stop();
            timer.reset();
            lowering = true;
            timer.start();
            }
        if (lowering)
            {
            if (timer.get() < DOWN_TIME)
                {
                this.servo.set(.8);
                }
            else
                {
                timer.stop();
                timer.reset();

                lowering = false;
                }
            }
        else if (!raising && !lowering)
            {
            timer.stop();
            timer.reset();
            this.servo.set(.5);
            }
    }

    boolean firstRunDown = true;

    public boolean lowerHood()
    {

        System.out.println("time: " + timer.get());
        if (firstRunDown && !lowering)
            {
            timer.stop();
            timer.reset();
            firstRunDown = false;
            lowering = true;
            timer.start();
            }
        if (lowering && isUp)
            {
            if (timer.get() < DOWN_TIME)
                {

                this.servo.set(.8);
                }
            else
                {
                isUp = false;
                firstRunDown = true;
                timer.stop();
                timer.reset();
                lowering = false;
                return true;
                }
            }
        else if (!raising && !lowering)
            {
            timer.stop();
            timer.reset();
            this.servo.set(.5);
            }
        return false;
    }

    public boolean getIsUp()
    {
        return isUp;
    }

    public void setIsUp(boolean value)
    {
        isUp = value;
    }

    // public boolean set
    final double FAR_ANGLE = 58;
    final double CLOSE_ANGLE = 37;
    final double UP_TIME = 2.5;
    final double DOWN_TIME = 2.5;
    final double UP_SPEED = .2;
    final double DOWN_SPEED = .8;
    }
