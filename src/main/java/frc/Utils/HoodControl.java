package frc.Utils;

import frc.Hardware.Hardware;
import frc.HardwareInterfaces.KilroyServo;
import frc.HardwareInterfaces.Potentiometer;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;

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

    Timer timer = new Timer();
    private boolean firstRun = true;

    public boolean raiseHood()
    {
        if (firstRun)
            {
            firstRun = false;
            this.timer.start();
            }
        if (this.timer.get() < UP_TIME)
            {
            this.servo.setSpeed(UP_SPEED);
            }
        else
            {
            this.servo.setSpeed(0);
            this.timer.stop();
            this.timer.reset();
            firstRun = true;
            return true;
            }
        return false;
    }

    public boolean lowerHood()
    {
        if (firstRun)
            {
            firstRun = false;
            this.timer.start();
            }
        if (this.timer.get() < DOWN_TIME)
            {
            this.servo.setSpeed(DOWN_SPEED);
            }
        else
            {
            firstRun = true;
            this.servo.setSpeed(0.0);
            this.timer.stop();
            this.timer.reset();
            return true;
            }
        return false;
    }

    public void testHood()
    {
        if (firstRun)
            {
            firstRun = false;
            this.timer.start();
            }
        if (Hardware.rightDriver.getRawButton(10))
            {
            System.out.println("time: " + this.timer.get());
            this.servo.setSpeed(UP_SPEED);
            }
        else
            {
            this.servo.setSpeed(0);
            firstRun = true;
            }
        System.out.println(timer.get());
    }

    // public boolean set
    final double FAR_ANGLE = 58;
    final double CLOSE_ANGLE = 37;
    final double UP_TIME = 0;
    final double DOWN_TIME = 0;
    final double UP_SPEED = .2;
    final double DOWN_SPEED = .2;
    }
