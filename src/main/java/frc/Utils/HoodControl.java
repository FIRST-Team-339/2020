package frc.Utils;

import frc.Hardware.Hardware;
import frc.HardwareInterfaces.KilroyServo;
import frc.HardwareInterfaces.Potentiometer;
import frc.Utils.Launcher.Position;
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

    Timer upTimer = new Timer();
    Timer downTimer = new Timer();
    public boolean raising = false;
    public boolean lowering = false;
    boolean isUp = false;

    boolean firstRunRaise = true;

    public HoodControl(Servo servo)
        {
            this.servo = servo;
        }

    public boolean raiseHood()
    {
        // System.out.println("time: " + timer.get());
        // System.out.println("raising");
        // System.out.println("is up: " + isUp);
        if (firstRunRaise && !raising)
            {
            // System.out.println("first running");
            upTimer.stop();
            upTimer.reset();
            raising = true;
            firstRunRaise = false;
            upTimer.start();
            }
        if (raising && !isUp)
            {
            if (upTimer.get() < UP_TIME)
                {
                raising = true;
                System.out.println("raising raising");
                this.servo.set(.2);
                }
            else
                {
                firstRunRaise = true;
                upTimer.stop();
                upTimer.reset();
                raising = false;
                isUp = true;
                if (!lowering && !raising)
                    {
                    this.servo.set(.5);
                    }
                return true;
                }
            }

        return false;
    }

    public void stopHoodMotor()
    {
        if (!raising && !lowering && !Hardware.launchButton.get())
            {
            // System.out.println("kjdabkljsdskjldffkjldfsfsn");
            firstRunDown = true;
            firstRunRaise = true;
            downTimer.stop();
            downTimer.reset();
            upTimer.stop();
            upTimer.reset();
            this.servo.set(.5);
            }
    }

    private boolean allowToggle = false;

    public void toggleHood(JoystickButton button)
    {
        // System.out.println("raise timer: " + upTimer.get());
        // System.out.println("down timer: " + downTimer.get());
        // System.out.println("is up: " + isUp);
        if (button.get() && !allowToggle)
            {
            Hardware.visionInterface.updateValues();
            allowToggle = true;
            }
        // System.out.println("hood target position: " + Hardware.launcher.targetPosition.toString());
        if (Hardware.visionInterface.getHasTargets())
            {
            if (Hardware.launcher.targetPosition == Position.CLOSE && !isUp)
                {
                System.out.println("not allow close");
                allowToggle = false;
                }
            if (Hardware.launcher.targetPosition == Position.FAR && isUp)
                {
                System.out.println("not allow far");
                allowToggle = false;
                }

            if (allowToggle)
                {
                if (isUp)
                    {
                    System.out.println("lowering hood");
                    if (lowerHood())
                        {
                        allowToggle = false;
                        }
                    }
                if (!isUp)
                    {
                    System.out.println("raising hood");
                    if (raiseHood())
                        {
                        allowToggle = false;
                        }
                    }
                }
            else
                {
                this.servo.set(.5);
                }
            }

    }

    boolean firstRunDown = true;

    public boolean lowerHood()
    {

        if (firstRunDown && !lowering)
            {
            downTimer.stop();
            downTimer.reset();
            firstRunDown = false;
            lowering = true;
            downTimer.start();
            }
        if (lowering && isUp)
            {
            if (downTimer.get() < DOWN_TIME)
                {
                lowering = true;
                System.out.println("lowering");
                this.servo.set(DOWN_SPEED);
                }
            else
                {
                System.out.println("lowered");
                firstRunDown = true;
                downTimer.stop();
                downTimer.reset();
                lowering = false;
                isUp = false;
                if (!lowering && !raising)
                    {
                    this.servo.set(.5);
                    }
                return true;
                }
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
    final double UP_TIME = 3.82;
    final double DOWN_TIME = 2.7;
    final double UP_SPEED = .2;
    final double DOWN_SPEED = .87;
    }
